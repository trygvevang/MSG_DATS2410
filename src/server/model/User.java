package server.model;

/**
 * Information about each user is stored in an instance this class.
 */
public class User
{
    private int port;
    private final String name;
    private String hostname, password;
    private int status;

    /**
     * Since this class is only accessed by the server controller, there is no need for a control structure towards input in the arguments.
     * @param name username of this user
     * @param  password password of this user
     */
    public User(String name, String password)
    {
        this.status = 0;
        this.name = name;
        this.password = password;
    }

    /**
     * Sets socket information of this user.
     * Since this method is only accessed by the server controller, there is no need for a control structure towards hostname and portnumber.
     * @param hostname IP address
     * @param port port number
     */
    public void setSocketInfo(String hostname, int port, int status)
    {
        this.setStatus(status);
        this.hostname = hostname;
        this.port = port;
    }

    /**
     * Returns this user's portnumber.
     * @return portnumber of this user
     */
    public int getPort()
    {
        return port;
    }

    /**
     * Returns this user's IP address.
     * @return IP address of this user
     */
    public String getHostname()
    {
        return hostname;
    }

    /**
     * Returns this user's username.
     * @return username for the object user
     */
    public String getName()
    {
        return name;
    }

    public String toString()
    {
        String delimiter = (char) 182 + "";
        return delimiter + name + delimiter + password;
    }

    /**
     * Returns the password registered for this user.
     * @return password for this user
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * Assign the status of this user
     * @param status an integer which represents a status. 0 = Offline, 1 = Online, 2 = Busy
     */
    public void setStatus(int status)
    {
        this.status = status;
    }

    /**
     * Returns the status in an integer form. 0 = Offline, 1 = Online, 2 = Busy
     * @return status of this user
     */
    public int getStatus()
    {
        return status;
    }

    /**
     * Returns the status in String form.
     * @return status of this user
     */
    public String getStatusString()
    {
        return status == 0 ? "Offline" : (status == 1 ? "Online" : "Busy");
    }
}
