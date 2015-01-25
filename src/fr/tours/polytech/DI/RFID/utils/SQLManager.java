package fr.tours.polytech.DI.RFID.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import com.mysql.jdbc.exceptions.jdbc4.MySQLNonTransientConnectionException;
import fr.tours.polytech.DI.RFID.objects.Student;

public class SQLManager
{
	private String UID_LABEL = "UID", NAME_LABEL = "Name", STAFF_LABEL = "Staff";
	private Connection con;
	private String table;
	private String databaseURL;
	private int port;
	private String databaseName;
	private String user;
	private String password;

	public SQLManager(String databaseURL, int port, String databaseName, String user, String password)
	{
		this.databaseURL = databaseURL;
		this.port = port;
		this.databaseName = databaseName;
		this.user = user;
		this.password = password;
		login();
		Utils.logger.log(Level.INFO, "Initializing SQL connection...");
		this.table = "Users";
		createBaseTable();
	}

	public Student getStudentByName(String name)
	{
		ResultSet result = sendQuerryRequest("SELECT " + this.UID_LABEL + ", " + this.STAFF_LABEL + " FROM " + this.table + " WHERE " + this.NAME_LABEL + " = \"" + name + "\";");
		try
		{
			if(result.next())
				return new Student(result.getString(this.UID_LABEL), name, result.getInt(this.STAFF_LABEL) == 1);
		}
		catch(SQLException e)
		{
			Utils.logger.log(Level.WARNING, "", e);
		}
		return null;
	}

	public Student getStudentByUID(String uid)
	{
		ResultSet result = sendQuerryRequest("SELECT " + this.NAME_LABEL + ", " + this.STAFF_LABEL + " FROM " + this.table + " WHERE " + this.UID_LABEL + " = \"" + uid + "\";");
		try
		{
			if(result.next())
				return new Student(uid, result.getString(this.NAME_LABEL), result.getInt(this.STAFF_LABEL) == 1);
		}
		catch(SQLException e)
		{
			Utils.logger.log(Level.WARNING, "", e);
		}
		return null;
	}

	public synchronized ResultSet sendQuerryRequest(String request)
	{
		return sendQuerryRequest(request, true);
	}

	public synchronized int sendUpdateRequest(String request)
	{
		return sendUpdateRequest(request, true);
	}

	private int createBaseTable()
	{
		return sendUpdateRequest("CREATE TABLE IF NOT EXISTS " + this.table + "(" + this.UID_LABEL + " varchar(18), " + this.NAME_LABEL + " varchar(255), " + this.STAFF_LABEL + " tinyint(1), PRIMARY KEY (" + this.UID_LABEL + ")) ENGINE=InnoDB DEFAULT CHARSET=utf8;");
	}

	private void login()
	{
		try
		{
			this.con = DriverManager.getConnection("jdbc:mysql://" + this.databaseURL + ":" + this.port + "/" + this.databaseName, this.user, this.password);
		}
		catch(SQLException e)
		{
			Utils.logger.log(Level.WARNING, "Error connecting to SQL database!", e);
		}
	}

	private ResultSet sendQuerryRequest(String request, boolean retry)
	{
		if(this.con == null)
			return null;
		Utils.logger.log(Level.INFO, "Sending MYSQL request...: " + request);
		ResultSet result = null;
		try
		{
			Statement stmt = this.con.createStatement();
			result = stmt.executeQuery(request);
		}
		catch(MySQLNonTransientConnectionException e)
		{
			login();
			if(retry)
				return sendQuerryRequest(request, false);
		}
		catch(SQLException e)
		{
			Utils.logger.log(Level.WARNING, "SQL ERROR", e);
		}
		return result;
	}

	private int sendUpdateRequest(String request, boolean retry)
	{
		if(this.con == null)
			return 0;
		Utils.logger.log(Level.INFO, "Sending MYSQL update...: " + request);
		int result = 0;
		try
		{
			Statement stmt = this.con.createStatement();
			result = stmt.executeUpdate(request);
		}
		catch(MySQLNonTransientConnectionException e)
		{
			login();
			if(retry)
				return sendUpdateRequest(request, false);
		}
		catch(SQLException e)
		{
			Utils.logger.log(Level.WARNING, "SQL ERROR", e);
		}
		return result;
	}
}
