package tw.kigi.kava.data.operator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import tw.kigi.kava.data.exception.ParseValueException;

public class CharOp implements Operator<Character> {

	@Override
	public Character parseValue(String value) throws ParseValueException {
		if (value == null || value.length() == 0) {
			return null;
		}
		return value.charAt(0);
	}

	@Override
	public void setParam(PreparedStatement statement, int index, Character value)
			throws SQLException {
		
		if (value != null) {
			statement.setString(index, String.valueOf(value));
		}
		else {
			statement.setNull(index, java.sql.Types.CHAR);
		}
		
	}

	@Override
	public Character getResult(ResultSet result, String label)
			throws SQLException {
		return parseValue(result.getString(label));
	}

	@Override
	public Class<Character> getOperand() {
		return Character.class;
	}

}
