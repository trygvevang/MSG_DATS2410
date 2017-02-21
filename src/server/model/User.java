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
     * Since this class is only accesed by the server controller, there is no need for a control structure towards UID and name.
     * @param name Full name of this user
     */
    public User(String name, String password)
    {
        this.status = 0;
        this.name = name;
        this.password = password;
    }

    /**
     * Since this method is only accesed by the server controller, there is no need for a control structure towards hostname and portnumber.
     * @param hostname IP address
     * @param port port number
     */
    public void setSocketInfo(String hostname, int port)
    {
        this.setStatus(1);
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
     * @return password for the object user
     */
    public String getPassword()
    {
        return password;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public int getStatus()
    {
        return status;
    }

    public String getStatusString()
    {
        return status == 0 ? "Offline" : (status == 1 ? "Online" : "Busy");
    }
}
