package fr.tours.polytech.DI.RFID.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
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

/**
 * Configuration class, used to store parameters in a text file.
 *
 * @author COLEAU Victor, COUCHOUD Thomas
 */
public class Configuration
{
	public static final String PERIODS = "periods";
	private File configFile;
	private String normalConfigName = "config.txt";
	private List<ConfigValue> configValues;
	private Timer timer;
	private Object syncing;

	/**
	 * Constructor, will use {@link #Configuration(boolean)} with a true parameter.
	 *
	 * @see fr.tours.polytech.DI.RFID.utils.Configuration#Configuration(boolean)
	 */
	public Configuration()
	{
		this(true);
	}

	/**
	 * Constructor, will create a Configuration Object. The file loaded is in the save folder as the JAR file and named with the valeu of {@link #normalConfigName}.It will be able to read and write configuration values.
	 *
	 * @param start Either to start the auto save thread or not after loading the text file.
	 */
	public Configuration(boolean start)
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
		catch(IOException exception)
		{
			Utils.logger.log(Level.SEVERE, "Failed to read configuration file", exception);
		}
		this.syncing = false;
		setAutoSaveStatus(start);
		Utils.logger.log(Level.FINE, "Configuration initialized with config file " + this.configFile.getAbsolutePath());
	}

	/**
	 * Should be called when the object will not be used anymore. Save the config.
	 */
	public void close()
	{
		setAutoSaveStatus(false);
		writeVars();
	}

	/**
	 * Used to delete a key from the config.
	 *
	 * @param configurationValue The {@link ConfigurationValue} to remove.
	 *
	 * @return true if the configuration have been modified, false if not.
	 */
	public synchronized boolean deleteVar(ConfigValue configurationValue)
	{
		Utils.logger.log(Level.WARNING, "Deletting config file with key " + configurationValue.getKey());
		this.configValues.remove(configurationValue);
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

	/**
	 * Used to delete a key in the config by his name.
	 *
	 * @param key The key to remove.
	 *
	 * @return true if the configuration have been modified, false if not.
	 *
	 * @see deleteVar(ConfigurationValue)
	 */
	public synchronized boolean deleteVar(String key)
	{
		return deleteVar(getConfigValue(key));
	}

	/**
	 * Used to get the {@link ConfigurationValue} associated with the key.
	 *
	 * @param key The key of the config value.
	 *
	 * @return The {@link ConfigurationValue} object corresponding to the key. If none is found, a new one with a blank value is returned.
	 */
	public ConfigValue getConfigValue(String key)
	{
		long time = System.currentTimeMillis();
		synchronized(this.syncing)
		{}
		if((time = System.currentTimeMillis() - time) > 0)
			Utils.logger.log(Level.INFO, "Waited " + time + " millis that the sync finishes");
		for(ConfigValue configurationValue : this.configValues)
			if(configurationValue.isKey(key))
				return configurationValue;
		ConfigValue cv = new ConfigValue(key, "");
		this.configValues.add(cv);
		return cv;
	}

	/**
	 * Used to remove values in a config value (useful if the value is an array).
	 *
	 * @param key The key where to remove the values.
	 * @param values The values to remove if they exists.
	 */
	public void removeInKey(String key, List<String> values)
	{
		Utils.logger.log(Level.INFO, "Removing values(" + values.size() + ") " + values.toString() + " from key " + key);
		getConfigValue(key).removeValue(values);
	}

	/**
	 * Used to remove a value in a config value (useful if the value is an array).
	 *
	 * @param key The key where to remove the value.
	 * @param values The value to remove if it exists.
	 */
	public void removeInKey(String key, String value)
	{
		Utils.logger.log(Level.INFO, "Removing value " + value + " from key " + key);
		getConfigValue(key).removeValue(value);
	}

	/**
	 * Set the status of the auto saving.
	 *
	 * @param status If true it will save the config every two minutes, if false will not save automatically.
	 */
	public void setAutoSaveStatus(boolean status)
	{
		if(!status)
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

	/**
	 * Used to write the ConfigurationValue list to the text file.
	 *
	 * @return true if the process have ended correctly, false if not.
	 */
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
		for(ConfigValue configurationValue : this.configValues)
			printWriter.println(configurationValue.toString());
		Utils.logger.log(Level.FINER, "Writting config file done!");
		printWriter.close();
		try
		{
			fileWriter.close();
		}
		catch(IOException exception)
		{}
		try
		{
			this.configValues = readConfigTextFile(this.configFile);
		}
		catch(IOException exception)
		{}
		return true;
	}

	/**
	 * Used to read the configuration text file.
	 *
	 * @param config The File representing the text file.
	 * @return A list of {@link ConfigurationValue}.
	 *
	 * @throws FileNotFoundException If the file can't be read.
	 *
	 * @see FileReader#FileReader(File)
	 */
	private List<ConfigValue> readConfigTextFile(final File config) throws FileNotFoundException
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
		try
		{
			bufferedReader.close();
		}
		catch(IOException e)
		{}
		return ConfigValue.getAllConfigs(fileLines);
	}

	/**
	 * Used to stop the auto saving thread.
	 */
	private void stop()
	{
		Utils.logger.log(Level.FINE, "Stopping config Thread");
		if(this.timer != null)
			this.timer.cancel();
	}

	/**
	 * Function used by the auto save thread to save the config file. It will block the config from any modifications while saving.
	 */
	private void sync()
	{
		synchronized(Configuration.this.syncing)
		{
			Utils.logger.log(Level.INFO, "Starting settings sync...");
			writeVars();
			Utils.logger.log(Level.INFO, "Settings sync finished");
		}
	}
}
