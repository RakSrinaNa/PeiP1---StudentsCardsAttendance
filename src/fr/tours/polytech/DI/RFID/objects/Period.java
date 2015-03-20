package fr.tours.polytech.DI.RFID.objects;

import fr.tours.polytech.DI.RFID.utils.Utils;
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
	public static final int MONDAY = 1, TUESDAY = 2, WEDNESDAY = 4, THURSDAY = 8, FRIDAY = 16, SATURDAY = 32, SUNDAY = 64;
	private int day;
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
	public Period(int day, String period) throws IllegalArgumentException
	{
		if(!Pattern.matches("(\\d{1,2})(h|H)(\\d{1,2})(-)(\\d{1,2})(h|H)(\\d{1,2})", period))
			throw new IllegalArgumentException("Time should be formatted as xx:xx-yy:yy (was " + period + ")");
		this.day = day;
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
	public String getRawTimeInterval()
	{
		return this.startingHour + "H" + this.decimalFormat.format(this.startingMinute) + " - " + this.endingHour + "H" + this.decimalFormat.format(this.endingMinute);
	}

	/**
	 * Used to get a String representing this interval.
	 *
	 * @return A string formatted as <b>xxHxx - yyHyy (days)</b>
	 */
	public String getTimeInterval()
	{
		return this.startingHour + "H" + this.decimalFormat.format(this.startingMinute) + " - " + this.endingHour + "H" + this.decimalFormat.format(this.endingMinute) + " (" + getDaysText() + ")";
	}

	public void setDay(int day)
	{
		this.day = day;
	}

	private String getDaysText()
	{
		StringBuilder sb = new StringBuilder();
		if(((day >> 0) & 0x01) == 0x01)
			sb.append(Utils.resourceBundle.getString("day_monday") + " ");
		if(((day >> 1) & 0x01) == 0x01)
			sb.append(Utils.resourceBundle.getString("day_tuesday") + " ");
		if(((day >> 2) & 0x01) == 0x01)
			sb.append(Utils.resourceBundle.getString("day_wednesday") + " ");
		if(((day >> 3) & 0x01) == 0x01)
			sb.append(Utils.resourceBundle.getString("day_thursday") + " ");
		if(((day >> 4) & 0x01) == 0x01)
			sb.append(Utils.resourceBundle.getString("day_friday") + " ");
		if(((day >> 5) & 0x01) == 0x01)
			sb.append(Utils.resourceBundle.getString("day_saturday") + " ");
		if(((day >> 6) & 0x01) == 0x01)
			sb.append(Utils.resourceBundle.getString("day_sunday") + " ");
		return sb.substring(0, sb.length());
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
		return period != null && period.isDaysOverlapped(period.getDay()) && (period.isInPeriod(getStartingDate()) || period.isInPeriod(getEndingDate()) || isInPeriod(period.getStartingDate()) || isInPeriod(period.getEndingDate()));
	}

	private boolean isDaysOverlapped(int dayy)
	{
		return (((day >> 0) & 0x01) == ((dayy >> 0) & 0x01)) || (((day >> 1) & 0x01) == ((dayy >> 1) & 0x01)) || (((day >> 2) & 0x01) == ((dayy >> 2) & 0x01)) || (((day >> 3) & 0x01) == ((dayy >> 3) & 0x01)) || (((day >> 4) & 0x01) == ((dayy >> 4) & 0x01)) || (((day >> 5) & 0x01) == ((dayy >> 5) & 0x01)) || (((day >> 6) & 0x01) == ((dayy >> 6) & 0x01));
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
		return getRawTimeInterval().replaceAll(" ", "") + " (" + getDaysText() + ")";
	}

	private boolean isSame(String name)
	{
		return this.toString().equalsIgnoreCase(name.replaceAll(" ", ""));
	}

	public boolean is(String name)
	{
		return isSame(name);
	}

	public int getDay()
	{
		return day;
	}
}
