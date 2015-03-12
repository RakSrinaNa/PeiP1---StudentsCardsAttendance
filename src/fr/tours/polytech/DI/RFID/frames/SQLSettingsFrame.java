package fr.tours.polytech.DI.RFID.frames;

import fr.tours.polytech.DI.RFID.Main;
import fr.tours.polytech.DI.RFID.utils.Utils;
import javax.swing.*;
import java.awt.*;
/**
 * Created by Tom on 12/03/2015.
 */
public class SQLSettingsFrame extends JDialog
{
	private MainFrame parent;

	public SQLSettingsFrame(MainFrame parent)
	{
		super(parent);
		this.parent = parent;
		this.setIconImages(Utils.icons);
		this.setTitle(Utils.resourceBundle.getString("sql_setting"));
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.getContentPane().setLayout(new GridBagLayout());
		int line = 0;
		GridBagConstraints gcb = new GridBagConstraints();
		getContentPane().setLayout(new GridBagLayout());
		gcb.anchor = GridBagConstraints.PAGE_START;
		gcb.fill = GridBagConstraints.BOTH;
		gcb.weighty = 1;
		gcb.weightx = 1;
		gcb.gridheight = 1;
		gcb.gridwidth = 1;
		gcb.gridx = 0;
		gcb.gridy = line++;
		this.getContentPane().setBackground(MainFrame.backColor);
		pack();
		this.setLocationRelativeTo(parent);
		this.setVisible(true);
	}
}
