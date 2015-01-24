package fr.tours.polytech.DI.RFID.frames;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableCellRenderer;
import fr.tours.polytech.DI.RFID.frames.components.JTableUneditableModel;
import fr.tours.polytech.DI.RFID.frames.components.StudentsRenderer;
import fr.tours.polytech.DI.RFID.interfaces.StaffListener;
import fr.tours.polytech.DI.RFID.interfaces.TerminalListener;
import fr.tours.polytech.DI.RFID.objects.RFIDCard;
import fr.tours.polytech.DI.RFID.objects.Student;
import fr.tours.polytech.DI.RFID.utils.CSV;
import fr.tours.polytech.DI.RFID.utils.Configuration;
import fr.tours.polytech.DI.RFID.utils.Utils;

public class MainFrame extends JFrame implements TerminalListener, Runnable
{
	private static final long serialVersionUID = -4989573496325827301L;
	private ArrayList<StaffListener> staffListeners;
	private Thread thread;
	private File studentsFile, teatchersFile;
	private ArrayList<Student> students;
	private ArrayList<Student> checkedStudents;
	private JPanel infoPanel, cardPanel;
	private JLabel cardTextLabel, infoTextLabel;
	private JScrollPane scrollPaneChecked;
	private JTable tableChecked;
	private JTableUneditableModel modelChecked;
	private Student currentStudent;
	private JMenuBar menuBar;
	private JMenu menuStaff;
	private JMenuItem menuItemStaffAddManually;

	public MainFrame(File data, File data2)
	{
		super("Gestion de présence");
		this.studentsFile = data;
		this.teatchersFile = data2;
		this.staffListeners = new ArrayList<StaffListener>();
		this.students = CSV.getStudents(this.studentsFile, false);
		this.students.addAll(CSV.getStudents(this.teatchersFile, true));
		this.checkedStudents = new ArrayList<Student>();
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
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
		this.menuStaff = new JMenu("Staff");
		this.menuStaff.setEnabled(false);
		this.menuItemStaffAddManually = new JMenuItem("Add manually");
		this.menuItemStaffAddManually.addActionListener(e -> {
			try
			{
				addManually();
			}
			catch(Exception e1)
			{
				e1.printStackTrace();
			}
		});
		this.menuStaff.add(this.menuItemStaffAddManually);
		this.menuBar.add(this.menuStaff);
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
		this.tableChecked.setBackground(Color.GRAY);
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
		gcb.gridx = 0;
		gcb.gridy = line++;
		this.infoPanel = new JPanel();
		this.infoPanel.add(this.infoTextLabel, gcb);
		this.infoPanel.setBackground(Color.GRAY);
		this.cardPanel = new JPanel(new GridBagLayout());
		this.cardPanel.add(this.cardTextLabel, gcb);
		this.scrollPaneChecked = new JScrollPane(this.tableChecked);
		this.scrollPaneChecked.setAutoscrolls(false);
		this.scrollPaneChecked.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.scrollPaneChecked.setBackground(Color.GRAY);
		// ///////////////////////////////////////////////////////////////////////////////////////////
		cardRemoved();
		// ///////////////////////////////////////////////////////////////////////////////////////////
		line = 0;
		gcb = new GridBagConstraints();
		getContentPane().setLayout(new GridBagLayout());
		getContentPane().setBackground(Color.GRAY);
		gcb.anchor = GridBagConstraints.PAGE_START;
		gcb.fill = GridBagConstraints.BOTH;
		gcb.weightx = 1;
		gcb.weighty = 1;
		gcb.gridwidth = 1;
		gcb.gridx = 0;
		gcb.gridy = line++;
		getContentPane().add(this.infoPanel, gcb);
		gcb.weighty = 10;
		gcb.gridy = line++;
		getContentPane().add(this.scrollPaneChecked, gcb);
		gcb.weighty = 1;
		gcb.gridy = line++;
		getContentPane().add(this.cardPanel, gcb);
		pack();
		setVisible(true);
		setLocationRelativeTo(null);
		this.thread = new Thread(this);
		this.thread.setName("RefreshInfoPanel");
		this.thread.start();
	}

	public void addStudent(Student student) throws IOException
	{
		if(getStudentByUID(student.getUid()) != null || getStudentByName(student.getName()) != null)
			return;
		Utils.writeStudent(student, this.studentsFile);
		this.students.add(student);
		updateList();
	}

	@Override
	public void cardAdded(RFIDCard rfidCard)
	{
		Student student = getStudentByUID(rfidCard.getUid());
		this.currentStudent = student;
		for(StaffListener staffListener : this.staffListeners)
			staffListener.cardAdded(rfidCard, student);
		if(student == null)
		{
			this.cardTextLabel.setText("Card detected : " + rfidCard);
			return;
		}
		Utils.logger.log(Level.INFO, "Card infos: " + (student == null ? "" : student) + " " + rfidCard);
		this.cardPanel.setBackground(Color.GREEN);
		this.cardTextLabel.setText("Card detected : " + student.getName() + " " + (student.isTeatcher() ? "(Staff)" : "(Student)"));
		try
		{
			if(!student.isTeatcher())
				if(!isTimeValid())
					this.cardTextLabel.setText("<html><p align=\"center\">" + this.cardTextLabel.getText() + "<br />Not in a period to validate</p></html>");
				else if(!this.checkedStudents.contains(student))
				{
					Utils.writeCheck(student);
					updateList();
					this.cardTextLabel.setText("<html><p align=\"center\">" + this.cardTextLabel.getText() + "<br />Card validated</p></html>");
				}
				else
					this.cardTextLabel.setText("<html><p align=\"center\">" + this.cardTextLabel.getText() + "<br />Card already validated</p></html>");
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		this.checkedStudents.add(student);
		if(student.isTeatcher())
			this.menuStaff.setEnabled(true);
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
		this.currentStudent = null;
		this.menuStaff.setEnabled(false);
		this.cardPanel.setBackground(Color.ORANGE);
		this.cardTextLabel.setText("No card detected");
	}

	public void exit()
	{
		if(this.thread != null)
			this.thread.interrupt();
		dispose();
	}

	public boolean hasChecked(String name)
	{
		return this.checkedStudents.contains(getStudentByName(name));
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
			{
				e.printStackTrace();
			}
			Date date = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			int hours = calendar.get(Calendar.HOUR);
			int i = -1;
			while(i < 24)
				if(hours >= ++i * Utils.config.getConfigValue(Configuration.HOUR_INTERVAL).getInt(2) + Utils.config.getConfigValue(Configuration.START_HOUR).getInt(0) % 2 && hours < (i + 1) * Utils.config.getConfigValue(Configuration.HOUR_INTERVAL).getInt(2) + Utils.config.getConfigValue(Configuration.START_HOUR).getInt(0) % 2)
					break;
			int min = i * Utils.config.getConfigValue(Configuration.HOUR_INTERVAL).getInt(2) + Utils.config.getConfigValue(Configuration.START_HOUR).getInt(0) % 2;
			int max = (i + 1) * Utils.config.getConfigValue(Configuration.HOUR_INTERVAL).getInt(2) + Utils.config.getConfigValue(Configuration.START_HOUR).getInt(0) % 2;
			if(isTimeValid())
				this.infoTextLabel.setText("<html><p align=\"center\">Current time : " + dateFormat.format(date) + "<br />Scan for period : " + min + "H - " + max + "H</p></html>");
			else
				this.infoTextLabel.setText("<html><p align=\"center\">Current time : " + dateFormat.format(date) + "<br />Not in a scan period</p></html>");
			boolean test = (hours + Utils.config.getConfigValue(Configuration.START_HOUR).getInt(0) % 2) % Utils.config.getConfigValue(Configuration.HOUR_INTERVAL).getInt(2) == 0 && isTimeValid();
			if(test && !hasBeenReset)
			{
				this.checkedStudents.clear();
				updateList();
				hasBeenReset = true;
				Utils.logger.log(Level.INFO, "List cleared, check again!");
			}
			else if(hasBeenReset && !test)
				hasBeenReset = false;
		}
	}

	public synchronized void updateList()
	{
		this.modelChecked.setRowCount(0);
		for(Object[] data : getTableList(this.students))
			this.modelChecked.addRow(data);
		this.modelChecked.fireTableDataChanged();
	}

	private void addManually() throws IOException
	{
		String uid = JOptionPane.showInputDialog(this, "UID", "Enter student card UID");
		String name = JOptionPane.showInputDialog(this, "Name", "Enter student name");
		if(uid == null || name == null || uid.equals("") || uid.equals("Enter student card UID") || name.equals("") || name.equals("Enter student name"))
			return;
		addStudent(new Student(uid, name, false));
	}

	private ArrayList<Student> getOnlyStudents()
	{
		ArrayList<Student> stu = new ArrayList<Student>();
		for(Student student : this.students)
			if(!student.isTeatcher())
				stu.add(student);
		return stu;
	}

	private Student getStudentByName(String name)
	{
		for(Student student : this.students)
			if(student.getName().equals(name))
				return student;
		return null;
	}

	private Student getStudentByUID(String uid)
	{
		for(Student student : this.students)
			if(student.getUid().equals(uid.replaceAll("-", "")))
				return student;
		return null;
	}

	private Student[][] getTableList(ArrayList<Student> students)
	{
		Student[][] student = new Student[getOnlyStudents().size()][1];
		int i = 0;
		for(Student stu : getOnlyStudents())
			student[i++][0] = stu;
		return student;
	}

	private boolean isTimeValid()
	{
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int hours = calendar.get(Calendar.HOUR);
		return hours >= Utils.config.getConfigValue(Configuration.START_HOUR).getInt(0) && hours < Utils.config.getConfigValue(Configuration.END_HOUR).getInt(24);
	}
}
