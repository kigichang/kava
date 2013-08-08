package tw.kigi.kava.data.operator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import tw.kigi.kava.data.exception.ParseValueException;

public interface Operator<T> {

	T parseValue(String value) throws ParseValueException;
	
	void setParam(PreparedStatement statement, int index, T value) throws SQLException;
	
	T getResult(ResultSet result, String label) throws SQLException;
	
	Class<T> getOperand();
}
