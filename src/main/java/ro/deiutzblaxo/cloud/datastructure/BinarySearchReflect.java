package ro.deiutzblaxo.cloud.datastructure;

import ro.deiutzblaxo.cloud.utils.Reflection;
import ro.deiutzblaxo.cloud.utils.objects.UtilsCompare;

import java.util.ArrayList;
import java.util.List;

public class BinarySearchReflect {

    public static <T, K extends Comparable> T BinarySearch(List<T> list, String by, K value) throws NoSuchFieldException {
        return list.get(pivotFinder(list, 0, list.size(), by, value));
    }

    public static <V, K extends Comparable> List<V> BinarySearchArray(List<V> list, String by, K value) throws NoSuchFieldException {
        ArrayList<V> lists = new ArrayList<>();

        int pivot = pivotFinder(list, 0, list.size(), by, value);
        System.out.println(pivot);
        int num = 1;
        while (pivot + num <= list.size() - 1 && Reflection.getVariableByName(list.get(pivot), by) == Reflection.getVariableByName(list.get(num + pivot), by)) {
            lists.add(list.get(num + pivot));
            num++;
        }
        num = 1;
        while (pivot - num >= 0 && Reflection.getVariableByName(list.get(pivot), by) == Reflection.getVariableByName(list.get(pivot - num), by)) {
            lists.add(list.get(pivot - num));
            num++;
        }
        lists.add(list.get(pivot));
        return lists;
    }


    public static <T, K extends Comparable> int pivotFinder(List<T> list, int leftest, int rightest, String by, K value) throws NoSuchFieldException {
        int middle = ((rightest + leftest) / 2);

        if (rightest <= leftest)
            if (Reflection.getVariableByName(list.get(middle), by).equals(value)) {
                return middle;
            }
        K middleValue = Reflection.getVariableByName(list.get(middle), by);
        if (UtilsCompare.compare(value, middleValue, OrderType.ASCENDING) > 0) {
            if (middle == leftest) {
                throw new RuntimeException("Not found");
            }
            return pivotFinder(list, middle, rightest, by, value);
        } else if (UtilsCompare.compare(value, middleValue, OrderType.ASCENDING) < 0) {
            if (middle == rightest) {
                throw new RuntimeException("Not found");
            }
            return pivotFinder(list, leftest, middle, by, value);
        } else {
            return middle;
        }


    }


}
