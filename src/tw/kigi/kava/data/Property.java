package tw.kigi.kava.data;

import java.lang.reflect.Field;

import tw.kigi.kava.data.annotation.Column;
import tw.kigi.kava.data.annotation.Id;
import tw.kigi.kava.data.annotation.Expression;
import tw.kigi.kava.data.exception.UnsupportedTypeException;
import tw.kigi.kava.data.operator.OpUtils;
import tw.kigi.kava.data.operator.Operator;

public final class Property {

	private String schemaName;
	private String name;
	
	private String tableName;
	private String columnName;
	
	private boolean expr = false;
	
	private String defaultValue;
	private boolean nullAble = false;
	
	private boolean primary = false;
	
	private Operator op;
	
	String toColumn(String column_name, String property_name) {
		return "".equals(column_name) 
				? Convention.toColumnName(property_name)
				: column_name;
	}
	
	protected Property(Class<?> clazz, String schema_name, String table_name, 
					   Field field) throws UnsupportedTypeException {
		
		schemaName = schema_name;
		name = field.getName();
		
		tableName = table_name;
		op = OpUtils.getOperator(field.getType());
	}
	
	public Property(Class<?> clazz, String schema_name, String table_name, 
					Field field, Column column) throws UnsupportedTypeException {
		
		this(clazz, schema_name, table_name, field);
		columnName = toColumn(column.name(), name);
		defaultValue = column.defaultValue();
		nullAble = column.nullAble();
	}
	
	public Property(Class<?> clazz, String schema_name, String table_name, 
					Field field, Id id) throws UnsupportedTypeException {
		
		this(clazz, schema_name, table_name, field);
		columnName = toColumn(id.name(), name);
		nullAble = false;
		primary = true;
	}
	
	public Property(Class<?> clazz, String schema_name, String table_name, 
					Field field, Expression expression) 
							throws UnsupportedTypeException {
		
		this(clazz, schema_name, table_name, field);
		expr = true;
	}
}
