package fr.tours.polytech.DI.RFID.enums;

import fr.tours.polytech.DI.RFID.utils.Utils;
import javax.sound.sampled.*;
import java.util.logging.Level;

/**
 * @author COLEAU Victor, COUCHOUD Thomas
 * @version 1.0
 */
public enum Sounds
{
	CARD_CHECKED("cardChecked.wav");
	private static final String srcPach = "sounds/";
	private static final boolean play = false;
	private final String path;

	Sounds(String name)
	{
		this.path = srcPach + name;
	}

	/**
	 * Called to play the sound.
	 */
	public synchronized void playSound()
	{
		if(play)
			new Thread()
			{
				@Override
				public void run()
				{
					try
					{
						final Clip clip = AudioSystem.getClip();
						AudioInputStream inputStream = AudioSystem.getAudioInputStream(Utils.class.getClassLoader().getResource(Sounds.this.path));
						clip.open(inputStream);
						clip.start();
						clip.addLineListener(new LineListener()
						{
							@Override
							public void update(LineEvent event)
							{
								if(event.getType() == LineEvent.Type.STOP)
									clip.close();
							}
						});
					}
					catch(Exception e)
					{
						Utils.logger.log(Level.WARNING, "Couldn't play sound " + Sounds.this.path, e);
					}
				}
			}.start();
	}
}