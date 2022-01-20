package ro.deiutzblaxo.cloud.datastructure;

import ro.deiutzblaxo.cloud.utils.Reflection;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class QuickSortReflectByMethodReturn {


    public static <Y> void sort(List<Y> lista, int leftest, int rightest, String methodName,OrderType orderType) throws NoSuchFieldException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {


        if (leftest < rightest) {
            int b = partition(lista, leftest, rightest, methodName,orderType);
            sort(lista, leftest, b - 1, methodName,orderType);
            sort(lista, b + 1, rightest, methodName,orderType);
        }
    }


    private static <T,V extends Comparable> int partition(List<T> lista, int leftest, int rightest, String methodName,OrderType type) throws NoSuchFieldException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        int left = leftest;
        int right = rightest;

        T pivot = lista.get(left);
        while (left < right) {

            if(type.equals(OrderType.ASCENDING)) {
                while (((V)Reflection.getValueByMethod(lista.get(left), methodName))
                        .compareTo((V)Reflection.getValueByMethod(pivot, methodName)) <= 0 && left < right) {
                    left += 1;
                }
                while (((V)Reflection.getValueByMethod(lista.get(right), methodName))
                        .compareTo((V)Reflection.getValueByMethod(pivot, methodName)) > 0) {

                    right -= 1;
                }
                if (left < right) {
                    swap(lista, left, right);
                }
            }else{
                while (((V)Reflection.getValueByMethod(pivot, methodName))
                        .compareTo((V)Reflection.getValueByMethod(lista.get(left), methodName)) <= 0 && left < right) {
                    left += 1;
                }
                while (((V)Reflection.getValueByMethod(pivot, methodName))
                        .compareTo((V)Reflection.getValueByMethod(lista.get(right), methodName)) > 0) {

                    right -= 1;
                }
            }
        }
        lista.set(leftest, lista.get(right));// inlocuim primul element ( pivot) cu elementul din dreapta
        lista.set(right, pivot);// inlocuim elementul din dreapta cu pivot
        return right;// return locatia pivotului
    }

    static <T> void swap(List<T> lista, int A, int B) {

        T temp = lista.get(A);
        lista.set(A, lista.get(B));
        lista.set(B, temp);

    }


}
