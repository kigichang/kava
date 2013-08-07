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

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import tw.kigi.kava.data.exception.UnsupportedTypeException;
import tw.kigi.kava.data.operator.OpUtils;
import tw.kigi.kava.data.operator.Operator;

public abstract class Query<T> {
	
	public static final ParamValue[] EMPTY_PARAM_ARRAY 
		= new ParamValue[] {};
	
	public static final OrderValue[] EMPTY_ORDER_ARRAY
		= new OrderValue[] {};
	
	
	private Class<T> clazz;
	private Schema schema;
	private Connection conn;
	
	private String sql;
	
	private boolean includeNull;
	private String condition;
	private ParamValue[] values;
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
	
	public Query<T> values(ParamValue<?>...values) {
		this.values = values == null || values.length == 0
						? EMPTY_PARAM_ARRAY
						: values;

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
		// TODO delete
		if (StringUtils.isBlank(condition) || EMPTY_PARAM_ARRAY.equals(values)) {
			throw new SQLException("Delte with no Condition is not permitted");
		}
		
		StringBuilder sql = new StringBuilder("delete from ")
								.append(schema.getTableName())
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
		sql = StringUtils.EMPTY;
		includeNull = false;
		condition = StringUtils.EMPTY;;
		values = EMPTY_PARAM_ARRAY;
		fields = ArrayUtils.EMPTY_STRING_ARRAY;
		groups = ArrayUtils.EMPTY_STRING_ARRAY;
		orders = EMPTY_ORDER_ARRAY;
	}
	
	/*public void free() {
		DBUtils.close(conn);
		conn = null;
	}*/
}
