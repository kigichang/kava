package tw.kigi.kava.data;

public class OrderValue {
	
	private String property;
	private Sort sort;
	
	public OrderValue(String property, Sort sort) {
		this.property = property;
		this.sort = sort;
	}
	
	public OrderValue(String property) {
		this(property, Sort.ASC);
	}

	public String getProperty() {
		return property;
	}

	public Sort getSort() {
		return sort;
	}

	
}
