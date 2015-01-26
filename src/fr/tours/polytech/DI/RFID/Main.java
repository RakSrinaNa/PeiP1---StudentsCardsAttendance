package fr.tours.polytech.DI.RFID;

import java.io.IOException;
import fr.tours.polytech.DI.RFID.utils.Utils;

/**
 * Program used to replace the attendance sheet with the student cards.
 *
 * @author COLEAU Victor, COUCHOUD Thomas
 * @version 1.0
 * @since 1.8
 */
public class Main
{
	/**
	 * The main function, launched on startup.
	 *
	 * @param args Arguments for the program - Not used.
	 *
	 * @throws SecurityException If the Student.csv file can't be read.
	 * @throws IOException If the Student.csv file can't be read.
	 *
	 * @see java.util.logging.FileHandler.FileHandler#FileHandler(String, boolean)
	 */
	public static void main(String[] args) throws SecurityException, IOException
	{
		Utils.init();
	}
}
