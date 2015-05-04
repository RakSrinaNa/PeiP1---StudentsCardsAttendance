package polytech.DI.RFID.utils;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import com.mysql.jdbc.exceptions.jdbc4.MySQLNonTransientConnectionException;
import polytech.DI.RFID.objects.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;

/**
 * Class that allow us to interact with a SQL database.
 *
 * @author COLEAU Victor, COUCHOUD Thomas
 */
public class SQLManager
{
	public final static String UID_LABEL = "CSN";
	public final static String FIRSTNAME_LABEL = "Firstname";
	public final static String LASTNAME_LABEL = "Lastname";
	private String tableName;
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
	 * @param tableName The table name.
	 * @param user The username.
	 * @param password The password for this user.
	 */
	public SQLManager(String databaseURL, int port, String databaseName, String tableName, String user, String password)
	{
		this.databaseURL = databaseURL;
		this.port = port;
		this.databaseName = databaseName;
		this.tableName = tableName;
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
	 * @param tableName The table name.
	 * @param user The username.
	 * @param password The password for this user.
	 */
	public void reloadInfos(String databaseURL, int port, String databaseName, String tableName, String user, String password)
	{
		this.databaseURL = databaseURL;
		this.port = port;
		this.databaseName = databaseName;
		this.tableName = tableName;
		this.user = user;
		this.password = password;
	}

	/**
	 * Used to add a student into the database.
	 *
	 * @param student The student to add.
	 */
	public void addStudentToDatabase(Student student)
	{
		sendUpdateRequest("INSERT INTO " + this.tableName + " (" + UID_LABEL + "," + FIRSTNAME_LABEL + "," + LASTNAME_LABEL + ") VALUES(\"" + student.getRawUid() + "\",\"" + student.getFirstName() + "\",\"" + student.getLastname() + "\")");
	}

	/**
	 * Used to retrieve a student from the database by his name.
	 *
	 * @param surname The surname of the student.
	 * @param firstname The firstname of the student.
	 * @return The student corresponding, null if not found.
	 */
	public Student getStudentByName(String surname, String firstname)
	{
		ResultSet result = sendQueryRequest("SELECT " + UID_LABEL + " FROM " + this.tableName + " WHERE " + FIRSTNAME_LABEL + " = \"" + firstname + "\" AND " + LASTNAME_LABEL + " = \"" + surname + "\";");
		try
		{
			if(result.next())
				return new Student(result.getString(UID_LABEL), surname, firstname);
		}
		catch(SQLException exception)
		{
			Utils.logger.log(Level.WARNING, "", exception);
		}
		catch(NullPointerException exception)
		{
		}
		return null;
	}

	/**
	 * Used to retrieve a student from the database by his UID.
	 *
	 * @param uid The UID of the student.
	 * @return The student corresponding, null if not found.
	 */
	public Student getStudentByUID(String uid)
	{
		ResultSet result = sendQueryRequest("SELECT " + LASTNAME_LABEL + ", " + FIRSTNAME_LABEL + " FROM " + this.tableName + " WHERE " + UID_LABEL + " = \"" + uid + "\";");
		try
		{
			if(result.next())
				return new Student(uid, result.getString(LASTNAME_LABEL), result.getString(FIRSTNAME_LABEL));
		}
		catch(NullPointerException e)
		{
		}
		catch(SQLException exception)
		{
			Utils.logger.log(Level.WARNING, "", exception);
		}
		return null;
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
		return sendUpdateRequest("CREATE TABLE IF NOT EXISTS " + this.tableName + "(" + UID_LABEL + " varchar(18), " + LASTNAME_LABEL + " varchar(255), " + FIRSTNAME_LABEL + " varchar(255)," + "PRIMARY KEY (" + UID_LABEL + ")) ENGINE=InnoDB DEFAULT CHARSET=utf8;");
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
			Utils.logger.log(Level.WARNING, "SQL ERROR when sending " + request + " -> Already got the student");
		}
		catch(SQLException exception)
		{
			Utils.logger.log(Level.WARNING, "SQL ERROR when sending " + request, exception);
		}
		return result;
	}

	/**
	 * Used to get all the students from the database.
	 *
	 * @return A list of the students.
	 */
	public ArrayList<Student> getAllStudents()
	{
		ArrayList<Student> students = new ArrayList<>();
		ResultSet result = sendQueryRequest("SELECT " + UID_LABEL + "," + LASTNAME_LABEL + ", " + FIRSTNAME_LABEL + " FROM " + this.tableName + " ORDER BY " + LASTNAME_LABEL + "," + FIRSTNAME_LABEL + ";");
		try
		{
			while(result.next())
				students.add(new Student(result.getString(UID_LABEL), result.getString(LASTNAME_LABEL), result.getString(FIRSTNAME_LABEL)));
		}
		catch(NullPointerException e)
		{
		}
		catch(Exception exception)
		{
			Utils.logger.log(Level.WARNING, "", exception);
		}
		return students;
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

	/**
	 * Used to get the table name where we are working.
	 *
	 * @return The table name
	 */
	public String getTableName()
	{
		return this.tableName;
	}
}
