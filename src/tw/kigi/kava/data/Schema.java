package tw.kigi.kava.data;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import tw.kigi.kava.data.annotation.Column;
import tw.kigi.kava.data.annotation.Expression;
import tw.kigi.kava.data.annotation.Id;
import tw.kigi.kava.data.annotation.Table;
import tw.kigi.kava.data.exception.UnsupportedTypeException;

public final class Schema {

	private static HashMap<String, Schema> schemas = new HashMap<String, Schema>();
	
	public static Schema getSchea(String name) throws SQLException {
		if (schemas.containsKey(name)) {
			return schemas.get(name);
		}
		
		throw new SQLException("Schema Not Found " + name);
	}
	
	public static Schema getSchema(Class<?> clazz) throws SQLException {
		String name = clazz.getSimpleName();
		
		if (schemas.containsKey(name)) {
			return schemas.get(name);
		}
		
		Schema ret;
		try {
			ret = new Schema(clazz, name);
		} catch (NoSuchMethodException | SecurityException
				| UnsupportedTypeException e) {
			throw new SQLException(e);
		}
		
		return ret;
	}
	
	private Class<?> clazz;
	private String schemaName;
	private String tableName;
	private HashMap<String, Property> alias;
	private HashMap<String, Property> property;
	private boolean autoIncrement = false;
	private Property[] primary;
	private String[] fields;
	
	private Schema(Class<?> clazz, String schema_name) throws UnsupportedTypeException, NoSuchMethodException, SecurityException {
		Table table = clazz.getAnnotation(Table.class);
		if (table == null) {
			throw new UnsupportedTypeException("Class Must Have [Table] Annotation " + clazz);
		}
		
		schemaName = schema_name;
		tableName = Convention.toColumn(table.name(), schema_name);
		this.clazz = clazz;
		
		alias = new HashMap<String, Property>();
		property = new HashMap<String, Property>();
		
		HashSet<String> field_set = new HashSet<String>();
		List<Property> primary_lst = new ArrayList<Property>();
		
		for(; !Object.class.equals(clazz); clazz = clazz.getSuperclass()) {
			if (clazz.getAnnotation(Table.class) == null) {
				continue;
			}
			
			for(Field field : clazz.getDeclaredFields()) {
				
				if (field_set.contains(field.getName())) {
					continue;
				}
				
				Property p = null;
				if (field.getAnnotation(Id.class) != null) {
					p = new Property(clazz, schemaName, tableName, field, field.getAnnotation(Id.class));
					autoIncrement |= p.isAutoIncrement();
					primary_lst.add(p);
				}
				else if (field.getAnnotation(Column.class) != null) {
					p = new Property(clazz, schemaName, tableName, field, field.getAnnotation(Column.class));
				}
				else if (field.getAnnotation(Expression.class) != null) {
					p = new Property(clazz, schemaName, tableName, field, field.getAnnotation(Expression.class));
				}
				
				if (p == null) {
					continue;
				}
				
				field_set.add(p.getName());
				alias.put(p.getAliasName(), p);
				property.put(p.getPropertyName(), p);
			}
		}
		
		primary = primary_lst.toArray(new Property[primary_lst.size()]);
		fields = property.keySet().toArray(new String[property.size()]);
		
		int len = fields.length;
		
		for (int i = 0; i < len; i++) {
			for (int j = i + 1; j < len; j++) {
				if (fields[i].length() < fields[j].length()) {
					String swap = fields[j];
					fields[j] = fields[i];
					fields[i] = swap;
				}
			}
		}
	}
	
	private Schema(Class<?> clazz) throws UnsupportedTypeException, NoSuchMethodException, SecurityException {
		this(clazz, clazz.getSimpleName());
	}
	
	public boolean hasAutoIncrement() {
		return this.autoIncrement;
	}
	
	public Property[] getPrimary() {
		return this.primary;
	}
	
	public String getTableName() {
		return this.tableName;
	}
	
	public String getSchemaName() {
		return this.schemaName;
	}
	
	public Property getAlias(String alias_name) throws SQLException {
		if (alias.containsKey(alias_name)) {
			return alias.get(alias_name);
		}
		
		throw new SQLException("Alias Name Not Exists " + alias_name);
	}
	
	public Property getProperty(String property_name) throws SQLException {
		if (property.containsKey(property_name)) {
			return property.get(property_name);
		}
		
		throw new SQLException("Property Name Not Exists " + property_name);
	}
	
	public String[] getFields() {
		return this.fields;
	}
	
	public Property[] getProperties() {
		return alias.values().toArray(new Property[alias.size()]);
	}
}
