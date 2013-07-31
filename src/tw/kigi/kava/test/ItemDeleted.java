package tw.kigi.kava.test;

import tw.kigi.kava.data.operator.EnumType;

public enum ItemDeleted implements EnumType<Integer> {
	ALIVE(1), DELETED(0);
	
	private Integer value;
	private ItemDeleted(Integer value) {
		this.value = value;
	}

	@Override
	public Integer toValue() {
		return value;
	}

}
