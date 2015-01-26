package fr.tours.polytech.DI.RFID.objects;

import java.util.ArrayList;
import java.util.List;
import fr.tours.polytech.DI.RFID.utils.Utils;

/**
 * Class that represents a value in the configuration file.
 *
 * @author COLEAU Victor, COUCHOUD Thomas
 */
public class ConfigValue
{
	private String key;
	private String value;

	/**
	 * Constructor.
	 *
	 * @param configLine A line in the config that should be formated as <b>[key]:[value]</b>.
	 */
	public ConfigValue(String configLine)
	{
		if(configLine == null || !configLine.contains(":"))
			throw new IllegalArgumentException("The string must be formatted as <key>:<value>!");
		this.key = configLine.substring(0, configLine.indexOf(":"));
		this.value = configLine.substring(configLine.indexOf(":") + 1);
		if(this.value == null)
			this.value = "";
	}

	/**
	 * Constructor.
	 *
	 * @param key The key of this config.
	 * @param value The value.
	 */
	public ConfigValue(String key, String value)
	{
		if(key == null || key.equals(""))
			throw new IllegalArgumentException("Key must not be null/empty!");
		this.key = key;
		this.value = value == null ? "" : value;
	}

	/**
	 * Used to get all config values from an array of lines.
	 *
	 * @param lines The array of lines.
	 * @return A list of config values.
	 *
	 * @see #ConfigValue(String)
	 */
	public static List<ConfigValue> getAllConfigs(List<String> lines)
	{
		List<ConfigValue> list = new ArrayList<ConfigValue>();
		for(String line : lines)
			try
			{
				list.add(new ConfigValue(line));
			}
			catch(Exception e)
			{}
		return list;
	}

	/**
	 * Used to add a double to the current value. (! It suppose that the value is a double !)
	 *
	 * @param toAdd The value to add.
	 * @param defaultValue The default value if the variable is not set or isn't a double.
	 */
	public void addDouble(double toAdd, double defaultValue)
	{
		setValue(getDouble(defaultValue) + toAdd);
	}

	/**
	 * Used to add an integer to the current value. (! It suppose that the value is an integer !)
	 *
	 * @param toAdd The value to add.
	 * @param defaultValue The default value if the variable is not set or isn't an integer.
	 */
	public void addInt(int toAdd, int defaultValue)
	{
		setValue(getInt(defaultValue) + toAdd);
	}

	/**
	 * Used to add the object in the array of the values. (! It suppose that the value is an array !)
	 *
	 * @param value the object to add (will use {@link Object#toString()} to save it).
	 */
	public void addValue(Object value)
	{
		if(containsValue(value))
			return;
		ArrayList<String> list = getStringList();
		list.add(value.toString());
		setListValue(list);
	}

	/**
	 * Used to know if the array of values is containing this object.
	 *
	 * @param value The object to test.
	 * @return true if in the array, false if not.
	 */
	public boolean containsValue(Object value)
	{
		if(value == null)
			return false;
		for(String val : getStringList())
			if(val.equals(value.toString()))
				return true;
		return false;
	}

	/**
	 * Used to get the value as a boolean.
	 *
	 * @param defaultValue The value to return if the value isn't set or isn't of that type.
	 * @return The value parsed.
	 */
	public boolean getBoolean(boolean defaultValue)
	{
		try
		{
			return Boolean.parseBoolean(getValue());
		}
		catch(Exception e)
		{
			setValue(defaultValue);
		}
		return defaultValue;
	}

	/**
	 * Used to get the value as a double.
	 *
	 * @param defaultValue The value to return if the value isn't set or isn't of that type.
	 * @return The value parsed.
	 */
	public double getDouble(double defaultValue)
	{
		try
		{
			return Double.parseDouble(getValue());
		}
		catch(Exception e)
		{
			setValue(defaultValue);
		}
		return defaultValue;
	}

	/**
	 * Used to get the value as an integer.
	 *
	 * @param defaultValue The value to return if the value isn't set or isn't of that type.
	 * @return The value parsed.
	 */
	public int getInt(int defaultValue)
	{
		try
		{
			return Integer.parseInt(getValue());
		}
		catch(Exception e)
		{
			setValue(defaultValue);
		}
		return defaultValue;
	}

	/**
	 * Used to get the key of the value.
	 *
	 * @return The key.
	 */
	public String getKey()
	{
		return this.key;
	}

	/**
	 * Used to get the value as a long.
	 *
	 * @param defaultValue The value to return if the value isn't set or isn't of that type.
	 * @return The value parsed.
	 */
	public long getLong(long defaultValue)
	{
		try
		{
			return Long.parseLong(getValue());
		}
		catch(Exception e)
		{
			setValue(defaultValue);
		}
		return defaultValue;
	}

	/**
	 * Used to get the value as a string.
	 *
	 * @param defaultValue The value to return if the value isn't set or isn't of that type.
	 * @return The value parsed.
	 */
	public String getString(String defaultValue)
	{
		return getValue().equals("") ? defaultValue : getValue();
	}

	/**
	 * Used to get the value as a string array.
	 *
	 * @param defaultValue The value to return if the value isn't set or isn't of that type.
	 * @return The value parsed.
	 */
	public String[] getStringArray()
	{
		ArrayList<String> list = getStringList();
		if(list.size() < 1)
			return new String[] {};
		return list.toArray(new String[list.size()]);
	}

	/**
	 * Used to get the value as a string list.
	 *
	 * @param defaultValue The value to return if the value isn't set or isn't of that type.
	 * @return The value parsed.
	 */
	public ArrayList<String> getStringList()
	{
		ArrayList<String> list = new ArrayList<String>();
		for(String value : this.value.split(","))
			list.add(value);
		list.remove("");
		list.remove(" ");
		return list;
	}

	/**
	 * Used to know if this config value have this key.
	 * 
	 * @param key the key to test.
	 * @return true if it's this key, false if not.
	 */
	public boolean isKey(String key)
	{
		return this.key.equals(key);
	}

	/**
	 * Used to remove values from the value.
	 *
	 * @param values The values to remove.
	 */
	public void removeValue(List<String> values)
	{
		if(values.size() < 1)
			return;
		ArrayList<String> list = getStringList();
		for(String value : values)
			list.remove(value);
		setListValue(list);
	}

	/**
	 * Used to remove an object from the value.
	 *
	 * @param value The object to remove (will use {@link Object#toString()} to save it).
	 */
	public void removeValue(Object value)
	{
		ArrayList<String> list = getStringList();
		list.remove(value.toString());
		setListValue(list);
	}

	/**
	 * Used to set the value as an array of objects.
	 *
	 * @param array the array to set.
	 */
	public void setArrayValue(Object[] array)
	{
		ArrayList<String> list = new ArrayList<String>();
		for(Object item : array)
			list.add(item.toString());
		setListValue(list);
	}

	/**
	 * Used to set the object as the value only if the value isn't currently defined.
	 *
	 * @param value The value to set.
	 */
	public void setIfEmpty(Object value)
	{
		if(getValue().equals(""))
			setValue(value);
	}

	/**
	 * Used to set the value of this config.
	 *
	 * @param value The value to set.
	 * @return The ConfigValue modified.
	 */
	public ConfigValue setValue(Object value)
	{
		this.value = value.toString().replace("\n", ",");
		return this;
	}

	/**
	 * Used to get a representation of this object. Will return the same format at what should be parsed in {@link #ConfigValue(String)}.
	 */
	@Override
	public String toString()
	{
		return this.key + ":" + this.value;
	}

	/**
	 * Used to add a long to the current value. (! It suppose that the value is a long !)
	 *
	 * @param toAdd The value to add.
	 * @param defaultValue The default value if the variable is not set or isn't a long.
	 */
	public void updateLong(long toAdd, long defaultValue)
	{
		setValue(getLong(defaultValue) + toAdd);
	}

	/**
	 * Used to get the raw value of the config.
	 *
	 * @return The value.
	 */
	private String getValue()
	{
		return this.value;
	}

	/**
	 * Used to set a list as value.
	 *
	 * @param list The list to set.
	 */
	private void setListValue(ArrayList<String> list)
	{
		String finalValue = "";
		list = Utils.removeDuplicates(list);
		list.remove("");
		list.remove(" ");
		for(String item : list)
		{
			if(!finalValue.equals("") || finalValue.length() > 0)
				finalValue += ",";
			finalValue += item;
		}
		setValue(finalValue);
	}
}
