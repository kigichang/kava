package tw.kigi.kava.data;

import java.util.HashMap;

import tw.kigi.kava.data.annotation.Table;

public final class Schema {

	protected static HashMap<Class<?>, Schema> schemas = 
			new HashMap<Class<?>, Schema>();
	
	
	private Class<?> clazz;
	private String tableName;
	
	private Schema(Class<?> clazz) {
		Table table = clazz.getAnnotation(Table.class);
		tableName = table.name();
		if (tableName == null || "".equals(tableName = tableName.trim())) {
			tableName = Convention.toColumnName(tableName);
		}
		
		this.clazz = clazz;
		
		
		
	}
}
