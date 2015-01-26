package fr.tours.polytech.DI.RFID.objects;

/**
 * Class representing a RFID card.
 *
 * @author COLEAU Victor, COUCHOUD Thomas
 */
public class RFIDCard
{
	private String atr;
	private String uid;

	/**
	 * Constructor.
	 *
	 * @param atr The ATR of the card.
	 * @param uid The UID of the card.
	 */
	public RFIDCard(String atr, String uid)
	{
		this.atr = atr;
		this.uid = uid;
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
