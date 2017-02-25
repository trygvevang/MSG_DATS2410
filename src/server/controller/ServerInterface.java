package server.controller;

/**
 * This interface is used to communicate between the JavaFX thread and the ServerConnection thread.
 */
public interface ServerInterface
{
    /**
     * Updates the user list in the GUI of the server.
     * Needed in this Interface because of the ServerConnection can not reach a non-static method in
     * the ServerController.
     */
    void updateUserListServer();
}
