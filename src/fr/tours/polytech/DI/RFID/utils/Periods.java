package fr.tours.polytech.DI.RFID.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

public class Periods
{
	private static final String PERIODS_TABLE = "Periods";
	private static final String PERIODS_ID_LABEL = "ID";
	private static final String PERIODS_DATE_LABEL = "Date";
	private static final String PERIODS_START_LABEL = "Start";
	private static final String PERIODS_END_LABEL = "End";

	public static int startNewPeriod()
	{
		int ID = getNewID();
		Utils.sql.sendUpdateRequest("INSERT INTO " + PERIODS_TABLE + " (" + PERIODS_ID_LABEL + ", " + PERIODS_DATE_LABEL + ", " + PERIODS_START_LABEL + ") VALUES(\"" + ID + "\", CURDATE(), NOW());");
		return ID;
	}

	private static int getNewID()
	{
		int results = 0;
		ResultSet result = Utils.sql.sendQueryRequest("SELECT MAX(" + PERIODS_ID_LABEL + ") AS Max FROM " + PERIODS_TABLE + ";");
		try
		{
			if(result.next())
				results = result.getInt("Max") + 1;
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

	public static String[] exportPeriodsTable()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("-- ---------------------------" + "\n");
		sb.append("-- STRUCTURE" + "\n");
		sb.append("-- ---------------------------" + "\n");
		sb.append("DROP TABLE IF EXISTS " + PERIODS_TABLE + ";" + "\n");
		sb.append(getCreatePeriodsTableText()).append("\n");
		sb.append("\n");
		sb.append("-- ---------------------------" + "\n");
		sb.append("-- DATA OF PERIODS" + "\n");
		sb.append("-- ---------------------------" + "\n");
		ResultSet entries = Utils.sql.sendQueryRequest("SELECT * FROM " + PERIODS_TABLE + ";");
		try
		{
			while(entries.next())
				sb.append("INSERT INTO " + PERIODS_TABLE + " (" + PERIODS_ID_LABEL + ", " + PERIODS_DATE_LABEL + ", " + PERIODS_START_LABEL + ", " + PERIODS_END_LABEL + ") VALUES(\"").append(entries.getString(PERIODS_ID_LABEL)).append("\", \"").append(entries.getString(PERIODS_DATE_LABEL)).append("\", \"").append(entries.getString(PERIODS_START_LABEL)).append("\", \"").append(entries.getString(PERIODS_END_LABEL)).append("\");").append("\n");
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return sb.toString().split("\n");
	}

	public static String getCreatePeriodsTableText()
	{
		return "CREATE TABLE IF NOT EXISTS " + PERIODS_TABLE + " (" + PERIODS_ID_LABEL + " INT UNSIGNED NOT NULL, " + PERIODS_DATE_LABEL + " DATE NOT NULL, " + PERIODS_START_LABEL + " TIME, " + PERIODS_END_LABEL + " TIME, PRIMARY KEY (" + PERIODS_ID_LABEL + "));";
	}

	public static int endPeriod(int periodID)
	{
		return Utils.sql.sendUpdateRequest("UPDATE Periods SET End=NOW() WHERE ID = \"" + periodID + "\"");
	}

	public static int resetPeriodsTable()
	{
		return Utils.sql.sendUpdateRequest("TRUNCATE TABLE " + PERIODS_TABLE + ";");
	}
}
