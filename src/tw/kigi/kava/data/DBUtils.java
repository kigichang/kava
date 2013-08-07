package tw.kigi.kava.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUtils {

	public static void close(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				
			}
		}
	}
	
	public static void close(Statement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				
			}
		}
	}
	
	public static void close(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				
			}
		}
	}
	
	public static void commit(Connection conn) {
		try {
			if (conn != null && !conn.isClosed() 
					&& !conn.isReadOnly() && !conn.getAutoCommit()) {
				conn.commit();
			}
		} catch (SQLException e) {
			
		}
	}
	
	public static void rollback(Connection conn) {
		try {
			if (conn != null && !conn.isClosed() 
					&& !conn.isReadOnly() && !conn.getAutoCommit()) {
				conn.rollback();
			}
		} catch (SQLException e) {
			
		}
	}
	
}
