package fr.tours.polytech.DI.RFID.utils;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import com.mysql.jdbc.exceptions.jdbc4.MySQLNonTransientConnectionException;
import java.sql.*;
import java.util.Date;
import java.util.logging.Level;

/**
 * Class that allow us to interact with a SQL database.
 *
 * @author COLEAU Victor, COUCHOUD Thomas
 */
public class SQLManager
{
	private static final boolean printQuerry = true;
	private static final String LOG_TABLE = "Log";
	private static final String LOG_CSN_LABEL = "CSN";
	private static final String LOG_TIME_LABEL = "Time";
	private static final String CHECKED_TABLE = "Checked";
	private static final String CHECKED_CSN_LABEL = "CSN", CHECKED_PERIOD_LABEL = "Period_ID";
	private static final String CHECKED_WHEN_LABEL = "CheckedON";
	private String databaseURL;
	private int port;
	private String databaseName;
	private String user;
	private String password;
	private Connection connection;
	private Date lastTimeConnect;
	private boolean isLogging;

	/**
	 * Constructor.
	 *
	 * @param databaseURL The URL of the database.
	 * @param port The port of the database.
	 * @param databaseName The database name.
	 * @param user The username.
	 * @param password The password for this user.
	 */
	public SQLManager(String databaseURL, int port, String databaseName, String user, String password)
	{
		this.databaseURL = databaseURL;
		this.port = port;
		this.databaseName = databaseName;
		this.user = user;
		this.password = password;
		login();
		Utils.logger.log(Level.INFO, "Initializing SQL connection...");
		createBaseTable();
	}

	/**
	 * Used to update the parameters of the connexion.
	 *
	 * @param databaseURL The URL of the database.
	 * @param port The port of the database.
	 * @param databaseName The database name.
	 * @param user The username.
	 * @param password The password for this user.
	 */
	public void reloadInfos(String databaseURL, int port, String databaseName, String user, String password)
	{
		this.databaseURL = databaseURL;
		this.port = port;
		this.databaseName = databaseName;
		this.user = user;
		this.password = password;
	}

	/**
	 * Used to send a query request to the database.
	 *
	 * @param request The request to send.
	 * @return The result of the query.
	 *
	 * @see ResultSet
	 */
	public synchronized ResultSet sendQueryRequest(String request)
	{
		return sendQueryRequest(request, true);
	}

	/**
	 * Used to send an update request to the database.
	 *
	 * @param request The request to send.
	 * @return How many lines were modified by the request.
	 */
	public synchronized int sendUpdateRequest(String request)
	{
		return sendUpdateRequest(request, true);
	}

	/**
	 * Used to create the default database.
	 *
	 * @return How many lines were modified by the request.
	 */
	public int createBaseTable()
	{
		int i = 0;
		i += sendUpdateRequest(Students.getCreateStudentsTableText());
		i += sendUpdateRequest(getCreateCheckedTableText());
		i += sendUpdateRequest(getCreateLogTableText());
		i += sendUpdateRequest(Periods.getCreatePeriodsTableText());
		return i;
	}

	private String getCreateCheckedTableText()
	{
		return "CREATE TABLE IF NOT EXISTS " + CHECKED_TABLE + " (" + CHECKED_CSN_LABEL + " VARCHAR(18) NOT NULL, " + CHECKED_PERIOD_LABEL + " INT UNSIGNED NOT NULL, " + CHECKED_WHEN_LABEL + " DATETIME NOT NULL, PRIMARY KEY(" + CHECKED_CSN_LABEL + ", " + CHECKED_PERIOD_LABEL + "));";
	}

	private String getCreateLogTableText()
	{
		return "CREATE TABLE IF NOT EXISTS " + LOG_TABLE + " (" + LOG_CSN_LABEL + " VARCHAR(18) NOT NULL, " + LOG_TIME_LABEL + " TIMESTAMP NOT NULL, PRIMARY KEY(" + LOG_CSN_LABEL + ", " + LOG_TIME_LABEL + "));";
	}

	/**
	 * Used to establish a connection with the database.
	 *
	 * @return True if the connexion wes etablished, false if not or if it's already trying to connect.
	 */
	public boolean login()
	{
		if(isLogging)
			return false;
		isLogging = true;
		boolean result = false;
		try
		{
			this.connection = DriverManager.getConnection("jdbc:mysql://" + this.databaseURL + ":" + this.port + "/" + this.databaseName, this.user, this.password);
		}
		catch(SQLException e)
		{
			Utils.logger.log(Level.WARNING, "Error connecting to SQL database! (" + e.getMessage() + ")");
		}
		try
		{
			if(connection != null)
				result = connection.isValid(2500);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		if(!result)
			connection = null;
		isLogging = false;
		lastTimeConnect = new Date();
		return result;
	}

	/**
	 * Used to send a query request to the database.
	 *
	 * @param request The request to send.
	 * @param retry Should retry to send the request another time if it failed?
	 * @return The result of the query.
	 *
	 * @see ResultSet
	 */
	private ResultSet sendQueryRequest(String request, boolean retry)
	{
		if(this.connection == null)
			return null;
		if(printQuerry)
			Utils.logger.log(Level.INFO, "Sending MYSQL request...: " + request);
		ResultSet result = null;
		try
		{
			Statement statement = this.connection.createStatement();
			result = statement.executeQuery(request);
		}
		catch(MySQLNonTransientConnectionException e)
		{
			login();
			if(retry)
				return sendQueryRequest(request, false);
		}
		catch(SQLException exception)
		{
			Utils.logger.log(Level.WARNING, "SQL ERROR when sending " + request, exception);
		}
		return result;
	}

	/**
	 * Used to send an update request to the database.
	 *
	 * @param request The request to send.
	 * @param retry Should retry to send the request another time if it failed?
	 * @return How many lines were modified by the request.
	 */
	private int sendUpdateRequest(String request, boolean retry)
	{
		if(this.connection == null)
			return 0;
		if(printQuerry)
			Utils.logger.log(Level.INFO, "Sending MYSQL update...: " + request);
		int result = 0;
		try
		{
			Statement statement = this.connection.createStatement();
			result = statement.executeUpdate(request);
		}
		catch(MySQLNonTransientConnectionException e)
		{
			login();
			if(retry)
				return sendUpdateRequest(request, false);
		}
		catch(MySQLIntegrityConstraintViolationException exception)
		{
			Utils.logger.log(Level.WARNING, "SQL ERROR when sending " + request + " -> PRIMARY KEY Constraint error");
		}
		catch(SQLException exception)
		{
			Utils.logger.log(Level.WARNING, "SQL ERROR when sending " + request, exception);
		}
		return result;
	}

	/**
	 * Used to know if the connection to the database is etablished.
	 *
	 * @return True if etablished, false if not.
	 */
	public boolean isConnected()
	{
		try
		{
			return connection != null && connection.isValid(2500);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Used to know when we last tried connected to the database.
	 *
	 * @return The time.
	 */
	public Date getLastConnectTime()
	{
		return lastTimeConnect;
	}

	/**
	 * Used to know if we are trying to connect to the database.
	 *
	 * @return True if trying to connect, false if not.
	 */
	public boolean isLogging()
	{
		return isLogging;
	}

	public String[] exportCheckTable()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("-- ---------------------------" + "\n");
		sb.append("-- STRUCTURE" + "\n");
		sb.append("-- ---------------------------" + "\n");
		sb.append("DROP TABLE IF EXISTS " + CHECKED_TABLE + ";" + "\n");
		sb.append(getCreateCheckedTableText()).append("\n");
		sb.append("\n");
		sb.append("-- ---------------------------" + "\n");
		sb.append("-- DATA OF CHECKED" + "\n");
		sb.append("-- ---------------------------" + "\n");
		ResultSet entries = sendQueryRequest("SELECT * FROM " + CHECKED_TABLE + ";");
		try
		{
			while(entries.next())
				sb.append("INSERT INTO " + CHECKED_TABLE + " (" + CHECKED_CSN_LABEL + ", " + CHECKED_PERIOD_LABEL + ", " + CHECKED_WHEN_LABEL + ") VALUES(\"").append(entries.getString(CHECKED_CSN_LABEL)).append("\", \"").append(entries.getString(CHECKED_PERIOD_LABEL)).append("\", \"").append(entries.getString(CHECKED_WHEN_LABEL)).append("\");").append("\n");
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return sb.toString().split("\n");
	}

	public String[] exportLogTable()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("-- ---------------------------" + "\n");
		sb.append("-- STRUCTURE" + "\n");
		sb.append("-- ---------------------------" + "\n");
		sb.append("DROP TABLE IF EXISTS " + LOG_TABLE + ";" + "\n");
		sb.append(getCreateLogTableText()).append("\n");
		sb.append("\n");
		sb.append("-- ---------------------------" + "\n");
		sb.append("-- DATA OF LOG" + "\n");
		sb.append("-- ---------------------------" + "\n");
		ResultSet entries = sendQueryRequest("SELECT * FROM " + LOG_TABLE + ";");
		try
		{
			while(entries.next())
				sb.append("INSERT INTO " + LOG_TABLE + " (" + LOG_CSN_LABEL + ", " + LOG_TIME_LABEL + ") VALUES(\"").append(entries.getString(LOG_CSN_LABEL)).append("\", \"").append(entries.getString(LOG_TIME_LABEL)).append("\");").append("\n");
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return sb.toString().split("\n");
	}

	public int logCheck(String UID)
	{
		return sendUpdateRequest("INSERT INTO " + LOG_TABLE + " (" + LOG_CSN_LABEL + ", " + LOG_TIME_LABEL + ") VALUES(\"" + UID.replaceAll("-", "") + "\", NOW());");
	}

	public int resetCheckedTable()
	{
		return sendUpdateRequest("TRUNCATE TABLE " + CHECKED_TABLE + ";");
	}
}
