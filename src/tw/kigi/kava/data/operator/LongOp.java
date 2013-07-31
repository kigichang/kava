package tw.kigi.kava.data.operator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import tw.kigi.kava.data.exception.ParseValueException;

public class LongOp implements Operator<Long> {

	@Override
	public Long parseValue(String value) throws ParseValueException {
		if (value == null || (value = value.trim()).length() == 0) {
			return null;
		}
		
		try {
			switch(value) {
			case "min":
			case "Min":
			case "MIN":
				return Long.MIN_VALUE;
				
			case "max":
			case "Max":
			case "MAX":
				return Long.MAX_VALUE;
				
			default:
				return new Long(value);
			}
		}
		catch(NumberFormatException e) {
			throw new ParseValueException(e);
		}
	}

	@Override
	public void setParam(PreparedStatement statement, int index, Long value)
			throws SQLException {
		
		if (value != null) {
			statement.setLong(index, value);
		}
		else {
			statement.setNull(index, java.sql.Types.BIGINT);
		}
	}

	@Override
	public Long getResult(ResultSet result, String label) throws SQLException {
		return result.getLong(label);
	}

	

}
