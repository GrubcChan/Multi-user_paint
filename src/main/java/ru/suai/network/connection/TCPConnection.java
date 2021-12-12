package ru.suai.network.connection;

import ru.suai.network.message.Message;

import java.io.*;
import java.net.Socket;

/**
 * Class describing TCP connection between Client and Server.
 */

public class TCPConnection implements Closeable {
    private final ObjectOutputStream out;

    private final ObjectInputStream in;

    public TCPConnection(Socket socket) throws IOException {
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
    }

    /**
     * This method implements sending a message.
     */
    public void send(Message message) throws IOException {
        synchronized (this.out) {
            out.writeObject(message);
        }
    }

    /**
     * This method implements message reception.
     */
    public Message receive() throws IOException, ClassNotFoundException {
        synchronized (this.in) {
            return (Message) in.readObject();
        }
    }

    /**
     * Closing a network connection
     */
    @Override
    public void close() throws IOException {
        in.close();
        out.close();
    }
}
