package tw.kigi.kava.data.operator;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import tw.kigi.kava.data.exception.ParseValueException;

public class DateOp implements Operator<java.sql.Date> {

	protected java.sql.Date toDate(long time) {
		return new java.sql.Date(time / 86400000);
	}
	
	protected java.sql.Date toDate(String value, String format) throws ParseValueException {
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
			return toDate("0001-01-01", "yyyy-MM-dd");
		
		case "max":
		case "Max":
		case "MAX":
			return toDate("9999-12-31", "yyyy-MM-dd");
			
		case "now":
		case "Now":
		case "NOW":
			return toDate(System.currentTimeMillis());
			
		default:
			return toDate(value, "yyyy-MM-dd");
		}
	}

	@Override
	public void setParam(PreparedStatement statement, int index, Date value)
			throws SQLException {
		
		statement.setDate(index, value);
		
	}

	@Override
	public Date getResult(ResultSet result, String label) throws SQLException {
		return result.getDate(label);
	}

}
