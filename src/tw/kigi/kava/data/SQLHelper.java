package tw.kigi.kava.data;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLHelper {

	
	public static String[] condition(String sql) throws SQLException {
		if (sql == null || "".equals(sql = sql.trim())) {
			throw new SQLException("Condition is Empty ");
		}
		
		List<String> tokens = new ArrayList<String>();
		
		int start = 0;
		int pos = 0;
		
		for(char c : sql.toCharArray()) {
			
			
			
		}
		
		return tokens.toArray(new String[tokens.size()]);
	}
}
