package fr.tours.polytech.DI.RFID.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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
				data.add(new Student(student[0], student[1], staff));
			}
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(bufferedReader != null)
				try
				{
					bufferedReader.close();
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
		}
		return data;
	}
}
