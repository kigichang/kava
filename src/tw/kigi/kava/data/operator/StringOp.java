package tw.kigi.kava.data.operator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import tw.kigi.kava.data.exception.ParseValueException;

public class StringOp implements Operator<String> {

	@Override
	public String parseValue(String value) throws ParseValueException {
		return value;
	}

	@Override
	public void setParam(PreparedStatement statement, int index, String value)
			throws SQLException {
	
		statement.setString(index, value);
		
	}

	@Override
	public String getResult(ResultSet result, String label) throws SQLException {
		return result.getString(label);
	}

	@Override
	public Class<String> getOperand() {
		return String.class;
	}

}
