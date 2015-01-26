package fr.tours.polytech.DI.RFID.threads;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CardTerminals;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;
import fr.tours.polytech.DI.RFID.enums.Commands;
import fr.tours.polytech.DI.RFID.interfaces.TerminalListener;
import fr.tours.polytech.DI.RFID.objects.RFIDCard;
import fr.tours.polytech.DI.RFID.utils.Utils;

/**
 * Thread that check the reader if there is one.
 *
 * @author COLEAU Victor, COUCHOUD Thomas
 */
public class TerminalReader implements Runnable
{
	private boolean isPresent;
	private List<TerminalListener> listenersTerminal;
	private String terminalName;
	private Thread thread;
	private RFIDCard lastCard;

	/**
	 * Constructor.
	 *
	 * @param name The name of the reader we should listen to.
	 */
	public TerminalReader(String name)
	{
		this.listenersTerminal = new ArrayList<TerminalListener>();
		this.terminalName = name;
		this.thread = new Thread(this);
		this.thread.setName("TerminalReader");
		this.thread.start();
	}

	/**
	 * Used to add a {@link TerminalListener}.
	 *
	 * @param listener The listener to add.
	 */
	public void addListener(TerminalListener listener)
	{
		this.listenersTerminal.add(listener);
		listener.cardReader(this.isPresent);
		if(this.lastCard != null)
			listener.cardAdded(this.lastCard);
	}

	/**
	 * What is doing the thread.
	 *
	 * Will check if there is a reader available containing the wanted name (will call {@link TerminalListener#cardReader(boolean)} if a listener is removed or added).
	 * If it is the case it will wait for a card placed, call {@link TerminalListener#cardAdded(RFIDCard)}, wait for the card to be removed then call {@link TerminalListener#cardRemoved()}
	 */
	@Override
	public void run()
	{
		final TerminalFactory terminalFactory = TerminalFactory.getDefault();
		while(!this.thread.interrupted())
		{
			boolean lastPresent = this.isPresent;
			try
			{
				final CardTerminals terminalList = terminalFactory.terminals();
				CardTerminal cardTerminal = null;
				try
				{
					for(CardTerminal terminal : terminalList.list())
						if(terminal.getName().contains(this.terminalName))
						{
							cardTerminal = terminal;
							this.isPresent = true;
							break;
						}
				}
				catch(CardException exception)
				{}
				if(cardTerminal == null)
					this.isPresent = false;
				if(this.isPresent != lastPresent)
				{
					if(this.isPresent)
						Utils.logger.log(Level.INFO, "Starting listening terminal " + cardTerminal.getName());
					else
						Utils.logger.log(Level.INFO, "Stopped listening");
					for(TerminalListener listener : this.listenersTerminal)
						listener.cardReader(this.isPresent);
				}
				if(!this.isPresent)
					continue;
				Utils.logger.log(Level.INFO, "Waiting for card...");
				cardTerminal.waitForCardPresent(0);
				Utils.logger.log(Level.INFO, "Card detected");
				this.lastCard = getCardInfos(cardTerminal.connect("*"));
				for(TerminalListener listener : this.listenersTerminal)
					listener.cardAdded(this.lastCard);
				cardTerminal.waitForCardAbsent(0);
				this.lastCard = null;
				Utils.logger.log(Level.INFO, "Card removed");
				for(TerminalListener listener : this.listenersTerminal)
					listener.cardRemoved();
			}
			catch(Exception exception)
			{
				Utils.logger.log(Level.WARNING, "", exception);
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
		CommandAPDU command = new CommandAPDU(Commands.UID.getCommand());
		Utils.logger.log(Level.INFO, "Sending command " + Commands.UID);
		ResponseAPDU response = cardChannel.transmit(command);
		return new RFIDCard(Utils.bytesToHex(card.getATR().getBytes()), Utils.bytesToHex(response.getBytes()));
	}
}
