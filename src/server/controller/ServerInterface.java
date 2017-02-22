package server.controller;

public interface ServerInterface
{
    boolean registerUser(String input);
    void updateUserConnection(String username, String host, int port, int status);
    String loginUser(String input);
    String sendUserList();
    void showUserList();
    void requestChat(String username);
}
