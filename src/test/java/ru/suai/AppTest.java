package ru.suai;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import ru.suai.client.model.ModelClient;
import ru.suai.server.controller.ControllerServer;
import ru.suai.server.model.ModelServer;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    @Test
    public void serverModelTest(){
        try {
            ModelServer modelServer = new ModelServer();
            ServerSocket serverSocket = new ServerSocket(34567);
            String[] bordNames = {"Board_#1", "Board_#2", "Board_#3" };
            BufferedImage[] images = {
                    new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB),
                    new BufferedImage(3, 498, BufferedImage.TYPE_INT_RGB),
                    new BufferedImage(830, 498, BufferedImage.TYPE_INT_RGB)
            };

            String[] usernames = {"Misha", "Dasha", "Sasha" };
            ControllerServer.ServerThread[] serverThreads = {
                    new ControllerServer.ServerThread(new Socket("localhost", 34567)),
                    new ControllerServer.ServerThread(new Socket("localhost", 34567)),
                    new ControllerServer.ServerThread(new Socket("localhost", 34567))
            };

            modelServer.addBoard(bordNames[0], images[0]);
            modelServer.addBoard(bordNames[1], images[1]);
            modelServer.addBoard(bordNames[2], images[2]);

            modelServer.addUser(usernames[0], serverThreads[0]);
            modelServer.addUser(usernames[1], serverThreads[1]);
            modelServer.addUser(usernames[2], serverThreads[2]);

            assertEquals(images[0], modelServer.getBoards().get(bordNames[0]));
            assertEquals(images[1], modelServer.getBoards().get(bordNames[1]));
            assertEquals(images[2], modelServer.getBoards().get(bordNames[2]));

            assertEquals(serverThreads[0], modelServer.getUsers().get(usernames[0]));
            assertEquals(serverThreads[1], modelServer.getUsers().get(usernames[1]));
            assertEquals(serverThreads[2], modelServer.getUsers().get(usernames[2]));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void clientModelTest(){
        ModelClient modelClient = new ModelClient();
        String[] bordNames = {"Board_#1", "Board_#2", "Board_#3" };
        String[] usernames = {"Misha", "Dasha", "Sasha" };

        modelClient.addBoard(bordNames[0]);
        modelClient.addBoard(bordNames[1]);
        modelClient.addBoard(bordNames[2]);

        modelClient.addUser(usernames[0]);
        modelClient.addUser(usernames[1]);
        modelClient.addUser(usernames[2]);

        assertTrue(modelClient.getUsers().contains(usernames[0]));
        assertTrue(modelClient.getUsers().contains(usernames[1]));
        assertTrue(modelClient.getUsers().contains(usernames[2]));
    }
}
