package fr.tours.polytech.DI.RFID.frames;

import fr.tours.polytech.DI.RFID.objects.Period;
import fr.tours.polytech.DI.RFID.utils.Utils;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ScheduleFrame extends JDialog
{
	private ArrayList<Period> result;

	public ScheduleFrame(GroupEditFrame parent)
	{
		super(parent);
		this.setIconImages(Utils.icons);
		this.setTitle(Utils.resourceBundle.getString("schedule_setting"));
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.getContentPane().setLayout(new GridBagLayout());
		/**************************************************************************/
		JButton valider = new JButton("Valider");
		valider.addActionListener(e -> {
			setVisible(false);
			dispose();
		});
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
		this.getContentPane().add(valider, gcb);
		this.getContentPane().setBackground(MainFrame.backColor);
		pack();
		this.setLocationRelativeTo(parent);
	}

	public ArrayList<Period> showDialog()
	{
		setVisible(true);
		return result;
	}
}
