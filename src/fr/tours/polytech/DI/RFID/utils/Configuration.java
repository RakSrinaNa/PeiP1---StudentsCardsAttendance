package fr.tours.polytech.DI.RFID.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import fr.tours.polytech.DI.RFID.objects.ConfigValue;

public class Configuration
{
	public static final String HOUR_INTERVAL = "hourInterval", START_HOUR = "startHour", END_HOUR = "endHour";
	private File configFile;
	private String normalConfigName = "config.txt";
	private List<ConfigValue> configValues;
	private Timer timer;
	private Object syncing;

	public Configuration()
	{
		this(true);
	}

	public Configuration(boolean b)
	{
		this.configFile = new File("." + File.separator + this.normalConfigName);
		try
		{
			if(!this.configFile.exists())
			{
				this.configFile.getParentFile().mkdirs();
				this.configFile.createNewFile();
			}
			this.configValues = readConfigTextFile(this.configFile);
		}
		catch(IOException e)
		{
			Utils.logger.log(Level.SEVERE, "Failed to read configuration file", e);
		}
		this.syncing = false;
		setStatus(b);
		Utils.logger.log(Level.FINE, "Configuration initialized with config file " + this.configFile.getAbsolutePath());
	}

	public void addVar(String key, Object value)
	{
		getConfigValue(key).addValue(value);
	}

	public void close()
	{
		writeVars();
	}

	public synchronized boolean deleteVar(ConfigValue cv)
	{
		Utils.logger.log(Level.WARNING, "Deletting config file with key " + cv.getKey());
		this.configValues.remove(cv);
		FileWriter fileWriter;
		try
		{
			fileWriter = new FileWriter(this.configFile, false);
		}
		catch(final IOException exception)
		{
			Utils.logger.log(Level.WARNING, "Fail when opening config file!", exception);
			return false;
		}
		final PrintWriter printWriter = new PrintWriter(new BufferedWriter(fileWriter));
		for(ConfigValue configValue : this.configValues)
			printWriter.println(configValue.toString());
		Utils.logger.log(Level.FINER, "Writting config file done!");
		printWriter.close();
		return true;
	}

	public synchronized boolean deleteVar(String key)
	{
		return deleteVar(getConfigValue(key));
	}

	public List<ConfigValue> getConfigsBeginingWith(String key)
	{
		List<ConfigValue> configValues = new ArrayList<ConfigValue>();
		Utils.logger.log(Level.FINER, "Getting keys begining with " + key);
		for(ConfigValue cv : this.configValues)
			if(cv.getKey().startsWith(key))
				configValues.add(cv);
		Utils.logger.log(Level.FINER, "Found " + configValues + " begining with " + key);
		return configValues;
	}

	public ConfigValue getConfigValue(String key)
	{
		long s = System.currentTimeMillis();
		synchronized(this.syncing)
		{}
		if((s = System.currentTimeMillis() - s) > 0)
			Utils.logger.log(Level.INFO, "Waited " + s + " millis that the sync finishes");
		for(ConfigValue cv : this.configValues)
			if(cv.isKey(key))
				return cv;
		ConfigValue cv = new ConfigValue(key, "");
		this.configValues.add(cv);
		return cv;
	}

	public void removeInKey(String key, List<String> values)
	{
		Utils.logger.log(Level.INFO, "Removing values(" + values.size() + ") " + values.toString() + " from key " + key);
		getConfigValue(key).removeValue(values);
	}

	public void removeInKey(String key, String value)
	{
		Utils.logger.log(Level.INFO, "Removing value " + value + " from key " + key);
		getConfigValue(key).removeValue(value);
	}

	public void setStatus(boolean b)
	{
		if(!b)
			stop();
		else
		{
			long time = 2 * 60 * 1000;
			this.timer = new Timer();
			this.timer.schedule(new TimerTask()
			{
				@Override
				public void run()
				{
					sync();
				}
			}, time, time);
		}
	}

	public void stop()
	{
		Utils.logger.log(Level.FINE, "Stopping config Thread");
		if(this.timer != null)
			this.timer.cancel();
	}

	public void sync()
	{
		synchronized(Configuration.this.syncing)
		{
			Utils.logger.log(Level.INFO, "Starting settings sync...");
			writeVars();
			Utils.logger.log(Level.INFO, "Settings sync finished");
		}
	}

	public synchronized boolean writeVars()
	{
		Utils.logger.log(Level.INFO, "Writting config file");
		FileWriter fileWriter;
		try
		{
			fileWriter = new FileWriter(this.configFile, false);
		}
		catch(final IOException exception)
		{
			Utils.logger.log(Level.WARNING, "Fail when opening config file!", exception);
			return false;
		}
		final PrintWriter printWriter = new PrintWriter(new BufferedWriter(fileWriter));
		for(ConfigValue cv : this.configValues)
			printWriter.println(cv.toString());
		Utils.logger.log(Level.FINER, "Writting config file done!");
		printWriter.close();
		try
		{
			fileWriter.close();
		}
		catch(IOException e1)
		{
			e1.printStackTrace();
		}
		try
		{
			this.configValues = readConfigTextFile(this.configFile);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		return true;
	}

	private List<ConfigValue> readConfigTextFile(final File config) throws IOException
	{
		BufferedReader bufferedReader = new BufferedReader(new FileReader(config));
		List<String> fileLines = null;
		try
		{
			String line = bufferedReader.readLine();
			fileLines = new ArrayList<String>();
			while(line != null)
			{
				fileLines.add(line);
				line = bufferedReader.readLine();
			}
		}
		catch(IOException exception)
		{
			Utils.logger.log(Level.SEVERE, "Failed to read configuration file", exception);
		}
		finally
		{
			bufferedReader.close();
		}
		return ConfigValue.getAllConfigs(fileLines);
	}
}
