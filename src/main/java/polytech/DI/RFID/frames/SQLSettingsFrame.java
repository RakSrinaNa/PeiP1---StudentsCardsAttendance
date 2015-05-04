package polytech.DI.RFID.frames;

import polytech.DI.RFID.utils.Utils;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * Frame for the configuration of the database.
 *
 * @author COLEAU Victor, COUCHOUD Thomas
 */
public class SQLSettingsFrame extends JDialog
{
	private static final long serialVersionUID = 4338843790906987061L;
	private final JTextArea dbName;
	private final JTextArea dbIP;
	private final JTextArea dbPort;
	private final JPasswordField dbPassword;
	private final JTextArea dbUser;
	private final JTextArea dbTableName;

	/**
	 * Constructor.
	 *
	 * @param parent The parent frame.
	 * @see JDialog#JDialog(Window)
	 */
	public SQLSettingsFrame(MainFrame parent)
	{
		super(parent);
		this.setIconImages(Utils.icons);
		this.setTitle(Utils.resourceBundle.getString("sql_setting"));
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.addWindowListener(new WindowListener()
		{
			@Override
			public void windowOpened(WindowEvent e)
			{
			}

			@Override
			public void windowClosing(WindowEvent e)
			{
				save();
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
		JLabel dbNameLabel = new JLabel(Utils.resourceBundle.getString("bdd_name") + ":");
		dbNameLabel.setHorizontalAlignment(JLabel.RIGHT);
		this.dbName = new JTextArea(Utils.configuration.getBddName());
		this.dbName.setWrapStyleWord(true);
		this.dbName.setLineWrap(true);
		JLabel dbTableNameLabel = new JLabel(Utils.resourceBundle.getString("bdd_table_name") + ":");
		dbTableNameLabel.setHorizontalAlignment(JLabel.RIGHT);
		this.dbTableName = new JTextArea(Utils.configuration.getBddTableName());
		this.dbTableName.setWrapStyleWord(true);
		this.dbTableName.setLineWrap(true);
		JLabel dbIPLabel = new JLabel(Utils.resourceBundle.getString("bdd_ip") + ":");
		dbNameLabel.setHorizontalAlignment(JLabel.RIGHT);
		this.dbIP = new JTextArea(Utils.configuration.getBddIP());
		this.dbIP.setWrapStyleWord(true);
		this.dbIP.setLineWrap(true);
		JLabel dbPortLabel = new JLabel(Utils.resourceBundle.getString("bdd_port") + ":");
		dbPortLabel.setHorizontalAlignment(JLabel.RIGHT);
		this.dbPort = new JTextArea("" + Utils.configuration.getBddPort());
		this.dbPort.setWrapStyleWord(true);
		this.dbPort.setLineWrap(true);
		JLabel dbUserLabel = new JLabel(Utils.resourceBundle.getString("bdd_user") + ":");
		dbUserLabel.setHorizontalAlignment(JLabel.RIGHT);
		this.dbUser = new JTextArea("" + Utils.configuration.getBddUser());
		this.dbUser.setWrapStyleWord(true);
		this.dbUser.setLineWrap(true);
		JLabel dbPasswordLabel = new JLabel(Utils.resourceBundle.getString("bdd_password") + ":");
		dbPasswordLabel.setHorizontalAlignment(JLabel.RIGHT);
		dbPassword = new JPasswordField(Utils.configuration.getBddPassword());
		JPanel ipPanel = new JPanel();
		ipPanel.add(dbIPLabel);
		ipPanel.add(dbIP);
		ipPanel.setBackground(MainFrame.backColor);
		JPanel portPanel = new JPanel();
		portPanel.add(dbPortLabel);
		portPanel.add(dbPort);
		portPanel.setBackground(MainFrame.backColor);
		JPanel namePanel = new JPanel();
		namePanel.add(dbNameLabel);
		namePanel.add(dbName);
		namePanel.setBackground(MainFrame.backColor);
		JPanel tableNamePanel = new JPanel();
		tableNamePanel.add(dbTableNameLabel);
		tableNamePanel.add(dbTableName);
		tableNamePanel.setBackground(MainFrame.backColor);
		JPanel userPanel = new JPanel();
		userPanel.add(dbUserLabel);
		userPanel.add(dbUser);
		userPanel.setBackground(MainFrame.backColor);
		JPanel passwordPanel = new JPanel();
		passwordPanel.add(dbPasswordLabel);
		passwordPanel.add(dbPassword);
		passwordPanel.setBackground(MainFrame.backColor);
		this.getContentPane().setLayout(new GridBagLayout());
		int line = 0;
		GridBagConstraints gcb = new GridBagConstraints();
		getContentPane().setLayout(new GridBagLayout());
		gcb.anchor = GridBagConstraints.PAGE_START;
		gcb.fill = GridBagConstraints.BOTH;
		gcb.weighty = 1;
		gcb.weightx = 1;
		gcb.insets = new Insets(10, 10, 10, 10);
		gcb.gridheight = 1;
		gcb.gridwidth = 1;
		gcb.gridx = line++;
		this.getContentPane().add(ipPanel, gcb);
		gcb.gridy = line++;
		this.getContentPane().add(portPanel, gcb);
		gcb.gridy = line++;
		this.getContentPane().add(namePanel, gcb);
		gcb.gridy = line++;
		this.getContentPane().add(tableNamePanel, gcb);
		gcb.gridy = line++;
		this.getContentPane().add(userPanel, gcb);
		gcb.gridy = line++;
		this.getContentPane().add(passwordPanel, gcb);
		this.getContentPane().setBackground(MainFrame.backColor);
		pack();
		this.setLocationRelativeTo(parent);
		this.setVisible(true);
	}

	/**
	 * Used to save the settings.
	 */
	private void save()
	{
		Utils.configuration.setBddIP(dbIP.getText());
		Utils.configuration.setBddPort(Integer.parseInt(dbPort.getText()));
		Utils.configuration.setBddName(dbName.getText());
		Utils.configuration.setBddTableName(dbTableName.getText());
		Utils.configuration.setBddUser(dbUser.getText());
		Utils.configuration.setBddPassword(getPassword());
		Utils.reloadSQLFromConfig();
	}

	/**
	 * Used to get the database password as a String.
	 *
	 * @return The database password.
	 */
	private String getPassword()
	{
		StringBuilder sb = new StringBuilder();
		for(char c : dbPassword.getPassword())
			sb.append(c);
		return sb.toString();
	}
}
