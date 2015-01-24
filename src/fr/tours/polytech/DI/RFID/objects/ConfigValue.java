package fr.tours.polytech.DI.RFID.objects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import fr.tours.polytech.DI.RFID.utils.Utils;

public class ConfigValue
{
	private String key;
	private String value;

	public ConfigValue(String line)
	{
		if(line == null || !line.contains(":"))
			throw new IllegalArgumentException("The string must be formatted as <key>:<value>!");
		this.key = line.substring(0, line.indexOf(":"));
		this.value = line.substring(line.indexOf(":") + 1);
		if(this.value == null)
			this.value = "";
	}

	public ConfigValue(String key, String value)
	{
		this(key, value, new Date(System.currentTimeMillis()));
	}

	public ConfigValue(String key, String value, Date date)
	{
		if(key == null || key.equals(""))
			throw new IllegalArgumentException("Key must not be null/empty!");
		this.key = key;
		this.value = value == null ? "" : value;
	}

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

	public void addInt(int toAdd, int defaultValue)
	{
		setValue(getInt(defaultValue) + toAdd);
	}

	public void addValue(Object value)
	{
		if(containsValue(value))
			return;
		ArrayList<String> list = getStringList();
		list.add(value.toString());
		setListValue(list);
	}

	public boolean containsValue(Object value)
	{
		if(value == null)
			return false;
		for(String val : getStringList())
			if(val.equals(value.toString()))
				return true;
		return false;
	}

	public boolean getBoolean(boolean defaultValue)
	{
		try
		{
			return Boolean.parseBoolean(getValue());
		}
		catch(Exception e)
		{
			this.setValue(defaultValue);
		}
		return defaultValue;
	}

	public double getDouble(double defaultValue)
	{
		try
		{
			return Double.parseDouble(getValue());
		}
		catch(Exception e)
		{
			this.setValue(defaultValue);
		}
		return defaultValue;
	}

	public int getInt(int defaultValue)
	{
		try
		{
			return Integer.parseInt(getValue());
		}
		catch(Exception e)
		{
			this.setValue(defaultValue);
		}
		return defaultValue;
	}

	public String getKey()
	{
		return this.key;
	}

	public long getLong(long defaultValue)
	{
		try
		{
			return Long.parseLong(getValue());
		}
		catch(Exception e)
		{
			this.setValue(defaultValue);
		}
		return defaultValue;
	}

	public String getString(String defaultValue)
	{
		return getValue().equals("") ? defaultValue : getValue();
	}

	public String[] getStringArray()
	{
		ArrayList<String> list = getStringList();
		if(list.size() < 1)
			return new String[] {};
		return list.toArray(new String[list.size()]);
	}

	public ArrayList<String> getStringList()
	{
		ArrayList<String> list = new ArrayList<String>();
		for(String value : this.value.split(","))
			list.add(value);
		list.remove("");
		list.remove(" ");
		return list;
	}

	public boolean isKey(String key)
	{
		return this.key.equals(key);
	}

	public void removeValue(List<String> values)
	{
		if(values.size() < 1)
			return;
		ArrayList<String> list = getStringList();
		for(String value : values)
			list.remove(value);
		setListValue(list);
	}

	public void removeValue(Object value)
	{
		ArrayList<String> list = getStringList();
		list.remove(value.toString());
		setListValue(list);
	}

	public void setArrayValue(Object[] array)
	{
		ArrayList<String> list = new ArrayList<String>();
		for(Object item : array)
			list.add(item.toString());
		setListValue(list);
	}

	public void setIfEmpty(Object value)
	{
		if(getValue().equals(""))
			setValue(value);
	}

	public ConfigValue setValue(boolean sendUpdate, Object value)
	{
		this.value = value.toString().replace("\n", ",");
		return this;
	}

	public ConfigValue setValue(Object value)
	{
		return setValue(true, value);
	}

	@Override
	public String toString()
	{
		return this.key + ":" + this.value;
	}

	public void updateDouble(double base, double d)
	{
		setValue(getDouble(base) + d);
	}

	public void updateLong(long base, long l)
	{
		setValue(getLong(base) + l);
	}

	private String getValue()
	{
		return this.value;
	}

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
