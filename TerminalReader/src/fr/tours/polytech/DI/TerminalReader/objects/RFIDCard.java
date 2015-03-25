package fr.tours.polytech.DI.TerminalReader.objects;

import javax.smartcardio.CardChannel;

/**
 * Class representing a TerminalReader card.
 *
 * @author COLEAU Victor, COUCHOUD Thomas
 */
public class RFIDCard
{
	private final String atr;
	private final String uid;
	private final CardChannel cardChannel;

	/**
	 * Constructor.
	 *
	 * @param atr The ATR of the card.
	 * @param uid The UID of the card.
	 * @param cardChannel The card channel of the card.
	 */
	public RFIDCard(String atr, String uid, CardChannel cardChannel)
	{
		this.atr = atr;
		this.uid = uid;
		this.cardChannel = cardChannel;
	}

	/**
	 * Used to get the card channel.
	 *
	 * @return The card channel.
	 */
	public CardChannel getCardChannel()
	{
		return this.cardChannel;
	}

	/**
	 * Used to get the ATR of the card.
	 *
	 * @return The ATR.
	 */
	public String getAtr()
	{
		return this.atr;
	}

	/**
	 * Used to get the UID of the card.
	 *
	 * @return The UID.
	 */
	public String getUid()
	{
		return this.uid;
	}

	/**
	 * Used to get a String representing the object. Will be formatted as <b>ATR: <i>atr</i> - UID: <i>uid</i></b>
	 */
	@Override
	public String toString()
	{
		return "ATR: " + this.atr + " - UID: " + this.uid;
	}
}
