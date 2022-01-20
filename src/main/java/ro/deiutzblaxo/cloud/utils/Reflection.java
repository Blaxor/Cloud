package ro.deiutzblaxo.cloud.utils;

import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Reflection {

    public static <Y, T extends Comparable> T getVariableByName(Y object, String variableName) throws NoSuchFieldException {
        Field field = object.getClass().getDeclaredField(variableName);
        boolean acc = field.isAccessible();
        field.setAccessible(true);
        T obj = null;
        try {
            obj = ((T) field.get(object));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        field.setAccessible(acc);

        return obj;
    }

    @SneakyThrows
    public static Object getVariableByType(Object object, Class type) {
        Field[] fields = object.getClass().getDeclaredFields();
        Field field = null;


        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            try {
                type.cast(fields[i].get(object));
                field = fields[i];
                break;
            } catch (Exception e) {
            }
        }
        if (field == null) {
            throw new RuntimeException("Field not found!");
        }
        field.setAccessible(true);
        return field.get(object);
    }

    public static Object getValueByMethod(Object object, String methodNmae) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = object.getClass().getMethod(methodNmae);
        boolean access = method.isAccessible();
        method.setAccessible(true);
        Object value = method.invoke(object);
        method.setAccessible(access);
        return value;

    }

}
