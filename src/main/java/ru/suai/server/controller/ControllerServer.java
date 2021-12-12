package ru.suai.server.controller;

import ru.suai.network.message.Message;
import ru.suai.network.message.MessageType;
import ru.suai.network.connection.TCPConnection;
import ru.suai.server.model.ModelServer;
import ru.suai.server.view.ViewServer;

import java.io.*;
import java.awt.*;
import java.net.*;
import java.awt.image.*;
import java.util.*;

/**
 * SERVER.
 * Server - side application "Multi-user Paint" gives clients access to whiteboards.
 * Server features:
 * 1. Wait for new clients to connect and create a new stream when they connect;
 * 2. Create a new board;
 * 3. Connect to the board (Transfer data to the client);
 * 4. Send the change on the board to all users (connected to this board).
 */

public class ControllerServer {
    private ServerSocket serverSocket;

    private static ModelServer modelServer;

    private static ViewServer logServer;

    private static volatile boolean isServerStart = false;

    public ControllerServer() {

    }

    /**
     * Program entry point and server control
     */
    public static void mainServer() {
        try {
            System.out.println("SERVER");
            ControllerServer server = new ControllerServer();
            modelServer = new ModelServer();
            logServer = new ViewServer();
            server.start();

            while (true) {
                if (isServerStart) {
                    server.acceptServer();
                    isServerStart = false;
                }
            }
        } catch (IOException err) {
            try {
                logServer.pushMessage("Error.\n" + err.getMessage());
            } catch (IOException e) {
                System.out.println("Log error."+e.getMessage());
            }
        }
    }

    /**
     * Server start
     */
    public void start() {
        try {
            this.serverSocket = new ServerSocket(0);
            isServerStart = true;
            System.out.println("PORT: " + this.serverSocket.getLocalPort());
            System.out.println("ADDRESS: " + this.serverSocket.getInetAddress());
        } catch (IOException err) {
            try {
                logServer.pushMessage("Socket error.\n" + err.getMessage());
            } catch (IOException e) {
                System.out.println("Log error."+e.getMessage());
            }
        }
    }

    /**
     * A method waiting for new clients to connect.
     * If they are connected, a new thread is created.
     */
    public void acceptServer() {
        try {
            while (true) {
                new ServerThread(serverSocket.accept()).start();
            }
        } catch (IOException err) {
            try {
                logServer.pushMessage("Connection error.\n" + err.getMessage());
            } catch (IOException e) {
                System.out.println("Log error."+e.getMessage());
            }
        }
    }

    /**
     * The method implements control over empty boards.
     * If there are no clients left on the board, it is deleted.
     */
    static void checkBoards(String boardName) {
        try {
            if (boardName == null) {
                return;
            }
            boolean boardUsed = false;
            for (Map.Entry<String, ServerThread> iClient : modelServer.getUsers().entrySet()) {
                synchronized (modelServer.getUsers()) {
                    if (iClient.getValue().boardName != null && iClient.getValue().boardName.equals(boardName)) {
                        boardUsed = true;
                        break;
                    }
                }
            }
            if (!boardUsed) {
                synchronized (modelServer.getBoards()) {
                    modelServer.removeBoard(boardName);
                    synchronized (logServer) {
                        logServer.pushMessage("Доска \"" + boardName + "\" не используется и была удалена");
                        logServer.pushMessage("Кол-во досок: " + modelServer.getBoards().size() + "\n");
                    }
                }
            }
        } catch (IOException err) {
            try {
                logServer.pushMessage("error.\n" + err.getMessage());
            } catch (IOException e) {
                System.out.println("Log error."+e.getMessage());
            }
        }
    }

    /**
     * The flow of processing Client requests.
     * Stores the name of the user and the name of the board on which he is located.
     * Opportunities:
     * 1. Accept request;
     * 2. Send a request;
     */
    public static class ServerThread extends Thread {
        private final Socket socket;

        /**
         * TCPConnection - class of client-server connection
         */
        private TCPConnection tcpConnection = null;

        private String boardName = null;

        private String username = null;

        private Graphics2D graphics = null;

        public ServerThread(Socket clientSocket) {
            this.socket = clientSocket;
        }

        public TCPConnection getTcpConnection() {
            return this.tcpConnection;
        }

        /**
         * This method implements the creation of a new board.
         */
        private void createBoard(String message) {
            try {
                String[] splitMessage = message.split(" ", 2);
                boolean isContainsBoardName;
                synchronized (modelServer.getBoards()) {
                    isContainsBoardName = modelServer.getBoards().containsKey(splitMessage[1]);
                }
                boolean isContainsUsername;
                synchronized (modelServer.getUsers()) {
                    isContainsUsername = modelServer.getUsers().containsKey(splitMessage[0]);
                }
                if (isContainsBoardName) {
                    synchronized (this) {
                        try {
                            tcpConnection.send(new Message(MessageType.CREATE_BOARD, "EXISTS"));
                        } catch (IOException err) {
                            try {
                                logServer.pushMessage("error.\n" + err.getMessage());
                            } catch (IOException e) {
                                System.out.println("Log error."+e.getMessage());
                            }
                        }
                    }
                } else if (isContainsUsername) {
                    synchronized (this) {
                        try {
                            tcpConnection.send(new Message(MessageType.CREATE_BOARD, "USERNAME EXISTS"));
                        } catch (IOException err) {
                            try {
                                logServer.pushMessage("Socket error.\n" + err.getMessage());
                            } catch (IOException e) {
                                System.out.println("Log error."+e.getMessage());
                            }
                        }
                    }
                } else {
                    synchronized (this) {
                        try {
                            tcpConnection.send(new Message(MessageType.CREATE_BOARD, "OK"));
                        } catch (IOException err) {
                            try {
                                logServer.pushMessage("Socket error.\n" + err.getMessage());
                            } catch (IOException e) {
                                System.out.println("Log error."+e.getMessage());
                            }
                        }
                    }
                    String boardNameOld = boardName;

                    username = splitMessage[0];
                    modelServer.addUser(username, this);
                    boardName = splitMessage[1];

                    synchronized (modelServer.getBoards()) {
                        modelServer.addBoard(boardName, new BufferedImage(830, 498, BufferedImage.TYPE_INT_RGB));
                        graphics = modelServer.getBoards().get(boardName).createGraphics();
                    }
                    synchronized (modelServer.getBoards().get(boardName)) {
                        graphics.setColor(Color.white);
                        graphics.fillRect(0, 0, 830, 498);
                    }
                    synchronized (logServer) {
                        logServer.pushMessage("Доска \"" + boardName + "\" создана");
                        synchronized (modelServer.getBoards()) {
                            logServer.pushMessage("Кол-во досок: " + modelServer.getBoards().size() + "\n");
                        }
                    }
                    checkBoards(boardNameOld);
                }
            } catch (IOException err) {
                try {
                    logServer.pushMessage("Socket error.\n" + err.getMessage());
                } catch (IOException e) {
                    System.out.println("Log error."+e.getMessage());
                }
            }
        }

        /**
         * This method implements connection to the board.
         */
        private void connectionBoard(String message) {
            String[] splitMessage = message.split(" ", 2);

            boolean isContains;
            synchronized (modelServer.getBoards()) {
                isContains = modelServer.getBoards().containsKey(splitMessage[1]);
            }
            boolean isContainsUsername;
            synchronized (modelServer.getUsers()) {
                isContainsUsername = modelServer.getUsers().containsKey(splitMessage[0]);
            }
            if (!isContains) {
                synchronized (this) {
                    try {
                        tcpConnection.send(new Message(MessageType.CONNECT_BOARD, "NOT FOUND"));
                    } catch (IOException err) {
                        try {
                            logServer.pushMessage("Socket error.\n" + err.getMessage());
                        } catch (IOException e) {
                            System.out.println("Log error."+e.getMessage());
                        }
                    }
                }
            } else if (isContainsUsername) {
                synchronized (this) {
                    try {
                        tcpConnection.send(new Message(MessageType.CONNECT_BOARD, "USERNAME EXISTS"));
                    } catch (IOException err) {
                        try {
                            logServer.pushMessage("Socket error.\n" + err.getMessage());
                        } catch (IOException e) {
                            System.out.println("Log error."+e.getMessage());
                        }
                    }
                }
            } else {
                String boardNameOld = boardName;

                username = splitMessage[0];
                modelServer.addUser(username, this);
                boardName = splitMessage[1];

                synchronized (modelServer.getBoards().get(boardName)) {
                    graphics = modelServer.getBoards().get(boardName).createGraphics();
                }
                int[] rgbArray = new int[413340];
                synchronized (modelServer.getBoards().get(boardName)) {
                    modelServer.getBoards().get(boardName).getRGB(0, 0, 830, 498, rgbArray, 0, 830);
                }
                StringBuilder str = new StringBuilder();
                synchronized (this) {
                    for (int j : rgbArray) {
                        str.append(j);
                        str.append(" ");
                    }
                }
                synchronized (this) {
                    try {
                        tcpConnection.send(new Message(MessageType.CONNECT_BOARD, str.toString()));
                    } catch (IOException err) {
                        try {
                            logServer.pushMessage("Socket error.\n" + err.getMessage());
                        } catch (IOException e) {
                            System.out.println("Log error."+e.getMessage());
                        }
                    }
                }
                checkBoards(boardNameOld);
            }
        }

        /**
         * Pencil drawing
         */
        public void drawingBoardPen(String message) {
            String[] splitMessage = message.split(" ", 6);
            int color = Integer.parseInt(splitMessage[0]);
            int xPad = Integer.parseInt(splitMessage[1]);
            int yPad = Integer.parseInt(splitMessage[2]);
            int getX = Integer.parseInt(splitMessage[3]);
            int getY = Integer.parseInt(splitMessage[4]);
            int size = Integer.parseInt(splitMessage[5]);
            synchronized (modelServer.getBoards().get(boardName)) {
                graphics.setColor(new Color(color));
                graphics.setStroke(new BasicStroke(size));
                graphics.drawLine(xPad, yPad, getX, getY);
            }

            /* Sending changes to all connected users. */
            synchronized (modelServer.getUsers()) {
                for (HashMap.Entry<String, ServerThread> iClient : modelServer.getUsers().entrySet()) {
                    if (iClient.getValue().boardName != null && iClient.getValue().boardName.equals(boardName)) {
                        synchronized (modelServer.getUsers()) {
                            try {
                                iClient.getValue().getTcpConnection().send(new Message(MessageType.PAINTING_PEN, message));
                            } catch (IOException err) {
                                try {
                                    logServer.pushMessage("Socket error.\n" + err.getMessage());
                                } catch (IOException e) {
                                    System.out.println("Log error."+e.getMessage());
                                }
                            }
                        }
                    }
                }
            }
        }

        /**
         * Painting with a brush
         */
        public void drawingBoardBrush(String message) {
            String[] splitMessage = message.split(" ", 6);
            int color = Integer.parseInt(splitMessage[0]);
            int xPad = Integer.parseInt(splitMessage[1]);
            int yPad = Integer.parseInt(splitMessage[2]);
            int getX = Integer.parseInt(splitMessage[3]);
            int getY = Integer.parseInt(splitMessage[4]);
            int size = Integer.parseInt(splitMessage[5]);
            synchronized (modelServer.getBoards().get(boardName)) {
                graphics.setColor(new Color(color));
                graphics.setStroke(new BasicStroke(size));
                graphics.fillOval(getX - (size/2), getY - (size/2), size, size);
            }

            /* Sending changes to all connected users. */
            synchronized (modelServer.getUsers()) {
                for (HashMap.Entry<String, ServerThread> iClient : modelServer.getUsers().entrySet()) {
                    if (iClient.getValue().boardName != null && iClient.getValue().boardName.equals(boardName)) {
                        synchronized (modelServer.getUsers()) {
                            try {
                                iClient.getValue().getTcpConnection().send(new Message(MessageType.PAINTING_BRUSH, message));
                            } catch (IOException err) {
                                try {
                                    logServer.pushMessage("Socket error.\n" + err.getMessage());
                                } catch (IOException e) {
                                    System.out.println("Log error."+e.getMessage());
                                }
                            }
                        }
                    }
                }
            }
        }

        /**
         * Drawing with an eraser
         */
        public void drawingBoardEraser(String message) {
            String[] splitMessage = message.split(" ", 5);
            int xPad = Integer.parseInt(splitMessage[0]);
            int yPad = Integer.parseInt(splitMessage[1]);
            int getX = Integer.parseInt(splitMessage[2]);
            int getY = Integer.parseInt(splitMessage[3]);
            int size = Integer.parseInt(splitMessage[4]);
            synchronized (modelServer.getBoards().get(boardName)) {
                graphics.setColor(Color.WHITE);
                graphics.setStroke(new BasicStroke(3.0f * size));
                graphics.drawLine(xPad, yPad, getX, getY);
            }

            /* Sending changes to all connected users. */
            synchronized (modelServer.getUsers()) {
                for (HashMap.Entry<String, ServerThread> iClient : modelServer.getUsers().entrySet()) {
                    if (iClient.getValue().boardName != null && iClient.getValue().boardName.equals(boardName)) {
                        synchronized (modelServer.getUsers()) {
                            try {
                                iClient.getValue().getTcpConnection().send(new Message(MessageType.PAINTING_ERASER, message));
                            } catch (IOException err) {
                                try {
                                    logServer.pushMessage("Socket error.\n" + err.getMessage());
                                } catch (IOException e) {
                                    System.out.println("Log error."+e.getMessage());
                                }
                            }
                        }
                    }
                }
            }
        }

        /**
         * Drawing Text
         */
        public void drawingBoardText(String message) {
            if (!message.equals("REQUEST_FOCUS")) {
                String[] splitMessage = message.split(" ", 5);
                int color = Integer.parseInt(splitMessage[0]);
                String str = splitMessage[1];
                int xPad = Integer.parseInt(splitMessage[2]);
                int yPad = Integer.parseInt(splitMessage[3]);
                int size = Integer.parseInt(splitMessage[4]);
                synchronized (modelServer.getBoards().get(boardName)) {
                    graphics.setColor(new Color(color));
                    graphics.setStroke(new BasicStroke(2.0f * size));
                    graphics.setFont(new Font("Arial", Font.PLAIN, 15));
                    graphics.drawString(str, xPad, yPad);
                }
            }

            /* Sending changes to all connected users. */
            synchronized (modelServer.getUsers()) {
                for (HashMap.Entry<String, ServerThread> iClient : modelServer.getUsers().entrySet()) {
                    if (iClient.getValue().boardName != null && iClient.getValue().boardName.equals(boardName)) {
                        synchronized (modelServer.getUsers()) {
                            try {
                                iClient.getValue().getTcpConnection().send(new Message(MessageType.PAINTING_TEXT, message));
                            } catch (IOException err) {
                                try {
                                    logServer.pushMessage("Socket error.\n" + err.getMessage());
                                } catch (IOException e) {
                                    System.out.println("Log error."+e.getMessage());
                                }
                            }
                        }
                    }
                }
            }
        }

        /**
         * Line Drawing
         */
        public void drawingBoardLine(String message) {
            String[] splitMessage = message.split(" ", 6);
            int color = Integer.parseInt(splitMessage[0]);
            int xPad = Integer.parseInt(splitMessage[1]);
            int yPad = Integer.parseInt(splitMessage[2]);
            int getX = Integer.parseInt(splitMessage[3]);
            int getY = Integer.parseInt(splitMessage[4]);
            int size = Integer.parseInt(splitMessage[5]);
            synchronized (modelServer.getBoards().get(boardName)) {
                graphics.setColor(new Color(color));
                graphics.setStroke(new BasicStroke(size));
                graphics.drawLine(xPad, yPad, getX, getY);
            }

            /* Sending changes to all connected users. */
            synchronized (modelServer.getUsers()) {
                for (HashMap.Entry<String, ServerThread> iClient : modelServer.getUsers().entrySet()) {
                    if (iClient.getValue().boardName != null && iClient.getValue().boardName.equals(boardName)) {
                        synchronized (modelServer.getUsers()) {
                            try {
                                iClient.getValue().getTcpConnection().send(new Message(MessageType.PAINTING_LINE, message));
                            } catch (IOException err) {
                                try {
                                    logServer.pushMessage("Socket error.\n" + err.getMessage());
                                } catch (IOException e) {
                                    System.out.println("Log error."+e.getMessage());
                                }
                            }
                        }
                    }
                }
            }
        }

        /**
         * Drawing a circle
         */
        public void drawingBoardOval(String message) {
            String[] splitMessage = message.split(" ", 6);
            int color = Integer.parseInt(splitMessage[0]);
            int xPad = Integer.parseInt(splitMessage[1]);
            int yPad = Integer.parseInt(splitMessage[2]);
            int getX = Integer.parseInt(splitMessage[3]);
            int getY = Integer.parseInt(splitMessage[4]);
            int size = Integer.parseInt(splitMessage[5]);
            synchronized (modelServer.getBoards().get(boardName)) {
                graphics.setColor(new Color(color));
                graphics.setStroke(new BasicStroke(size));
                graphics.drawOval(xPad, yPad, getX, getY);
            }

            /* Sending changes to all connected users. */
            synchronized (modelServer.getUsers()) {
                for (HashMap.Entry<String, ServerThread> iClient : modelServer.getUsers().entrySet()) {
                    if (iClient.getValue().boardName != null && iClient.getValue().boardName.equals(boardName)) {
                        synchronized (modelServer.getUsers()) {
                            try {
                                iClient.getValue().getTcpConnection().send(new Message(MessageType.PAINTING_OVAL, message));
                            } catch (IOException err) {
                                try {
                                    logServer.pushMessage("Socket error.\n" + err.getMessage());
                                } catch (IOException e) {
                                    System.out.println("Log error."+e.getMessage());
                                }
                            }
                        }
                    }
                }
            }
        }

        /**
         * Draw a rectangle
         */
        public void drawingBoardRect(String message) {
            String[] splitMessage = message.split(" ", 6);
            int color = Integer.parseInt(splitMessage[0]);
            int xPad = Integer.parseInt(splitMessage[1]);
            int yPad = Integer.parseInt(splitMessage[2]);
            int getX = Integer.parseInt(splitMessage[3]);
            int getY = Integer.parseInt(splitMessage[4]);
            int size = Integer.parseInt(splitMessage[5]);
            synchronized (modelServer.getBoards().get(boardName)) {
                graphics.setColor(new Color(color));
                graphics.setStroke(new BasicStroke(size));
                graphics.drawRect(xPad, yPad, getX, getY);
            }

            /* Sending changes to all connected users. */
            synchronized (modelServer.getUsers()) {
                for (HashMap.Entry<String, ServerThread> iClient : modelServer.getUsers().entrySet()) {
                    if (iClient.getValue().boardName != null && iClient.getValue().boardName.equals(boardName)) {
                        synchronized (modelServer.getUsers()) {
                            try {
                                iClient.getValue().getTcpConnection().send(new Message(MessageType.PAINTING_RECT, message));
                            } catch (IOException err) {
                                try {
                                    logServer.pushMessage("Socket error.\n" + err.getMessage());
                                } catch (IOException e) {
                                    System.out.println("Log error."+e.getMessage());
                                }
                            }
                        }
                    }
                }
            }
        }

        /**
         * Updating the board
         */
        public void drawingBoardRepaint(String message) {
            /* Sending changes to all connected users. */
            synchronized (modelServer.getUsers()) {
                for (HashMap.Entry<String, ServerThread> iClient : modelServer.getUsers().entrySet()) {
                    if (iClient.getValue().boardName != null && iClient.getValue().boardName.equals(boardName)) {
                        synchronized (modelServer.getUsers()) {
                            try {
                                iClient.getValue().getTcpConnection().send(new Message(MessageType.PAINTING_REPAINT, message));
                            } catch (IOException err) {
                                try {
                                    logServer.pushMessage("Socket error.\n" + err.getMessage());
                                } catch (IOException e) {
                                    System.out.println("Log error."+e.getMessage());
                                }
                            }
                        }
                    }
                }
            }
        }

        /**
         * This method accepts and processes requests from the client.
         */
        private void messagingBetweenUsers() {
            try {
                try {
                    while (true) {
                        Message message = tcpConnection.receive();
                        if (message.getTypeMessage().equals(MessageType.CREATE_BOARD)) {
                            createBoard(message.getTextMessage());
                        } else if (message.getTypeMessage().equals(MessageType.CONNECT_BOARD)) {
                            connectionBoard(message.getTextMessage());
                        } else if (boardName != null && message.getTypeMessage().equals(MessageType.PAINTING_PEN)) {
                            drawingBoardPen(message.getTextMessage());
                        } else if (boardName != null && message.getTypeMessage().equals(MessageType.PAINTING_BRUSH)) {
                            drawingBoardBrush(message.getTextMessage());
                        } else if (boardName != null && message.getTypeMessage().equals(MessageType.PAINTING_ERASER)) {
                            drawingBoardEraser(message.getTextMessage());
                        } else if (boardName != null && message.getTypeMessage().equals(MessageType.PAINTING_TEXT)) {
                            drawingBoardText(message.getTextMessage());
                        } else if (boardName != null && message.getTypeMessage().equals(MessageType.PAINTING_LINE)) {
                            drawingBoardLine(message.getTextMessage());
                        } else if (boardName != null && message.getTypeMessage().equals(MessageType.PAINTING_OVAL)) {
                            drawingBoardOval(message.getTextMessage());
                        } else if (boardName != null && message.getTypeMessage().equals(MessageType.PAINTING_RECT)) {
                            drawingBoardRect(message.getTextMessage());
                        } else if (boardName != null && message.getTypeMessage().equals(MessageType.PAINTING_REPAINT)) {
                            drawingBoardRepaint(message.getTextMessage());
                        } else if (boardName != null && message.getTypeMessage().equals(MessageType.SAVE_IMAGE)) {
                            logServer.pushMessage("Client @" + username + "save image.");
                        }
                    }
                } catch (ClassNotFoundException err) {
                    try {
                        logServer.pushMessage("Socket error.\n" + err.getMessage());
                    } catch (IOException e) {
                        System.out.println("Log error."+e.getMessage());
                    }
                } finally {
                    tcpConnection.close();
                    synchronized (modelServer.getUsers()) {
                        modelServer.removeUser(username);
                        synchronized (logServer) {
                            logServer.pushMessage("Клиент недоступен");
                            logServer.pushMessage("Кол-во клиентов: " + modelServer.getUsers().size());
                        }
                    }
                    checkBoards(boardName);
                }
            } catch (IOException err) {
                synchronized (logServer) {
                    try {
                        logServer.pushMessage(err + "\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public Set<String> getBoardsList() {
            Set<String> listBoards = new HashSet<>();
            for (HashMap.Entry<String, BufferedImage> boards : modelServer.getBoards().entrySet()) {
                listBoards.add(boards.getKey());
            }
            return listBoards;
        }

        public void run() {
            try {
                synchronized (logServer) {
                    logServer.pushMessage("Клиент подключился");
                    synchronized (modelServer.getUsers()) {
                        logServer.pushMessage("Кол-во клиентов: " + modelServer.getUsers().size() + "\n");
                    }
                }
                tcpConnection = new TCPConnection(socket);
                tcpConnection.send(new Message(MessageType.BOARD_NAME_LIST, this.getBoardsList()));
                messagingBetweenUsers();
            } catch (IOException err) {
                try {
                    logServer.pushMessage("Socket error.\n" + err.getMessage());
                } catch (IOException e) {
                    System.out.println("Log error."+e.getMessage());
                }
            }
        }
    }
}