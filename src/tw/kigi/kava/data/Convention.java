package tw.kigi.kava.data;

public class Convention {
	public static boolean isUpper(char c) {
		return ('A' <= c && c <= 'Z');
	}
	
	public static boolean isLower(char c) {
		return ('a' <= c && c <= 'z');
	}
	
	public static char toUpper(char c) {
		return ('a' <= c && c <= 'z') ? (char)(0x5f & c) : c;
	}
	
	public static char toLower(char c) {
		return ('A' <= c && c <= 'Z') ? (char)(0x20 | c) : c;
	}
	
	public static String capitalize(String str) {
		int len = 0;
		if (str == null || (len = str.length()) == 0) {
			return str;
		}
		
		return new StringBuffer(len)
					.append(toUpper(str.charAt(0)))
					.append(str.substring(1)).toString();
	}
	
	public static String uncapitalize(String str) {
		int len = 0;
		if (str == null || (len = str.length()) == 0) {
			return str;
		}
		
		return new StringBuffer(len)
				.append(toLower(str.charAt(0)))
				.append(str.substring(1)).toString();
	}
	
	public static String toPropertyName(String name) {
		int len = 0;
		if (name == null || (len = name.length()) == 0) {
			return name;
		}
		
		StringBuffer ret = new StringBuffer(len).append(toUpper(name.charAt(0)));
		
		for (int i = 1; i < len; i++) {
			if (name.charAt(i) == '_') {
				continue;
			}
			else if (name.charAt(i - 1) == '_') {
				ret.append(toUpper(name.charAt(i)));
			}
			else {
				ret.append(name.charAt(i));
			}
		}
		
		return ret.toString();
	}
	
	public static String toColumnName(String name) {
		int len = 0;
		if(name == null || (len = name.length()) == 0) {
			return name;
		}
		
		/* 10 是猜底線不會加超過 10 個 */
		StringBuffer ret = 
				new StringBuffer(len + 10).append(toLower(name.charAt(0)));

		for (int i = 1; i < len; i++) {
			if (isUpper(name.charAt(i))) {
				ret.append('_').append(toLower(name.charAt(i)));
			}
			else {
				ret.append(name.charAt(i));
			}
		}
		
		return ret.toString();
	}
	
	public static String toColumn(String column_name, String property_name) {
		return column_name == null || "".equals(column_name = column_name.trim()) 
				? Convention.toColumnName(property_name)
				: column_name;
	}
}
