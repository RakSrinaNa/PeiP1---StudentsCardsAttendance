/**
 * ****************************************************************************
 * Copyright (c) 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p>
 * Contributors:
 * IBM Corporation - initial API and implementation
 * *****************************************************************************
 */
package fr.tours.polytech.DI.RFID.objects;

import javax.smartcardio.CardChannel;
/**
 * Class representing a RFID card.
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
	 * @param atr The ATR of the card.
	 * @param uid The UID of the card.
	 * @param cardChannel The card channel of the card.
	 * @param cardChannel
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