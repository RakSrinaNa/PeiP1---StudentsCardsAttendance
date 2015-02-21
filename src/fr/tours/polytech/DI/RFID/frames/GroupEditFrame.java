package fr.tours.polytech.DI.RFID.frames;

import fr.tours.polytech.DI.RFID.frames.components.JTableUneditableModel;
import fr.tours.polytech.DI.RFID.objects.Group;
import fr.tours.polytech.DI.RFID.objects.Period;
import fr.tours.polytech.DI.RFID.objects.Student;
import fr.tours.polytech.DI.RFID.utils.Utils;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.logging.Level;

public class GroupEditFrame extends JDialog
{
	private final JTable tableStudents;
	private final JTable tablePeriods;
	private final JTableUneditableModel modelPeriods;
	private final JTableUneditableModel modelStudents;
	private final Group group;

	public GroupEditFrame(GroupSettingsFrame parent, Group group)
	{
		super(parent);
		this.group = group;
		this.setTitle("Edition du groupe : " + group.getName());
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.getContentPane().setBackground(MainFrame.backColor);
		this.getContentPane().setLayout(new GridBagLayout());
		this.addWindowListener(new WindowListener()
		{
			@Override
			public void windowOpened(WindowEvent e)
			{

			}

			@Override
			public void windowClosing(WindowEvent e)
			{
			}

			@Override
			public void windowClosed(WindowEvent e)
			{

			}

			@Override
			public void windowIconified(WindowEvent e)
			{

			}

			@Override
			public void windowDeiconified(WindowEvent e)
			{

			}

			@Override
			public void windowActivated(WindowEvent e)
			{

			}

			@Override
			public void windowDeactivated(WindowEvent e)
			{

			}
		});
		/**************************************************************************/
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		this.modelStudents = new JTableUneditableModel(getStudentsTableList(group.getStudents()), new String[]{"Etudiants"});
		this.tableStudents = new JTable(this.modelStudents)
		{
			private static final long serialVersionUID = 4244155500155330717L;

			@Override
			public Class<?> getColumnClass(int column)
			{
				return String.class;
			}
		};
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
					Student student = Utils.getStudentByName(GroupEditFrame.this.tableStudents.getValueAt(rowindex, 0).toString().replace("(Staff)", "").trim(), true);
					JPopupMenu popup = new JPopupMenu();
					JMenuItem deleteGroup = new JMenuItem("Retirer l'\351tudiant");
					deleteGroup.addActionListener(event1 -> {
						try
						{
							removeStudent(student, rowindex);
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
		this.tableStudents.setDefaultRenderer(String.class, centerRenderer);
		this.tableStudents.getTableHeader().setReorderingAllowed(false);
		this.tableStudents.getTableHeader().setResizingAllowed(true);
		this.tableStudents.setRowHeight(20);
		this.tableStudents.setShowGrid(true);
		this.tableStudents.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		this.tableStudents.setGridColor(Color.BLACK);
		JScrollPane scrollPaneStudents = new JScrollPane(this.tableStudents);
		scrollPaneStudents.setAutoscrolls(false);
		scrollPaneStudents.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		/**************************************************************************/
		this.modelPeriods = new JTableUneditableModel(getPeriodsTableList(group.getPeriods()), new String[]{"P\351riodes"});
		this.tablePeriods = new JTable(this.modelPeriods)
		{
			private static final long serialVersionUID = 4244155500155330717L;

			@Override
			public Class<?> getColumnClass(int column)
			{
				return String.class;
			}
		};
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
					Period period = getPeriodByName(GroupEditFrame.this.tablePeriods.getValueAt(rowindex, 0).toString());
					JPopupMenu popup = new JPopupMenu();
					JMenuItem deletePeriod = new JMenuItem("Supprimer la p\351riode");
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
					popup.add(deletePeriod);
					popup.show(event.getComponent(), event.getX(), event.getY());
				}
			}
		});
		this.tablePeriods.setDefaultRenderer(String.class, centerRenderer);
		this.tablePeriods.getTableHeader().setReorderingAllowed(false);
		this.tablePeriods.getTableHeader().setResizingAllowed(true);
		this.tablePeriods.setRowHeight(20);
		this.tablePeriods.setShowGrid(true);
		this.tablePeriods.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		this.tablePeriods.setGridColor(Color.BLACK);
		JScrollPane scrollPanePeriods = new JScrollPane(this.tablePeriods);
		scrollPanePeriods.setAutoscrolls(false);
		scrollPanePeriods.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JButton addPeriod = new JButton("Ajouter une p\351riode");
		addPeriod.setBackground(MainFrame.backColor);
		addPeriod.addActionListener(event -> addPeriod());
		JButton addStudent = new JButton("Ajouter un \351tudiant");
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
		this.setLocationRelativeTo(parent);
		pack();
		this.setVisible(true);
	}

	private void addStudent()
	{
		ArrayList<Student> students = group.getAddableStudents();
		if(students.size() < 1)
		{
			JOptionPane.showMessageDialog(this, "Aucun \351tudiant n'est disponible pour l'ajout!", "ERREUR", JOptionPane.ERROR_MESSAGE);
			return;
		}
		Student student = (Student) JOptionPane.showInputDialog(this, "S\351lectionnez l'\351tudiant \340 ajouter:", "Ajouter un \351tudiant", JOptionPane.QUESTION_MESSAGE, null, students.toArray(new Student[students.size()]), students.get(0));
		if(!group.addStudent(student))
		{
			JOptionPane.showMessageDialog(this, "L'\351tudiant est inconnu dans la base de donn\351es ou est d\351j\340 dans la liste!", "ERREUR", JOptionPane.ERROR_MESSAGE);
			return;
		}
		modelStudents.addRow(new Student[]{student});
		modelStudents.fireTableDataChanged();
	}

	private void addPeriod()
	{
		try
		{
			Period period = new Period(JOptionPane.showInputDialog(this, "Entrez la periode (xxHxx-yyHyy):", ""));
			if(group.addPeriod(period))
			{
				modelPeriods.addRow(new Period[]{period});
				modelPeriods.fireTableDataChanged();
			}
			else
				JOptionPane.showMessageDialog(this, "Cette p\351riode en chevauche une autre!", "ERREUR", JOptionPane.ERROR_MESSAGE);
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(this, "La p\351riode n'est pas \351crite de mani\350re correcte!", "ERREUR", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void removePeriod(Period period, int index)
	{
		group.remove(period);
		modelPeriods.removeRow(index);
		modelPeriods.fireTableDataChanged();
	}

	private void removeStudent(Student student, int index)
	{
		group.remove(student);
		modelStudents.removeRow(index);
		modelStudents.fireTableDataChanged();
	}

	private Period getPeriodByName(String name)
	{
		return group.getPeriodByName(name);
	}

	private Period[][] getPeriodsTableList(ArrayList<Period> period)
	{
		Period[][] periods = new Period[period.size()][1];
		int i = 0;
		for(Period per : period)
			periods[i++][0] = per;
		return periods;
	}

	private Student[][] getStudentsTableList(ArrayList<Student> students)
	{
		Student[][] student = new Student[students.size()][1];
		int i = 0;
		for(Student stu : students)
			student[i++][0] = stu;
		return student;
	}
}
