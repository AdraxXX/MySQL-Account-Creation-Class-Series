/*
* Author: Daniel Johnston
* Date uploaded: 06 Sep 2019
* Program Details: This class can be used in Java to create a connection to a MySQL database 
*and allows connection access with in the main.
*/
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;

public class MySQLConnect 
{
    private final String HOSTNAME;// Initializes HOSTNAME to store MySQL hostname
    private final String PORT;// Initializes PORT to store MySQL port
    private final String DBNAME;// Initializes DBNAME to store MySQL database Name
    private final String USERNAME;// Initializes USERNAME to store MySQL username for login
    private final String PASSWORD;// Initializes PASSWORD to store MySQL password for login
    private Connection connection;// Initializes connection to store MySQL connection link
    
    //This function is the constructor for the MySQLConnect class with reads in
    //HOSTNAME, PORT, DBNAME, USERNAME and PASSWORD
    MySQLConnect(final String HOSTNAME,final String PORT,final String DBNAME,
            final String USERNAME,final String PASSWORD)
    {
        this.HOSTNAME = HOSTNAME;// Stores input hostname into HOSTNAME
        this.PORT = PORT;// Stores input port into PORT
        this.DBNAME = DBNAME;// Stores input database name into DBNAME
        this.USERNAME = USERNAME;// Stores input username into USERNAME
        this.PASSWORD = PASSWORD;// Stores input password into PASSWORD
        this.connection = setupConnection();// Creates a new blink connection
    }
    
    //Start of getters
    public String getHOSTNAME()
    {
        return this.HOSTNAME;
    }
    
    public String getDBNAME()
    {
        return this.DBNAME;
    }
    
    public String getPORT()
    {
        return this.PORT;
    }
    
    public Connection getConnection()
    {
        return this.connection;
    }
    //End of getters
    
    //This function starts the connection to the to the MySQL database using the input HOSTNAME, PORT, DBNAME
    //USERNAME and PASSWORD.
    private Connection setupConnection() 
    {
        final String url = "jdbc:mysql://" + HOSTNAME + ":" + PORT + "/" + DBNAME
                + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        
        try 
        {
            connection = DriverManager.getConnection(url, USERNAME, PASSWORD);// Creation of MySQL connection using finals
        } 
        catch (SQLException error) 
        {
            System.out.printf("====================\n\tError:\n\t\t"
                + "Connection Error Update May Be Needed.\n====================\n");  
        }
        catch (Exception error)
        {
            System.out.printf("====================\n\tError:\n\t\t"
                + "%s\n====================\n", error);  
        }
        return connection;
    }
}
