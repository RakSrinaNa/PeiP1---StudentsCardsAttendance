package fr.tours.polytech.DI.RFID.objects;

import java.io.*;

public class Configuration implements Serializable
{
	private String bddUser;
	private String bddPassword;
	private String bddName;
	private String bddTableName;
	private String bddIP;
	private int bddPort;
	private boolean logAll;
	private boolean addNewStudents;

	public Configuration()
	{
		this.setBddUser("rfid");
		this.setBddPassword("PolytechDI26");
		this.setBddName("rfid");
		this.setBddTableName("name");
		this.setBddIP("127.0.0.1");
		this.setBddPort(3306);
		this.setLogAll(true);
		this.setAddNewStudents(true);
	}

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

	public String getBddUser()
	{
		return bddUser;
	}

	public void setBddUser(String bddUser)
	{
		this.bddUser = bddUser;
	}

	public String getBddPassword()
	{
		return bddPassword;
	}

	public void setBddPassword(String bddPassword)
	{
		this.bddPassword = bddPassword;
	}

	public String getBddName()
	{
		return bddName;
	}

	public void setBddName(String bddName)
	{
		this.bddName = bddName;
	}

	public boolean isLogAll()
	{
		return logAll;
	}

	public void setLogAll(boolean logAll)
	{
		this.logAll = logAll;
	}

	public boolean isAddNewStudents()
	{
		return addNewStudents;
	}

	public void setAddNewStudents(boolean addNewStudents)
	{
		this.addNewStudents = addNewStudents;
	}

	public String getBddIP()
	{
		return bddIP;
	}

	public void setBddIP(String bddIP)
	{
		this.bddIP = bddIP;
	}

	public int getBddPort()
	{
		return bddPort;
	}

	public void setBddPort(int bddPort)
	{
		this.bddPort = bddPort;
	}

	public String getBddTableName()
	{
		return bddTableName;
	}

	public void setBddTableName(String bddTableName)
	{
		this.bddTableName = bddTableName;
	}
}
