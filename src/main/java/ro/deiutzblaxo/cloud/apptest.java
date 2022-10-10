package ro.deiutzblaxo.cloud;

import com.google.common.net.InetAddresses;
import com.google.gson.JsonObject;
import ro.deiutzblaxo.cloud.datastructure.BinarySearchReflect;
import ro.deiutzblaxo.cloud.datastructure.OrderType;
import ro.deiutzblaxo.cloud.datastructure.QuickSortReflectByVariable;
import ro.deiutzblaxo.cloud.http.request.RequestMethod;
import ro.deiutzblaxo.cloud.http.request.RequestPrefab;
import ro.deiutzblaxo.cloud.http.request.RequestSender;
import ro.deiutzblaxo.cloud.math.geometry.twod.objects.Point2D;
import ro.deiutzblaxo.cloud.net.udp.UDPConnection;
import ro.deiutzblaxo.cloud.utils.objects.Pair;

import java.io.*;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class apptest {

    static void printSpiral(int n) {
        for (int i = n - 5; i < n; i++) {
            for (int j = 0; j < n; j++) {

                // x stores the layer in which (i, j)th
                // element lies
                int x;
                new InetAddress("rwar");

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

    //CLIENT
    public static void main(String[] args) throws IOException {
        ArrayList<Object> objs = new ArrayList<Object>() {{
    add("test1");
    add(132);
    add(123444444444l);
        }};
        UDPConnection.sendMessage(InetAddress.getByName("78.96.84.250"),51112,objs);
    }

    public static List<Object> read(byte[] bytes) throws IOException {
        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        ObjectInputStream stream = new ObjectInputStream(input);
        int numberObjects = stream.readInt();
        List<Object> result = new ArrayList<>();
        for (int i = 0; i < numberObjects; i++) {
            try {
                result.add(stream.readObject());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static int readInt(byte[] bytes) throws IOException {
        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        DataInputStream stream = new DataInputStream(input);
        return stream.readInt();

    }

    public static byte[] write(int i) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        DataOutputStream stream = new DataOutputStream(output);
        stream.write(i);
        stream.flush();
        return output.toByteArray();

    }

    public static byte[] write(List<Object> objects) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ObjectOutputStream stream = new ObjectOutputStream(output);
        stream.writeInt(objects.size());
        for (Object o : objects) {
            try {
                stream.writeObject(o);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        byte[] result = output.toByteArray();
        stream.flush();
        return result;
    }


    public static void main123(String[] args) {

        RequestPrefab requestDemo = new RequestPrefab();
        RequestSender requestMethod = new RequestSender();

        try {
            requestDemo.setURL(new URL("https://jsonplaceholder.typicode.com/posts"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        requestDemo.setRequestMethod(RequestMethod.POST);

        requestDemo.setHeader(new HashMap<>() {{
            put("Content-type", "application/json");
            put("charset", "UTF-8");
        }});
        JsonObject object = new JsonObject();
        object.addProperty("title", "test");
        requestDemo.setBody(object.toString());

        try {
            requestMethod.sendRequest(requestDemo);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
