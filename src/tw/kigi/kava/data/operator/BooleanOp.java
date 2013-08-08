package tw.kigi.kava.data.operator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import tw.kigi.kava.data.exception.ParseValueException;

public class BooleanOp implements Operator<Boolean> {

	@Override
	public Boolean parseValue(String value) throws ParseValueException {
		if (value == null || (value = value.trim()).length() == 0) {
			return null;
		}
		
		switch(value.toLowerCase()) {
		case "yes":
		case "true":
		case "on":
		case "1":
		case "y":
		case "ok":
			return Boolean.TRUE;
			
		case "no":
		case "false":
		case "off":
		case "0":
		case "n":
		case "cancel":
			return Boolean.FALSE;
			
		default:
			throw new ParseValueException("Can Not Verify Value to Boolean " + value);
		}
	}

	@Override
	public void setParam(PreparedStatement statement, int index, Boolean value)
			throws SQLException {
		
		if (value != null) {
			statement.setBoolean(index, value);
		}
		else {
			statement.setNull(index, java.sql.Types.BOOLEAN);
		}
		
	}

	@Override
	public Boolean getResult(ResultSet result, String label)
			throws SQLException {
		
		boolean ret = result.getBoolean(label);
		return result.wasNull() ? null : ret;
	}

	@Override
	public Class<Boolean> getOperand() {
		return Boolean.class;
	}

}
