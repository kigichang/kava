package tw.kigi.kava.data;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import tw.kigi.kava.data.annotation.Column;
import tw.kigi.kava.data.annotation.Id;
import tw.kigi.kava.data.annotation.Expression;
import tw.kigi.kava.data.exception.UnsupportedTypeException;
import tw.kigi.kava.data.operator.OpUtils;
import tw.kigi.kava.data.operator.Operator;

public final class Property {

	private String schemaName;
	private String name;
	private String aliasName;
	private String propertyName;
	
	private String tableName;
	private String columnName;
	private String column;
	
	private boolean expr;
	
	private boolean nullAble;
	
	private boolean primary;
	private boolean autoIncrement;
	private String sequence;
	
	private Operator<?> operator;
	private Object defaultValue;
	
	private Method setter;
	private Method getter;
	
	protected Property(Class<?> clazz, String schema_name, String table_name, 
					   Field field) throws UnsupportedTypeException, NoSuchMethodException, SecurityException {
		
		schemaName = schema_name;
		name = field.getName();
		String tmp = Convention.capitalize(name);
		aliasName = new StringBuilder(schema_name)
						.append('_')
						.append(tmp).toString();
		
		propertyName = new StringBuilder(schema_name)
						.append('.')
						.append(tmp).toString();
		
		tableName = table_name;
		operator = OpUtils.getOperator(field.getType());
		
		setter = clazz.getMethod("set" + tmp, field.getType());
		getter = clazz.getMethod(
					(Boolean.class.equals(field.getType()) ? "is" : "get") + tmp);
	}
	
	public Property(Class<?> clazz, String schema_name, String table_name, 
					Field field, Column column) throws UnsupportedTypeException, NoSuchMethodException, SecurityException {
		
		this(clazz, schema_name, table_name, field);
		this.column = Convention.toColumn(column.name(), name);
		
		columnName = new StringBuilder(schema_name)
						.append('.')
						.append(this.column).toString();
		
		defaultValue = operator.parseValue(column.defaultValue());
		nullAble = column.nullAble();
		expr = false;
		primary = false;
		autoIncrement = false;
	}
	
	public Property(Class<?> clazz, String schema_name, String table_name, 
					Field field, Id id) throws UnsupportedTypeException, NoSuchMethodException, SecurityException {
		
		this(clazz, schema_name, table_name, field);
		this.column = Convention.toColumn(id.name(), name);
		
		columnName = new StringBuilder(schema_name)
						.append('.')
						.append(this.column).toString();
		
		nullAble = false;
		expr = false;
		primary = true;
		autoIncrement = id.autoIncrement();
		sequence = id.sequence();
	}
	
	public Property(Class<?> clazz, String schema_name, String table_name, 
					Field field, Expression expression) 
							throws UnsupportedTypeException, NoSuchMethodException, SecurityException {
		
		// TODO expression
		this(clazz, schema_name, table_name, field);
		nullAble = true;
		expr = true;
		primary = false;
		autoIncrement = false;
	}
	
	public Object get(Object data) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return getter.invoke(data);
	}
	
	public void set(Object data, Object value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		setter.invoke(data, value);
	}

	public String getSchemaName() {
		return schemaName;
	}

	public String getName() {
		return name;
	}

	public String getAliasName() {
		return aliasName;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public String getTableName() {
		return tableName;
	}

	public String getColumnName() {
		return columnName;
	}

	public boolean isExpr() {
		return expr;
	}

	public boolean isNullAble() {
		return nullAble;
	}

	public boolean isPrimary() {
		return primary;
	}

	public boolean isAutoIncrement() {
		return autoIncrement;
	}

	public String getSequence() {
		return sequence;
	}

	public Operator<?> getOperator() {
		return operator;
	}

	public Object getDefaultValue() {
		return defaultValue;
	}
}
