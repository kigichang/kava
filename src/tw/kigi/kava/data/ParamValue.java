package tw.kigi.kava.data;

import tw.kigi.kava.data.exception.UnsupportedTypeException;
import tw.kigi.kava.data.operator.OpUtils;
import tw.kigi.kava.data.operator.Operator;

public class ParamValue<T> {
	protected Operator<T> op;
	protected T value;

	@SuppressWarnings("unchecked")
	public ParamValue(T value) throws UnsupportedTypeException {
		
		this.op = (Operator<T>) OpUtils.getOperator(value.getClass());
		this.value = value;
	}
}
