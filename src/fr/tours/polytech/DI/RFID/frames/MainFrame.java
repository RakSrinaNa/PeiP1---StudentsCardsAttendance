package fr.tours.polytech.DI.RFID.frames;

import fr.tours.polytech.DI.RFID.enums.Sounds;
import fr.tours.polytech.DI.RFID.frames.components.JTableUneditableModel;
import fr.tours.polytech.DI.RFID.frames.components.StudentsRenderer;
import fr.tours.polytech.DI.RFID.objects.Group;
import fr.tours.polytech.DI.RFID.objects.Student;
import fr.tours.polytech.DI.RFID.utils.Utils;
import fr.tours.polytech.DI.TerminalReader.interfaces.TerminalListener;
import fr.tours.polytech.DI.TerminalReader.objects.RFIDCard;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;

/**
 * Class of the main frame.
 *
 * @author COLEAU Victor, COUCHOUD Thomas
 */
public class MainFrame extends JFrame implements TerminalListener, Runnable
{
	public static final String VERSION = "1.0";
	private static final long serialVersionUID = -4989573496325827301L;
	private final Thread thread;
	private final JPanel cardPanel;
	private final JPanel staffPanel;
	private final JLabel cardTextLabel;
	private final JLabel groupsInfoLabel;
	private final JTable tableChecked;
	private final JMenuItem menuItemExit;
	private final JTableUneditableModel modelChecked;
	public static Color backColor;
	private Student currentStudent;

	/**
	 * Constructor.
	 */
	public MainFrame()
	{
		super(Utils.resourceBundle.getString("app_title"));
		this.setIconImages(Utils.icons);
		backColor = new Color(224, 242, 255);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setPreferredSize(new Dimension(800, 600));
		addWindowListener(new WindowListener()
		{
			@Override
			public void windowOpened(WindowEvent event)
			{
			}

			@Override
			public void windowClosing(WindowEvent event)
			{
				if(true || MainFrame.this.currentStudent != null && MainFrame.this.currentStudent.isStaff())
					Utils.exit(0);
				else
					JOptionPane.showMessageDialog(MainFrame.this, Utils.resourceBundle.getString("require_staff_card_close"), Utils.resourceBundle.getString("not_allowed").toUpperCase(), JOptionPane.ERROR_MESSAGE);
			}

			@Override
			public void windowClosed(WindowEvent event)
			{
			}

			@Override
			public void windowIconified(WindowEvent event)
			{
			}

			@Override
			public void windowDeiconified(WindowEvent event)
			{
			}

			@Override
			public void windowActivated(WindowEvent event)
			{
			}

			@Override
			public void windowDeactivated(WindowEvent event)
			{
			}
		});
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control alt P"), "openStaff");
		getRootPane().getActionMap().put("openStaff", new AbstractAction()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				setStaffInfos(!staffPanel.isVisible());
			}
		});
		// ///////////////////////////////////////////////////////////////////////////////////////////
		JMenuBar menuBar = new JMenuBar();
		JMenu menuFile = new JMenu(Utils.resourceBundle.getString("menu_file"));
		JMenu menuHelp = new JMenu(Utils.resourceBundle.getString("menu_help"));
		this.menuItemExit = new JMenuItem(Utils.resourceBundle.getString("menu_item_quit"));
		JMenuItem menuItemHelp = new JMenuItem(Utils.resourceBundle.getString("menu_item_help"));
		JMenuItem menuItemAbout = new JMenuItem(Utils.resourceBundle.getString("menu_item_about"));
		this.menuItemExit.addActionListener(event -> Utils.exit(0));
		menuItemHelp.addActionListener(event -> {
			try
			{
				Desktop.getDesktop().browse(new URL("https://github.com/MrCraftCod/TerminalReader/wiki").toURI());
			}
			catch(Exception exception)
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
		groupsInfoLabel = new JLabel();
		groupsInfoLabel.setVerticalAlignment(JLabel.CENTER);
		groupsInfoLabel.setHorizontalAlignment(JLabel.CENTER);
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		modelChecked = new JTableUneditableModel(new Student[][]{}, new String[]{Utils.resourceBundle.getString("name")});
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
			{
			}

			@Override
			public void mousePressed(MouseEvent event)
			{
			}

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
					Student student = Utils.getStudentByName(MainFrame.this.tableChecked.getValueAt(rowindex, 0).toString().replace("(Staff)", "").trim(), false);
					JPopupMenu popup = new JPopupMenu();
					JMenuItem checkStudent = new JMenuItem(Utils.resourceBundle.getString("check_student"));
					checkStudent.addActionListener(event1 -> {
						try
						{
							Utils.checkStudent(student);
						}
						catch(Exception exception)
						{
							Utils.logger.log(Level.WARNING, "", exception);
						}
					});
					JMenuItem uncheckStudent = new JMenuItem(Utils.resourceBundle.getString("uncheck_student"));
					uncheckStudent.addActionListener(event1 -> {
						try
						{
							Utils.uncheckStudent(student);
						}
						catch(Exception exception)
						{
							Utils.logger.log(Level.WARNING, "", exception);
						}
					});
					if(!Utils.hasChecked(student))
						popup.add(checkStudent);
					else
						popup.add(uncheckStudent);
					popup.show(event.getComponent(), event.getX(), event.getY());
				}
			}

			@Override
			public void mouseEntered(MouseEvent event)
			{
			}

			@Override
			public void mouseExited(MouseEvent event)
			{
			}
		});
		this.tableChecked.setBackground(backColor);
		this.tableChecked.setDefaultRenderer(Student.class, new StudentsRenderer(centerRenderer));
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
		infoPanel.add(groupsInfoLabel, gcb);
		infoPanel.setBackground(backColor);
		this.cardPanel = new JPanel(new GridBagLayout());
		this.cardPanel.add(this.cardTextLabel, gcb);
		this.cardPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		this.staffPanel = new JPanel(new GridBagLayout());
		this.staffPanel.setBackground(backColor);
		// ///////////////////////////////////////////////////////////////////////////////////////////
		JPanel panelSettings = new JPanel(new BorderLayout());
		panelSettings.setBackground(backColor);
		JCheckBox addNewCardCheck = new JCheckBox("<html><p align=\"center\">" + Utils.resourceBundle.getString("add_new_card") + "</p></html>");
		addNewCardCheck.setBackground(backColor);
		addNewCardCheck.setSelected(Utils.configuration.isAddNewStudents());
		addNewCardCheck.addActionListener(event -> Utils.configuration.setAddNewStudents(((JCheckBox) event.getSource()).isSelected()));
		JCheckBox logAllCheck = new JCheckBox(Utils.resourceBundle.getString("log_all"));
		logAllCheck.setBackground(backColor);
		logAllCheck.setSelected(Utils.configuration.isLogAll());
		logAllCheck.addActionListener(event -> Utils.configuration.setLogAll(((JCheckBox) event.getSource()).isSelected()));
		JButton groupSettings = new JButton(Utils.resourceBundle.getString("group_settings"));
		groupSettings.setBackground(backColor);
		groupSettings.addActionListener(event -> new GroupSettingsFrame(MainFrame.this, Utils.groups));
		JButton sqlSettings = new JButton(Utils.resourceBundle.getString("sql_settings"));
		sqlSettings.setBackground(backColor);
		sqlSettings.addActionListener(event -> new SQLSettingsFrame(MainFrame.this));
		line = 0;
		gcb.anchor = GridBagConstraints.CENTER;
		gcb.fill = GridBagConstraints.HORIZONTAL;
		gcb.insets = new Insets(10, 20, 10, 20);
		gcb.gridy = line++;
		this.staffPanel.add(groupSettings, gcb);
		gcb.gridy = line++;
		this.staffPanel.add(sqlSettings, gcb);
		gcb.gridy = line++;
		this.staffPanel.add(addNewCardCheck, gcb);
		gcb.gridy = line++;
		this.staffPanel.add(logAllCheck, gcb);
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
		setLocationRelativeTo(null);
		setVisible(true);
		this.thread = new Thread(this);
		this.thread.setName("RefreshInfoPanel");
		this.thread.start();
	}

	/**
	 * Called by the {@link TerminalListener} interface when a card id added.
	 * <p>
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
			this.cardTextLabel.setText(Utils.resourceBundle.getString("card_detected") + " : " + rfidCard);
			if(Utils.configuration.isAddNewStudents())
			{
				String name = JOptionPane.showInputDialog(this, Utils.resourceBundle.getString("new_card_name")+ ":", "");
				student = new Student(rfidCard.getUid(), name.substring(0, name.lastIndexOf(" ")).trim(), name.substring(name.lastIndexOf(" ")).trim() , false);
				if(student.hasValidName())
					Utils.sql.addStudentToDatabase(student);
			}
			return;
		}
		Utils.logger.log(Level.INFO, Utils.resourceBundle.getString("card_info") + ": " + student + " " + rfidCard);
		this.cardPanel.setBackground(Color.GREEN);
		this.cardTextLabel.setText(Utils.resourceBundle.getString("card_detected") + " : " + student.getName() + " " + (student.isStaff() ? "(Staff)" : "(Student)"));
		if(Utils.checkStudent(student))
		{
			Utils.logCheck(student);
			Sounds.CARD_CHECKED.playSound();
		}
		setStaffInfos(student.isStaff());
	}

	/**
	 * Called by the {@link TerminalListener} interface when a reader is added.
	 * <p>
	 * Set the panel text.
	 */
	@Override
	public void cardReaderAdded()
	{
		cardRemoved();
	}

	/**
	 * Called by the {@link TerminalListener} interface when a reader is removed.
	 * <p>
	 * Set the panel text.
	 */
	@Override
	public void cardReaderRemoved()
	{
		this.cardPanel.setBackground(Color.RED);
		this.cardTextLabel.setText(Utils.resourceBundle.getString("no_reader").toUpperCase() + "!");
	}

	/**
	 * Called by the {@link TerminalListener} interface when a card id removed.
	 * <p>
	 * Set the panel text and eventually close the staff panel.
	 */
	@Override
	public void cardRemoved()
	{
		setStaffInfos(false);
		this.currentStudent = null;
		this.cardPanel.setBackground(Color.ORANGE);
		this.cardTextLabel.setText(Utils.resourceBundle.getString("no_card"));
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
	 * Thread. Will update clock, periods and student list.
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
			{
			}
			Date date = new Date();
			if(!Utils.sql.isConnected())
			{
				if(date.getTime() > (Utils.sql.getLastConnectTime().getTime() + 15000))
				{
					if(!Utils.sql.isLogging())
					{
						this.cardPanel.setBackground(Color.ORANGE);
						this.cardTextLabel.setText(Utils.resourceBundle.getString("sql_retry_now"));
						if(Utils.sql.login())
						{
							this.cardPanel.setBackground(Color.GREEN);
							this.cardTextLabel.setText(Utils.resourceBundle.getString("sql_connected"));
							Utils.students = Utils.removeDuplicates(Utils.sql.getAllStudents());
						}
					}
				}
				else if(!Utils.sql.isLogging())
				{
					this.cardPanel.setBackground(Color.RED);
					this.cardTextLabel.setText(String.format(Utils.resourceBundle.getString("sql_retry"), (15000 - (date.getTime() - Utils.sql.getLastConnectTime().getTime())) / 1000));
				}
			}
			StringBuilder groupsInfo = new StringBuilder("<html><p align=\"center\">").append(dateFormat.format(date)).append("<br />");
			ArrayList<Student> toCheck = new ArrayList<>();
			for(Group group : Utils.groups)
			{
				group.update();
				toCheck.addAll(group.getAllToCheck());
				if(group.isCurrentlyPeriod())
					groupsInfo.append(Utils.resourceBundle.getString("group")).append(" ").append(group.getName()).append(": ").append(group.getCurrentPeriodString()).append("<br />");
			}
			this.groupsInfoLabel.setText(groupsInfo.append("</p></html>").toString());
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
}
