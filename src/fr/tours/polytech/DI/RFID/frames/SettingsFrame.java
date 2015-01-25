package fr.tours.polytech.DI.RFID.frames;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import fr.tours.polytech.DI.RFID.frames.components.JTextFieldLimitNumbers;
import fr.tours.polytech.DI.RFID.utils.Configuration;
import fr.tours.polytech.DI.RFID.utils.Utils;

public class SettingsFrame extends JDialog
{
	private static final long serialVersionUID = 3288422241245947694L;
	private JTextField periodTimeField;

	public SettingsFrame(JFrame parent)
	{
		super(parent);
		setLayout(new GridBagLayout());
		setAlwaysOnTop(false);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowListener()
		{
			@Override
			public void windowActivated(WindowEvent e)
			{}

			@Override
			public void windowClosed(WindowEvent e)
			{}

			@Override
			public void windowClosing(final WindowEvent e)
			{
				if(isSettingsModified())
				{
					int result = JOptionPane.showConfirmDialog(null, "Save changes?", "Changes", JOptionPane.YES_NO_OPTION);
					if(result == JOptionPane.YES_OPTION)
					{
						if(!save())
							return;
					}
					else if(result != JOptionPane.NO_OPTION)
						return;
				}
				dispose();
			}

			@Override
			public void windowDeactivated(WindowEvent e)
			{}

			@Override
			public void windowDeiconified(WindowEvent e)
			{}

			@Override
			public void windowIconified(WindowEvent e)
			{}

			@Override
			public void windowOpened(WindowEvent e)
			{}
		});
		JLabel periodLabel = new JLabel("Duration of a period (in hours): ");
		periodLabel.setHorizontalAlignment(JLabel.RIGHT);
		this.periodTimeField = new JTextField();
		this.periodTimeField.setDocument(new JTextFieldLimitNumbers(2));
		this.periodTimeField.setText("" + Utils.config.getConfigValue(Configuration.HOUR_INTERVAL).getInt(2));
		JButton valid = new JButton("Valid");
		valid.addActionListener(e -> {
			save();
			dispose();
		});
		int lines = 0;
		GridBagConstraints constraint = new GridBagConstraints();
		constraint.anchor = GridBagConstraints.PAGE_START;
		constraint.fill = GridBagConstraints.BOTH;
		constraint.gridwidth = 1;
		constraint.gridx = 0;
		constraint.gridy = lines++;
		constraint.weightx = 1;
		constraint.weighty = 1;
		this.add(periodLabel, constraint);
		constraint.gridx = 1;
		this.add(this.periodTimeField, constraint);
		int frameHeight = (lines + 1) * 30;
		setPreferredSize(new Dimension(400, frameHeight));
		pack();
		setVisible(true);
	}

	public boolean isSettingsModified()
	{
		return false;
	}

	public boolean save()
	{
		return true;
	}
}
