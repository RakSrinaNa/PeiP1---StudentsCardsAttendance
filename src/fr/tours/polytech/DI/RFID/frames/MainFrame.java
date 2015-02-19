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

import fr.tours.polytech.DI.RFID.enums.Sounds;
import fr.tours.polytech.DI.RFID.frames.components.JTableUneditableModel;
import fr.tours.polytech.DI.RFID.frames.components.StudentsRenderer;
import fr.tours.polytech.DI.RFID.interfaces.TerminalListener;
import fr.tours.polytech.DI.RFID.objects.Group;
import fr.tours.polytech.DI.RFID.objects.RFIDCard;
import fr.tours.polytech.DI.RFID.objects.Student;
import fr.tours.polytech.DI.RFID.utils.Utils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;

/**
 * Class of the main frame.
 *
 * @author COLEAU Victor, COUCHOUD Thomas
 */
public class MainFrame extends JFrame implements TerminalListener, Runnable
{
	private static final long serialVersionUID = -4989573496325827301L;
	public static final String VERSION = "1.0";
	public static Color backColor;
	private Thread thread;
	private JPanel cardPanel;
	private JPanel staffPanel;
	private JLabel cardTextLabel;
	private JLabel infoTextLabel;
	private JTable tableChecked;
	private Student currentStudent;
	private JMenuItem menuItemExit;
	private JTableUneditableModel modelChecked;

	/**
	 * Constructor.
	 */
	public MainFrame()
	{
		super("Student presence management");
		backColor = new Color(224, 242, 255);
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
				if(true || MainFrame.this.currentStudent != null && MainFrame.this.currentStudent.isStaff())
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
		JMenuBar menuBar = new JMenuBar();
		JMenu menuFile = new JMenu("File");
		JMenu menuHelp = new JMenu("About");
		this.menuItemExit = new JMenuItem("Exit");
		JMenuItem menuItemHelp = new JMenuItem("Help");
		JMenuItem menuItemAbout = new JMenuItem("About");
		this.menuItemExit.addActionListener(event -> Utils.exit(0));
		menuItemHelp.addActionListener(event -> {
			try
			{
				Desktop.getDesktop().browse(new URL("https://github.com/MrCraftCod/RFID/wiki").toURI());
			}
			catch (Exception exception)
			{
				Utils.logger.log(Level.WARNING, "Error when opening wiki page", exception);
			}
		});
		menuItemAbout.addActionListener(event -> new AboutFrame(MainFrame.this));
		menuFile.add(this.menuItemExit);
		menuHelp.add(menuItemHelp);
		menuHelp.add(menuItemAbout);
		menuBar.add(menuFile);
		menuBar.add(menuHelp);
		setJMenuBar(menuBar);
		// ///////////////////////////////////////////////////////////////////////////////////////////
		this.cardTextLabel = new JLabel();
		this.cardTextLabel.setVerticalAlignment(JLabel.CENTER);
		this.cardTextLabel.setHorizontalAlignment(JLabel.CENTER);
		infoTextLabel = new JLabel();
		infoTextLabel.setVerticalAlignment(JLabel.CENTER);
		infoTextLabel.setHorizontalAlignment(JLabel.CENTER);
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		modelChecked = new JTableUneditableModel(getTableList(Utils.students), new String[]{"Name"});
		this.tableChecked = new JTable(modelChecked)
		{
			private static final long serialVersionUID = 4244155500155330717L;

			@Override
			public Class<?> getColumnClass(int column)
			{
				return Student.class;
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
					Student student = Utils.getStudentByName(MainFrame.this.tableChecked.getValueAt(rowindex, 0).toString(), false);
					JPopupMenu popup = new JPopupMenu();
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
					uncheckStudent.addActionListener(event1 ->
					{
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
					popup.show(event.getComponent(), event.getX(), event.getY());
				}
			}
		});
		this.tableChecked.setBackground(backColor);
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
		JPanel infoPanel = new JPanel();
		infoPanel.add(infoTextLabel, gcb);
		infoPanel.setBackground(backColor);
		this.cardPanel = new JPanel(new GridBagLayout());
		this.cardPanel.add(this.cardTextLabel, gcb);
		this.cardPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		this.staffPanel = new JPanel(new GridBagLayout());
		this.staffPanel.setBackground(backColor);
		// ///////////////////////////////////////////////////////////////////////////////////////////
		Border border = BorderFactory.createLineBorder(Color.BLACK);
		JPanel panelCheckManually = new JPanel(new BorderLayout());
		JPanel panelSettings = new JPanel(new BorderLayout());
		panelCheckManually.setBackground(backColor);
		panelSettings.setBackground(backColor);
		JTextArea studentManuallyCheckArea = new JTextArea();
		studentManuallyCheckArea.setLineWrap(true);
		studentManuallyCheckArea.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(2, 2, 2, 2)));
		studentManuallyCheckArea.setPreferredSize(new Dimension(100, 25));
		JButton checkManuallyButton = new JButton("Check manually");
		checkManuallyButton.setBackground(backColor);
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
		JCheckBox addNewCardCheck = new JCheckBox("Add new cards to database");
		addNewCardCheck.setBackground(backColor);
		addNewCardCheck.setSelected(Utils.addNewCards);
		addNewCardCheck.addActionListener(event -> Utils.addNewCards = ((JCheckBox) event.getSource()).isSelected());
		JCheckBox logAllCheck = new JCheckBox("Log all checks");
		logAllCheck.setBackground(backColor);
		logAllCheck.setSelected(Utils.addNewCards);
		logAllCheck.addActionListener(event -> Utils.logAll = ((JCheckBox) event.getSource()).isSelected());
		JButton groupSettings = new JButton("Group settings");
		groupSettings.setBackground(backColor);
		groupSettings.addActionListener(event -> new GroupSettingsFrame(MainFrame.this, Utils.groups));
		panelCheckManually.add(studentManuallyCheckArea, BorderLayout.NORTH);
		panelCheckManually.add(checkManuallyButton, BorderLayout.SOUTH);
		panelSettings.add(groupSettings, BorderLayout.NORTH);
		panelSettings.add(addNewCardCheck, BorderLayout.CENTER);
		panelSettings.add(logAllCheck, BorderLayout.SOUTH);
		line = 0;
		gcb.anchor = GridBagConstraints.NORTH;
		gcb.fill = GridBagConstraints.HORIZONTAL;
		gcb.insets = new Insets(10, 20, 10, 20);
		gcb.gridy = line++;
		gcb.gridy = line++;
		this.staffPanel.add(panelCheckManually, gcb);
		gcb.gridy = line++;
		this.staffPanel.add(panelSettings, gcb);
		// ///////////////////////////////////////////////////////////////////////////////////////////
		JScrollPane scrollPaneChecked = new JScrollPane(this.tableChecked);
		scrollPaneChecked.setAutoscrolls(false);
		scrollPaneChecked.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPaneChecked.setBackground(backColor);
		// ///////////////////////////////////////////////////////////////////////////////////////////
		line = 0;
		gcb = new GridBagConstraints();
		getContentPane().setLayout(new GridBagLayout());
		getContentPane().setBackground(backColor);
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
		getContentPane().add(infoPanel, gcb);
		gcb.gridwidth = 1;
		gcb.weighty = 10;
		gcb.weightx = 1;
		gcb.gridy = line++;
		getContentPane().add(this.staffPanel, gcb);
		gcb.gridx = 1;
		gcb.weightx = 10;
		getContentPane().add(scrollPaneChecked, gcb);
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

	private void uncheckStudent(Student student)
	{
		for(Group group : Utils.groups)
			group.uncheckStudent(student);
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
		Student student = Utils.getStudentByUID(rfidCard.getUid(), true);
		this.currentStudent = student;
		if(student == null)
		{
			this.cardTextLabel.setText("Card detected : " + rfidCard);
			if(Utils.addNewCards)
			{
				student = new Student(rfidCard.getUid(), JOptionPane.showInputDialog(this, "Entrez le nom de l'étudiant (NOM Prénom):", ""), false);
				if(student.hasValidName())
					Utils.sql.addStudentToDatabase(student);
			}
			return;
		}
		Utils.logger.log(Level.INFO, "Card infos: " + student + " " + rfidCard);
		this.cardPanel.setBackground(Color.GREEN);
		this.cardTextLabel.setText("Card detected : " + student.getName() + " " + (student.isStaff() ? "(Staff)" : "(Student)"));
		if(checkStudent(student, false))
			Sounds.CARD_CHECKED.playSound();
		setStaffInfos(student.isStaff());
	}

	private boolean checkStudent(Student student, boolean printMessage)
	{
		boolean checked = false;
		for(Group group : Utils.groups)
			if(group.checkStudent(student))
				checked |= true;
		return checked;
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
		checkStudent(Utils.getStudentByName(name, true), true);
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
	 * Thread.
	 *
	 * Will update clock, and activate/deactivate periods.
	 */
	@Override
	public void run()
	{
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		while(!Thread.interrupted())
		{
			try
			{
				Thread.sleep(500);
			}
			catch(InterruptedException exception)
			{}
			Date date = new Date();
			this.infoTextLabel.setText(dateFormat.format(date));
			ArrayList<Student> toCheck = new ArrayList<Student>();
			for(Group group : Utils.groups)
			{
				group.update();
				toCheck.addAll(group.getAllToCheck());
			}
			Utils.removeDuplicates(toCheck);
			Vector vec = modelChecked.getDataVector();
			for(Student student : toCheck)
				if(!Utils.containsStudent(vec, student))
					modelChecked.addRow(new Object[]{student});
			for(int i = 0; i < modelChecked.getRowCount(); i++)
				if(!Utils.containsStudent(toCheck, ((Student) modelChecked.getValueAt(i, 0))))
					modelChecked.removeRow(i);
			modelChecked.fireTableDataChanged();
		}
	}

	/**
	 * Used to parse the students to an Object[][] for the table.
	 *
	 * @param students the students to parse.
	 * @return The parsed students.
	 */
	private Student[][] getTableList(ArrayList<Student> students)
	{
		Student[][] student = new Student[students.size()][1];
		int i = 0;
		for(Student stu : students)
			student[i++][0] = stu;
		return student;
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
		this.menuItemExit.setEnabled(staffMember);
	}

	public boolean hasChecked(Student student)
	{
		boolean checked = true;
		for(Group group : Utils.groups)
			checked &= group.hasChecked(student);
		return checked;
	}
}
