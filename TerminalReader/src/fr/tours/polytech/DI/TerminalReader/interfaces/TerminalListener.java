package fr.tours.polytech.DI.TerminalReader.interfaces;

import fr.tours.polytech.DI.TerminalReader.objects.RFIDCard;
import fr.tours.polytech.DI.TerminalReader.threads.TerminalReader;

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
	 * Called when a reader is added.
	 */
	void cardReaderAdded();

	/**
	 * Called when a reader is removed.
	 */
	void cardReaderRemoved();

	/**
	 * Called when a card is removed from the reader.
	 */
	void cardRemoved();
}
