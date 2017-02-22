package server.controller;

/**
 * Created by Jesper Nylend on 13.02.2017.
 * s305070
 */
public interface ServerInterface
{
    boolean registerUser(String input);
    void updateUserConnection(String username, String host, int port, int status);
    String loginUser(String input);
    String sendUserList();
}
