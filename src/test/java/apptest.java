import ro.deiutzblaxo.cloud.data.redis.RedisConnection;

import java.io.*;

public class apptest {
    static void printSpiral(int n) {
        for (int i = 0; i < n; i++) {
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


    public static void main(String[] args) throws FileNotFoundException, IOException {
        RedisConnection con = new RedisConnection("192.168.1.132",6379,"","clouddev");
        System.out.println(con.jedisPool.getResource().set("test","a"));

    }
}
