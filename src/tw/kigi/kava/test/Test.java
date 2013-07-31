package tw.kigi.kava.test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import tw.kigi.kava.data.operator.EnumType;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		for(Type t : ItemDeleted.class.getGenericInterfaces()) {
			System.out.println(t);
			ParameterizedType pt = (ParameterizedType)t;
			//System.out.println(pt.getActualTypeArguments())
			System.out.println(pt.getClass());
			System.out.println(pt.getOwnerType());
			System.out.println(pt.getRawType());
			if (pt.getRawType().equals(EnumType.class)) {
				System.out.println("ok");
			}
			for (Type t1 : pt.getActualTypeArguments()) {
				System.out.println(t1);
			}
		}
		
	}

}
