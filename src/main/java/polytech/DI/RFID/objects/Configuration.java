package polytech.DI.RFID.objects;

import java.io.*;

/**
 * Class to save and load some options.
 *
 * @author COLEAU Victor, COUCHOUD Thomas
 */
public class Configuration implements Serializable
{
	private static final int SERIALIZATION_VERSION = 2;
	private static final long serialVersionUID = 8289555994600359883L;
	private String bddUser;
	private String bddPassword;
	private String bddName;
	private String bddTableName;
	private String bddIP;
	private String readerName;
	private int bddPort;
	private boolean logAll;
	private boolean addNewStudents;

	/**
	 * Constructor.
	 */
	public Configuration()
	{
		this.setBddUser("rfid");
		this.setBddPassword("PolytechDI26");
		this.setBddName("rfid");
		this.setBddTableName("students");
		this.setBddIP("127.0.0.1");
		this.setBddPort(3306);
		this.setReaderName("");
		this.setLogAll(true);
		this.setAddNewStudents(true);
	}

	/**
	 * Used to deserialize a Configuration object.
	 *
	 * @param file The file where the object is saved.
	 * @return The object, a new one will be created if the file couldn't be readed.
	 */
	public static Configuration deserialize(File file)
	{
		try
		{
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
			Configuration config;
			config = (Configuration) ois.readObject();
			ois.close();
			return config;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return new Configuration();
		}
	}

	/**
	 * Used for deserialization.
	 *
	 * @param ois The stream of the file.
	 * @throws IOException If there is an error reading the file.
	 */
	private void readObject(final ObjectInputStream ois) throws IOException
	{
		int ver = ois.readInt();
		if(ver >= 1)
		{
			this.bddUser = ois.readUTF();
			this.bddPassword = ois.readUTF();
			this.bddName = ois.readUTF();
			this.bddTableName = ois.readUTF();
			this.bddIP = ois.readUTF();
			this.bddPort = ois.readInt();
			this.logAll = ois.readBoolean();
			this.addNewStudents = ois.readBoolean();
		}
		if(ver >= 2)
			this.readerName = ois.readUTF();
	}

	/**
	 * Used to serialize.
	 *
	 * @param oos The stream of the file.
	 * @throws IOException If there is an error writing the file.
	 */
	private void writeObject(final ObjectOutputStream oos) throws IOException
	{
		oos.writeInt(SERIALIZATION_VERSION);
		oos.writeUTF(bddUser);
		oos.writeUTF(bddPassword);
		oos.writeUTF(bddName);
		oos.writeUTF(bddTableName);
		oos.writeUTF(bddIP);
		oos.writeInt(bddPort);
		oos.writeBoolean(logAll);
		oos.writeBoolean(addNewStudents);
		oos.writeUTF(readerName);
	}

	/**
	 * Used to serialize this object.
	 *
	 * @param file The file where to save the deserialized object.
	 */
	public void serialize(File file)
	{
		try
		{
			if(!file.getParentFile().exists())
				file.getParentFile().mkdirs();
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
			oos.writeObject(this);
			oos.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Used to get the database user.
	 *
	 * @return the database user.
	 */
	public String getBddUser()
	{
		return bddUser;
	}

	/**
	 * Used to set the database user.
	 *
	 * @param bddUser The value to set.
	 */
	public void setBddUser(String bddUser)
	{
		this.bddUser = bddUser;
	}

	/**
	 * Used to get the password for the databse user.
	 *
	 * @return The password.
	 */
	public String getBddPassword()
	{
		return bddPassword;
	}

	/**
	 * Used to get the database password.
	 *
	 * @param bddPassword The password for the databse user.
	 */
	public void setBddPassword(String bddPassword)
	{
		this.bddPassword = bddPassword;
	}

	/**
	 * Used to get the database name.
	 *
	 * @return The database name.
	 */
	public String getBddName()
	{
		return bddName;
	}

	/**
	 * Used to set the database name.
	 *
	 * @param bddName The database name.
	 */
	public void setBddName(String bddName)
	{
		this.bddName = bddName;
	}

	/**
	 * Used to know if the log file should be updated when a student check.
	 *
	 * @return True if we should log, false if not.
	 */
	public boolean isLogAll()
	{
		return logAll;
	}

	/**
	 * Used to set if the log file should be updated when a student check.
	 *
	 * @param logAll The state of this function.
	 */
	public void setLogAll(boolean logAll)
	{
		this.logAll = logAll;
	}

	/**
	 * Used to know if we should add unknown cards to the database.
	 *
	 * @return True if we should add to database, false if not.
	 */
	public boolean isAddNewStudents()
	{
		return addNewStudents;
	}

	/**
	 * Used to set if we should add unknown cards to the database.
	 *
	 * @param addNewStudents The state of this function.
	 */
	public void setAddNewStudents(boolean addNewStudents)
	{
		this.addNewStudents = addNewStudents;
	}

	/**
	 * Used to get the database IP.
	 *
	 * @return The database IP.
	 */
	public String getBddIP()
	{
		return bddIP;
	}

	/**
	 * Used to set the database IP.
	 *
	 * @param bddIP The IP to set.
	 */
	public void setBddIP(String bddIP)
	{
		this.bddIP = bddIP;
	}

	/**
	 * Used to get the database port.
	 *
	 * @return The port.
	 */
	public int getBddPort()
	{
		return bddPort;
	}

	/**
	 * used to set the database port.
	 *
	 * @param bddPort The port to set.
	 */
	public void setBddPort(int bddPort)
	{
		this.bddPort = bddPort;
	}

	/**
	 * Used to get the table where the datas are saved.
	 *
	 * @return The table name.
	 */
	public String getBddTableName()
	{
		return bddTableName;
	}

	/**
	 * Used to set the table where the datas are saved.
	 *
	 * @param bddTableName The table name to set.
	 */
	public void setBddTableName(String bddTableName)
	{
		this.bddTableName = bddTableName;
	}

	/**
	 * Used to get the reader name.
	 *
	 * @return The reader name.
	 */
	public String getReaderName()
	{
		return readerName;
	}

	/**
	 * Used to set the reader name.
	 *
	 * @param readerName The reader name.
	 */
	public void setReaderName(String readerName)
	{
		this.readerName = readerName;
	}
}
