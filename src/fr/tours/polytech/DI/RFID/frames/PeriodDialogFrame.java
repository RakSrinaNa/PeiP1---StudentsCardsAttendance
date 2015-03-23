package fr.tours.polytech.DI.RFID.frames;

import fr.tours.polytech.DI.RFID.objects.Period;
import fr.tours.polytech.DI.RFID.utils.Utils;
import javax.swing.*;
import java.awt.*;

public class PeriodDialogFrame extends JDialog
{
	private final JTextArea enter;
	private final JCheckBox w1, w2, w3, w4, w5, w6, w7;
	private Period result;

	public PeriodDialogFrame(GroupEditFrame parent, String title, String info, Period period)
	{
		super(parent);
		this.setTitle(title);
		this.setIconImages(Utils.icons);
		this.setTitle(title);
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.getContentPane().setLayout(new GridBagLayout());
		/**************************************************************************/
		JLabel textInfo = new JLabel(info);
		textInfo.setHorizontalAlignment(JLabel.CENTER);
		enter = new JTextArea();
		enter.setWrapStyleWord(true);
		enter.setLineWrap(true);
		JButton valid = new JButton(Utils.resourceBundle.getString("validate"));
		valid.addActionListener(e ->
		{
			setVisible(false);
			dispose();
		});
		w1 = new JCheckBox(Utils.resourceBundle.getString("day_monday"));
		w2 = new JCheckBox(Utils.resourceBundle.getString("day_tuesday"));
		w3 = new JCheckBox(Utils.resourceBundle.getString("day_wednesday"));
		w4 = new JCheckBox(Utils.resourceBundle.getString("day_thursday"));
		w5 = new JCheckBox(Utils.resourceBundle.getString("day_friday"));
		w6 = new JCheckBox(Utils.resourceBundle.getString("day_saturday"));
		w7 = new JCheckBox(Utils.resourceBundle.getString("day_sunday"));
		w1.setBackground(MainFrame.backColor);
		w2.setBackground(MainFrame.backColor);
		w3.setBackground(MainFrame.backColor);
		w4.setBackground(MainFrame.backColor);
		w5.setBackground(MainFrame.backColor);
		w6.setBackground(MainFrame.backColor);
		w7.setBackground(MainFrame.backColor);
		if(period != null)
		{
			enter.setText(period.getRawTimeInterval().replaceAll(" ", ""));
			w1.setSelected(period.isDaySet(Period.MONDAY));
			w2.setSelected(period.isDaySet(Period.TUESDAY));
			w3.setSelected(period.isDaySet(Period.WEDNESDAY));
			w4.setSelected(period.isDaySet(Period.THURSDAY));
			w5.setSelected(period.isDaySet(Period.FRIDAY));
			w6.setSelected(period.isDaySet(Period.SATURDAY));
			w7.setSelected(period.isDaySet(Period.SUNDAY));
		}
		/**************************************************************************/
		int line = 0;
		GridBagConstraints gcb = new GridBagConstraints();
		getContentPane().setLayout(new GridBagLayout());
		gcb.anchor = GridBagConstraints.PAGE_START;
		gcb.fill = GridBagConstraints.BOTH;
		gcb.weighty = 100;
		gcb.weightx = 1;
		gcb.gridheight = 1;
		gcb.gridwidth = 7;
		gcb.gridx = 0;
		gcb.gridy = line++;
		this.getContentPane().add(textInfo, gcb);
		gcb.gridy = line++;
		this.getContentPane().add(enter, gcb);
		gcb.gridwidth = 1;
		gcb.gridy = line++;
		this.getContentPane().add(w1, gcb);
		gcb.gridx = 1;
		this.getContentPane().add(w2, gcb);
		gcb.gridx = 2;
		this.getContentPane().add(w3, gcb);
		gcb.gridx = 3;
		this.getContentPane().add(w4, gcb);
		gcb.gridx = 4;
		this.getContentPane().add(w5, gcb);
		gcb.gridx = 5;
		this.getContentPane().add(w6, gcb);
		gcb.gridx = 6;
		this.getContentPane().add(w7, gcb);
		gcb.gridy = line++;
		gcb.gridwidth = 7;
		gcb.weighty = 1;
		gcb.gridx = 0;
		this.getContentPane().add(valid, gcb);
		this.getContentPane().setBackground(MainFrame.backColor);
		pack();
		this.setLocationRelativeTo(parent);
	}

	public Period showDialog()
	{
		setVisible(true);
		return new Period(getDay(), enter.getText());
	}

	private int getDay()
	{
		return (w1.isSelected() ? Period.MONDAY : 0) + (w2.isSelected() ? Period.TUESDAY : 0) + (w3.isSelected() ? Period.WEDNESDAY : 0) + (w4.isSelected() ? Period.THURSDAY : 0) + (w5.isSelected() ? Period.FRIDAY : 0) + (w6.isSelected() ? Period.SATURDAY : 0) + (w7.isSelected() ? Period.SUNDAY : 0);
	}
}
