package server.model;

/**
 * Information about each user is stored in an instance this class.
 */
public class User
{
    private final int uid;
    private int port;
    private final String name;
    private String hostname, password;

    /**
     * Since this class is only accesed by the server controller, there is no need for a control structure towards UID and name.
     * @param uid User ID
     * @param name Full name of this user
     */
    public User(int uid, String name, String password)
    {
        this.uid = uid;
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
     * Returns this user's full name.
     * @return full name of this user
     */
    public String getName()
    {
        return name;
    }

    public int getUid()
    {
        return uid;
    }

    public String getUidString()
    {
        return uid+"";
    }

    public String toStringAll()
    {
        String delimiter = (char) 182 + "";
        return uid + delimiter + name + delimiter + password;
        //return uid +"";
    }

    public String toString()
    {
        return uid+"";
    }

    /**
     * Returns the password registered for this user.
     * @return password for the object user
     */
    public String getPassword()
    {
        return password;
    }
}
