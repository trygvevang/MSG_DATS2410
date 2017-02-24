package server.controller;

public interface ServerInterface
{
    /**
     * Updates the user list in the GUI of the server.
     * Needed in this Interface because of the ServerConnection can not reach a non-static method in
     * the ServerController.
     */
    void updateUserListServer();
}
