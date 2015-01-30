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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import fr.tours.polytech.DI.RFID.objects.Student;

/**
 * Class used to read CSV files.
 *
 * @author COLEAU Victor, COUCHOUD Thomas
 */
public class CSV
{
	/**
	 * Used to get a list of {@link Student} from a CSV file. The CSV file should contain a name in the first column.
	 *
	 * @param file The File representing the CSV file.
	 * @return An ArrayList of the students.
	 */
	public static ArrayList<Student> getStudents(File file)
	{
		ArrayList<Student> students = new ArrayList<Student>();
		BufferedReader bufferedReader = null;
		String line = "";
		try
		{
			bufferedReader = new BufferedReader(new FileReader(file));
			while((line = bufferedReader.readLine()) != null)
			{
				String[] studentLine = line.split(";|,");
				Student student = Student.fetchSQL(studentLine[0]);
				if(student != null)
					students.add(student);
			}
		}
		catch(FileNotFoundException exception)
		{
			Utils.logger.log(Level.SEVERE, "Can't import student list, file not found!");
		}
		catch(IOException exception)
		{
			Utils.logger.log(Level.WARNING, "Error reading student file", exception);
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
		return students;
	}
}
