package tw.kigi.kava.data.operator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import tw.kigi.kava.data.exception.ParseValueException;

public class FloatOp implements Operator<Float> {

	@Override
	public Float parseValue(String value) throws ParseValueException {
		if (value == null || (value = value.trim()).length() == 0) {
			return null;
		}
		try {
			switch(value) {
			case "min":
			case "Min":
			case "MIN":
				return Float.MIN_VALUE;
			
			case "max":
			case "Max":
			case "MAX":
				return Float.MAX_VALUE;
				
			default:
				return new Float(value);
			}
		}
		catch(NumberFormatException e) {
			throw new ParseValueException(e);
		}
	}

	@Override
	public void setParam(PreparedStatement statement, int index, Float value)
			throws SQLException {
		
		if (value != null) {
			statement.setFloat(index, value);
		}
		else {
			statement.setNull(index, java.sql.Types.FLOAT);
		}
		
	}

	@Override
	public Float getResult(ResultSet result, String label) throws SQLException {

		float ret = result.getFloat(label);
		return result.wasNull() ? null : ret;
	}

	@Override
	public Class<Float> getOperand() {
		return Float.class;
	}

}
