package tw.kigi.kava.data;

import tw.kigi.kava.data.exception.UnsupportedTypeException;
import tw.kigi.kava.data.operator.OpUtils;
import tw.kigi.kava.data.operator.Operator;

public class SQLParam<T> {

	protected Class<T> type;
	protected Operator<T> op;
	protected T value;
	
	public SQLParam(Class<T> type, T value) throws UnsupportedTypeException {
		if (value != null && !value.getClass().equals(type)) {
			throw new UnsupportedTypeException("Value and Type not the same");
		}
		
		this.op = OpUtils.getOperator(type);
		this.value = value;
		this.type = type;
	}
	
}
