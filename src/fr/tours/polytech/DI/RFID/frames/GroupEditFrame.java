package fr.tours.polytech.DI.RFID.frames;

import fr.tours.polytech.DI.RFID.objects.Group;

import javax.swing.*;

public class GroupEditFrame extends JDialog
{
	public GroupEditFrame(GroupSettingsFrame parent, Group group)
	{
		super(parent);
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setLocationRelativeTo(parent);
		pack();
		this.setVisible(true);
	}
}
