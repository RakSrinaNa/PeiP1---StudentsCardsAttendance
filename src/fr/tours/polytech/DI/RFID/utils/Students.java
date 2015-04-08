package fr.tours.polytech.DI.RFID.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.regex.Pattern;
public class Students
{
	private static final String STUDENTS_TABLE = "Students";
	private static final String STUDENTS_CSN_LABEL = "CSN", STUDENTS_LASTNAME_LABEL = "Lastname", STUDENTS_FIRSTNAME_LABEL = "Firstname";

	public static String[] exportStudentsTable()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("-- ---------------------------" + "\n");
		sb.append("-- STRUCTURE" + "\n");
		sb.append("-- ---------------------------" + "\n");
		sb.append("DROP TABLE IF EXISTS " + STUDENTS_TABLE + ";" + "\n");
		sb.append(getCreateStudentsTableText()).append("\n");
		sb.append("\n");
		sb.append("-- ---------------------------" + "\n");
		sb.append("-- DATA OF STUDENTS" + "\n");
		sb.append("-- ---------------------------" + "\n");
		ResultSet students = Utils.sql.sendQueryRequest("SELECT * FROM " + STUDENTS_TABLE + ";");
		try
		{
			while(students.next())
				sb.append("INSERT INTO " + STUDENTS_TABLE + " (" + STUDENTS_CSN_LABEL + ", " + STUDENTS_LASTNAME_LABEL + ", " + STUDENTS_FIRSTNAME_LABEL + ") VALUES(\"").append(students.getString(STUDENTS_CSN_LABEL)).append("\", \"").append(students.getString(STUDENTS_LASTNAME_LABEL)).append("\", \"").append(students.getString(STUDENTS_FIRSTNAME_LABEL)).append("\");").append("\n");
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return sb.toString().split("\n");
	}

	public static String getCreateStudentsTableText()
	{
		return "CREATE TABLE IF NOT EXISTS " + STUDENTS_TABLE + " (" + STUDENTS_CSN_LABEL + " VARCHAR(18) NOT NULL, " + STUDENTS_FIRSTNAME_LABEL + " VARCHAR(100) NOT NULL, " + STUDENTS_LASTNAME_LABEL + " VARCHAR(100) NOT NULL, PRIMARY KEY(" + STUDENTS_CSN_LABEL + "));";
	}

	public static ArrayList<String> getAllStudents()
	{
		ArrayList<String> results = new ArrayList<String>();
		ResultSet result = Utils.sql.sendQueryRequest("SELECT CONCAT(" + STUDENTS_LASTNAME_LABEL + ", \" \", " + STUDENTS_FIRSTNAME_LABEL + ") AS Name FROM " + STUDENTS_TABLE + " ORDER BY " + STUDENTS_LASTNAME_LABEL + ", " + STUDENTS_FIRSTNAME_LABEL + ";");
		try
		{
			while(result.next())
				results.add(result.getString("Name"));
		}
		catch(NullPointerException e)
		{
		}
		catch(Exception exception)
		{
			Utils.logger.log(Level.WARNING, "", exception);
		}
		return results;
	}

	public static boolean hasStudentChecked(String name)
	{
		ResultSet result = Utils.sql.sendQueryRequest("SELECT " + STUDENTS_LASTNAME_LABEL + ", " + STUDENTS_FIRSTNAME_LABEL + " FROM " + STUDENTS_TABLE + " JOIN (Checked CROSS JOIN Periods) ON (Students." + STUDENTS_CSN_LABEL + " = Checked.CSN AND Checked.Period_ID = Periods.ID) WHERE CONCAT(" + STUDENTS_LASTNAME_LABEL + " ,\" \", " + STUDENTS_FIRSTNAME_LABEL + ")=\"" + name + "\" AND Start <= NOW() AND (End > NOW() OR End IS NULL);");
		try
		{
			return result.next();
		}
		catch(NullPointerException e)
		{
		}
		catch(Exception exception)
		{
			Utils.logger.log(Level.WARNING, "", exception);
		}
		return false;
	}

	public static int resetStudentsTable()
	{
		return Utils.sql.sendUpdateRequest("DROP TABLE IF EXISTS " + STUDENTS_TABLE + ";");
	}

	public static String getStudentNameByUID(String UID)
	{
		String results = "Unknown";
		ResultSet result = Utils.sql.sendQueryRequest("SELECT CONCAT(" + STUDENTS_LASTNAME_LABEL + ", \" \", " + STUDENTS_FIRSTNAME_LABEL + ") AS Name FROM " + STUDENTS_TABLE + " WHERE " + STUDENTS_CSN_LABEL + "=\"" + UID.replaceAll("-", "") + "\";");
		try
		{
			if(result.next())
				results = result.getString("Name");
		}
		catch(NullPointerException e)
		{
		}
		catch(Exception exception)
		{
			Utils.logger.log(Level.WARNING, "", exception);
		}
		return results;
	}

	public static String getLastname(String UID)
	{
		String results = "Unknown";
		ResultSet result = Utils.sql.sendQueryRequest("SELECT (" + STUDENTS_LASTNAME_LABEL + ") FROM " + STUDENTS_TABLE + " WHERE " + STUDENTS_CSN_LABEL + "=\"" + UID.replaceAll("-", "") + "\";");
		try
		{
			if(result.next())
				results = result.getString(STUDENTS_LASTNAME_LABEL);
		}
		catch(NullPointerException e)
		{
		}
		catch(Exception exception)
		{
			Utils.logger.log(Level.WARNING, "", exception);
		}
		return results;
	}

	public static String getFirstname(String UID)
	{
		String results = "Unknown";
		ResultSet result = Utils.sql.sendQueryRequest("SELECT (" + STUDENTS_FIRSTNAME_LABEL + ") FROM " + STUDENTS_TABLE + " WHERE " + STUDENTS_CSN_LABEL + "=\"" + UID.replaceAll("-", "") + "\";");
		try
		{
			if(result.next())
				results = result.getString(STUDENTS_FIRSTNAME_LABEL);
		}
		catch(NullPointerException e)
		{
		}
		catch(Exception exception)
		{
			Utils.logger.log(Level.WARNING, "", exception);
		}
		return results;
	}

	public static boolean isStudentKnown(String UID)
	{
		ResultSet result = Utils.sql.sendQueryRequest("SELECT " + STUDENTS_LASTNAME_LABEL + " FROM " + STUDENTS_TABLE + " WHERE " + STUDENTS_CSN_LABEL + "=\"" + UID.replaceAll("-", "") + "\";");
		try
		{
			return result.next();
		}
		catch(NullPointerException e)
		{
		}
		catch(Exception exception)
		{
			Utils.logger.log(Level.WARNING, "", exception);
		}
		return false;
	}

	public static boolean addStudent(String name, String UID)
	{
		if(name == null || name.equals(""))
			throw new IllegalArgumentException("Name must not be null or empty");
		if(!Pattern.matches("((\\w||-)+)( )((\\w||-)+)", name))
			throw new IllegalArgumentException("Name should be Lastname Firstname, was " + name);
		return Utils.sql.sendUpdateRequest("INSERT INTO " + STUDENTS_TABLE + " (" + STUDENTS_CSN_LABEL + ", " + STUDENTS_LASTNAME_LABEL + ", " + STUDENTS_FIRSTNAME_LABEL + ") VALUES(\"" + UID.replaceAll("-", "") + "\", \"" + name.split(" ")[0] + "\", \"" + name.split(" ")[1] + "\");") > 0;
	}

	public static int checkStudent(String UID, int periodID)
	{
		return Utils.sql.sendUpdateRequest("INSERT INTO Checked (CSN, Period_ID, CheckedON) VALUES(\"" + UID.replaceAll("-", "") + "\", \"" + periodID + "\", NOW());");
	}

	public static ArrayList<String> getAllStudentsCSN()
	{
		ArrayList<String> results = new ArrayList<String>();
		ResultSet result = Utils.sql.sendQueryRequest("SELECT " + STUDENTS_CSN_LABEL + " FROM " + STUDENTS_TABLE + " ORDER BY " + STUDENTS_LASTNAME_LABEL + ", " + STUDENTS_FIRSTNAME_LABEL + ";");
		try
		{
			while(result.next())
				results.add(result.getString(STUDENTS_CSN_LABEL));
		}
		catch(NullPointerException e)
		{
		}
		catch(Exception exception)
		{
			Utils.logger.log(Level.WARNING, "", exception);
		}
		return results;
	}
}
