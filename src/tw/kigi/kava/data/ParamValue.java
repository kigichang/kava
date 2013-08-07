package tw.kigi.kava.data;

import tw.kigi.kava.data.exception.UnsupportedTypeException;
import tw.kigi.kava.data.operator.OpUtils;
import tw.kigi.kava.data.operator.Operator;

public class ParamValue<T> {
	protected Operator<T> op;
	protected T value;

	public ParamValue(T value) throws UnsupportedTypeException {
		
		this.op = OpUtils.getOperator(getValueClass());
		this.value = value;
	}
	
	@SuppressWarnings("unchecked")
	public Class<T> getValueClass() {
		return (Class<T>) value.getClass();
	}
}
