package tw.kigi.kava.data;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import tw.kigi.kava.data.exception.UnsupportedTypeException;
import tw.kigi.kava.data.operator.OpUtils;
import tw.kigi.kava.data.operator.Operator;

public abstract class Query<T> {
	
	private Class<T> clazz;
	private Schema schema;
	private Connection conn;
	
	private boolean includeNull = false;
	private String condition;
	private ParamValue[] values;
	private String[] fields;
	
	
	public Query(Class<T> clazz, Connection conn) throws SQLException {
		schema = Schema.getSchema(clazz);
		this.conn = conn;
		this.clazz = clazz;
	}
	
	public Query<T> condition(String condition) {
		this.condition = condition;
		return this;
	}
	
	public Query<T> values(ParamValue<?>...objects) {
		this.values = objects;
		return this;
	}
	
	public Query<T> fields(String...fields) {
		this.fields = fields;
		return this;
	}
	
	public Query<T> includeNull() {
		includeNull = true;
		return this;
	}
	
	
	protected String selectExpr() throws SQLException {
		if (fields == null || fields.length == 0) {
			fields = schema.getFields();
		}
		
		StringBuilder ret = new StringBuilder("select ");
		for(String field : fields) {
			Property p = field.indexOf('0') < 0 
					? schema.getProperty(schema.getSchemaName() + "." + field)
					: schema.getProperty(field);
			
			ret.append(p.getColumnName() + " " + p.getAliasName() + ",");
		}
		
		ret.setCharAt(ret.length() - 1, ' ');
		return ret.toString();
		
	}
	
	protected String fromExpr() {
		StringBuilder ret = new StringBuilder("from ");
		
		ret.append(schema.getTableName()).append(' ').append(schema.getSchemaName());
		
		return ret.toString();
	}
	
	protected String whereExpr() throws SQLException {
		
		String[] fields = schema.getFields();
		String tmp = condition;
		for(String f : fields) {
			if (tmp.indexOf(f) >= 0) {
				Property p = schema.getProperty(f);
				tmp = StringUtils.replace(tmp, f, p.getColumnName());
			}
		}
		return tmp;
	}
	
	protected String groupExpr() {
		// TODO group expression string
		return null;
	}
	
	protected String orderExpr() {
		// TODO order expression string
		return null;
	}
	
	 
	@SuppressWarnings({ "unchecked", "rawtypes" }) 
	public T[] find() throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append(selectExpr())
			.append(fromExpr())
			.append(whereExpr())
			.append(groupExpr())
			.append(orderExpr());
		
		
		PreparedStatement stmt = conn.prepareStatement(sql.toString());
		int seq = 0;
		for (ParamValue param: values) {
			param.op.setParam(stmt, ++seq, param.value);
		}
		
		ResultSet rs = stmt.executeQuery();
		List<T> ret = new ArrayList<T>();
		try {
			ResultSetMetaData meta = rs.getMetaData();
			int col_count = meta.getColumnCount();
			while(rs.next()) {
				T obj = clazz.newInstance();
				for (int i = 1; i <= col_count; i++) {
					String label = meta.getColumnLabel(i);
					Property p = schema.getAlias(label);
					p.set(obj, p.getOperator().getResult(rs, label));
				}
			
				ret.add(obj);
			}
		} catch (InstantiationException | IllegalAccessException 
				| IllegalArgumentException | InvocationTargetException e) {
			
			throw new SQLException(e);
		}
		finally {
			DBUtils.close(rs);
			DBUtils.close(stmt);
		}
		
		return (T[])(ret.toArray());
		
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public T[] findBy(String field, Object value) throws SQLException, UnsupportedTypeException {
		this.condition = field + " = ?";
		this.values = new ParamValue[] { new ParamValue(value) };
		return find();
	}
	
	public int update() throws SQLException {
		// TODO update
		return 0;
	}
	
	public int update(T data) throws SQLException {
		// TODO update
		return 0;
	}
	
	public int insert() throws SQLException {
		// TODO insert
		return 0;
	}
	
	public int insert(T data) throws SQLException {
		// TODO insert
		return 0;
	}
	
	public boolean replace(T data) throws SQLException {
		// TODO replace
		return false;
	}
	
	public abstract T[] paginate(int start, int length) throws SQLException;
	
	public void clear() {
		includeNull = false;
		condition = null;
		values = null;
		fields = null;
	}
	
	public void free() {
		DBUtils.close(conn);
		conn = null;
	}
}
