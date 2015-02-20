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

import fr.tours.polytech.DI.RFID.frames.MainFrame;
import fr.tours.polytech.DI.RFID.objects.Group;
import fr.tours.polytech.DI.RFID.objects.Period;
import fr.tours.polytech.DI.RFID.objects.Student;
import fr.tours.polytech.DI.RFID.threads.TerminalReader;

import javax.swing.*;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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
	public static SQLManager sql;
	public static ArrayList<Student> students;
	public static ArrayList<Group> groups;
	public static boolean logAll, addNewCards;

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
		Group.saveGroups(Utils.groups);
		terminalReader.stop();
		System.exit(exitStaus);
	}

	/**
	 * Call when the program is starting. Initalize some variables like
	 * groups, students, logger, reader and SQL connection.
	 *
	 * @throws SecurityException If the Student.csv file can't be read.
	 * @throws IOException If the Student.csv file can't be read.
	 *
	 * @see java.util.logging.FileHandler#FileHandler(String, boolean)
	 */
	public static void init() throws SecurityException, IOException
	{
		logger = Logger.getLogger("RFID");
		logAll = true;
		addNewCards = true;
		terminalReader = new TerminalReader("Contactless");
		sql = new SQLManager("127.0.0.1", 3306, "rfid", "rfid", "PolytechDI26");
		if(!sql.isConnected())
		{
			JOptionPane.showMessageDialog(null, "Can't connect to database!", "ERROR", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		students = Utils.sql.getAllStudents();
		groups = Group.loadGroups();
		mainFrame = new MainFrame();
		terminalReader.addListener(mainFrame);
	}

	/**
	 * Used to get a student by his name.
	 *
	 * @param name The name of the student.
	 * @param checkDB Should check him in the database if we don't know him?
	 * @return The student or null if unknown.
	 */
	public static Student getStudentByName(String name, boolean checkDB)
	{
		for(Student student : students)
			if(student != null && student.equals(name))
				return student;
		return checkDB ? Utils.sql.getStudentByName(name) : null;
	}

	/**
	 * Used to get a student by his UID.
	 *
	 * @param uid The student's card UID.
	 * @param checkDB Should check him in the database if we don't know him?
	 * @return The student or null if unknown.
	 */
	public static Student getStudentByUID(String uid, boolean checkDB)
	{
		for(Student student : students)
			if(student != null && student.getUid().equals(uid.replaceAll("-", "")))
				return student;
		return checkDB ? Utils.sql.getStudentByUID(uid.replaceAll("-", "")) : null;
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
		if(!logAll)
			return;
		FileWriter fileWriter = null;
		BufferedWriter bufferedWriter = null;
		PrintWriter printWriter = null;
		try
		{
			DateFormat dateFormat = new SimpleDateFormat("[zzz] dd/MM/yyyy HH:mm:ss");
			Date date = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			File file = new File("." + File.separator + "log" + File.separator + "checked_" + calendar.get(Calendar.YEAR) + ".csv");
			if(!file.exists())
			{
				file.getParentFile().mkdirs();
				try
				{
					file.createNewFile();
				}
				catch(IOException exception)
				{
					exception.printStackTrace();
				}
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
	 * Used to log all absents students in a CSV file with their name.
	 *
	 * @param period The period when the students haven't checked.
	 * @param students The list of all the students that need to check.
	 * @param checkedStudents The students that have checked.
	 *
	 * @throws IOException If file can't be opened or wrote.
	 */
	public static void writeAbsents(Period period, ArrayList<Student> students, ArrayList<Student> checkedStudents)
	{
		for(Student student : students)
			if(!checkedStudents.contains(student))
			{
				logger.log(Level.INFO, student + " is missing");
				FileWriter fileWriter = null;
				BufferedWriter bufferedWriter = null;
				PrintWriter printWriter = null;
				try
				{
					DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
					Date date = new Date();
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(date);
					File file = new File("." + File.separator + "absents" + File.separator + "absent_" + student.getName() + "_" + calendar.get(Calendar.YEAR) + "_" + (calendar.get(Calendar.MONTH) + 1) + ".csv");
					if(!file.exists())
					{
						file.getParentFile().mkdirs();
						try
						{
							file.createNewFile();
						}
						catch(IOException exception)
						{
							exception.printStackTrace();
						}
					}
					fileWriter = new FileWriter(file, true);
					bufferedWriter = new BufferedWriter(fileWriter);
					printWriter = new PrintWriter(bufferedWriter);
					printWriter.print(dateFormat.format(date) + ";" + period.getTimeInterval() + ";" + student.getName() + "\n");
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
	}

	/**
	 * Used to know if a collection contains a student.
	 *
	 * @param collection The collection to verify.
	 * @param student The student to search for.
	 * @return True if in the collection, false if not.
	 */
	public static boolean containsStudent(Collection collection, Student student)
	{
		if(collection == null || collection.size() < 1)
			return false;
		if(collection.iterator().next() instanceof Vector)
		{
			for(Object obj : collection)
			{
				Vector<Student> vec = (Vector<Student>)obj;
				for (Student stu : vec)
					if(stu.equals(student))
						return true;
			}
		}
		else
			for(Student stu : (Collection<Student>)collection)
				if(stu.equals(student))
					return true;
		return false;
	}
}
