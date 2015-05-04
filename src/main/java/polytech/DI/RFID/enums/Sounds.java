package polytech.DI.RFID.enums;

import polytech.DI.RFID.utils.Utils;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import java.util.logging.Level;

/**
 * @author COLEAU Victor, COUCHOUD Thomas
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
			new Thread(() -> {
				try
				{
					final Clip clip = AudioSystem.getClip();
					AudioInputStream inputStream = AudioSystem.getAudioInputStream(Utils.class.getClassLoader().getResource(Sounds.this.path));
					clip.open(inputStream);
					clip.start();
					clip.addLineListener(arg0 -> {
						if(arg0.getType() == LineEvent.Type.STOP)
							clip.close();
					});
				}
				catch(Exception e)
				{
					Utils.logger.log(Level.WARNING, "Couldn't play sound " + Sounds.this.path, e);
				}
			}).start();
	}
}