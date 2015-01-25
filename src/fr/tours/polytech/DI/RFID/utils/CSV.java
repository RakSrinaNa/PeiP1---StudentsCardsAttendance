package fr.tours.polytech.DI.RFID.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import fr.tours.polytech.DI.RFID.objects.Student;

public class CSV
{
	public static ArrayList<Student> getStudents(File file, boolean staff)
	{
		ArrayList<Student> data = new ArrayList<Student>();
		BufferedReader bufferedReader = null;
		String line = "";
		try
		{
			bufferedReader = new BufferedReader(new FileReader(file));
			while((line = bufferedReader.readLine()) != null)
			{
				String[] student = line.split(";|,");
				Student studentt = Student.fetch(student[0]);
				if(studentt != null)
					data.add(studentt);
			}
		}
		catch(FileNotFoundException e)
		{
			Utils.logger.log(Level.SEVERE, "Can't import student list, file not found!");
		}
		catch(IOException e)
		{
			Utils.logger.log(Level.WARNING, "Error reading student file", e);
		}
		finally
		{
			if(bufferedReader != null)
				try
				{
					bufferedReader.close();
				}
				catch(IOException e)
				{}
		}
		return data;
	}
}
