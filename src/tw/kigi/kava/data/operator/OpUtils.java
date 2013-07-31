package tw.kigi.kava.data.operator;

import java.math.BigDecimal;
import java.util.HashMap;

import tw.kigi.kava.data.exception.UnsupportedTypeException;

public class OpUtils {
	private static HashMap<Class<?>, Operator<?>> ops;
	
	{
		ops = new HashMap<Class<?>, Operator<?>>();
		
		ops.put(String.class, new StringOp());
		ops.put(BigDecimal.class, new BigDecimalOp());
		ops.put(Long.class, new LongOp());
		ops.put(Integer.class, new IntegerOp());
		ops.put(ShortOp.class, new ShortOp());
		ops.put(Float.class, new FloatOp());
		ops.put(Double.class, new DoubleOp());
		ops.put(java.util.Date.class, new DateTimeOp());
		ops.put(java.sql.Date.class, new DateOp());
		ops.put(java.sql.Time.class, new TimeOp());
		ops.put(Character.class, new CharOp());
		ops.put(Boolean.class, new BooleanOp());
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> Operator<T> getOperator(Class<T> clazz) throws UnsupportedTypeException {
		
		Operator<T> op = (Operator<T>)ops.get(clazz);
		
		if (op != null) {
			return op;
		}
		
		if (clazz.isEnum()) {
			EnumOp eop = new EnumOp(clazz);
			ops.put(clazz, eop);
			return eop;
		}
		
		throw new UnsupportedTypeException("Class not Supported " + clazz);
	}
}
