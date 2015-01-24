package fr.tours.polytech.DI.RFID.interfaces;

import fr.tours.polytech.DI.RFID.objects.RFIDCard;

public interface TerminalListener
{
	void cardAdded(RFIDCard rfidCard);

	void cardReader(boolean isPresent);

	void cardRemoved();
}
