package tw.kigi.kava.data.operator;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import tw.kigi.kava.data.exception.ParseValueException;
import tw.kigi.kava.data.exception.UnsupportedTypeException;

public class EnumOp<T, E extends Enum<?> & EnumType<T>> implements Operator<E> {

	protected Class<E> clazz;
	protected Operator<T> op;
	
	@SuppressWarnings("unchecked")
	public EnumOp(Class<E> clazz) throws UnsupportedTypeException {
		
		if (clazz.isEnum()) {
			for (Type type : clazz.getGenericInterfaces()) {
				ParameterizedType pt = (ParameterizedType) type;
				
				if (EnumType.class.equals(pt.getRawType())) {
					Type type1 = pt.getActualTypeArguments()[0];	
					op = OpUtils.getOperator((Class<T>) type1);
					
					if (op != null) {
						this.clazz = clazz;
						return;
					}
				}
			}
		}
		
		throw new UnsupportedTypeException("Type Not Supported " + clazz);
	}
	
	protected E toEnum(T value) {
		for (E e : clazz.getEnumConstants()) {
			if (e.toValue().equals(value)) {
				return e;
			}
		}
		
		throw new ParseValueException("Value is not in Enum " + value);
	}
	
	@Override
	public E parseValue(String value) throws ParseValueException {
		for (E e : clazz.getEnumConstants()) {
			if (e.name().equals(value)) {
				return e;
			}
		}
		
		return toEnum(op.parseValue(value));
	}

	@Override
	public void setParam(PreparedStatement statement, int index, E value)
			throws SQLException {
		
		op.setParam(statement, index, value.toValue());
	}

	@Override
	public E getResult(ResultSet result, String label) throws SQLException {
		
		return toEnum(op.getResult(result, label));
	}


}
