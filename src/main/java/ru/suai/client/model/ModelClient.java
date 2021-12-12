package ru.suai.client.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Application Client Model "Multi-user Paint".
 * 1. Keeps a list of the names of all boards stored on the server;
 * 2. Keeps a list of the names of all users on the board.
 */

public class ModelClient {
    private Set<String> users;

    private Set<String> boards;

    public ModelClient() {
        this.users = new HashSet<>();
        this.boards = new HashSet<>();
    }

    public Set<String> getUsers() {
        return users;
    }

    public void addUser(String username) {
        users.add(username);
    }

    public void removeUser(String username) {
        users.remove(username);
    }

    public void setUsers(Set<String> users) {
        this.users = users;
    }

    public Set<String> getBoards() {
        return boards;
    }

    public void addBoard(String boardName) {
        boards.add(boardName);
    }

    public void removeBoard(String boardName) {
        boards.remove(boardName);
    }

    public void setBoards(Set<String> boards) {
        this.boards = boards;
    }
}