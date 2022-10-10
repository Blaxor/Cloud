package ro.deiutzblaxo.cloud.net.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Connection implements AutoCloseable {

    private String hostname;
    private int port;
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;

    public Connection(String hostname, int port) throws IOException {
        this.hostname = hostname;
        this.port = port;
        socket = new Socket(hostname, port);
        writer = createWriter(socket);
        reader = createReader(socket);
    }

    public Connection(Socket connection) throws IOException {
        this.socket = connection;
        this.hostname = connection.getInetAddress().getHostName();
        this.port = connection.getPort();
        writer = createWriter(socket);
        reader = createReader(socket);
    }

    private PrintWriter createWriter(Socket socket) throws IOException {
        return new PrintWriter(socket.getOutputStream(), true);
    }

    private BufferedReader createReader(Socket socket) throws IOException {
        return new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public String readMessage() throws IOException {
        return reader.readLine();
    }

    public void sendMessage(String message) {
        writer.println(message);
    }


    @Override
    public void close() throws IOException {
        socket.close();
        writer.close();
        reader.close();
    }

    @Override
    public String toString() {
        return "Port = " + port + " ,HostName = " + hostname;
    }
}

