package ro.deiutzblaxo.cloud.net.udp;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class UDPConnection {
    /*

        DatagramSocket socket;
        public UDPConnection(int port) throws SocketException {
            InetAddress address = InetAddress.getByAddress()

            socket = new DatagramSocket();
        }
    */
    public static void sendMessage(InetAddress address, int port, List<Object> objects) throws IOException {

        DatagramSocket socket = new DatagramSocket();
        byte[] message = write(objects);
        byte[] numberBytes = write(message.length);
        DatagramPacket packet = new DatagramPacket(numberBytes, numberBytes.length, address, port);

        socket.send(packet);
        packet = new DatagramPacket(message, message.length, address, port);
        socket.send(packet);
        socket.close();

    }


    public static List<Object> readMessage(int port) throws IOException {
        DatagramSocket socket = new DatagramSocket(port);
        byte[] bytes = new byte[4];
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
        socket.receive(packet);
        int numberBytes = readInt(packet.getData());
        System.out.println(new String(packet.getData()));
        System.out.println("Bytes: " + numberBytes);
        bytes = new byte[numberBytes];
        packet = new DatagramPacket(bytes, numberBytes);
        socket.receive(packet);
        socket.close();
        return read(bytes);
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
        System.out.println(output.toByteArray().length);
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

}
