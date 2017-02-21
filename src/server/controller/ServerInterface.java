package server.controller;

/**
 * Created by Jesper Nylend on 13.02.2017.
 * s305070
 */
public interface ServerInterface
{
    String updateUsers(String input);
    void updateUserConnection(int uid, String ip, int port);
    String loginUser(String input);
}
