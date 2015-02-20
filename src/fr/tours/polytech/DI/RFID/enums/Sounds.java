package fr.tours.polytech.DI.RFID.enums;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;

import fr.tours.polytech.DI.RFID.utils.Utils;

public enum Sounds
{
	CARD_CHECKED("cardChecked.wav");
	private final String path;
	private static final String srcPach = "sounds/";

	Sounds(String name)
	{
		this.path = srcPach + name;
	}

	public synchronized void playSound()
	{
		new Thread(() -> {
			try
			{
				final Clip clip = AudioSystem.getClip();
				AudioInputStream inputStream = AudioSystem.getAudioInputStream(Utils.class.getClassLoader().getResource(Sounds.this.path));
				clip.open(inputStream);
				clip.start();
				clip.addLineListener(arg0 ->
				{
					if(arg0.getType() == LineEvent.Type.STOP)
						clip.close();
				});
			}
			catch(Exception e)
			{
				//Utils.logger.log(Level.WARNING, "Couldn't play sound " + Sounds.this.path, e);
			}
		}).start();
	}
}