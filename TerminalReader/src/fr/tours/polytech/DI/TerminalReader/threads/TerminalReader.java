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
package fr.tours.polytech.DI.TerminalReader.threads;

import fr.tours.polytech.DI.TerminalReader.enums.APDUResponse;
import fr.tours.polytech.DI.TerminalReader.enums.Commands;
import fr.tours.polytech.DI.TerminalReader.interfaces.TerminalListener;
import fr.tours.polytech.DI.TerminalReader.objects.RFIDCard;
import javax.smartcardio.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Thread that check the reader if there is one.
 *
 * @author COLEAU Victor, COUCHOUD Thomas
 */
public class TerminalReader implements Runnable
{
	private final ArrayList<TerminalListener> listenersTerminal;
	private String terminalName;
	private final Thread thread;
	private final Logger logger;
	private final TerminalFactory terminalFactory;
	private boolean isPresent;
	private RFIDCard lastCard;

	/**
	 * Constructor.
	 *
	 * @param name The name of the reader we should listen to.
	 */
	public TerminalReader(String name)
	{
		this.listenersTerminal = new ArrayList<>();
		this.terminalName = name;
		this.logger = Logger.getGlobal();
		this.thread = new Thread(this);
		this.thread.setName("TerminalReader");
		this.thread.start();
		terminalFactory = TerminalFactory.getDefault();
	}

	/**
	 * Used to transform an array of bytes to a String like FF-FF-FF...
	 *
	 * @param bytes The array of bytes to transform.
	 * @return The String representing this array.
	 */
	public static String bytesToHex(byte[] bytes)
	{
		char[] hexArray = "0123456789ABCDEF".toCharArray();
		char[] hexChars = new char[bytes.length * 3];
		for(int j = 0; j < bytes.length; j++)
		{
			int v = bytes[j] & 0xFF;
			hexChars[j * 3] = hexArray[v >>> 4];
			hexChars[j * 3 + 1] = hexArray[v & 0x0F];
			hexChars[j * 3 + 2] = '-';
		}
		return new String(hexChars).substring(0, hexChars.length - 1);
	}

	/**
	 * Used to add a {@link TerminalListener}.
	 *
	 * @param listener The listener to add.
	 */
	public void addListener(TerminalListener listener)
	{
		this.listenersTerminal.add(listener);
		if(this.isPresent)
			listener.cardReaderAdded();
		else
			listener.cardReaderRemoved();
		if(this.lastCard != null)
			listener.cardAdded(this.lastCard);
	}

	/**
	 * What is doing the thread.
	 * <p>
	 * Will check if there is a reader available containing the wanted name (will call {@link TerminalListener#cardReaderRemoved()} ()} if a listener is removed or added).
	 * If it is the case it will wait for a card placed, call {@link TerminalListener#cardAdded(RFIDCard)}, wait for the card to be removed then call {@link TerminalListener#cardRemoved()}
	 */
	@Override
	public void run()
	{
		while(!Thread.interrupted())
		{
			if(terminalFactory == null)
				try
				{
					Thread.sleep(500);
				}
				catch(InterruptedException e)
				{
				}
			boolean lastPresent = this.isPresent;
			try
			{
				final CardTerminals terminalList = terminalFactory.terminals();
				CardTerminal cardTerminal = null;
				try
				{
					for(CardTerminal terminal : terminalList.list())
						if(terminal.getName().equals(this.terminalName))
						{
							cardTerminal = terminal;
							this.isPresent = true;
							break;
						}
				}
				catch(CardException exception)
				{
				}
				if(cardTerminal == null)
					this.isPresent = false;
				if(this.isPresent != lastPresent)
				{
					if(this.isPresent)
						logger.log(Level.INFO, "Starting listening terminal " + cardTerminal.getName());
					else
						logger.log(Level.INFO, "Stopped listening");
					for(TerminalListener listener : this.listenersTerminal)
						if(this.isPresent)
							listener.cardReaderAdded();
						else
							listener.cardReaderRemoved();
				}
				if(!this.isPresent)
					continue;
				logger.log(Level.INFO, "Waiting for card...");
				cardTerminal.waitForCardPresent(0);
				logger.log(Level.INFO, "Card detected");
				this.lastCard = getCardInfos(cardTerminal.connect("*"));
				for(TerminalListener listener : this.listenersTerminal)
					listener.cardAdded(this.lastCard);
				cardTerminal.waitForCardAbsent(0);
				this.lastCard = null;
				logger.log(Level.INFO, "Card removed");
				for(TerminalListener listener : this.listenersTerminal)
					listener.cardRemoved();
			}
			catch(Exception exception)
			{
				logger.log(Level.WARNING, "", exception);
			}
		}
	}

	/**
	 * Used to stop the thread.
	 */
	public void stop()
	{
		if(this.thread != null)
			this.thread.interrupt();
	}

	/**
	 * Used to retrieve the card informations when a card is detected.
	 *
	 * @param card The card that have been placed.
	 * @return The card informations.
	 *
	 * @throws CardException If the card can't be read.
	 */
	private RFIDCard getCardInfos(Card card) throws CardException
	{
		CardChannel cardChannel = card.getBasicChannel();
		CommandAPDU command = Commands.UID.getAPDU();
		logger.log(Level.INFO, "Sending command " + Commands.UID);
		ResponseAPDU response = cardChannel.transmit(command);
		logger.log(Level.INFO, "Got response : " + APDUResponse.getErrorString(response.getSW()));
		return new RFIDCard(bytesToHex(card.getATR().getBytes()), bytesToHex(response.getData()), cardChannel);
	}

	/**
	 * Used to get the name of the readers connected.
	 *
	 * @return A list of the names.
	 */
	public ArrayList<String> getReadersName()
	{
		ArrayList<String> list = new ArrayList<>();
		try
		{
			for(CardTerminal terminal : terminalFactory.terminals().list())
				list.add(terminal.getName());
		}
		catch(CardException exception)
		{
		}
		return list;
	}

	/**
	 * Used to set the terminal name.
	 *
	 * @param terminalName The terminal name to set.
	 */
	public void setTerminalName(String terminalName)
	{
		this.terminalName = terminalName;
	}
}
