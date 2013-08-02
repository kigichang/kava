package tw.kigi.kava.data;

import java.sql.SQLException;

public interface Query<T> {
	
	Query<T> condition(String condition);
	Query<T> values(Object...objects);
	
	Query<T> fields(String...fields);
	Query<T> includeNull();
	
	
	T[] find() throws SQLException;
	T[] findBy(String field, Object value) throws SQLException;
	
	int update() throws SQLException;
	int update(T data) throws SQLException;
	
	int insert() throws SQLException;
	int insert(T data) throws SQLException;
	
	int replace(T data) throws SQLException;
	
}
