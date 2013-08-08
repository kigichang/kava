package tw.kigi.kava.data.operator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import tw.kigi.kava.data.exception.ParseValueException;

public class DoubleOp implements Operator<Double> {

	@Override
	public Double parseValue(String value) throws ParseValueException {
		if (value == null || (value = value.trim()).length() == 0) {
			return null;
		}
		
		try {
			switch(value) {
			case "min":
			case "Min":
			case "MIN":
				return Double.MIN_VALUE;
			
			case "max":
			case "Max":
			case "MAX":
				return Double.MAX_VALUE;
				
			default:
				return new Double(value);
			}
		}
		catch(NumberFormatException e) {
			throw new ParseValueException(e);
		}
	}

	@Override
	public void setParam(PreparedStatement statement, int index, Double value)
			throws SQLException {
		
		if (value != null) {
			statement.setDouble(index, value);
		}
		else {
			statement.setNull(index, java.sql.Types.DOUBLE);
		}
		
	}

	@Override
	public Double getResult(ResultSet result, String label) throws SQLException {
		
		double ret = result.getDouble(label);
		return result.wasNull() ? null : ret;
	}

	@Override
	public Class<Double> getOperand() {
		return Double.class;
	}

}
