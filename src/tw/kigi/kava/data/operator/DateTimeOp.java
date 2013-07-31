package tw.kigi.kava.data.operator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import tw.kigi.kava.data.exception.ParseValueException;

public class DateTimeOp implements Operator<java.util.Date> {

	protected java.util.Date toDate(long time) {
		return new java.util.Date(time);
	}
	
	protected java.util.Date toDate(String value, String format) throws ParseValueException {
		try {
			return toDate(new SimpleDateFormat(format).parse(value).getTime());
		}
		catch(NullPointerException | IllegalArgumentException | ParseException e) {
			throw new ParseValueException(e);
		}
	}
	
	@Override
	public Date parseValue(String value) throws ParseValueException {
		if (value == null || (value = value.trim()).length() == 0) {
			return null;
		}
		
		switch(value) {
		case "min":
		case "Min":
		case "MIN":
			return toDate("0001-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss");
		
		case "max":
		case "Max":
		case "MAX":
			return toDate("9999-12-31 23:59:59", "yyyy-MM-dd HH:mm:ss");
			
		case "now":
		case "Now":
		case "NOW":
			return toDate(System.currentTimeMillis());
			
		default:
			return toDate(value, "yyyy-MM-dd HH:mm:ss");
		}
	}

	@Override
	public void setParam(PreparedStatement statement, int index, Date value)
			throws SQLException {
		if (value != null) {
			statement.setTimestamp(index, new java.sql.Timestamp(value.getTime()));
		}
		else {
			statement.setNull(index, java.sql.Types.TIMESTAMP);
		}
	}

	@Override
	public Date getResult(ResultSet result, String label) throws SQLException {
		java.sql.Timestamp t = result.getTimestamp(label);
		return t != null ? toDate(t.getTime()) : null;
	}

}
