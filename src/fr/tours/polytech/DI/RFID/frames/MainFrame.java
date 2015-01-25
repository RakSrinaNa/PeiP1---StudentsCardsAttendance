package fr.tours.polytech.DI.RFID.frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableCellRenderer;
import fr.tours.polytech.DI.RFID.frames.components.JTableUneditableModel;
import fr.tours.polytech.DI.RFID.frames.components.StudentsRenderer;
import fr.tours.polytech.DI.RFID.interfaces.TerminalListener;
import fr.tours.polytech.DI.RFID.objects.Period;
import fr.tours.polytech.DI.RFID.objects.RFIDCard;
import fr.tours.polytech.DI.RFID.objects.Student;
import fr.tours.polytech.DI.RFID.utils.CSV;
import fr.tours.polytech.DI.RFID.utils.Configuration;
import fr.tours.polytech.DI.RFID.utils.Utils;

public class MainFrame extends JFrame implements TerminalListener, Runnable
{
	private static final long serialVersionUID = -4989573496325827301L;
	public static final String VERSION = "1.0";
	private Thread thread;
	private File studentsFile;
	private ArrayList<Student> students;
	private ArrayList<Student> checkedStudents;
	private ArrayList<Period> periods;
	private JPanel infoPanel, cardPanel, staffPanel;
	private JLabel cardTextLabel, infoTextLabel;
	private JScrollPane scrollPaneChecked;
	private JTable tableChecked;
	private JTableUneditableModel modelChecked;
	private Student currentStudent;
	private JMenuBar menuBar;
	private JMenu menuFile, menuHelp;
	private JMenuItem menuItemReloadStudents, menuItemExit, menuItemHelp, menuItemAbout;
	private Color backColor;
	private Period lastPeriod;
	private JComboBox<Period> removePeriodArea;

	public MainFrame(File data)
	{
		super("Student presence management");
		this.studentsFile = data;
		this.students = CSV.getStudents(this.studentsFile, false);
		this.checkedStudents = new ArrayList<Student>();
		this.periods = Period.loadPeriods();
		this.backColor = new Color(224, 242, 255);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setPreferredSize(new Dimension(800, 600));
		addWindowListener(new WindowListener()
		{
			@Override
			public void windowActivated(WindowEvent arg0)
			{}

			@Override
			public void windowClosed(WindowEvent arg0)
			{}

			@Override
			public void windowClosing(WindowEvent arg0)
			{
				if(MainFrame.this.currentStudent != null && MainFrame.this.currentStudent.isTeatcher())
					Utils.exit(0);
				else
					JOptionPane.showMessageDialog(MainFrame.this, "A card of a staff member need to be in the reader to exit the app", "NOT AUTHORIZED", JOptionPane.ERROR_MESSAGE);
			}

			@Override
			public void windowDeactivated(WindowEvent arg0)
			{}

			@Override
			public void windowDeiconified(WindowEvent arg0)
			{}

			@Override
			public void windowIconified(WindowEvent arg0)
			{}

			@Override
			public void windowOpened(WindowEvent arg0)
			{}
		});
		// ///////////////////////////////////////////////////////////////////////////////////////////
		this.menuBar = new JMenuBar();
		this.menuFile = new JMenu("File");
		this.menuHelp = new JMenu("About");
		this.menuItemReloadStudents = new JMenuItem("Reload CSV Students file");
		this.menuItemExit = new JMenuItem("Exit");
		this.menuItemHelp = new JMenuItem("Help");
		this.menuItemAbout = new JMenuItem("About");
		this.menuItemReloadStudents.addActionListener(e -> {
			this.students = CSV.getStudents(this.studentsFile, false);
			updateList();
		});
		this.menuItemExit.addActionListener(e -> {
			Utils.exit(0);
		});
		this.menuItemHelp.addActionListener(e -> {
			try
			{
				Desktop.getDesktop().browse(new URL("https://github.com/MrCraftCod/RFID/wiki").toURI());
			}
			catch(Exception e1)
			{
				Utils.logger.log(Level.WARNING, "Error when opening wiki page", e1);
			}
		});
		this.menuItemAbout.addActionListener(e -> {
			new AboutFrame(MainFrame.this);
		});
		this.menuFile.add(this.menuItemReloadStudents);
		this.menuFile.addSeparator();
		this.menuFile.add(this.menuItemExit);
		this.menuHelp.add(this.menuItemHelp);
		this.menuHelp.add(this.menuItemAbout);
		this.menuBar.add(this.menuFile);
		this.menuBar.add(this.menuHelp);
		setJMenuBar(this.menuBar);
		// ///////////////////////////////////////////////////////////////////////////////////////////
		this.cardTextLabel = new JLabel();
		this.cardTextLabel.setVerticalAlignment(JLabel.CENTER);
		this.cardTextLabel.setHorizontalAlignment(JLabel.CENTER);
		this.infoTextLabel = new JLabel();
		this.infoTextLabel.setVerticalAlignment(JLabel.CENTER);
		this.infoTextLabel.setHorizontalAlignment(JLabel.CENTER);
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		this.modelChecked = new JTableUneditableModel(getTableList(this.students), new String[]
		{"Name"});
		this.tableChecked = new JTable(this.modelChecked)
		{
			private static final long serialVersionUID = 4244155500155330717L;

			@Override
			public Class<?> getColumnClass(int column)
			{
				return String.class;
			}
		};
		this.tableChecked.addMouseListener(new MouseListener()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{}

			@Override
			public void mouseEntered(MouseEvent e)
			{}

			@Override
			public void mouseExited(MouseEvent e)
			{}

			@Override
			public void mousePressed(MouseEvent e)
			{}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				int row = MainFrame.this.tableChecked.rowAtPoint(e.getPoint());
				if(row >= 0 && row < MainFrame.this.tableChecked.getRowCount())
					MainFrame.this.tableChecked.setRowSelectionInterval(row, row);
				else
					MainFrame.this.tableChecked.clearSelection();
				int rowindex = MainFrame.this.tableChecked.getSelectedRow();
				if(e.isPopupTrigger() && e.getComponent() instanceof JTable)
				{
					Student student = getStudentByName(MainFrame.this.tableChecked.getValueAt(rowindex, 0).toString(), false);
					JPopupMenu popup = new JPopupMenu();
					JMenuItem deleteStudent = new JMenuItem("Delete student");
					deleteStudent.addActionListener(event -> {
						try
						{
							removeStudent(student);
						}
						catch(Exception e1)
						{
							Utils.logger.log(Level.WARNING, "", e1);
						}
					});
					JMenuItem checkStudent = new JMenuItem("Check student");
					checkStudent.addActionListener(event -> {
						try
						{
							checkStudent(student, true);
						}
						catch(Exception e1)
						{
							Utils.logger.log(Level.WARNING, "", e1);
						}
					});
					JMenuItem uncheckStudent = new JMenuItem("Uncheck student");
					uncheckStudent.addActionListener(event -> {
						try
						{
							uncheckStudent(student);
						}
						catch(Exception e1)
						{
							Utils.logger.log(Level.WARNING, "", e1);
						}
					});
					if(!hasChecked(student))
						popup.add(checkStudent);
					else
						popup.add(uncheckStudent);
					popup.add(deleteStudent);
					popup.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
		this.tableChecked.setBackground(this.backColor);
		this.tableChecked.setDefaultRenderer(String.class, new StudentsRenderer(centerRenderer, this));
		this.tableChecked.getTableHeader().setReorderingAllowed(false);
		this.tableChecked.getTableHeader().setResizingAllowed(true);
		this.tableChecked.setRowHeight(20);
		this.tableChecked.setShowGrid(true);
		this.tableChecked.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		this.tableChecked.setGridColor(Color.BLACK);
		// ///////////////////////////////////////////////////////////////////////////////////////////
		int line = 0;
		GridBagConstraints gcb = new GridBagConstraints();
		gcb.anchor = GridBagConstraints.PAGE_START;
		gcb.fill = GridBagConstraints.BOTH;
		gcb.weightx = 1;
		gcb.weighty = 1;
		gcb.gridwidth = 1;
		gcb.gridheight = 1;
		gcb.gridx = 0;
		gcb.gridy = line++;
		this.infoPanel = new JPanel();
		this.infoPanel.add(this.infoTextLabel, gcb);
		this.infoPanel.setBackground(this.backColor);
		this.cardPanel = new JPanel(new GridBagLayout());
		this.cardPanel.add(this.cardTextLabel, gcb);
		this.cardPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		this.staffPanel = new JPanel(new GridBagLayout());
		this.staffPanel.setBackground(this.backColor);
		// ///////////////////////////////////////////////////////////////////////////////////////////
		Border border = BorderFactory.createLineBorder(Color.BLACK);
		JPanel panelAddManually = new JPanel(new BorderLayout());
		JPanel panelCheckManually = new JPanel(new BorderLayout());
		JPanel panelAddPeriod = new JPanel(new BorderLayout());
		JPanel panelRemovePeriod = new JPanel(new BorderLayout());
		panelAddManually.setBackground(this.backColor);
		panelCheckManually.setBackground(this.backColor);
		panelAddPeriod.setBackground(this.backColor);
		panelRemovePeriod.setBackground(this.backColor);
		JTextArea studentManuallyAddArea = new JTextArea();
		studentManuallyAddArea.setLineWrap(true);
		studentManuallyAddArea.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(2, 2, 2, 2)));
		studentManuallyAddArea.setPreferredSize(new Dimension(100, 25));
		JButton addManuallyButton = new JButton("Add manually");
		addManuallyButton.addActionListener(e -> {
			try
			{
				MainFrame.this.addStudentManually(studentManuallyAddArea.getText());
				studentManuallyAddArea.setText("");
			}
			catch(Exception e1)
			{
				Utils.logger.log(Level.WARNING, "", e1);
			}
		});
		JTextArea studentManuallyCheckArea = new JTextArea();
		studentManuallyCheckArea.setLineWrap(true);
		studentManuallyCheckArea.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(2, 2, 2, 2)));
		studentManuallyCheckArea.setPreferredSize(new Dimension(100, 25));
		JButton checkManuallyButton = new JButton("Check manually");
		checkManuallyButton.addActionListener(e -> {
			try
			{
				MainFrame.this.checkStudentManually(studentManuallyCheckArea.getText());
				studentManuallyCheckArea.setText("");
			}
			catch(Exception e1)
			{
				Utils.logger.log(Level.WARNING, "", e1);
			}
		});
		JTextArea addPeriodArea = new JTextArea();
		addPeriodArea.setLineWrap(true);
		addPeriodArea.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(2, 2, 2, 2)));
		addPeriodArea.setPreferredSize(new Dimension(100, 25));
		JButton addPeriodButton = new JButton("Add period");
		addPeriodButton.addActionListener(e -> {
			try
			{
				try
				{
					Period period = new Period(addPeriodArea.getText());
					if(!periodOverlap(period))
					{
						this.periods.add(period);
						this.removePeriodArea.addItem(period);
						Utils.config.getConfigValue(Configuration.PERIODS).addValue(period);
					}
				}
				catch(Exception e1)
				{
					Utils.logger.log(Level.WARNING, "Can't parse perriod", e1);
				}
				addPeriodArea.setText("");
			}
			catch(Exception e1)
			{
				Utils.logger.log(Level.WARNING, "", e1);
			}
		});
		this.removePeriodArea = new JComboBox<Period>();
		for(Period period : this.periods)
			this.removePeriodArea.addItem(period);
		this.removePeriodArea.setPreferredSize(new Dimension(100, 25));
		JButton removePeriodButton = new JButton("Remove period");
		removePeriodButton.addActionListener(e -> {
			try
			{
				Period period = (Period) this.removePeriodArea.getSelectedItem();
				this.periods.remove(period);
				Utils.config.getConfigValue(Configuration.PERIODS).removeValue(period);
				this.removePeriodArea.removeItem(period);
			}
			catch(Exception e1)
			{
				Utils.logger.log(Level.WARNING, "", e1);
			}
		});
		panelAddManually.add(studentManuallyAddArea, BorderLayout.NORTH);
		panelAddManually.add(addManuallyButton, BorderLayout.SOUTH);
		panelCheckManually.add(studentManuallyCheckArea, BorderLayout.NORTH);
		panelCheckManually.add(checkManuallyButton, BorderLayout.SOUTH);
		panelAddPeriod.add(addPeriodArea, BorderLayout.NORTH);
		panelAddPeriod.add(addPeriodButton, BorderLayout.SOUTH);
		panelRemovePeriod.add(this.removePeriodArea, BorderLayout.NORTH);
		panelRemovePeriod.add(removePeriodButton, BorderLayout.SOUTH);
		line = 0;
		gcb.anchor = GridBagConstraints.NORTH;
		gcb.fill = GridBagConstraints.HORIZONTAL;
		gcb.insets = new Insets(10, 20, 10, 20);
		gcb.gridy = line++;
		this.staffPanel.add(panelAddManually, gcb);
		gcb.gridy = line++;
		this.staffPanel.add(panelCheckManually, gcb);
		gcb.gridy = line++;
		this.staffPanel.add(panelAddPeriod, gcb);
		gcb.gridy = line++;
		this.staffPanel.add(panelRemovePeriod, gcb);
		// ///////////////////////////////////////////////////////////////////////////////////////////
		this.scrollPaneChecked = new JScrollPane(this.tableChecked);
		this.scrollPaneChecked.setAutoscrolls(false);
		this.scrollPaneChecked.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.scrollPaneChecked.setBackground(this.backColor);
		// ///////////////////////////////////////////////////////////////////////////////////////////
		line = 0;
		gcb = new GridBagConstraints();
		getContentPane().setLayout(new GridBagLayout());
		getContentPane().setBackground(this.backColor);
		gcb.anchor = GridBagConstraints.PAGE_START;
		gcb.fill = GridBagConstraints.BOTH;
		gcb.weightx = 1;
		gcb.insets = new Insets(0, 0, 0, 0);
		gcb.weighty = 1;
		gcb.weightx = 10;
		gcb.gridheight = 1;
		gcb.gridwidth = 2;
		gcb.gridx = 0;
		gcb.gridy = line++;
		getContentPane().add(this.infoPanel, gcb);
		gcb.gridwidth = 1;
		gcb.weighty = 10;
		gcb.weightx = 1;
		gcb.gridy = line++;
		getContentPane().add(this.staffPanel, gcb);
		gcb.gridx = 1;
		gcb.weightx = 10;
		getContentPane().add(this.scrollPaneChecked, gcb);
		gcb.gridwidth = 2;
		gcb.weighty = 1;
		gcb.gridx = 0;
		gcb.gridy = line++;
		getContentPane().add(this.cardPanel, gcb);
		setStaffInfos(false);
		cardRemoved();
		pack();
		setVisible(true);
		setLocationRelativeTo(null);
		this.thread = new Thread(this);
		this.thread.setName("RefreshInfoPanel");
		this.thread.start();
	}

	public void addStudent(Student student) throws IOException
	{
		if(student == null || getStudentByUID(student.getUid(), false) != null || getStudentByName(student.getName(), false) != null)
			return;
		Utils.writeStudent(student, this.studentsFile);
		this.students.add(student);
		updateList();
	}

	public void addStudentManually(String name) throws IOException
	{
		addStudent(getStudentByName(name, true));
	}

	@Override
	public void cardAdded(RFIDCard rfidCard)
	{
		Student student = getStudentByUID(rfidCard.getUid(), true);
		this.currentStudent = student;
		if(student == null)
		{
			this.cardTextLabel.setText("Card detected : " + rfidCard);
			return;
		}
		Utils.logger.log(Level.INFO, "Card infos: " + (student == null ? "" : student) + " " + rfidCard);
		this.cardPanel.setBackground(Color.GREEN);
		this.cardTextLabel.setText("Card detected : " + student.getName() + " " + (student.isTeatcher() ? "(Staff)" : "(Student)"));
		checkStudent(student, false);
		setStaffInfos(student.isTeatcher());
	}

	@Override
	public void cardReader(boolean isPresent)
	{
		if(isPresent)
			cardRemoved();
		else
		{
			this.cardPanel.setBackground(Color.RED);
			this.cardTextLabel.setText("NO DEVICE CONNECTED!");
		}
	}

	@Override
	public void cardRemoved()
	{
		setStaffInfos(false);
		this.currentStudent = null;
		this.cardPanel.setBackground(Color.ORANGE);
		this.cardTextLabel.setText("No card detected");
	}

	public void checkStudentManually(String name)
	{
		checkStudent(getStudentByName(name, true), true);
	}

	public void exit()
	{
		if(this.thread != null)
			this.thread.interrupt();
		dispose();
	}

	public boolean hasChecked(String name)
	{
		return hasChecked(getStudentByName(name, true));
	}

	public boolean hasChecked(Student student)
	{
		return this.checkedStudents.contains(student);
	}

	public void removeStudent(Student student) throws IOException
	{
		this.students.remove(student);
		Utils.writeStudent(this.students, this.studentsFile);
		updateList();
	}

	public void removeStudentManually(String name) throws IOException
	{
		removeStudent(getStudentByName(name, true));
	}

	@Override
	public void run()
	{
		boolean hasBeenReset = false;
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		while(!this.thread.interrupted())
		{
			try
			{
				Thread.sleep(500);
			}
			catch(InterruptedException e)
			{}
			Date date = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			boolean validPeriod = isTimeValid();
			boolean newPeriod = isNewPeriod();
			if(validPeriod)
			{
				Period period = getCurrentPeriod();
				this.infoTextLabel.setText("<html><p align=\"center\">Current time : " + dateFormat.format(date) + "<br />Scan for period : " + period.getTimeInterval() + "</p></html>");
			}
			else
				this.infoTextLabel.setText("<html><p align=\"center\">Current time : " + dateFormat.format(date) + "<br />Not in a scan period</p></html>");
			if(!hasBeenReset && newPeriod)
			{
				this.checkedStudents.clear();
				updateList();
				hasBeenReset = true;
				Utils.logger.log(Level.INFO, "List cleared, check again!");
			}
			else if(hasBeenReset && !newPeriod)
				hasBeenReset = false;
		}
	}

	public void uncheckStudent(String name, boolean b)
	{
		checkStudent(getStudentByName(name, true), true);
	}

	public void uncheckStudent(Student student)
	{
		if(student == null)
			return;
		if(!student.isTeatcher())
			if(this.checkedStudents.contains(student))
			{
				this.checkedStudents.remove(student);
				updateList();
			}
	}

	public synchronized void updateList()
	{
		this.modelChecked.setRowCount(0);
		for(Object[] data : getTableList(this.students))
			this.modelChecked.addRow(data);
		this.modelChecked.fireTableDataChanged();
	}

	private void checkStudent(Student student, boolean manually)
	{
		if(student == null)
			return;
		try
		{
			if(!isTimeValid())
			{
				if(!manually)
					this.cardTextLabel.setText("<html><p align=\"center\">" + this.cardTextLabel.getText() + "<br />Not in a period to validate</p></html>");
			}
			else if(!this.checkedStudents.contains(student))
			{
				Utils.writeCheck(student);
				if(!manually)
					this.cardTextLabel.setText("<html><p align=\"center\">" + this.cardTextLabel.getText() + "<br />Card validated</p></html>");
				this.checkedStudents.add(student);
				updateList();
			}
			else if(!manually)
				this.cardTextLabel.setText("<html><p align=\"center\">" + this.cardTextLabel.getText() + "<br />Card already validated</p></html>");
		}
		catch(IOException e)
		{
			Utils.logger.log(Level.SEVERE, "Error writing student check!", e);
		}
	}

	private Period getCurrentPeriod()
	{
		Date date = new Date();
		for(Period period : this.periods)
			if(period.isInPeriod(date))
				return period;
		return null;
	}

	private Student getStudentByName(String name, boolean checkDB)
	{
		for(Student student : this.students)
			if(student != null && student.toString().equalsIgnoreCase(name))
				return student;
		return checkDB ? Utils.sql.getStudentByName(name) : null;
	}

	private Student getStudentByUID(String uid, boolean checkDB)
	{
		for(Student student : this.students)
			if(student != null && student.getUid().equals(uid.replaceAll("-", "")))
				return student;
		return checkDB ? Utils.sql.getStudentByUID(uid.replaceAll("-", "")) : null;
	}

	private Student[][] getTableList(ArrayList<Student> students)
	{
		Student[][] student = new Student[this.students.size()][1];
		int i = 0;
		for(Student stu : this.students)
			student[i++][0] = stu;
		return student;
	}

	private boolean isNewPeriod()
	{
		boolean result = getCurrentPeriod() == this.lastPeriod;
		if(result)
			this.lastPeriod = getCurrentPeriod();
		return result;
	}

	private boolean isTimeValid()
	{
		return getCurrentPeriod() != null;
	}

	private boolean periodOverlap(Period period)
	{
		for(Period per : this.periods)
			if(per.isOverlapped(period))
				return true;
		return false;
	}

	private void setStaffInfos(boolean b)
	{
		this.staffPanel.setVisible(b);
		this.staffPanel.setEnabled(b);
		this.menuItemReloadStudents.setEnabled(b);
		this.menuItemExit.setEnabled(b);
	}
}
