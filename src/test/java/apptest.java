import ro.deiutzblaxo.cloud.data.mysql.classic.MySQLManagerNormal;
import ro.deiutzblaxo.cloud.data.mysql.hikari.MySQLConnectionHikari;
import ro.deiutzblaxo.cloud.expcetions.ToManyArgs;
import ro.deiutzblaxo.cloud.math.geometry.twod.PointsGenerator;
import ro.deiutzblaxo.cloud.math.geometry.twod.objects.Point2D;
import ro.deiutzblaxo.cloud.datastructure.BinarySearchReflect;
import ro.deiutzblaxo.cloud.datastructure.OrderType;
import ro.deiutzblaxo.cloud.datastructure.QuickSortReflectByVariable;
import ro.deiutzblaxo.cloud.utils.objects.Pair;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class apptest {

    static void printSpiral(int n) {
        for (int i = n - 5; i < n; i++) {
            for (int j = 0; j < n; j++) {

                // x stores the layer in which (i, j)th
                // element lies
                int x;

                // Finds minimum of four inputs
                x = Math.min(Math.min(i, j),
                        Math.min(n - 1 - i, n - 1 - j));

                // For upper right half
                if (i <= j)
                    System.out.print((n - 2 * x) * (n - 2 * x) -
                            (i - x) - (j - x) + "\t");

                    // for lower left half
                else
                    System.out.print((n - 2 * x - 2) * (n - 2 * x - 2) +
                            (i - x) + (j - x) + "\t");
            }
            System.out.println();
        }
    }


    public static void main(String[] args) {
        System.out.println("started: " + LocalDateTime.now());
        //PointsGenerator.pointsForACircle(new Point2D(0, 0), 5, 100);
        System.out.println("finish: " + LocalDateTime.now());
        MySQLManagerNormal manager = new MySQLManagerNormal(
                new MySQLConnectionHikari(
                        "192.168.1.132",
                        3306,
                        "TEST",
                        "deiu",
                        "password",
                        "",
                        5,
                        0),
                2);
        System.out.println("test 2");
        manager.createTable("test", "ID VARCHAR(255)", "TEST VARCHAR(255)");
        System.out.println("test 3");
        try {
            manager.insert("test", new String[]{"ID","TEST"}, new String[]{"test","TEST"});
        } catch (ToManyArgs e) {
            e.printStackTrace();
        }
        System.out.println("test 4");
        System.out.println("test 5");
       manager.getConnection().close();
    }


    private static void testMath() {
        Point2D from = new Point2D(0, 0);
        Point2D to = new Point2D(1, 1);

        System.out.println(from.distance(to));

    }

    private static void testPointsGenerator() {
        Point2D from = new Point2D(5, 6);
        Point2D to = new Point2D(-3, 3);
        System.out.println(LocalDateTime.now());
        System.out.println(from);
/*
        for (Point2D point2D : PointsGenerator.solution2(from,to,100)) {
            System.out.println(point2D);
        }*/
        System.out.println(to);
        System.out.println(LocalDateTime.now());
    }

    private static void testBinary() throws NoSuchFieldException {
        ArrayList<Pair<String, Integer>> abc = new ArrayList<Pair<String, Integer>>() {{
            add(new Pair<String, Integer>("String1", 1));
            add(new Pair<String, Integer>("String11", 1));
            add(new Pair<String, Integer>("String3", 3));
            add(new Pair<String, Integer>("String4", 4));
            add(new Pair<String, Integer>("String2", 2));
            add(new Pair<String, Integer>("String5", 5));

        }};

        System.out.println(abc);

        QuickSortReflectByVariable.sort(abc, 0, abc.size() - 1, "last", OrderType.ASCENDING);
        System.out.println(abc);

        System.out.println(BinarySearchReflect.BinarySearchArray(abc, "last", 1));
    }
}
