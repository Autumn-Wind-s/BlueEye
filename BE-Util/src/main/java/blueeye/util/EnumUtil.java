package blueeye.util;

import sun.reflect.ConstructorAccessor;
import sun.reflect.FieldAccessor;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author SDJin
 * @CreationDate 2023/2/16 21:26
 * @Description ：
 */
public class EnumUtil {
    /**
     * 新增枚举值
     * @param clazz 枚举类型
     * @param fieldClass 枚举字段类型，第一个是枚举名类型String
     * @param fieldNames 枚举字段，第一个是枚举名
     * @throws Exception
     */
    public static <T>void addEnum(Class clazz,Class[] fieldClass, String[]fieldNames) throws Exception {
        ReflectionFactory reflectionFactory = ReflectionFactory.getReflectionFactory();
        //name,ordinal,其他自定义字段
        List<Class> allFieldClass = new ArrayList<>(fieldClass.length+1);
        allFieldClass.add(fieldClass[0]);
        allFieldClass.add(int.class);
        for(int i=1;i<fieldClass.length;i++){
            allFieldClass.add(fieldClass[i]);
        }
        Class[] classes = allFieldClass.toArray(new Class[]{});
        Constructor<T> constructor = clazz.getDeclaredConstructor(classes);
        ConstructorAccessor constructorAccessor = reflectionFactory.newConstructorAccessor(constructor);
        List<Object> allFields = new ArrayList<>(fieldNames.length+1);
        allFields.add(fieldNames[0]);
        allFields.add(0);
        for(int i=1;i<fieldNames.length;i++){
            allFields.add(fieldNames[i]);
        }
        T newEnum = (T) constructorAccessor.newInstance(allFields.toArray());
        System.out.println("新增枚举："+newEnum);
        Field $VALUES = clazz.getDeclaredField("$VALUES");
        $VALUES.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        int modifiers = modifiersField.getInt($VALUES);
        modifiers &= ~Modifier.FINAL;
        modifiersField.setInt($VALUES, modifiers);
        FieldAccessor fieldAccessor = reflectionFactory.newFieldAccessor($VALUES, false);
        T[] ts = (T[]) fieldAccessor.get(clazz);
        List<T> list = new ArrayList<>(Arrays.asList(ts));
        list.add(newEnum);
        fieldAccessor.set(clazz,list.toArray(ts));
    }

}
