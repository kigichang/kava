package tw.kigi.kava.data.operator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import tw.kigi.kava.data.exception.ParseValueException;

public class IntegerOp implements Operator<Integer> {

	@Override
	public Integer parseValue(String value) throws ParseValueException {
		if (value == null || (value = value.trim()).length() == 0) {
			return null;
		}
		
		try {
			switch(value) {
			case "min":
			case "Min":
			case "MIN":
				return Integer.MIN_VALUE;
				
			case "max":
			case "Max":
			case "MAX":
				return Integer.MAX_VALUE;
				
			default:
				return new Integer(value);
			}
		}
		catch(NumberFormatException e) {
			throw new ParseValueException(e);
		}
	}

	@Override
	public void setParam(PreparedStatement statement, int index, Integer value)
			throws SQLException {
		
		if (value != null) {
			statement.setInt(index, value);
		}
		else {
			statement.setNull(index, java.sql.Types.INTEGER);
		}
		
	}

	@Override
	public Integer getResult(ResultSet result, String label)
			throws SQLException {
	  
	  int ret = result.getInt(label);
	  return result.wasNull() ? null : ret; 
	}

}
