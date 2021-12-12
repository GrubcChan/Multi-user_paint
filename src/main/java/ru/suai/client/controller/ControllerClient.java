package ru.suai.client.controller;

import ru.suai.client.model.ModelClient;
import ru.suai.client.view.ViewClient;
import ru.suai.network.connection.TCPConnection;
import ru.suai.network.message.Message;

import javax.swing.*;
import java.io.IOException;
import java.net.Socket;
import java.util.Set;

/**
 * CLIENT.
 * This class provides the user with the ability to send messages to the server and receive from the server
 * answers and inquiries. Here is all the logical part of working with the network and transferring the coordinates of the rendered
 * pixels.
 * Client Opportunities:
 * 1. Create a board;
 * 2. Connect to the board;
 * 3. Ask yourself a unique name;
 * 4. Draw on the board;
 * 5. See the board change from other users.
 */

public class ControllerClient {
    private TCPConnection tcpConnection = null;

    private static ModelClient modelGuiClient;

    private static ViewClient viewGuiClient;

    private volatile boolean isConnected = false;

    /**
     * Program entry point and client control
     * The address and port are transmitted through the console:
     *
     * @param host = args[1];
     * @param port args[0].
     */
    public static void mainClient(String host, int port) {
        System.out.println("CLIENT");
        ControllerClient client = new ControllerClient();
        modelGuiClient = new ModelClient();
        client.connectToServer(host, port);

        SwingUtilities.invokeLater(() -> {
            viewGuiClient = new ViewClient(client);
            viewGuiClient.initFrameClient();
        });
    }

    public ControllerClient() {
    }

    /**
     * This method implements a connection to the server
     */
    public void connectToServer(String serverHost, int serverPort) {
        if (!isConnected) {
            try {
                this.tcpConnection = new TCPConnection(new Socket(serverHost, serverPort));
                new ServerNet().start();
            } catch (IOException e) {
                viewGuiClient.errorMessage("Not connection" + e.getMessage());
            }
        }
    }

    /**
     * A class that receives messages from the server.
     */
    class ServerNet extends Thread {
        public void run() {
            try {
                Message message;
                while (true) {
                    message = tcpConnection.receive();
                    switch (message.getTypeMessage()) {
                        case CREATE_BOARD:
                            switch (message.getTextMessage()) {
                                case "OK":
                                    viewGuiClient.closeRegistrationWindow();
                                    viewGuiClient.createPaintWindow();
                                    isConnected = true;
                                    break;
                                case "EXISTS":
                                    viewGuiClient.getRegistrationWindow().errorDialogWindow("A board with the same name already exists!");
                                    break;
                                case "USERNAME EXISTS":
                                    viewGuiClient.getRegistrationWindow().errorDialogWindow("A user with the same name already exists!");
                                    break;
                            }
                            break;
                        case CONNECT_BOARD:
                            switch (message.getTextMessage()) {
                                case "NOT FOUND":
                                    viewGuiClient.getRegistrationWindow().errorDialogWindow("No board with this name was found!");
                                    break;
                                case "USERNAME EXISTS":
                                    viewGuiClient.getRegistrationWindow().errorDialogWindow("A user with the same name already exists!");
                                    break;
                                default:
                                    viewGuiClient.closeRegistrationWindow();
                                    viewGuiClient.connectPaintWindow(message.getTextMessage());
                                    isConnected = true;
                                    break;
                            }
                            break;
                        case PAINTING_PEN: {
                            String[] splitMessage = message.getTextMessage().split(" ", 6);
                            int color = Integer.parseInt(splitMessage[0]);
                            int xPad = Integer.parseInt(splitMessage[1]);
                            int yPad = Integer.parseInt(splitMessage[2]);
                            int getX = Integer.parseInt(splitMessage[3]);
                            int getY = Integer.parseInt(splitMessage[4]);
                            int size = Integer.parseInt(splitMessage[5]);
                            viewGuiClient.getPaintWindow().drawPen(color, xPad, yPad, getX, getY, size);
                            break;
                        }
                        case PAINTING_BRUSH: {
                            String[] splitMessage = message.getTextMessage().split(" ", 6);
                            int color = Integer.parseInt(splitMessage[0]);
                            int xPad = Integer.parseInt(splitMessage[1]);
                            int yPad = Integer.parseInt(splitMessage[2]);
                            int getX = Integer.parseInt(splitMessage[3]);
                            int getY = Integer.parseInt(splitMessage[4]);
                            int size = Integer.parseInt(splitMessage[5]);
                            viewGuiClient.getPaintWindow().drawBrash(color, xPad, yPad, getX, getY, size);
                            break;
                        }
                        case PAINTING_ERASER: {
                            String[] splitMessage = message.getTextMessage().split(" ", 5);
                            int xPad = Integer.parseInt(splitMessage[0]);
                            int yPad = Integer.parseInt(splitMessage[1]);
                            int getX = Integer.parseInt(splitMessage[2]);
                            int getY = Integer.parseInt(splitMessage[3]);
                            int size = Integer.parseInt(splitMessage[4]);
                            viewGuiClient.getPaintWindow().drawEraser(xPad, yPad, getX, getY, size);
                            break;
                        }
                        case PAINTING_TEXT:
                            if (!message.getTextMessage().equals("REQUEST_FOCUS")) {
                                String[] splitMessage = message.getTextMessage().split(" ", 5);
                                int color = Integer.parseInt(splitMessage[0]);
                                String str = splitMessage[1];
                                int xPad = Integer.parseInt(splitMessage[2]);
                                int yPad = Integer.parseInt(splitMessage[3]);
                                int size = Integer.parseInt(splitMessage[4]);
                                viewGuiClient.getPaintWindow().drawText(color, str, xPad, yPad, size);
                            } else {
                                viewGuiClient.getPaintWindow().drawTextRequestFocus();
                            }
                            break;
                        case PAINTING_LINE: {
                            String[] splitMessage = message.getTextMessage().split(" ", 6);
                            int color = Integer.parseInt(splitMessage[0]);
                            int xPad = Integer.parseInt(splitMessage[1]);
                            int yPad = Integer.parseInt(splitMessage[2]);
                            int getX = Integer.parseInt(splitMessage[3]);
                            int getY = Integer.parseInt(splitMessage[4]);
                            int size = Integer.parseInt(splitMessage[5]);
                            viewGuiClient.getPaintWindow().drawLine(color, xPad, yPad, getX, getY, size);
                            break;
                        }
                        case PAINTING_OVAL: {
                            String[] splitMessage = message.getTextMessage().split(" ", 6);
                            int color = Integer.parseInt(splitMessage[0]);
                            int xPad = Integer.parseInt(splitMessage[1]);
                            int yPad = Integer.parseInt(splitMessage[2]);
                            int getX = Integer.parseInt(splitMessage[3]);
                            int getY = Integer.parseInt(splitMessage[4]);
                            int size = Integer.parseInt(splitMessage[5]);
                            viewGuiClient.getPaintWindow().drawOval(color, xPad, yPad, getX, getY, size);
                            break;
                        }
                        case PAINTING_RECT: {
                            String[] splitMessage = message.getTextMessage().split(" ", 6);
                            int color = Integer.parseInt(splitMessage[0]);
                            int xPad = Integer.parseInt(splitMessage[1]);
                            int yPad = Integer.parseInt(splitMessage[2]);
                            int getX = Integer.parseInt(splitMessage[3]);
                            int getY = Integer.parseInt(splitMessage[4]);
                            int size = Integer.parseInt(splitMessage[5]);
                            viewGuiClient.getPaintWindow().drawRect(color, xPad, yPad, getX, getY, size);
                            break;
                        }
                        case PAINTING_REPAINT:
                            viewGuiClient.getPaintWindow().drawRepaint();
                            break;
                        case BOARD_NAME_LIST:
                            modelGuiClient.setBoards(message.getList());
                            break;
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                viewGuiClient.errorMessage("Error connection" + e.getMessage());
            }
        }
    }

    public TCPConnection getTcpConnection() {
        return tcpConnection;
    }

    public void closeTCPConnection() {
        try {
            this.tcpConnection.close();
        } catch (IOException e) {
            viewGuiClient.errorMessage(e.getMessage());
        }
    }

    public Set<String> getBoards() {
        return modelGuiClient.getBoards();
    }

    public Set<String> getUsers() {
        return modelGuiClient.getUsers();
    }
}