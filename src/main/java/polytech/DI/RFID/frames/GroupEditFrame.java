package polytech.DI.RFID.frames;

import polytech.DI.RFID.frames.components.JTableUneditableModel;
import polytech.DI.RFID.frames.components.StudentsEditGroupRenderer;
import polytech.DI.RFID.objects.Group;
import polytech.DI.RFID.objects.Period;
import polytech.DI.RFID.objects.Student;
import polytech.DI.RFID.utils.Utils;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;

/**
 * Class of the group editing frame.
 *
 * @author COLEAU Victor, COUCHOUD Thomas
 */
public class GroupEditFrame extends JDialog
{
	private static final long serialVersionUID = 9133860717845268188L;
	private final JTable tableStudents;
	private final JTable tablePeriods;
	private final JTableUneditableModel modelPeriods;
	private final JTableUneditableModel modelStudents;
	private final Group group;

	/**
	 * Constructor.
	 *
	 * @param parent The parent frame.
	 * @param group The group to edit.
	 */
	public GroupEditFrame(GroupSettingsFrame parent, Group group)
	{
		super(parent);
		this.group = group;
		this.setIconImages(Utils.icons);
		this.setTitle(Utils.resourceBundle.getString("group_edit") + " : " + group.getName());
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.getContentPane().setBackground(MainFrame.backColor);
		this.getContentPane().setLayout(new GridBagLayout());
		/**************************************************************************/
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		this.modelStudents = new JTableUneditableModel(getStudentsTableList(group.getStudents()), new String[]{Utils.resourceBundle.getString("students")});
		this.tableStudents = new JTable(this.modelStudents)
		{
			private static final long serialVersionUID = 4244155500155330717L;

			@Override
			public Class<?> getColumnClass(int column)
			{
				return String.class;
			}
		};
		this.tableStudents.addKeyListener(new KeyListener()
		{
			@Override
			public void keyTyped(KeyEvent e)
			{
			}

			@Override
			public void keyPressed(KeyEvent e)
			{
				int rowindex = GroupEditFrame.this.tableStudents.getSelectedRow();
				if(rowindex < 0)
					return;
				if(e.getExtendedKeyCode() != KeyEvent.VK_DELETE)
					return;
				String name = GroupEditFrame.this.tableStudents.getValueAt(rowindex, 0).toString().trim();
				Student student = Utils.getStudentByName(name, true);
				removeStudent(student, rowindex, name);
				try
				{
					GroupEditFrame.this.tableStudents.setRowSelectionInterval(rowindex, rowindex);
				}
				catch(IllegalArgumentException e1)
				{
				}
			}

			@Override
			public void keyReleased(KeyEvent e)
			{
			}
		});
		this.tableStudents.addMouseListener(new MouseListener()
		{
			@Override
			public void mouseClicked(MouseEvent event)
			{
			}

			@Override
			public void mouseEntered(MouseEvent event)
			{
			}

			@Override
			public void mouseExited(MouseEvent event)
			{
			}

			@Override
			public void mousePressed(MouseEvent event)
			{
			}

			@Override
			public void mouseReleased(MouseEvent event)
			{
				int row = GroupEditFrame.this.tableStudents.rowAtPoint(event.getPoint());
				if(row >= 0 && row < GroupEditFrame.this.tableStudents.getRowCount())
					GroupEditFrame.this.tableStudents.setRowSelectionInterval(row, row);
				else
					GroupEditFrame.this.tableStudents.clearSelection();
				int rowindex = GroupEditFrame.this.tableStudents.getSelectedRow();
				if(event.isPopupTrigger() && event.getComponent() instanceof JTable)
				{
					String name = GroupEditFrame.this.tableStudents.getValueAt(rowindex, 0).toString().trim();
					Student student = Utils.getStudentByName(name, true);
					JPopupMenu popup = new JPopupMenu();
					JMenuItem deleteGroup = new JMenuItem(Utils.resourceBundle.getString("remove_student"));
					deleteGroup.addActionListener(event1 -> {
						try
						{
							removeStudent(student, rowindex, name);
						}
						catch(Exception exception)
						{
							Utils.logger.log(Level.WARNING, "", exception);
						}
					});
					popup.add(deleteGroup);
					popup.show(event.getComponent(), event.getX(), event.getY());
				}
			}
		});
		this.tableStudents.setDefaultRenderer(String.class, new StudentsEditGroupRenderer(centerRenderer));
		this.tableStudents.getTableHeader().setReorderingAllowed(false);
		this.tableStudents.getTableHeader().setResizingAllowed(true);
		this.tableStudents.setRowHeight(20);
		this.tableStudents.setShowGrid(true);
		this.tableStudents.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		this.tableStudents.setGridColor(Color.BLACK);
		JScrollPane scrollPaneStudents = new JScrollPane(this.tableStudents);
		scrollPaneStudents.setAutoscrolls(false);
		scrollPaneStudents.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		TableRowSorter<TableModel> sorter = new TableRowSorter<>(tableStudents.getModel());
		tableStudents.setRowSorter(sorter);
		ArrayList<RowSorter.SortKey> sortKeys = new ArrayList<>();
		sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
		sorter.setSortKeys(sortKeys);
		sorter.sort();
		/**************************************************************************/
		DefaultTableCellRenderer centerRenderer2 = new DefaultTableCellRenderer();
		centerRenderer2.setHorizontalAlignment(JLabel.CENTER);
		this.modelPeriods = new JTableUneditableModel(getPeriodsTableList(group.getPeriods()), new String[]{Utils.resourceBundle.getString("periods")});
		this.tablePeriods = new JTable(this.modelPeriods)
		{
			private static final long serialVersionUID = 4244155500155330717L;

			@Override
			public Class<?> getColumnClass(int column)
			{
				return String.class;
			}
		};
		this.tablePeriods.addKeyListener(new KeyListener()
		{
			@Override
			public void keyTyped(KeyEvent e)
			{
			}

			@Override
			public void keyPressed(KeyEvent e)
			{
				int rowindex = GroupEditFrame.this.tablePeriods.getSelectedRow();
				if(rowindex < 0)
					return;
				if(e.getExtendedKeyCode() != KeyEvent.VK_DELETE)
					return;
				Period period = group.getPeriodByName(GroupEditFrame.this.tablePeriods.getValueAt(rowindex, 0).toString());
				removePeriod(period, rowindex);
				try
				{
					GroupEditFrame.this.tablePeriods.setRowSelectionInterval(rowindex, rowindex);
				}
				catch(Exception e1)
				{
				}
			}

			@Override
			public void keyReleased(KeyEvent e)
			{
			}
		});
		this.tablePeriods.addMouseListener(new MouseListener()
		{
			@Override
			public void mouseClicked(MouseEvent event)
			{
			}

			@Override
			public void mouseEntered(MouseEvent event)
			{
			}

			@Override
			public void mouseExited(MouseEvent event)
			{
			}

			@Override
			public void mousePressed(MouseEvent event)
			{
				int row = GroupEditFrame.this.tablePeriods.rowAtPoint(event.getPoint());
				if(row >= 0 && row < GroupEditFrame.this.tablePeriods.getRowCount())
					GroupEditFrame.this.tablePeriods.setRowSelectionInterval(row, row);
				else
					GroupEditFrame.this.tablePeriods.clearSelection();
				int rowindex = GroupEditFrame.this.tablePeriods.getSelectedRow();
				if(event.getClickCount() == 2 && event.getComponent() instanceof JTable)
					editPeriod(group.getPeriodByName(GroupEditFrame.this.tablePeriods.getValueAt(rowindex, 0).toString()), rowindex);
			}

			@Override
			public void mouseReleased(MouseEvent event)
			{
				int row = GroupEditFrame.this.tablePeriods.rowAtPoint(event.getPoint());
				if(row >= 0 && row < GroupEditFrame.this.tablePeriods.getRowCount())
					GroupEditFrame.this.tablePeriods.setRowSelectionInterval(row, row);
				else
					GroupEditFrame.this.tablePeriods.clearSelection();
				int rowindex = GroupEditFrame.this.tablePeriods.getSelectedRow();
				if(event.isPopupTrigger() && event.getComponent() instanceof JTable)
				{
					Period period = group.getPeriodByName(GroupEditFrame.this.tablePeriods.getValueAt(rowindex, 0).toString());
					JPopupMenu popup = new JPopupMenu();
					JMenuItem deletePeriod = new JMenuItem(Utils.resourceBundle.getString("remove_period"));
					deletePeriod.addActionListener(event1 -> {
						try
						{
							removePeriod(period, rowindex);
						}
						catch(Exception exception)
						{
							Utils.logger.log(Level.WARNING, "", exception);
						}
					});
					JMenuItem editPeriod = new JMenuItem(Utils.resourceBundle.getString("edit_period"));
					editPeriod.addActionListener(event1 -> editPeriod(period, rowindex));
					popup.add(editPeriod);
					popup.add(deletePeriod);
					popup.show(event.getComponent(), event.getX(), event.getY());
				}
			}
		});
		this.tablePeriods.setDefaultRenderer(String.class, centerRenderer2);
		this.tablePeriods.getTableHeader().setReorderingAllowed(false);
		this.tablePeriods.getTableHeader().setResizingAllowed(true);
		this.tablePeriods.setRowHeight(20);
		this.tablePeriods.setShowGrid(true);
		this.tablePeriods.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		this.tablePeriods.setGridColor(Color.BLACK);
		JScrollPane scrollPanePeriods = new JScrollPane(this.tablePeriods);
		scrollPanePeriods.setAutoscrolls(false);
		scrollPanePeriods.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JButton addPeriod = new JButton(Utils.resourceBundle.getString("add_period"));
		addPeriod.setBackground(MainFrame.backColor);
		addPeriod.addActionListener(event -> addPeriod(getNewPeriod(null)));
		JButton addStudent = new JButton(Utils.resourceBundle.getString("add_student"));
		addStudent.setBackground(MainFrame.backColor);
		addStudent.addActionListener(event -> addStudent());
		/**************************************************************************/
		int line = 0;
		GridBagConstraints gcb = new GridBagConstraints();
		getContentPane().setLayout(new GridBagLayout());
		gcb.anchor = GridBagConstraints.PAGE_START;
		gcb.fill = GridBagConstraints.BOTH;
		gcb.weighty = 100;
		gcb.weightx = 1;
		gcb.gridheight = 1;
		gcb.gridwidth = 1;
		gcb.gridx = 0;
		gcb.gridy = line++;
		this.getContentPane().add(scrollPaneStudents, gcb);
		gcb.gridx = 1;
		this.getContentPane().add(scrollPanePeriods, gcb);
		gcb.gridx = 0;
		gcb.gridy = line++;
		gcb.weighty = 1;
		this.getContentPane().add(addStudent, gcb);
		gcb.gridx = 1;
		this.getContentPane().add(addPeriod, gcb);
		pack();
		this.setLocationRelativeTo(parent);
		this.setVisible(true);
	}

	/**
	 * Called to edit a period.
	 *
	 * @param period The period to edit.
	 * @param rowindex The row where the period is.
	 */
	private void editPeriod(Period period, int rowindex)
	{
		try
		{
			Period p = getNewPeriod(period);
			if(p == null)
				return;
			removePeriod(period, rowindex);
			if(!addPeriod(p))
				addPeriod(period);
		}
		catch(Exception exception)
		{
			Utils.logger.log(Level.WARNING, "", exception);
		}
	}

	/**
	 * Used to create a new period.
	 *
	 * @param period The period to edit if there is one.
	 * @return The period created.
	 */
	private Period getNewPeriod(Period period)
	{
		PeriodDialogFrame dialog = new PeriodDialogFrame(this, Utils.resourceBundle.getString("schedule_setting"), period);
		return dialog.showDialog();
	}

	/**
	 * Used when we need to add a student.
	 */
	private void addStudent()
	{
		ArrayList<Student> students = group.getAddableStudents();
		if(students.size() < 1)
		{
			JOptionPane.showMessageDialog(this, Utils.resourceBundle.getString("no_student"), Utils.resourceBundle.getString("error").toUpperCase(), JOptionPane.ERROR_MESSAGE);
			return;
		}
		SelectListDialogFrame<Student> dialog = new SelectListDialogFrame(this, Utils.resourceBundle.getString("add_student"), Utils.resourceBundle.getString("select_student") + ":", students, null, true);
		for(Student student : dialog.showDialog())
			if(group.addStudent(student))
				modelStudents.addRow(new Student[]{student});
			else
				JOptionPane.showMessageDialog(this, Utils.resourceBundle.getString("already_in_list"), Utils.resourceBundle.getString("error").toUpperCase(), JOptionPane.ERROR_MESSAGE);
		modelStudents.fireTableDataChanged();
	}

	/**
	 * Used when we need to add a period.
	 *
	 * @param period The period to add.
	 * @return True is the period was added, false if not.
	 */
	private boolean addPeriod(Period period)
	{
		boolean r = false;
		try
		{
			if(period == null)
				return false;
			if(group.addPeriod(period))
			{
				modelPeriods.addRow(new Period[]{period});
				modelPeriods.fireTableDataChanged();
				r = true;
			}
			else
				JOptionPane.showMessageDialog(this, Utils.resourceBundle.getString("period_overlapping"), Utils.resourceBundle.getString("error").toUpperCase(), JOptionPane.ERROR_MESSAGE);
		}
		catch(IllegalArgumentException e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, Utils.resourceBundle.getString("wrong_period"), Utils.resourceBundle.getString("error").toUpperCase(), JOptionPane.ERROR_MESSAGE);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return r;
	}

	/**
	 * Used to remove a period.
	 *
	 * @param period The period to remove.
	 * @param index The index in the table of the period.
	 */
	private void removePeriod(Period period, int index)
	{
		group.remove(period);
		modelPeriods.removeRow(index);
		modelPeriods.fireTableDataChanged();
	}

	/**
	 * Used to remove a student.
	 *
	 * @param student The student to remove.
	 * @param index The index in the table of the student.
	 * @param name The name of teh student.
	 */
	private void removeStudent(Student student, int index, String name)
	{
		if(student != null)
			group.remove(student);
		else
			group.removeStudent(name);
		modelStudents.removeRow(index);
		modelStudents.fireTableDataChanged();
	}

	/**
	 * Used to create the period table.
	 *
	 * @param period The periods in the table.
	 * @return An array representing the list.
	 */
	private Period[][] getPeriodsTableList(ArrayList<Period> period)
	{
		Period[][] periods = new Period[period.size()][1];
		int i = 0;
		for(Period per : period)
			periods[i++][0] = per;
		return periods;
	}

	/**
	 * Used to create the student table.
	 *
	 * @param students The students in the table.
	 * @return An array representing the list.
	 */
	private Student[][] getStudentsTableList(ArrayList<Student> students)
	{
		Collections.sort(students);
		Student[][] student = new Student[students.size()][1];
		int i = 0;
		for(Student stu : students)
			student[i++][0] = stu;
		return student;
	}
}
