package fr.tours.polytech.DI.RFID;

import fr.tours.polytech.DI.RFID.utils.Utils;
import java.io.IOException;

/**
 * Program used to replace the attendance sheet with the student cards.
 *
 * @author COLEAU Victor, COUCHOUD Thomas
 * @version 1.0
 */
public class Main
{
	/**
	 * The main function, launched on startup.
	 *
	 * @param args Arguments for the program - Not used.
	 * @throws IOException If files couldn't be read.
	 * @throws SecurityException If the database connection can't be made.
	 * @see Utils#init()
	 */
	public static void main(String[] args) throws SecurityException, IOException
	{
		Utils.init(args);
	}
}
