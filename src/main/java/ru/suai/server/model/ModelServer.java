package ru.suai.server.model;

import ru.suai.server.controller.ControllerServer;

import java.awt.image.BufferedImage;
import java.util.HashMap;

/**
 * Application Server Model "Multi-user Paint".
 * 1. The list of boards is stored in the following form:
 * key - Board name;
 * value - an object of the "BufferedImage" class.
 * 2. The list of Clients is stored in the following form:
 * key - Username;
 * value is the "Server.ServerThread" thread object.
 * 3. Methods provide handling of class fields.
 */

public class ModelServer {
    private final HashMap<String, BufferedImage> boards = new HashMap<>();

    private final HashMap<String, ControllerServer.ServerThread> users = new HashMap<>();

    public HashMap<String, BufferedImage> getBoards() {
        return boards;
    }

    public void addBoard(String nameBoard, BufferedImage image) {
        this.boards.put(nameBoard, image);
    }

    public void removeBoard(String nameBoard) {
        this.boards.remove(nameBoard);
    }

    public HashMap<String, ControllerServer.ServerThread> getUsers() {
        return this.users;
    }

    public void addUser(String username, ControllerServer.ServerThread thread) {
        this.users.put(username, thread);
    }

    public void removeUser(String username) {
        this.users.remove(username);
    }
}