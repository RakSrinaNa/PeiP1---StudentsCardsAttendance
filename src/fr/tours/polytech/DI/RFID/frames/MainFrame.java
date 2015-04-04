package fr.tours.polytech.DI.RFID.frames;

import fr.tours.polytech.DI.RFID.Main;
import fr.tours.polytech.DI.RFID.frames.components.ImagePanel;
import fr.tours.polytech.DI.RFID.frames.components.JTableUneditableModel;
import fr.tours.polytech.DI.RFID.frames.components.StudentsRenderer;
import fr.tours.polytech.DI.RFID.utils.Periods;
import fr.tours.polytech.DI.RFID.utils.Students;
import fr.tours.polytech.DI.RFID.utils.Utils;
import fr.tours.polytech.DI.TerminalReader.interfaces.TerminalListener;
import fr.tours.polytech.DI.TerminalReader.objects.RFIDCard;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
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
	public static final String VERSION = "2.0";
	private static final long serialVersionUID = -4989573496325827301L;
	private final Thread thread;
	private final JPanel cardPanel;
	private final JPanel staffPanel;
	private final JLabel cardTextLabel;
	private final JLabel groupsInfoLabel;
	private final JTable tableChecked;
	private final ImagePanel openPanelImage;
	private final JTableUneditableModel modelChecked;
	private final TableRowSorter<TableModel> sorter;
	private int periodID;
	public static Color backColor;
	private boolean cardPresent;
	private boolean checking;
	private boolean needRefresh;

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
		checking = false;
		needRefresh = false;
		cardPresent = false;
		periodID = 0;
		addWindowListener(new WindowListener()
		{
			@Override
			public void windowOpened(WindowEvent event)
			{
			}

			@Override
			public void windowClosing(WindowEvent event)
			{
				Utils.exit(0);
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
			private static final long serialVersionUID = -8761878907892686344L;

			@Override
			public void actionPerformed(ActionEvent e)
			{
				setStaffInfos(!staffPanel.isVisible());
			}
		});
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control alt C"), "card");
		getRootPane().getActionMap().put("card", new AbstractAction()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(!cardPresent)
					cardAdded(new RFIDCard("", JOptionPane.showInputDialog(MainFrame.this, "Entrez l'UID de la carte:", "Simuler une carte", JOptionPane.QUESTION_MESSAGE), null));
				else
					cardRemoved();
			}
		});
		// ///////////////////////////////////////////////////////////////////////////////////////////
		JMenuBar menuBar = new JMenuBar();
		JMenu menuFile = new JMenu(Utils.resourceBundle.getString("menu_file"));
		JMenu menuHelp = new JMenu(Utils.resourceBundle.getString("menu_help"));
		JMenu menuResult = new JMenu(Utils.resourceBundle.getString("menu_result"));
		JMenuItem menuItemExit = new JMenuItem(Utils.resourceBundle.getString("menu_item_quit"));
		JMenuItem menuItemHelp = new JMenuItem(Utils.resourceBundle.getString("menu_item_help"));
		JMenuItem menuItemAbout = new JMenuItem(Utils.resourceBundle.getString("menu_item_about"));
		JMenuItem menuItemExportSQL = new JMenuItem(Utils.resourceBundle.getString("menu_item_export_sql"));
		JMenuItem menuItemImportSQL = new JMenuItem(Utils.resourceBundle.getString("menu_item_import_sql"));
		JMenuItem menuItemImportCSV = new JMenuItem(Utils.resourceBundle.getString("menu_item_import_csv"));
		JMenuItem menuItemExportResult = new JMenuItem(Utils.resourceBundle.getString("menu_item_export_result"));
		menuItemExit.addActionListener(event -> Utils.exit(0));
		menuItemHelp.addActionListener(event -> {
			try
			{
				Desktop.getDesktop().browse(new URL("https://github.com/MrCraftCod/RFID/wiki").toURI());
			}
			catch(Exception exception)
			{
				Utils.logger.log(Level.WARNING, "Error when opening wiki page", exception);
			}
		});
		menuItemAbout.addActionListener(event -> new AboutFrame(MainFrame.this));
		menuItemExportSQL.addActionListener(event -> Utils.exportSQL(this));
		menuItemImportSQL.addActionListener(event -> Utils.importSQL(this));
		menuItemImportCSV.addActionListener(event -> Utils.importCSV(this));
		menuItemExportResult.addActionListener(event -> Utils.exportResults(MainFrame.this));
		menuFile.add(menuItemExportSQL);
		menuFile.add(menuItemImportSQL);
		menuFile.addSeparator();
		menuFile.add(menuItemImportCSV);
		menuFile.addSeparator();
		menuFile.add(menuItemExit);
		menuHelp.add(menuItemHelp);
		menuHelp.add(menuItemAbout);
		menuResult.add(menuItemExportResult);
		menuBar.add(menuFile);
		menuBar.add(menuResult);
		menuBar.add(menuHelp);
		setJMenuBar(menuBar);
		// ///////////////////////////////////////////////////////////////////////////////////////////
		JButton startButton = new JButton(Utils.resourceBundle.getString("button_start"));
		startButton.addActionListener(e ->
		{
			checking = !checking;
			if(checking)
				periodID = Periods.startNewPeriod();
			else
				Periods.endPeriod(periodID);
			startButton.setText(Utils.resourceBundle.getString(checking ? "button_stop" : "button_start"));
		});
		this.cardTextLabel = new JLabel();
		this.cardTextLabel.setVerticalAlignment(JLabel.CENTER);
		this.cardTextLabel.setHorizontalAlignment(JLabel.CENTER);
		groupsInfoLabel = new JLabel();
		groupsInfoLabel.setVerticalAlignment(JLabel.CENTER);
		groupsInfoLabel.setHorizontalAlignment(JLabel.CENTER);
		openPanelImage = new ImagePanel();
		openPanelImage.setPreferredSize(new Dimension(20, 20));
		openPanelImage.setBackground(backColor);
		openPanelImage.addMouseListener(new MouseListener()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
			}

			@Override
			public void mousePressed(MouseEvent e)
			{
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				setStaffInfos(!staffPanel.isVisible());
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
			}
		});
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		modelChecked = new JTableUneditableModel(new String[][]{}, new String[]{Utils.resourceBundle.getString("name")});
		this.tableChecked = new JTable(modelChecked)
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
			{
			}

			@Override
			public void mousePressed(MouseEvent event)
			{
			}

			@Override
			public void mouseReleased(MouseEvent event)
			{
				int row = MainFrame.this.tableChecked.rowAtPoint(event.getPoint());
				if(row >= 0 && row < MainFrame.this.tableChecked.getRowCount())
					MainFrame.this.tableChecked.setRowSelectionInterval(row, row);
				else
					MainFrame.this.tableChecked.clearSelection();
				int rowindex = MainFrame.this.tableChecked.getSelectedRow();
				MainFrame.this.tableChecked.clearSelection();
				if(rowindex < 0)
					return;
				if(event.isPopupTrigger() && event.getComponent() instanceof JTable)
				{
					JPopupMenu popup = new JPopupMenu();
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
		this.tableChecked.setDefaultRenderer(String.class, new StudentsRenderer(centerRenderer));
		this.tableChecked.getTableHeader().setReorderingAllowed(false);
		this.tableChecked.getTableHeader().setResizingAllowed(true);
		this.tableChecked.setRowHeight(20);
		this.tableChecked.setShowGrid(true);
		this.tableChecked.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		this.tableChecked.setGridColor(Color.BLACK);
		sorter = new TableRowSorter<>(tableChecked.getModel());
		tableChecked.setRowSorter(sorter);
		ArrayList<RowSorter.SortKey> sortKeys = new ArrayList<>();
		sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
		sorter.setSortKeys(sortKeys);
		sorter.setSortsOnUpdates(false);
		sorter.sort();
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
		this.cardPanel = new JPanel(new GridBagLayout());
		this.cardPanel.add(this.cardTextLabel, gcb);
		this.cardPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		this.staffPanel = new JPanel(new GridBagLayout());
		this.staffPanel.setBackground(backColor);
		// ///////////////////////////////////////////////////////////////////////////////////////////
		JPanel panelSettings = new JPanel(new BorderLayout());
		panelSettings.setBackground(backColor);
		JCheckBox addNewCardCheck = new JCheckBox("<html><p width=\"200\" align=\"center\">" + Utils.resourceBundle.getString("add_new_card") + "</p></html>");
		addNewCardCheck.setBackground(backColor);
		addNewCardCheck.setSelected(Utils.configuration.isAddNewStudents());
		addNewCardCheck.addActionListener(event -> Utils.configuration.setAddNewStudents(((JCheckBox) event.getSource()).isSelected()));
		JCheckBox logAllCheck = new JCheckBox("<html><p width=\"200\" align=\"center\">" + Utils.resourceBundle.getString("log_all") + "</p></html>");
		logAllCheck.setBackground(backColor);
		logAllCheck.setSelected(Utils.configuration.isLogAll());
		logAllCheck.addActionListener(event -> Utils.configuration.setLogAll(((JCheckBox) event.getSource()).isSelected()));
		JButton sqlSettings = new JButton(Utils.resourceBundle.getString("sql_settings"));
		sqlSettings.setBackground(backColor);
		sqlSettings.addActionListener(event -> new SQLSettingsFrame(MainFrame.this));
		JButton readerSelect = new JButton(Utils.resourceBundle.getString("select_reader"));
		readerSelect.setBackground(backColor);
		readerSelect.addActionListener(event -> {
			ArrayList<String> selected = new ArrayList<>();
			selected.add(Utils.configuration.getReaderName());
			ArrayList<String> selection = new SelectListDialogFrame<String>(MainFrame.this, Utils.resourceBundle.getString("select_reader"), Utils.resourceBundle.getString("selection_reader"), Utils.terminalReader.getReadersName(), selected, false).showDialog();
			if(selection != null && selection.size() > 0)
			{
				Utils.configuration.setReaderName(selection.get(0));
				Utils.terminalReader.setTerminalName(selection.get(0));
			}
		});
		line = 0;
		gcb = new GridBagConstraints();
		gcb.anchor = GridBagConstraints.CENTER;
		gcb.fill = GridBagConstraints.HORIZONTAL;
		gcb.weightx = 1;
		gcb.weighty = 1;
		gcb.gridwidth = 1;
		gcb.gridheight = 1;
		gcb.gridx = 0;
		gcb.gridy = line++;
		gcb.insets = new Insets(10, 20, 10, 20);
		this.staffPanel.add(sqlSettings, gcb);
		gcb.gridy = line++;
		this.staffPanel.add(readerSelect, gcb);
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
		gcb.anchor = GridBagConstraints.WEST;
		gcb.fill = GridBagConstraints.NONE;
		gcb.weightx = 1;
		gcb.insets = new Insets(0, 0, 0, 0);
		gcb.weighty = 1;
		gcb.weightx = 10;
		gcb.gridheight = 1;
		gcb.gridwidth = 2;
		gcb.gridx = 0;
		gcb.gridy = line++;
		getContentPane().add(openPanelImage, gcb);
		gcb.anchor = GridBagConstraints.PAGE_START;
		gcb.fill = GridBagConstraints.BOTH;
		gcb.gridx = 1;
		getContentPane().add(groupsInfoLabel, gcb);
		gcb.gridy = line++;
		getContentPane().add(startButton, gcb);
		gcb.gridwidth = 1;
		gcb.weighty = 10;
		gcb.weightx = 1;
		gcb.gridx = 0;
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
		cardPresent = true;
		if(!Students.isStudentKnown(rfidCard.getUid()))
		{
			this.cardTextLabel.setText(Utils.resourceBundle.getString("card_detected") + " : " + rfidCard);
			try
			{
				if(!Students.addStudent(JOptionPane.showInputDialog(this, Utils.resourceBundle.getString("new_card_name") + ":", ""), rfidCard.getUid()))
					return;
			}
			catch(Exception e)
			{
				Utils.logger.log(Level.INFO, "Can't add student -> " + e.getMessage());
				return;
			}
		}
		Utils.logger.log(Level.INFO, Utils.resourceBundle.getString("card_info") + ": " + " " + rfidCard);
		this.cardPanel.setBackground(Color.GREEN);
		this.cardTextLabel.setText(Utils.resourceBundle.getString("card_detected") + " : " + Students.getStudentNameByUID(rfidCard.getUid()));
		Utils.sql.logCheck(rfidCard.getUid());
		needRefresh = checkCard(rfidCard);
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
		cardPresent = false;
		this.cardPanel.setBackground(Color.ORANGE);
		this.cardTextLabel.setText(Utils.resourceBundle.getString("no_card"));
	}

	private boolean checkCard(RFIDCard rfidCard)
	{
		if(checking)
		{
			int i = Students.checkStudent(rfidCard.getUid(), periodID);
			System.out.println(i);
			return i > 0;
		}
		return false;
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
		long lastRefreshTime = 0;
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
			ArrayList<String> toCheck = new ArrayList<>();
			ArrayList<String> toAdd = new ArrayList<>();
			if(date.getTime() - lastRefreshTime > 1500)
			{
				if(checking)
					toCheck.addAll(Students.getAllStudents());
				lastRefreshTime = date.getTime();
				Utils.removeDuplicates(toCheck);
				Vector<String> vec = modelChecked.getDataVector();
				for(String student : toCheck)
					if(student != null)
						if(!Utils.vectorContains(vec, student))
							toAdd.add(student);
				if(toAdd.size() > 0)
					SwingUtilities.invokeLater(() -> {
						try
						{
							needRefresh = true;
							for(String etu : toAdd)
								modelChecked.addRow(new String[]{etu});
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					});
				if(toCheck.size() < 1)
					for(int i = 0; i < modelChecked.getRowCount(); i++)
					{
						needRefresh = true;
						modelChecked.setRowCount(0);
					}
				else
					for(int i = 0; i < modelChecked.getRowCount(); i++)
						if(!toCheck.contains(modelChecked.getValueAt(i, 0)))
						{
							needRefresh = true;
							modelChecked.removeRow(i);
						}
			}
			this.groupsInfoLabel.setText(groupsInfo.append("</p></html>").toString());
			if(needRefresh)
				SwingUtilities.invokeLater(() -> {
					try
					{
						modelChecked.fireTableDataChanged();
						sorter.sort();
						needRefresh = false;
					}
					catch(NullPointerException e)
					{
						needRefresh = true;
					}
				});
		}
	}

	/**
	 * Used to set the staff panel.
	 *
	 * @param staffMember Is it a staff member?
	 */
	@SuppressWarnings("ConstantConditions")
	private void setStaffInfos(boolean staffMember)
	{
		this.staffPanel.setVisible(staffMember);
		this.staffPanel.setEnabled(staffMember);
		if(staffPanel.isVisible())
		{
			try
			{
				openPanelImage.setImage(ImageIO.read(Main.class.getClassLoader().getResource("images/open_panel.png")));
			}
			catch(IOException exception)
			{
				Utils.logger.log(Level.WARNING, "Couldn't load logo image", exception);
			}
		}
		else
		{
			try
			{
				openPanelImage.setImage(ImageIO.read(Main.class.getClassLoader().getResource("images/close_panel.png")));
			}
			catch(IOException exception)
			{
				Utils.logger.log(Level.WARNING, "Couldn't load logo image", exception);
			}
		}
	}
}
