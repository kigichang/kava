package tw.kigi.kava.data.operator;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import tw.kigi.kava.data.exception.ParseValueException;

public class BigDecimalOp implements Operator<BigDecimal> {

	@Override
	public BigDecimal parseValue(String value) throws ParseValueException {
		try {
			return value == null || (value = value.trim()).length() == 0 ?
					null : new BigDecimal(value);
		}
		catch(NumberFormatException e) {
			throw new ParseValueException(e);
		}
	}

	@Override
	public void setParam(PreparedStatement statement, int index,
			BigDecimal value) throws SQLException {
		
		statement.setBigDecimal(index, value);
		
	}

	@Override
	public BigDecimal getResult(ResultSet result, String label)
			throws SQLException {
		
		return result.getBigDecimal(label);
	}

}
