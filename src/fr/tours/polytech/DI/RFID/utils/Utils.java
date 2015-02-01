/*******************************************************************************
 * Copyright (c) 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package fr.tours.polytech.DI.RFID.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import fr.tours.polytech.DI.RFID.frames.MainFrame;
import fr.tours.polytech.DI.RFID.objects.Student;
import fr.tours.polytech.DI.RFID.threads.TerminalReader;

/**
 * Utility class, contain useful methods for the application.
 *
 * @author COLEAU Victor, COUCHOUD Thomas
 */
public class Utils
{
	private static TerminalReader terminalReader;
	private static MainFrame mainFrame;
	public static Logger logger;
	public static Configuration config;
	public static SQLManager sql;

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
	 * Call when we need to exit the program.
	 *
	 * @param exitStaus The parameter given to {@link System#exit(int)}
	 *
	 * @see System#exit(int)
	 */
	public static void exit(int exitStaus)
	{
		mainFrame.exit();
		terminalReader.stop();
		config.close();
		System.exit(exitStaus);
	}

	/**
	 * Call when the program is starting. Initalize some variables like
	 * configuration, logger, reader and SQL connection.
	 *
	 * @throws SecurityException If the Student.csv file can't be read.
	 * @throws IOException If the Student.csv file can't be read.
	 *
	 * @see java.util.logging.FileHandler.FileHandler#FileHandler(String, boolean)
	 */
	public static void init() throws SecurityException, IOException
	{
		logger = Logger.getLogger("RFID");
		config = new Configuration();
		terminalReader = new TerminalReader("Contactless");
		sql = new SQLManager("127.0.0.1", 3306, "rfid", "rfid", "polytechDI26");
		mainFrame = new MainFrame(new File(".", "Students.csv"));
		terminalReader.addListener(mainFrame);
	}

	/**
	 * Used to log a check in the CSV file.
	 *
	 * @param student The student that checked.
	 *
	 * @throws IOException If file can't be opened or wrote.
	 */
	public static void logCheck(Student student) throws IOException
	{
		FileWriter fileWriter = null;
		BufferedWriter bufferedWriter = null;
		PrintWriter printWriter = null;
		try
		{
			DateFormat dateFormat = new SimpleDateFormat("[zzz] dd/MM/yyyy HH:mm:ss");
			Date date = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			File file = new File("." + File.separator + "checked_" + calendar.get(Calendar.YEAR) + "_" + (calendar.get(Calendar.MONTH) + 1) + "_" + (calendar.get(Calendar.WEEK_OF_MONTH) + 1) + ".csv");
			if(!file.exists())
			{
				file.getParentFile().mkdirs();
				try
				{
					file.createNewFile();
				}
				catch(IOException exception)
				{}
			}
			fileWriter = new FileWriter(file, true);
			bufferedWriter = new BufferedWriter(fileWriter);
			printWriter = new PrintWriter(bufferedWriter);
			printWriter.print(dateFormat.format(date) + ";" + student.getName() + ";" + student.getUid().replaceAll("-", "") + "\n");
		}
		catch(Exception exception)
		{
			Utils.logger.log(Level.SEVERE, "Cannot write checked file", exception);
		}
		if(printWriter != null)
			try
			{
				printWriter.close();
			}
			catch(Exception exception)
			{}
		if(bufferedWriter != null)
			try
			{
				bufferedWriter.close();
			}
			catch(Exception exception)
			{}
		if(fileWriter != null)
			try
			{
				fileWriter.close();
			}
			catch(Exception exception)
			{}
	}

	/**
	 * Used to remove duplicates in an ArrayList.
	 *
	 * @param list The list where to remove duplicates.
	 * @return The list without duplicates.
	 */
	public static <T> ArrayList<T> removeDuplicates(ArrayList<T> list)
	{
		Set<T> setItems = new LinkedHashSet<T>(list);
		list.clear();
		list.addAll(setItems);
		return list;
	}

	/**
	 * Used to write the student list that need to check to the CSV file.
	 *
	 * @param students The students list.
	 * @param file The CSV file.
	 *
	 * @throws IOException If the file can't be modified.
	 */
	public static void writeStudentsToFile(List<Student> students, File file) throws IOException
	{
		file.delete();
		if(!file.exists())
		{
			file.getParentFile().mkdirs();
			try
			{
				file.createNewFile();
			}
			catch(IOException exception)
			{}
		}
		FileWriter fileWriter = new FileWriter(file, true);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		PrintWriter printWriter = new PrintWriter(bufferedWriter);
		for(Student student : students)
			printWriter.print(student.getName() + "\n");
		printWriter.close();
		bufferedWriter.close();
		fileWriter.close();
	}
}
