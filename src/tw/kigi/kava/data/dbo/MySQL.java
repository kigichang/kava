package tw.kigi.kava.data.dbo;

import java.sql.Connection;
import java.sql.SQLException;

import tw.kigi.kava.data.Query;

public class MySQL<T> extends Query<T> {

	public MySQL(Class<T> clazz, Connection conn) throws SQLException {
		super(clazz, conn);
		// TODO Auto-generated constructor stub
	}

	@Override
	public T[] paginate(int start, int length) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
