package fr.tours.polytech.DI.RFID.frames.components;

import fr.tours.polytech.DI.RFID.frames.MainFrame;
import fr.tours.polytech.DI.RFID.utils.Utils;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class DaySchedulePane extends JPanel
{
	public final static int HOURS = -1, MONDAY = 0, TUESDAY = 1, WEDNESDAY = 2, THURSDAY = 3, FRIDAY = 4, SATURDAY = 5, SUNDAY = 6;
	private int day;

	public DaySchedulePane(int day)
	{
		super();
		this.day = day;
		this.setLayout(new GridBagLayout());
		this.setBorder(BorderFactory.createLineBorder(Color.black));
		this.setBackground(MainFrame.backColor);
		/**************************************************************************/
		JLabel dayLabel = new JLabel(getDayLabel(day));
		dayLabel.setHorizontalAlignment(JLabel.CENTER);
		dayLabel.setBackground(MainFrame.backColor);
		/**************************************************************************/
		int line = 0;
		GridBagConstraints gcb = new GridBagConstraints();
		gcb.anchor = GridBagConstraints.NORTH;
		gcb.fill = GridBagConstraints.HORIZONTAL;
		gcb.weighty = 1;
		gcb.weightx = 1;
		gcb.gridheight = 1;
		gcb.gridwidth = 1;
		gcb.gridx = 0;
		gcb.gridy = line++;
		this.add(dayLabel, gcb);
		gcb.weighty = 100;
		for(int i = 0; i < 48; i++)
		{
			if(i % 4 == 0)
			{
				gcb.gridy = line++;
				this.add(new JSeparator(SwingConstants.HORIZONTAL), gcb);
			}
			gcb.gridy = line++;
			this.add(day == HOURS ? getSample(i) : getDayCase(i, false), gcb);
		}
	}

	private JPanel getSample(int i)
	{
		int h = 8 + i / 4;
		JPanel pane = new JPanel();
		pane.setBackground(MainFrame.backColor);
		JLabel label = new JLabel();
		//pane.add(label);
		if(i % 4 == 0)
			label.setText(h + "h - " + h + "h15");
		else if(i % 4 == 2)
			label.setText(h + "h30 - " + h + "h45");
		//Font labelFont = label.getFont();
		//label.setFont(new Font(labelFont.getName(), Font.PLAIN, 5));
		return pane;
	}

	private JPanel getDayCase(int i, boolean checked)
	{
		JPanel pane = new JPanel();
		pane.setBackground(checked ? Color.ORANGE : Color.WHITE);
		pane.addMouseListener(new MouseListener()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
			}

			@Override
			public void mousePressed(MouseEvent e)
			{
				pane.setBackground(pane.getBackground() == Color.WHITE ? Color.ORANGE : Color.WHITE);
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
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
		return pane;
	}

	private String getDayLabel(int day)
	{
		switch(day)
		{
			case MONDAY:
				return Utils.resourceBundle.getString("day_monday");
			case TUESDAY:
				return Utils.resourceBundle.getString("day_tuesday");
			case WEDNESDAY:
				return Utils.resourceBundle.getString("day_wednesday");
			case THURSDAY:
				return Utils.resourceBundle.getString("day_thursday");
			case FRIDAY:
				return Utils.resourceBundle.getString("day_friday");
			case SATURDAY:
				return Utils.resourceBundle.getString("day_saturday");
			case SUNDAY:
				return Utils.resourceBundle.getString("day_sunday");
		}
		return Utils.resourceBundle.getString("day_hour");
	}
}
