package tw.kigi.kava.data.operator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import tw.kigi.kava.data.exception.ParseValueException;

public class ShortOp implements Operator<Short> {

	@Override
	public Short parseValue(String value) throws ParseValueException {
		if (value == null || (value = value.trim()).length() == 0) {
			return null;
		}
		
		try {
			switch(value) {
			case "min":
			case "Min":
			case "MIN":
				return Short.MIN_VALUE;
				
			case "max":
			case "Max":
			case "MAX":
				return Short.MAX_VALUE;
				
			default:
				return new Short(value);
			}
		}
		catch(NumberFormatException e) {
			throw new ParseValueException(e);
		}
	}

	@Override
	public void setParam(PreparedStatement statement, int index, Short value)
			throws SQLException {

		if (value != null) {
			statement.setShort(index, value);
		}
		else {
			statement.setNull(index, java.sql.Types.SMALLINT);
		}
		
		
	}

	@Override
	public Short getResult(ResultSet result, String label) throws SQLException {
		short ret = result.getShort(label);
		return result.wasNull() ? null : ret;
		
	}

}
