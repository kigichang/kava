package tw.kigi.kava.data;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import tw.kigi.kava.data.exception.UnsupportedTypeException;

public abstract class Query<T> {
	
	@SuppressWarnings("rawtypes")
	public static final ParamValue[] EMPTY_PARAM_ARRAY 
		= new ParamValue[] { };
	
	public static final OrderValue[] EMPTY_ORDER_ARRAY
		= new OrderValue[] {};
	
	
	private Class<T> clazz;
	private Schema schema;
	private Connection conn;
	
	private String sql;
	
	private boolean includeNull;
	
	private String condition;
	private ParamValue<?>[] values;
	private String[] fields;
	private String[] groups;
	private OrderValue[] orders;
	
	public Query(Class<T> clazz, Connection conn) throws SQLException {
		schema = Schema.getSchema(clazz);
		this.conn = conn;
		this.clazz = clazz;
		
		clear();
	}
	
	public Query<T> condition(String condition) {
		this.condition = StringUtils.trimToEmpty(condition);
		return this;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Query<T> values(Object...values) throws UnsupportedTypeException {
		if (values == null || values.length == 0) {
			this.values = EMPTY_PARAM_ARRAY;
		}
		else {
			this.values = new ParamValue[values.length];
			int i = 0;
			for(Object val : values) {
				this.values[i++] = new ParamValue(val);
			}
		}

		return this;
	}
	
	public Query<T> fields(String...fields) {
		this.fields = fields == null || fields.length == 0
						? ArrayUtils.EMPTY_STRING_ARRAY
						: fields;
		
		return this;
	}
	
	public Query<T> groupBy(String...groups) {
		this.groups = groups == null || groups.length == 0
						? ArrayUtils.EMPTY_STRING_ARRAY
						: groups;
		
		return this;
	}
	
	public Query<T> orderBy(OrderValue...orders) {
		this.orders = orders == null || orders.length == 0
						? EMPTY_ORDER_ARRAY
						: orders;
		
		return this;
	}
	
	public Query<T> includeNull() {
		includeNull = true;
		return this;
	}
	
	public Query<T> excludeNull() {
		includeNull = false;
		return this;
	}
	
	protected String selectExpr() throws SQLException {
		if (ArrayUtils.EMPTY_STRING_ARRAY.equals(fields)) {
			fields = schema.getFields();
		}
		
		StringBuilder ret = new StringBuilder("select ");
		for(String field : fields) {
			Property p = field.indexOf('.') < 0 
					? schema.getProperty(schema.getSchemaName() + "." + field)
					: schema.getProperty(field);
			
			ret.append(p.getColumnName() + " " + p.getAliasName() + ",");
		}
		
		ret.setCharAt(ret.length() - 1, ' ');
		return ret.toString();
		
	}
	
	protected String updateExpr() throws SQLException {
		if (ArrayUtils.EMPTY_STRING_ARRAY.equals(fields)
				|| EMPTY_PARAM_ARRAY.equals(values)
				|| StringUtils.EMPTY.equals(condition)) {
			throw new SQLException("Update without Condition is not permitted");
			
		}
			
		
		StringBuilder ret = new StringBuilder("update ")
								.append(schema.getTableName())
								.append(" set ");
		
		for (String field : fields) {
			Property p = field.indexOf('.') < 0 
					? schema.getProperty(schema.getSchemaName() + "." + field)
					: schema.getProperty(field);
					
			ret.append(p.getColumn()).append(" = ?,");
		}
		
		ret.setLength(ret.length() - 1);
		return ret.toString();
	}
	
	protected String insertExpr() throws SQLException {
		if (ArrayUtils.EMPTY_STRING_ARRAY.equals(fields) || EMPTY_PARAM_ARRAY.equals(values)) {
			throw new SQLException("Insert without Values is not permitted");
		}
		
		if (fields.length != values.length) {
			throw new SQLException("Length of Fiels and Values are not matched");
		}
		
		StringBuilder ret = new StringBuilder("insert into ").append(schema.getTableName()).append(" (");
		StringBuilder val = new StringBuilder(" values(");
		
		for (String field : fields) {
			Property p = field.indexOf('.') < 0 
							? schema.getProperty(schema.getSchemaName() + "." + field)
							: schema.getProperty(field);

			ret.append(p.getColumn()).append(",");
			val.append("?,");
		}
		ret.replace(ret.length() - 1, ret.length(), ")");
		val.replace(val.length() - 1, val.length(), ")");
		return ret.append(val).toString();
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
		return "where " + tmp;
	}
	
	protected String groupExpr() throws SQLException {
		if (ArrayUtils.EMPTY_STRING_ARRAY.equals(groups)) {
			return "";
		}
		
		StringBuilder ret = new StringBuilder("group by ");
		
		for(String g : groups) {
			ret.append(schema.getProperty(g).getColumnName()).append(',');
		}
		
		ret.setLength(ret.length() - 1);
		return ret.toString();
	}
	
	protected String orderExpr() throws SQLException {
		if (EMPTY_ORDER_ARRAY.equals(orders)) {
			return "";
		}
		
		StringBuilder ret = new StringBuilder("order by ");
		for(OrderValue order : orders) {
			ret.append(schema.getProperty(order.getProperty()).getColumnName())
				.append(' ')
				.append(order.getSort().name()).append(',');
		}
		
		ret.setLength(ret.length() - 1);
		return ret.toString();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected PreparedStatement prepared(String sql) throws SQLException {
		this.sql = sql;
		PreparedStatement stmt = conn.prepareStatement(sql);
		
		int seq = 0;
		for (ParamValue param: values) {
			param.op.setParam(stmt, ++seq, param.value);
		}
		
		return stmt;
	}
	
	/**
	 * 
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings({ "unchecked" }) 
	public T[] find() throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append(selectExpr()).append(' ')
			.append(fromExpr()).append(' ')
			.append(whereExpr()).append(' ')
			.append(groupExpr()).append(' ')
			.append(orderExpr());
		
		
		PreparedStatement stmt = prepared(sql.toString());
		
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
	
	public int delete() throws SQLException {
		if (StringUtils.isBlank(condition) || EMPTY_PARAM_ARRAY.equals(values)) {
			throw new SQLException("Delete without Condition is not permitted");
		}
		
		StringBuilder sql = new StringBuilder("delete from ")
								.append(schema.getTableName()).append(' ')
								.append(whereExpr());
		
		
		PreparedStatement stmt = prepared(sql.toString());
		
		int ret = stmt.executeUpdate();
		DBUtils.close(stmt);
		return ret;
		
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public int deleteBy(String field, Object value) throws SQLException, UnsupportedTypeException {
		this.condition = field + " = ?";
		this.values = new ParamValue[] { new ParamValue(value) };
		return delete();
	}
	
	
	public int update() throws SQLException {
		StringBuilder sql = new StringBuilder(updateExpr()).append(' ')
								.append(whereExpr());
		
		PreparedStatement stmt = prepared(sql.toString());
		int ret = stmt.executeUpdate();
		DBUtils.close(stmt);
		return ret;
	}
	
	public int update(T data) throws SQLException {
		// TODO update
		return 0;
	}
	
	public int insert() throws SQLException {
		PreparedStatement stmt = prepared(insertExpr());
		int ret = stmt.executeUpdate();
		DBUtils.close(stmt);
		
		return ret;
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
	
	public Query<T> clear() {
		sql = StringUtils.EMPTY;
		includeNull = false;
		condition = StringUtils.EMPTY;;
		values = EMPTY_PARAM_ARRAY;
		fields = ArrayUtils.EMPTY_STRING_ARRAY;
		groups = ArrayUtils.EMPTY_STRING_ARRAY;
		orders = EMPTY_ORDER_ARRAY;
		return this;
	}
	
	@SuppressWarnings("rawtypes")
	public String preparedSQL() {
		StringBuilder ret = new StringBuilder(sql);
		ret.append("\n");
		
		for (ParamValue p : values) {
			ret.append(p.getValueClass())
			.append(":[").append(p.value).append("] ");
		}
		
		return ret.toString();
	}
	
	/*public void free() {
		DBUtils.close(conn);
		conn = null;
	}*/
}
