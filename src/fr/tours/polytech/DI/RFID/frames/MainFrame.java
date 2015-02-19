/*******************************************************************************
 * Copyright (c) 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
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
import java.util.Date;
import java.util.logging.Level;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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
import fr.tours.polytech.DI.RFID.enums.Sounds;
import fr.tours.polytech.DI.RFID.frames.components.JTableUneditableModel;
import fr.tours.polytech.DI.RFID.frames.components.StudentsRenderer;
import fr.tours.polytech.DI.RFID.interfaces.TerminalListener;
import fr.tours.polytech.DI.RFID.objects.Period;
import fr.tours.polytech.DI.RFID.objects.RFIDCard;
import fr.tours.polytech.DI.RFID.objects.Student;
import fr.tours.polytech.DI.RFID.utils.CSV;
import fr.tours.polytech.DI.RFID.utils.Configuration;
import fr.tours.polytech.DI.RFID.utils.Utils;

/**
 * Class of the main frame.
 *
 * @author COLEAU Victor, COUCHOUD Thomas
 */
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

	/**
	 * Constructor.
	 *
	 * @param data The File that represents the Students.csv file.
	 */
	public MainFrame(File data)
	{
		super("Student presence management");
		this.studentsFile = data;
		this.students = CSV.getStudents(this.studentsFile);
		this.checkedStudents = new ArrayList<Student>();
		this.periods = Period.loadPeriods();
		this.backColor = new Color(224, 242, 255);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setPreferredSize(new Dimension(800, 600));
		addWindowListener(new WindowListener()
		{
			@Override
			public void windowActivated(WindowEvent event)
			{}

			@Override
			public void windowClosed(WindowEvent event)
			{}

			@Override
			public void windowClosing(WindowEvent event)
			{
				if(MainFrame.this.currentStudent != null && MainFrame.this.currentStudent.isStaff())
					Utils.exit(0);
				else
					JOptionPane.showMessageDialog(MainFrame.this, "A card of a staff member need to be in the reader to exit the app", "NOT AUTHORIZED", JOptionPane.ERROR_MESSAGE);
			}

			@Override
			public void windowDeactivated(WindowEvent event)
			{}

			@Override
			public void windowDeiconified(WindowEvent event)
			{}

			@Override
			public void windowIconified(WindowEvent event)
			{}

			@Override
			public void windowOpened(WindowEvent event)
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
		this.menuItemReloadStudents.addActionListener(event -> {
			this.students = CSV.getStudents(this.studentsFile);
			updateList();
		});
		this.menuItemExit.addActionListener(event -> {
			Utils.exit(0);
		});
		this.menuItemHelp.addActionListener(event -> {
			try
			{
				Desktop.getDesktop().browse(new URL("https://github.com/MrCraftCod/RFID/wiki").toURI());
			}
			catch(Exception exception)
			{
				Utils.logger.log(Level.WARNING, "Error when opening wiki page", exception);
			}
		});
		this.menuItemAbout.addActionListener(event -> {
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
			public void mouseClicked(MouseEvent event)
			{}

			@Override
			public void mouseEntered(MouseEvent event)
			{}

			@Override
			public void mouseExited(MouseEvent event)
			{}

			@Override
			public void mousePressed(MouseEvent event)
			{}

			@Override
			public void mouseReleased(MouseEvent event)
			{
				if(MainFrame.this.currentStudent == null || !MainFrame.this.currentStudent.isStaff())
					return;
				int row = MainFrame.this.tableChecked.rowAtPoint(event.getPoint());
				if(row >= 0 && row < MainFrame.this.tableChecked.getRowCount())
					MainFrame.this.tableChecked.setRowSelectionInterval(row, row);
				else
					MainFrame.this.tableChecked.clearSelection();
				int rowindex = MainFrame.this.tableChecked.getSelectedRow();
				if(event.isPopupTrigger() && event.getComponent() instanceof JTable)
				{
					Student student = getStudentByName(MainFrame.this.tableChecked.getValueAt(rowindex, 0).toString(), false);
					JPopupMenu popup = new JPopupMenu();
					JMenuItem deleteStudent = new JMenuItem("Delete student");
					deleteStudent.addActionListener(event1 -> {
						try
						{
							removeStudent(student);
						}
						catch(Exception exception)
						{
							Utils.logger.log(Level.WARNING, "", exception);
						}
					});
					JMenuItem checkStudent = new JMenuItem("Check student");
					checkStudent.addActionListener(event1 -> {
						try
						{
							checkStudent(student, true);
						}
						catch(Exception exception)
						{
							Utils.logger.log(Level.WARNING, "", exception);
						}
					});
					JMenuItem uncheckStudent = new JMenuItem("Uncheck student");
					uncheckStudent.addActionListener(event1 -> {
						try
						{
							uncheckStudent(student);
						}
						catch(Exception exception)
						{
							Utils.logger.log(Level.WARNING, "", exception);
						}
					});
					if(!hasChecked(student))
						popup.add(checkStudent);
					else
						popup.add(uncheckStudent);
					popup.add(deleteStudent);
					popup.show(event.getComponent(), event.getX(), event.getY());
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
		JPanel panelSettings = new JPanel(new BorderLayout());
		panelAddManually.setBackground(this.backColor);
		panelCheckManually.setBackground(this.backColor);
		panelAddPeriod.setBackground(this.backColor);
		panelRemovePeriod.setBackground(this.backColor);
		panelSettings.setBackground(this.backColor);
		JTextArea studentManuallyAddArea = new JTextArea();
		studentManuallyAddArea.setLineWrap(true);
		studentManuallyAddArea.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(2, 2, 2, 2)));
		studentManuallyAddArea.setPreferredSize(new Dimension(100, 25));
		JButton addManuallyButton = new JButton("Add manually");
		addManuallyButton.addActionListener(event -> {
			try
			{
				MainFrame.this.addStudentManually(studentManuallyAddArea.getText());
				studentManuallyAddArea.setText("");
			}
			catch(Exception exception)
			{
				Utils.logger.log(Level.WARNING, "", exception);
			}
		});
		JTextArea studentManuallyCheckArea = new JTextArea();
		studentManuallyCheckArea.setLineWrap(true);
		studentManuallyCheckArea.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(2, 2, 2, 2)));
		studentManuallyCheckArea.setPreferredSize(new Dimension(100, 25));
		JButton checkManuallyButton = new JButton("Check manually");
		checkManuallyButton.addActionListener(event -> {
			try
			{
				MainFrame.this.checkStudentManually(studentManuallyCheckArea.getText());
				studentManuallyCheckArea.setText("");
			}
			catch(Exception exception)
			{
				Utils.logger.log(Level.WARNING, "", exception);
			}
		});
		JTextArea addPeriodArea = new JTextArea();
		addPeriodArea.setLineWrap(true);
		addPeriodArea.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(2, 2, 2, 2)));
		addPeriodArea.setPreferredSize(new Dimension(100, 25));
		JButton addPeriodButton = new JButton("Add period");
		addPeriodButton.addActionListener(event -> {
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
					else
						JOptionPane.showMessageDialog(MainFrame.this, "This period is overlapping an other one!", "Couldn't add this period", JOptionPane.WARNING_MESSAGE);
				}
				catch(Exception exception1)
				{
					JOptionPane.showMessageDialog(MainFrame.this, "This period isn't valid: xxHxx-yyHyy", "Couldn't add this period", JOptionPane.WARNING_MESSAGE);
					Utils.logger.log(Level.WARNING, "Can't parse perriod", exception1);
				}
				addPeriodArea.setText("");
			}
			catch(Exception exception)
			{
				Utils.logger.log(Level.WARNING, "", exception);
			}
		});
		this.removePeriodArea = new JComboBox<Period>();
		for(Period period : this.periods)
			this.removePeriodArea.addItem(period);
		this.removePeriodArea.setPreferredSize(new Dimension(100, 25));
		JButton removePeriodButton = new JButton("Remove period");
		removePeriodButton.addActionListener(event -> {
			try
			{
				Period period = (Period) this.removePeriodArea.getSelectedItem();
				this.periods.remove(period);
				Utils.config.getConfigValue(Configuration.PERIODS).removeValue(period);
				this.removePeriodArea.removeItem(period);
			}
			catch(Exception exception)
			{
				Utils.logger.log(Level.WARNING, "", exception);
			}
		});
		JCheckBox addNewCardCheck = new JCheckBox("Add new cards to database");
		addNewCardCheck.setBackground(this.backColor);
		addNewCardCheck.setSelected(Utils.addNewCards);
		addNewCardCheck.addActionListener(event -> {
			Utils.addNewCards = ((JCheckBox) event.getSource()).isSelected();
		});
		JCheckBox logAllCheck = new JCheckBox("Log all checks");
		logAllCheck.setBackground(this.backColor);
		logAllCheck.setSelected(Utils.addNewCards);
		logAllCheck.addActionListener(event -> {
			Utils.logAll = ((JCheckBox) event.getSource()).isSelected();
		});
		panelAddManually.add(studentManuallyAddArea, BorderLayout.NORTH);
		panelAddManually.add(addManuallyButton, BorderLayout.SOUTH);
		panelCheckManually.add(studentManuallyCheckArea, BorderLayout.NORTH);
		panelCheckManually.add(checkManuallyButton, BorderLayout.SOUTH);
		panelAddPeriod.add(addPeriodArea, BorderLayout.NORTH);
		panelAddPeriod.add(addPeriodButton, BorderLayout.SOUTH);
		panelRemovePeriod.add(this.removePeriodArea, BorderLayout.NORTH);
		panelRemovePeriod.add(removePeriodButton, BorderLayout.SOUTH);
		panelSettings.add(addNewCardCheck, BorderLayout.NORTH);
		panelSettings.add(logAllCheck, BorderLayout.SOUTH);
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
		gcb.gridy = line++;
		this.staffPanel.add(panelSettings, gcb);
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
		cardAdded(new RFIDCard("a", "b"));
	}

	/**
	 * used to add a student to the list.
	 *
	 * @param student The student to add.
	 *
	 * @throws IOException If the Student.CSV file couldn't be modified.
	 */
	public void addStudent(Student student) throws IOException
	{
		if(student == null || getStudentByUID(student.getUid(), false) != null || getStudentByName(student.getName(), false) != null)
			return;
		this.students.add(student);
		Utils.writeStudentsToFile(this.students, this.studentsFile);
		updateList();
	}

	/**
	 * Used to add a student by his name.
	 *
	 * @param name The name of the student.
	 *
	 * @throws IOException If the Student.CSV file couldn't be modified.
	 *
	 * @see #addStudent(Student)
	 */
	public void addStudentManually(String name) throws IOException
	{
		addStudent(getStudentByName(name, true));
	}

	/**
	 * Called by the {@link TerminalListener} interface when a card id added.
	 *
	 * Check the student if needed and open the staff panel if it should be
	 * opened.
	 */
	@Override
	public void cardAdded(RFIDCard rfidCard)
	{
		Student student = getStudentByUID(rfidCard.getUid(), true);
		this.currentStudent = student;
		if(student == null)
		{
			this.cardTextLabel.setText("Card detected : " + rfidCard);
			if(Utils.addNewCards)
			{
				student = new Student(rfidCard.getUid(), JOptionPane.showInputDialog(this, "Entrez le nom de l'étudiant:", ""), false);
				if(student.hasValidName())
					Utils.sql.addStudentToDatabase(student);
			}
			return;
		}
		Utils.logger.log(Level.INFO, "Card infos: " + (student == null ? "" : student) + " " + rfidCard);
		this.cardPanel.setBackground(Color.GREEN);
		this.cardTextLabel.setText("Card detected : " + student.getName() + " " + (student.isStaff() ? "(Staff)" : "(Student)"));
		if(checkStudent(student, false))
			Sounds.CARD_CHECKED.playSound();
		setStaffInfos(student.isStaff());
	}

	/**
	 * Called by the {@link TerminalListener} interface when a reader is added.
	 *
	 * Set the panel text.
	 */
	@Override
	public void cardReaderAdded()
	{
		cardRemoved();
	}

	/**
	 * Called by the {@link TerminalListener} interface when a reader is removed.
	 *
	 * Set the panel text.
	 */
	@Override
	public void cardReaderRemoved()
	{
		this.cardPanel.setBackground(Color.RED);
		this.cardTextLabel.setText("NO DEVICE CONNECTED!");
	}

	/**
	 * Called by the {@link TerminalListener} interface when a card id removed.
	 *
	 * Set the panel text and eventually close the staff panel.
	 */
	@Override
	public void cardRemoved()
	{
		setStaffInfos(false);
		this.currentStudent = null;
		this.cardPanel.setBackground(Color.ORANGE);
		this.cardTextLabel.setText("No card detected");
	}

	/**
	 * Used to check a student manually.
	 *
	 * @param name The student name.
	 *
	 * @see #checkStudent(Student, boolean)
	 */
	public void checkStudentManually(String name)
	{
		checkStudent(getStudentByName(name, true), true);
	}

	/**
	 * Used to exit the frame and stop the thread.
	 */
	public void exit()
	{
		if(this.thread != null)
			this.thread.interrupt();
		dispose();
	}

	/**
	 * Used to know if a student have checked.
	 *
	 * @param name the name of the student.
	 * @return true if he has checked, false if not.
	 *
	 * @see #hasChecked(Student)
	 */
	public boolean hasChecked(String name)
	{
		return hasChecked(getStudentByName(name, true));
	}

	/**
	 * Used to know if a student have checked.
	 *
	 * @param student The student.
	 * @return true if he has checked, false if not.
	 */
	public boolean hasChecked(Student student)
	{
		return this.checkedStudents.contains(student);
	}

	/**
	 * Used to remove a student.
	 *
	 * @param student The student to remove.
	 * @throws IOException If the Student.CSV file couldn't be modified.
	 */
	public void removeStudent(Student student) throws IOException
	{
		this.students.remove(student);
		Utils.writeStudentsToFile(this.students, this.studentsFile);
		updateList();
	}

	/**
	 * Used to remove a student by his name.
	 *
	 * @param student The student name.
	 * @throws IOException If the Student.CSV file couldn't be modified.
	 *
	 * @see #removeStudent(Student)
	 */
	public void removeStudentManually(String name) throws IOException
	{
		removeStudent(getStudentByName(name, true));
	}

	/**
	 * Thread.
	 *
	 * Will update clock, and activate/deactivate periods.
	 */
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
			catch(InterruptedException exception)
			{}
			Date date = new Date();
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

	/**
	 * Used to uncheck a student.
	 *
	 * @param name The name of the student.
	 *
	 * @see #checkStudent(Student, boolean)
	 */
	public void uncheckStudent(String name)
	{
		uncheckStudent(getStudentByName(name, true));
	}

	/**
	 * Used to uncheck a student.
	 *
	 * @param name The student.
	 */
	public void uncheckStudent(Student student)
	{
		if(student == null)
			return;
		this.checkedStudents.remove(student);
		updateList();
	}

	/**
	 * Used to update the table.
	 */
	public synchronized void updateList()
	{
		this.modelChecked.setRowCount(0);
		for(Object[] data : getTableList(this.students))
			this.modelChecked.addRow(data);
		this.modelChecked.fireTableDataChanged();
	}

	/**
	 * Used to check a student manually.
	 *
	 * @param student The student to check.
	 * @param printMessages Should print messages in the bottom?
	 * @return True if the user is now checked, false if not.
	 */
	private boolean checkStudent(Student student, boolean printMessages)
	{
		boolean checked = false;
		if(student == null)
			return false;
		try
		{
			if(!isTimeValid())
			{
				if(!printMessages)
					this.cardTextLabel.setText("<html><p align=\"center\">" + this.cardTextLabel.getText() + "<br />Not in a period to validate</p></html>");
			}
			else if(!this.checkedStudents.contains(student))
			{
				if(hasToValidate(student))
				{
					Utils.logCheck(student);
					if(!printMessages)
						this.cardTextLabel.setText("<html><p align=\"center\">" + this.cardTextLabel.getText() + "<br />Card validated</p></html>");
					this.checkedStudents.add(student);
					updateList();
					checked = true;
				}
				else
					this.cardTextLabel.setText("<html><p align=\"center\">" + this.cardTextLabel.getText() + "<br />You do not belong to this list</p></html>");
			}
			else if(!printMessages)
				this.cardTextLabel.setText("<html><p align=\"center\">" + this.cardTextLabel.getText() + "<br />Card already validated</p></html>");
		}
		catch(IOException exception)
		{
			Utils.logger.log(Level.SEVERE, "Error writing student check!", exception);
		}
		return checked;
	}

	/**
	 * Used to get the current period.
	 *
	 * @return The current period, null if no period.
	 */
	private Period getCurrentPeriod()
	{
		Date date = new Date();
		for(Period period : this.periods)
			if(period.isInPeriod(date))
				return period;
		return null;
	}

	/**
	 * Used to get a student by his name.
	 *
	 * @param name The name of the student.
	 * @param checkDB Should check him in the database if we don't know him?
	 * @return The student or null if unknown.
	 */
	private Student getStudentByName(String name, boolean checkDB)
	{
		for(Student student : this.students)
			if(student != null && student.toString().equalsIgnoreCase(name))
				return student;
		return checkDB ? Utils.sql.getStudentByName(name) : null;
	}

	/**
	 * Used to get a student by his UID.
	 *
	 * @param uid The student's card UID.
	 * @param checkDB Should check him in the database if we don't know him?
	 * @return The student or null if unknown.
	 */
	private Student getStudentByUID(String uid, boolean checkDB)
	{
		for(Student student : this.students)
			if(student != null && student.getUid().equals(uid.replaceAll("-", "")))
				return student;
		return checkDB ? Utils.sql.getStudentByUID(uid.replaceAll("-", "")) : null;
	}

	/**
	 * Used to parse the students to an Object[][] for the table.
	 *
	 * @param students the students to parse.
	 * @return The parsed students.
	 */
	private Student[][] getTableList(ArrayList<Student> students)
	{
		Student[][] student = new Student[this.students.size()][1];
		int i = 0;
		for(Student stu : this.students)
			student[i++][0] = stu;
		return student;
	}

	/**
	 * Used to know if a student needs to check during this period.
	 *
	 * @param student The student.
	 * @return True if he needs to, false if not.
	 */
	private boolean hasToValidate(Student student)
	{
		for(int i = 0; i < this.modelChecked.getRowCount(); i++)
			if(student.toString().equalsIgnoreCase(this.tableChecked.getValueAt(i, 0).toString()))
				return true;
		return false;
	}

	/**
	 * Used to know if we have changed period since the last check.
	 *
	 * @return true if it's a new period, false if not.
	 */
	private boolean isNewPeriod()
	{
		boolean result = getCurrentPeriod() == this.lastPeriod;
		if(result)
			this.lastPeriod = getCurrentPeriod();
		return result;
	}

	/**
	 * Used to know if we are in a valid period.
	 *
	 * @return true id a period is currently running, false if not.
	 */
	private boolean isTimeValid()
	{
		return getCurrentPeriod() != null;
	}

	/**
	 * Used to know if a period overlap any others.
	 *
	 * @param period The period to verify.
	 * @return true if overlapping, false if not.
	 */
	private boolean periodOverlap(Period period)
	{
		for(Period per : this.periods)
			if(per.isOverlapped(period))
				return true;
		return false;
	}

	/**
	 * Used to set the staff panel.
	 *
	 * @param staffMember Is it a staff member?
	 */
	private void setStaffInfos(boolean staffMember)
	{
		this.staffPanel.setVisible(staffMember);
		this.staffPanel.setEnabled(staffMember);
		this.menuItemReloadStudents.setEnabled(staffMember);
		this.menuItemExit.setEnabled(staffMember);
	}
}
