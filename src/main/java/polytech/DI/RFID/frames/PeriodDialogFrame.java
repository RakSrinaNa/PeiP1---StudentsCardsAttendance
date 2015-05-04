package polytech.DI.RFID.frames;

import polytech.DI.RFID.frames.components.TextFieldLimitNumbersDocument;
import polytech.DI.RFID.objects.Period;
import polytech.DI.RFID.utils.Utils;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * The frame for editting Periods.
 *
 * @author COLEAU Victor, COUCHOUD Thomas
 */
public class PeriodDialogFrame extends JDialog
{
	private static final long serialVersionUID = 5964100179648537443L;
	private final JTextArea h1, h2, m1, m2;
	private final JCheckBox w1, w2, w3, w4, w5, w6, w7;
	private Period result;

	/**
	 * Constructor.
	 *
	 * @param parent The parent frame.
	 * @param title The title of the frame.
	 * @param period The period to edit, null if creating a new one.
	 */
	public PeriodDialogFrame(GroupEditFrame parent, String title, Period period)
	{
		super(parent);
		this.setTitle(title);
		this.setIconImages(Utils.icons);
		this.setTitle(title);
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.getContentPane().setLayout(new GridBagLayout());
		/**************************************************************************/
		JLabel t1 = new JLabel("H");
		t1.setHorizontalAlignment(JLabel.CENTER);
		JLabel t2 = new JLabel("-");
		t2.setHorizontalAlignment(JLabel.CENTER);
		JLabel t3 = new JLabel("H");
		t3.setHorizontalAlignment(JLabel.CENTER);
		h1 = new JTextArea();
		h1.setDocument(new TextFieldLimitNumbersDocument(2));
		h1.setWrapStyleWord(true);
		h1.setLineWrap(true);
		h1.addKeyListener(new KeyListener()
		{
			@Override
			public void keyTyped(KeyEvent e)
			{

			}

			@Override
			public void keyPressed(KeyEvent e)
			{
				if(e.getKeyCode() == KeyEvent.VK_TAB)
				{
					if(e.getModifiers() > 0)
						h1.transferFocusBackward();
					else
						h1.transferFocus();
					e.consume();
				}
			}

			@Override
			public void keyReleased(KeyEvent e)
			{

			}
		});
		h2 = new JTextArea();
		h2.setDocument(new TextFieldLimitNumbersDocument(2));
		h2.setWrapStyleWord(true);
		h2.setLineWrap(true);
		h2.addKeyListener(new KeyListener()
		{
			@Override
			public void keyTyped(KeyEvent e)
			{

			}

			@Override
			public void keyPressed(KeyEvent e)
			{
				if(e.getKeyCode() == KeyEvent.VK_TAB)
				{
					if(e.getModifiers() > 0)
						h2.transferFocusBackward();
					else
						h2.transferFocus();
					e.consume();
				}
			}

			@Override
			public void keyReleased(KeyEvent e)
			{

			}
		});
		m1 = new JTextArea();
		m1.setDocument(new TextFieldLimitNumbersDocument(2));
		m1.setWrapStyleWord(true);
		m1.setLineWrap(true);
		m1.addKeyListener(new KeyListener()
		{
			@Override
			public void keyTyped(KeyEvent e)
			{

			}

			@Override
			public void keyPressed(KeyEvent e)
			{
				if(e.getKeyCode() == KeyEvent.VK_TAB)
				{
					if(e.getModifiers() > 0)
						m1.transferFocusBackward();
					else
						m1.transferFocus();
					e.consume();
				}
			}

			@Override
			public void keyReleased(KeyEvent e)
			{

			}
		});
		m2 = new JTextArea();
		m2.setDocument(new TextFieldLimitNumbersDocument(2));
		m2.setWrapStyleWord(true);
		m2.setLineWrap(true);
		m2.addKeyListener(new KeyListener()
		{
			@Override
			public void keyTyped(KeyEvent e)
			{

			}

			@Override
			public void keyPressed(KeyEvent e)
			{
				if(e.getKeyCode() == KeyEvent.VK_TAB)
				{
					if(e.getModifiers() > 0)
						m2.transferFocusBackward();
					else
						m2.transferFocus();
					e.consume();
				}
			}

			@Override
			public void keyReleased(KeyEvent e)
			{

			}
		});
		JButton valid = new JButton(Utils.resourceBundle.getString("validate"));
		valid.addActionListener(e -> {
			try
			{
				result = new Period(getDay(), getPeriod());
				setVisible(false);
				dispose();
			}
			catch(IllegalArgumentException e1)
			{
				JOptionPane.showMessageDialog(this, Utils.resourceBundle.getString("wrong_period"), Utils.resourceBundle.getString("error").toUpperCase(), JOptionPane.ERROR_MESSAGE);
			}
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
			h1.setText("" + period.getStartingHour());
			h2.setText("" + period.getEndingHour());
			m1.setText("" + period.getStartingMinute());
			m2.setText("" + period.getEndingMinute());
			w1.setSelected(period.isDaySet(Period.MONDAY));
			w2.setSelected(period.isDaySet(Period.TUESDAY));
			w3.setSelected(period.isDaySet(Period.WEDNESDAY));
			w4.setSelected(period.isDaySet(Period.THURSDAY));
			w5.setSelected(period.isDaySet(Period.FRIDAY));
			w6.setSelected(period.isDaySet(Period.SATURDAY));
			w7.setSelected(period.isDaySet(Period.SUNDAY));
		}
		/**************************************************************************/
		JPanel periodPane = new JPanel(new GridBagLayout());
		periodPane.setBackground(MainFrame.backColor);
		int line = 0;
		GridBagConstraints gcb = new GridBagConstraints();
		getContentPane().setLayout(new GridBagLayout());
		gcb.anchor = GridBagConstraints.PAGE_START;
		gcb.fill = GridBagConstraints.BOTH;
		gcb.insets = new Insets(10, 10, 10, 10);
		gcb.weighty = 100;
		gcb.weightx = 100;
		gcb.gridheight = 1;
		gcb.gridwidth = 1;
		gcb.gridx = 0;
		gcb.gridy = line++;
		periodPane.add(h1, gcb);
		gcb.gridx = 1;
		gcb.weightx = 1;
		periodPane.add(t1, gcb);
		gcb.gridx = 2;
		gcb.weightx = 100;
		periodPane.add(m1, gcb);
		gcb.gridx = 3;
		gcb.weightx = 1;
		periodPane.add(t2, gcb);
		gcb.gridx = 4;
		gcb.weightx = 100;
		periodPane.add(h2, gcb);
		gcb.gridx = 5;
		gcb.weightx = 1;
		periodPane.add(t3, gcb);
		gcb.gridx = 6;
		gcb.weightx = 100;
		periodPane.add(m2, gcb);
		/**************************************************************************/
		line = 0;
		gcb = new GridBagConstraints();
		getContentPane().setLayout(new GridBagLayout());
		gcb.anchor = GridBagConstraints.PAGE_START;
		gcb.fill = GridBagConstraints.BOTH;
		gcb.weighty = 100;
		gcb.weightx = 100;
		gcb.gridheight = 1;
		gcb.gridwidth = 7;
		gcb.gridx = 0;
		gcb.gridy = line++;
		this.getContentPane().add(periodPane, gcb);
		gcb.gridwidth = 1;
		gcb.gridx = 0;
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

	/**
	 * USed to show the dialog frame.
	 *
	 * @return The configured Period.
	 */
	public Period showDialog()
	{
		setVisible(true);
		return result;
	}

	/**
	 * Used to get the day that is set.
	 *
	 * @return The day.
	 */
	private int getDay()
	{
		return (w1.isSelected() ? Period.MONDAY : 0) + (w2.isSelected() ? Period.TUESDAY : 0) + (w3.isSelected() ? Period.WEDNESDAY : 0) + (w4.isSelected() ? Period.THURSDAY : 0) + (w5.isSelected() ? Period.FRIDAY : 0) + (w6.isSelected() ? Period.SATURDAY : 0) + (w7.isSelected() ? Period.SUNDAY : 0);
	}

	/**
	 * Used to construct the configured Period.
	 *
	 * @return The Period.
	 */
	public String getPeriod()
	{
		int h1 = 0;
		int h2 = 0;
		int m1 = 0;
		int m2 = 0;
		try
		{
			h1 = Integer.parseInt(this.h1.getText());
		}
		catch(Exception e)
		{
		}
		try
		{
			h2 = Integer.parseInt(this.h2.getText());
		}
		catch(Exception e)
		{
		}
		try
		{
			m1 = Integer.parseInt(this.m1.getText());
		}
		catch(Exception e)
		{
		}
		try
		{
			m2 = Integer.parseInt(this.m2.getText());
		}
		catch(Exception e)
		{
		}
		return h1 + "H" + m1 + "-" + h2 + "H" + m2;
	}
}
