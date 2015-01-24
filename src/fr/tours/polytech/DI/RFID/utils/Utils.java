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
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import fr.tours.polytech.DI.RFID.frames.MainFrame;
import fr.tours.polytech.DI.RFID.objects.Student;
import fr.tours.polytech.DI.RFID.threads.TerminalReader;

public class Utils
{
	private static FileHandler logFileHandler;
	private static TerminalReader terminalReader;
	private static MainFrame mainFrame;
	public static Logger logger;
	public static Configuration config;

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

	public static void exit(int codeExit)
	{
		mainFrame.exit();
		terminalReader.stop();
		config.stop();
		logFileHandler.close();
		System.exit(codeExit);
	}

	public static void init() throws SecurityException, IOException
	{
		logger = Logger.getLogger("RFID");
		logFileHandler = new FileHandler(new File(".", "log.log").getAbsolutePath(), true);
		logFileHandler.setFormatter(new LogFormatter());
		logFileHandler.setEncoding("UTF-8");
		logger.addHandler(logFileHandler);
		config = new Configuration();
		terminalReader = new TerminalReader("Contactless");
		mainFrame = new MainFrame(new File(".", "Students.csv"), new File(".", "Teatchers.csv"));
		terminalReader.addListener(mainFrame);
	}

	public static <T> ArrayList<T> removeDuplicates(ArrayList<T> list)
	{
		Set<T> setItems = new LinkedHashSet<T>(list);
		list.clear();
		list.addAll(setItems);
		return list;
	}

	public static void writeCheck(Student student) throws IOException
	{
		System.out.println("Writting to file");
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
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		FileWriter fileWriter = new FileWriter(file, true);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		PrintWriter printWriter = new PrintWriter(bufferedWriter);
		printWriter.print(dateFormat.format(date) + ";" + student.getName() + ";" + student.getUid().replaceAll("-", "") + "\n\r");
		printWriter.close();
		bufferedWriter.close();
		fileWriter.close();
	}

	public static void writeStudent(Student student, File file) throws IOException
	{
		System.out.println("Writting to file");
		if(!file.exists())
		{
			file.getParentFile().mkdirs();
			try
			{
				file.createNewFile();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		FileWriter fileWriter = new FileWriter(file, true);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		PrintWriter printWriter = new PrintWriter(bufferedWriter);
		printWriter.print(student.getUid() + ";" + student.getName() + "\n\r");
		printWriter.close();
		bufferedWriter.close();
		fileWriter.close();
	}
}
