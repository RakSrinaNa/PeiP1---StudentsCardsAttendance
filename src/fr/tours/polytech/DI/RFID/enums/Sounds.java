package fr.tours.polytech.DI.RFID.enums;

import java.util.logging.Level;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import fr.tours.polytech.DI.RFID.utils.Utils;

public enum Sounds
{
	CARD_CHECKED("cardChecked.wav");
	private final String path;
	private static final String srcPach = "resources/sounds/";

	Sounds(String name)
	{
		this.path = srcPach + name;
	}

	public synchronized void playSound()
	{
		new Thread(new Runnable()
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
						public void update(LineEvent arg0)
						{
							if(arg0.getType() == LineEvent.Type.STOP)
								clip.close();
						}
					});
				}
				catch(Exception e)
				{
					Utils.logger.log(Level.WARNING, "Couldn't play sound " + Sounds.this.path, e);
				}
			}
		}).start();
	}
}