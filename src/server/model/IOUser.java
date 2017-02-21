package server.model;


import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Useful static methods for I/O of the users
 */
public class IOUser
{

    public static boolean loginconfirmation(String login, List<User> l)
    {

        String loginID = login.substring(0, 4);
        int id = Integer.parseInt(loginID);
        User user = l.get(id - 1001);
        String p = user.toStringAll( );
        boolean loginConfirmed = p.endsWith(user.getPassword( ));

        return loginConfirmed;
    }

    /**
     * Reads from a file, and generates a list of users
     * @param path path of the file
     * @return list of users
     * @throws IOException
     */
    public static List<User> read(String path) throws IOException
    {
        List<User> users = new ArrayList<>();
        String reg = (char) 182 + "";
        String[] parts;

        try
        (
            BufferedReader in = new BufferedReader(new FileReader(new File(path)))
        )
        {
            for (String l = in.readLine(); l != null; l = in.readLine())
            {
                parts = l.split(reg);
                users.add(new User(Integer.parseInt(parts[0]), parts[1], parts[2]));
            }
        }
        return users;
    }

    /**
     * Writes user information to the users.txt
     *
     * @param path path of the file users.txt
     * @param u    the user containing the user information
     * @throws IOException
     */
    public static void write(String path, User u)
    {
        try
        (
            FileWriter fileWriterw = new FileWriter(path, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriterw);
            PrintWriter out = new PrintWriter(bufferedWriter)
        )
        {
            out.println(u.toStringAll());
        }
        catch (IOException e)
        {
            System.err.println("An I/O error has occured, while trying to write to the file: " + path + "\n" + e.getMessage());
        }
    }

    /**
     * Registers a new user in the system. This function updates both, the list of users and
     * the text file where the users are stored between sessions.
     *
     * @param users The list of users known to the server
     * @param info  input from client. Used for creating a new user
     * @return the user id, so the user know what to log in with
     */
    public static String register(List<User> users, String info)
    {
        String[] tokens = parseLine(info, (char) 182 + "");
        int uid = users.get(users.size() - 1).getUid();
        uid++;
        User tmp = new User(uid, tokens[1], tokens[2]);
        users.add(tmp);
        IOUser.write("src/server/resources/users.txt", tmp);
        return String.valueOf(uid);
    }

    /**
     * Splits the string into an array of strings, based on a regular expression
     *
     * @param line string to be split
     * @return String[] without the delimiter/regex
     */
    public static String[] parseLine(String line, String reg) //TODO: Maybe add a parameter so we can choose what the regex should be
    {
        return line.split(reg);
    }

    /**
     * Checks the input from user to see if the password and User ID corresponds to the registered user
     *
     * @param users List of users, to make the checking faster
     * @param input from the client to the server
     * @return either Accepted or Decliend, basically true or false
     */
    public static String logIn(List<User> users, String input)
    { //TODO: Fix IP and port
        String[] info = parseLine(input, (char) 169 + "");
        int id = Integer.parseInt(info[1]);
        String pswdAtmpt = info[2];

        String pswdCheck = users.get(id - 1001).getPassword();

        return pswdAtmpt.equals(pswdCheck) ? "Accepted" : "Declined";
    }
}
