package fr.tours.polytech.DI.RFID.objects;

public class RFIDCard
{
	private String atr;
	private String uid;

	public RFIDCard(String atr, String uid)
	{
		this.atr = atr;
		this.uid = uid;
	}

	public String getAtr()
	{
		return this.atr;
	}

	public String getUid()
	{
		return this.uid;
	}

	@Override
	public String toString()
	{
		return "ATR: " + this.atr + " - UID: " + this.uid;
	}
}
