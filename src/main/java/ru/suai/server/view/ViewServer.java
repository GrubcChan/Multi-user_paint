package ru.suai.server.view;

import java.io.Closeable;
import java.io.FileWriter;
import java.io.IOException;

/**
 * A class that writes messages from the server to a log file.
 */

public class ViewServer implements Closeable {
    private final FileWriter log;

    public ViewServer() throws IOException {
        log = new FileWriter("serverText.log");
    }

    public void pushMessage(String message) throws IOException {
        log.write(message + "\n");
        log.flush();
    }

    @Override
    public void close() throws IOException {
        log.close();
    }
}
