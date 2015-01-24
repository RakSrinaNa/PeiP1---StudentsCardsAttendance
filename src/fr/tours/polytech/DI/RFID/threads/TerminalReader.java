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

public class TerminalReader implements Runnable
{
	private boolean isPresent;
	private List<TerminalListener> listenersTerminal;
	private String terminalName;
	private Thread thread;
	private RFIDCard lastCard;

	public TerminalReader(String name)
	{
		this.listenersTerminal = new ArrayList<TerminalListener>();
		this.terminalName = name;
		this.thread = new Thread(this);
		this.thread.setName("TerminalReader");
		this.thread.start();
	}

	public void addListener(TerminalListener listener)
	{
		this.listenersTerminal.add(listener);
		listener.cardReader(this.isPresent);
		if(this.lastCard != null)
			listener.cardAdded(this.lastCard);
	}

	@Override
	public void run()
	{
		final TerminalFactory factory = TerminalFactory.getDefault();
		while(!this.thread.interrupted())
		{
			boolean lastPresent = this.isPresent;
			try
			{
				final CardTerminals terminalList = factory.terminals();
				CardTerminal terminal = null;
				try
				{
					for(CardTerminal term : terminalList.list())
						if(term.getName().contains(this.terminalName))
						{
							terminal = term;
							this.isPresent = true;
							break;
						}
				}
				catch(CardException e1)
				{}
				if(terminal == null)
					this.isPresent = false;
				if(this.isPresent != lastPresent)
				{
					if(this.isPresent)
						Utils.logger.log(Level.INFO, "Starting listening terminal " + terminal.getName());
					else
						Utils.logger.log(Level.INFO, "Stopped listening");
					for(TerminalListener listener : this.listenersTerminal)
						listener.cardReader(this.isPresent);
				}
				if(!this.isPresent)
					continue;
				Utils.logger.log(Level.INFO, "Waiting for card...");
				terminal.waitForCardPresent(0);
				Utils.logger.log(Level.INFO, "Card detected");
				this.lastCard = getCardInfos(terminal.connect("*"));
				for(TerminalListener listener : this.listenersTerminal)
					listener.cardAdded(this.lastCard);
				terminal.waitForCardAbsent(0);
				this.lastCard = null;
				Utils.logger.log(Level.INFO, "Card removed");
				for(TerminalListener listener : this.listenersTerminal)
					listener.cardRemoved();
			}
			catch(Exception e1)
			{
				e1.printStackTrace();
			}
		}
	}

	public void stop()
	{
		if(this.thread != null)
			this.thread.interrupt();
	}

	private RFIDCard getCardInfos(Card card) throws CardException
	{
		CardChannel cardChannel = card.getBasicChannel();
		CommandAPDU command = new CommandAPDU(Commands.UID.getCommand());
		Utils.logger.log(Level.INFO, "Sending command " + Commands.UID);
		ResponseAPDU response = cardChannel.transmit(command);
		return new RFIDCard(Utils.bytesToHex(card.getATR().getBytes()), Utils.bytesToHex(response.getBytes()));
	}
}
