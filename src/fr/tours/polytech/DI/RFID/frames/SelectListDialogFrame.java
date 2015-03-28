package fr.tours.polytech.DI.RFID.frames;

import fr.tours.polytech.DI.RFID.utils.Utils;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

/**
 * The frame to select Students to add to a group.
 *
 * @author COLEAU Victor, COUCHOUD Thomas
 */
public class SelectListDialogFrame<T extends Comparable<T>> extends JDialog
{
	private ArrayList<T> result;

	/**
	 * Constructor.
	 *
	 * @param parent The parent frame.
	 * @param title The title of the frame.
	 * @param message The message to show.
	 * @param elements The elements that will populate the list.
	 */
	public SelectListDialogFrame(Window parent, String title, String message, ArrayList<T> elements)
	{
		super(parent);
		Collections.sort(elements);
		Color color = MainFrame.backColor;
		this.setTitle(title);
		this.setIconImages(Utils.icons);
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.getContentPane().setLayout(new GridBagLayout());
		/**************************************************************************/
		JList list = new JList(elements.toArray());
		list.setSelectionModel(new DefaultListSelectionModel()
		{
			@Override
			public void setSelectionInterval(int index0, int index1)
			{
				if(super.isSelectedIndex(index0))
					super.removeSelectionInterval(index0, index1);
				else
					super.addSelectionInterval(index0, index1);
			}
		});
		JScrollPane scrollPane = new JScrollPane(list);
		scrollPane.setAutoscrolls(false);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		/**************************************************************************/
		JLabel messageLabel = new JLabel(message);
		messageLabel.setHorizontalAlignment(JLabel.CENTER);
		messageLabel.setBackground(color);
		JButton valid = new JButton(Utils.resourceBundle.getString("validate"));
		valid.addActionListener(e -> {
			result = new ArrayList<>();
			for(int i : list.getSelectedIndices())
				result.add(elements.get(i));
			setVisible(false);
			dispose();
		});
		/**************************************************************************/
		int line = 0;
		GridBagConstraints gcb = new GridBagConstraints();
		getContentPane().setLayout(new GridBagLayout());
		gcb.anchor = GridBagConstraints.PAGE_START;
		gcb.fill = GridBagConstraints.BOTH;
		gcb.insets = new Insets(2, 10, 2, 10);
		gcb.weighty = 1;
		gcb.weightx = 1;
		gcb.gridheight = 1;
		gcb.gridwidth = 1;
		gcb.gridx = 0;
		gcb.gridy = line++;
		this.getContentPane().add(messageLabel, gcb);
		gcb.insets = new Insets(0, 0, 0, 0);
		gcb.gridy = line++;
		gcb.weighty = 100;
		this.getContentPane().add(scrollPane, gcb);
		gcb.gridy = line++;
		gcb.weighty = 1;
		this.getContentPane().add(valid, gcb);
		this.getContentPane().setBackground(MainFrame.backColor);
		pack();
		this.setLocationRelativeTo(parent);
	}

	/**
	 * USed to show the dialog frame.
	 *
	 * @return A list of the selected elements.
	 */
	public ArrayList<T> showDialog()
	{
		setVisible(true);
		return result == null ? new ArrayList<>() : result;
	}
}
