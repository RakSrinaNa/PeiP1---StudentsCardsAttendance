package fr.tours.polytech.DI.RFID.interfaces;

import fr.tours.polytech.DI.RFID.objects.RFIDCard;
import fr.tours.polytech.DI.RFID.threads.TerminalReader;

/**
 * Interface used by {@link TerminalReader}
 *
 * @author COLEAU Victor, COUCHOUD Thomas
 */
public interface TerminalListener
{
	/**
	 * Called when a card added in the reader.
	 *
	 * @param rfidCard The card placed.
	 */
	void cardAdded(RFIDCard rfidCard);

	/**
	 * Called when a reader is added or removed.
	 *
	 * @param isPresent If there is a reader currently present or not.
	 */
	void cardReader(boolean isPresent);

	/**
	 * Called when a card is removed from the reader.
	 */
	void cardRemoved();
}
