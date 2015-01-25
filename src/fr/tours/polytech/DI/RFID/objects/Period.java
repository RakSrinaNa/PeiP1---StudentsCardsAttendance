package fr.tours.polytech.DI.RFID.objects;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.regex.Pattern;
import fr.tours.polytech.DI.RFID.utils.Configuration;
import fr.tours.polytech.DI.RFID.utils.Utils;

public class Period
{
	private int startingHour;
	private int startingMinute;
	private int endingHour;
	private int endingMinute;
	private Calendar calendar;
	private DecimalFormat decimalFormat;

	public Period(String period) throws IllegalArgumentException
	{
		if(!Pattern.matches("(\\d{1,2})(h|H)(\\d{1,2})(-)(\\d{1,2})(h|H)(\\d{1,2})", period))
			throw new IllegalArgumentException("Time should be formatted as xx:xx-yy:yy (was " + period + ")");
		period = period.toUpperCase().replaceAll(" ", "");
		String starting = period.substring(0, period.indexOf('-'));
		String ending = period.substring(period.indexOf('-') + 1);
		this.startingHour = Integer.parseInt(starting.substring(0, starting.indexOf("H")));
		this.startingMinute = Integer.parseInt(starting.substring(starting.indexOf("H") + 1));
		this.endingHour = Integer.parseInt(ending.substring(0, ending.indexOf("H")));
		this.endingMinute = Integer.parseInt(ending.substring(ending.indexOf("H") + 1));
		this.calendar = Calendar.getInstance();
		this.decimalFormat = new DecimalFormat("00");
	}

	public static ArrayList<Period> loadPeriods()
	{
		ArrayList<Period> periods = new ArrayList<Period>();
		String[] stringPeriods = Utils.config.getConfigValue(Configuration.PERIODS).getStringArray();
		for(String stringPeriod : stringPeriods)
			try
			{
				periods.add(new Period(stringPeriod));
			}
			catch(Exception e)
			{
				Utils.logger.log(Level.WARNING, "Can't load perriod " + stringPeriod, e);
			}
		return periods;
	}

	public String getTimeInterval()
	{
		return this.startingHour + "H" + this.decimalFormat.format(this.startingMinute) + " - " + this.endingHour + "H" + this.decimalFormat.format(this.endingMinute);
	}

	public boolean isInPeriod(Date date)
	{
		this.calendar.setTime(date);
		int hours = this.calendar.get(Calendar.HOUR);
		int minutes = this.calendar.get(Calendar.MINUTE);
		if(this.startingHour == this.endingHour)
		{
			if(hours == this.startingHour)
				if(minutes >= this.startingMinute && minutes < this.endingMinute)
					return true;
		}
		else if(hours >= this.startingHour && hours < this.endingHour)
			if(hours == this.startingHour)
			{
				if(minutes >= this.startingMinute)
					return true;
			}
			else if(hours == this.endingHour)
			{
				if(minutes < this.endingMinute)
					return true;
			}
			else
				return true;
		return false;
	}

	public boolean isOverlapped(Period period) // TODO
	{
		return false;
	}

	@Override
	public String toString()
	{
		return getTimeInterval().replaceAll(" ", "");
	}
}
