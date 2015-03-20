package fr.tours.polytech.DI.RFID.frames;

import fr.tours.polytech.DI.RFID.frames.components.DaySchedulePane;
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
		result = new ArrayList<>();
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
		DaySchedulePane w0 = new DaySchedulePane(DaySchedulePane.HOURS);
		DaySchedulePane w1 = new DaySchedulePane(DaySchedulePane.MONDAY);
		DaySchedulePane w2 = new DaySchedulePane(DaySchedulePane.TUESDAY);
		DaySchedulePane w3 = new DaySchedulePane(DaySchedulePane.WEDNESDAY);
		DaySchedulePane w4 = new DaySchedulePane(DaySchedulePane.THURSDAY);
		DaySchedulePane w5 = new DaySchedulePane(DaySchedulePane.FRIDAY);
		DaySchedulePane w6 = new DaySchedulePane(DaySchedulePane.SATURDAY);
		DaySchedulePane w7 = new DaySchedulePane(DaySchedulePane.SUNDAY);
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
		this.getContentPane().add(w0, gcb);
		gcb.gridx = 1;
		this.getContentPane().add(w1, gcb);
		gcb.gridx = 2;
		this.getContentPane().add(w2, gcb);
		gcb.gridx = 3;
		this.getContentPane().add(w3, gcb);
		gcb.gridx = 4;
		this.getContentPane().add(w4, gcb);
		gcb.gridx = 5;
		this.getContentPane().add(w5, gcb);
		gcb.gridx = 6;
		this.getContentPane().add(w6, gcb);
		gcb.gridx = 7;
		this.getContentPane().add(w7, gcb);
		gcb.gridx = 0;
		gcb.gridwidth = 7;
		gcb.weighty = 1;
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
