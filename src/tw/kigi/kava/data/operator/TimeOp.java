package tw.kigi.kava.data.operator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import tw.kigi.kava.data.exception.ParseValueException;

public class TimeOp implements Operator<java.sql.Time> {

	protected Time toTime(long time) {
		return new java.sql.Time(time);
	}
	
	protected Time toTime(String value, String format) throws ParseValueException {
		try {
			return toTime(new SimpleDateFormat(format).parse(value).getTime());
		}
		catch(NullPointerException | IllegalArgumentException | ParseException e) {
			throw new ParseValueException(e);
		}
	}
	
	
	@Override
	public Time parseValue(String value) throws ParseValueException {
		if (value == null || (value = value.trim()).length() == 0) {
			return null;
		}
		
		switch(value) {
		case "min":
		case "Min":
		case "MIN":
			return toTime("00:00:00", "HH:mm:ss");
			
		case "max":
		case "Max":
		case "MAX":
			return toTime("23:59:59", "HH:mm:ss");
		
		case "now":
		case "Now":
		case "NOW":
			return toTime(System.currentTimeMillis());
		
		default:
			return toTime(value, "HH:mm:ss");
		}
	}

	@Override
	public void setParam(PreparedStatement statement, int index, Time value)
			throws SQLException {
		
		statement.setTime(index, value);
	}

	@Override
	public Time getResult(ResultSet result, String label) throws SQLException {
		return result.getTime(label);
	}

}
