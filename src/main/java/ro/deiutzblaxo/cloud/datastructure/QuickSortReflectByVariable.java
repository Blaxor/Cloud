package ro.deiutzblaxo.cloud.datastructure;

import ro.deiutzblaxo.cloud.utils.Reflection;

import java.util.List;

public class QuickSortReflectByVariable {


    public static <Y> void sort(List<Y> lista, int leftest, int rightest, String fieldName,OrderType orderType) throws NoSuchFieldException {


        if (leftest < rightest) {
            int b = partition(lista, leftest, rightest, fieldName,orderType);
            sort(lista, leftest, b - 1, fieldName,orderType);
            sort(lista, b + 1, rightest, fieldName,orderType);
        }
    }


    private static <T> int partition(List<T> lista, int leftest, int rightest, String fieldName,OrderType type) throws NoSuchFieldException {
        int left = leftest;
        int right = rightest;

        T pivot = lista.get(left);
        while (left < right) {

            if(type.equals(OrderType.ASCENDING)) {
                while (Reflection.getVariableByName(lista.get(left), fieldName)
                        .compareTo(Reflection.getVariableByName(pivot, fieldName)) <= 0 && left < right) {
                    left += 1;
                }
                while (Reflection.getVariableByName(lista.get(right), fieldName)
                        .compareTo(Reflection.getVariableByName(pivot, fieldName)) > 0) {

                    right -= 1;
                }
                if (left < right) {
                    swap(lista, left, right);
                }
            }else{
                while (Reflection.getVariableByName(pivot, fieldName)
                        .compareTo(Reflection.getVariableByName(lista.get(left), fieldName)) <= 0 && left < right) {
                    left += 1;
                }
                while (Reflection.getVariableByName(pivot, fieldName)
                        .compareTo(Reflection.getVariableByName(lista.get(right), fieldName)) > 0) {

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
