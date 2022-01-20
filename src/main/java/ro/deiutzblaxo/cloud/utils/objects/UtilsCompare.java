package ro.deiutzblaxo.cloud.utils.objects;

import ro.deiutzblaxo.cloud.datastructure.OrderType;

import java.lang.reflect.Field;

public class UtilsCompare {

    public static boolean implementsComparableField(Field field) {

        return field.getClass().isAssignableFrom(Comparable.class);

    }

    public static <T extends Comparable> int compare(T object1, T object2, OrderType type) {

        switch (type) {
            case ASCENDING:
                return object1.compareTo(object2);
            case DESCENDING:
                return object2.compareTo(object1);
            default:
                return compare(object1, object2, OrderType.ASCENDING);
        }


    }

}
