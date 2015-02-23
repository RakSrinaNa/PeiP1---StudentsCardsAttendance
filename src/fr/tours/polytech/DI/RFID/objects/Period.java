package fr.tours.polytech.DI.RFID.objects;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Class representing a period for checking.
 *
 * @author COLEAU Victor, COUCHOUD Thomas
 */
public class Period implements Serializable
{
	private static final long serialVersionUID = 546546521L;
	private int startingHour;
	private int startingMinute;
	private int endingHour;
	private int endingMinute;
	private Calendar calendar;
	private DecimalFormat decimalFormat;

	/**
	 * Constructor.
	 *
	 * @param period A string representing the period. This should be formatted
	 * as
	 * <i>xx</i><b>h</b><i>xx</i><b>-</b><i>yy</i><b>h</b><i>yy</i>
	 * where <i>xx</i> and <i>yy</i> are the time to set.
	 * @throws IllegalArgumentException If the period isn't formatted as it
	 * should be.
	 */
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
		this.calendar = Calendar.getInstance(Locale.getDefault());
		this.decimalFormat = new DecimalFormat("00");
	}

	/**
	 * Used to get a String representing this interval.
	 *
	 * @return A string formatted as <b>xxHxx - yyHyy</b>
	 */
	public String getTimeInterval()
	{
		return this.startingHour + "H" + this.decimalFormat.format(this.startingMinute) + " - " + this.endingHour + "H" + this.decimalFormat.format(this.endingMinute);
	}

	/**
	 * Used to know if the date is in this period.
	 *
	 * @param date The date to verify.
	 * @return true if the date is in the period, false if not.
	 */
	public boolean isInPeriod(Date date)
	{
		this.calendar.setTime(date);
		int hours = this.calendar.get(Calendar.HOUR_OF_DAY);
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
			else
				return true;
		return false;
	}

	/**
	 * Used to know if two Period objects are overlapping.
	 *
	 * @param period The other Period to check with.
	 * @return true if overlapping, false if not.
	 */
	public boolean isOverlapped(Period period)
	{
		return period != null && (period.isInPeriod(getStartingDate()) || period.isInPeriod(getEndingDate()) || isInPeriod(period.getStartingDate()) || isInPeriod(period.getEndingDate()));
	}

	/**
	 * Use to get the ending date of this period in the current day.
	 *
	 * @return The ending date.
	 */
	private Date getEndingDate()
	{
		Calendar calen = Calendar.getInstance(Locale.getDefault());
		calen.setTime(new Date());
		calen.set(Calendar.HOUR_OF_DAY, this.endingHour);
		calen.set(Calendar.MINUTE, this.endingMinute);
		return calen.getTime();
	}

	/**
	 * Use to get the starting date of this period in the current day.
	 *
	 * @return The starting date.
	 */
	private Date getStartingDate()
	{
		Calendar calen = Calendar.getInstance(Locale.getDefault());
		calen.setTime(new Date());
		calen.set(Calendar.HOUR_OF_DAY, this.startingHour);
		calen.set(Calendar.MINUTE, this.startingMinute);
		return calen.getTime();
	}

	@Override
	public boolean equals(Object o)
	{
		if(o instanceof Period)
			return isSame(o.toString());
		return o == this;
	}

	@Override
	public String toString()
	{
		return getTimeInterval().replaceAll(" ", "");
	}

	private boolean isSame(String name)
	{
		return this.toString().equalsIgnoreCase(name.replaceAll(" ", ""));
	}

	public boolean is(String name)
	{
		return isSame(name);
	}
}
