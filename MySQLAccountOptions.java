/*
* Author: Daniel Johnston
* Date uploaded: 07 Sep 2019
* Program Details: This class can be used in Java to create a account to be used for other login systems within a MySQL database.
*/
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.IOException;

public class MySQLAccountOptions 
{
    private static Statement mySQLStatement;// Initializes a Statement to store MySQL statements on the MySQL connection
    private static ResultSet mySQLResults;  // Initializes a ResultSet to store result of MySQL statements
    private static MySQLConnect dataBase;   // Initializes a MySQLConnect to create MySQL link
    private final String KEYTABLENAME;      // Initializes a String for Key's Table Location
    private final String RECOVERYTABLENAME; // Initializes a String for Recovery Information Table Location
    private final String ACCOUNTTABLENAME;  // Initializes a String for Account Information Table Location
    private final String PASSWORDFIELDID;   // Initializes a String for Password's field title
    private final String USERNAMEFIELDID;   // Initializes a String for Username's field title
    private final String KEYFIELDID;        // Initializes a String for Key's field title
    private final String USERIDFIELDID;     // Initializes a String for UserID's field title
    private static int userID;              // Initializes a int for user identification
    
    //This function is the constructor for the MySQLAccountOptions using ADDRESS,
    //PORT, HOSTNAME, USERNAME and PASSWORD to make a connection to the database in which
    //was described by the input information.
    MySQLAccountOptions(final String HOSTNAME, final String PORT, final String DBNAME, 
            final String USERNAME, final String PASSWORD, final String[] TABLENAMESANDFIELDIDS)
    { 
        KEYTABLENAME = TABLENAMESANDFIELDIDS[0];     //Assigns the Key's Table Location
        RECOVERYTABLENAME = TABLENAMESANDFIELDIDS[1];//Assigns the Recovery Information's Table Location
        ACCOUNTTABLENAME = TABLENAMESANDFIELDIDS[2]; //Assigns the Account Information's Table Location
        PASSWORDFIELDID = TABLENAMESANDFIELDIDS[3];  //Assigns the password's field title
        USERNAMEFIELDID = TABLENAMESANDFIELDIDS[4];  //Assigns the Username's field title
        KEYFIELDID = TABLENAMESANDFIELDIDS[5];       //Assigns the Key's field title
        USERIDFIELDID = TABLENAMESANDFIELDIDS[6];    //Assigns the UserID's field title
      
        try 
        {
            dataBase = new MySQLConnect(HOSTNAME, PORT, DBNAME, USERNAME, PASSWORD);// Creates the new connection to the MySQL database.
            mySQLStatement = dataBase.getConnection().createStatement();// Creates a blank statement to be used later as needed.
        }
        
        // catch to see if SQL exception fires while trying to run our try statement Which would be a server-side only error
        catch (SQLException e) 
        {
            System.out.printf("====================\n\tServer Error:\n\t\t"
                + "%s\n====================\n", "Connecting to dataBase. Using MySQLConnect Class");
        }
    }
    
    //This function is to create an account 
    public void createAccount(final String CLIENTTEXTSTREAM, final String SPLITPHRASE)
    {
        // Initializes SECUREPASSWORD with a default SecurePhrase.
        final SecurePhrase SECUREPASSWORD = new SecurePhrase();
        
        // Splits CLIENTTEXTSTREAM into a array of Strings using SPLITPHRASE as the separator.
        final String CLIENTSTREAMARRAY[] = CLIENTTEXTSTREAM.split(SPLITPHRASE); 
        
        // Assigns String in array location 0 of CLIENTSTREAMARRAY to LOGINNAME
        final String LOGINNAME = CLIENTSTREAMARRAY[0];    
        
        // Assigns String in array location 1 of CLIENTSTREAMARRAY to SCREENNAME
        final String SCREENNAME = CLIENTSTREAMARRAY[1];   
        
        // Assigns String in array location 2 of CLIENTSTREAMARRAY to NEWPASSWORD
        final String NEWPASSWORD = CLIENTSTREAMARRAY[2];  
        
        // Assigns String in array location 3 of CLIENTSTREAMARRAY to EMAIL
        final String EMAIL = CLIENTSTREAMARRAY[3];  
        
        // Assigns String in array location 4 of CLIENTSTREAMARRAY to QUESTIONONE
        final String QUESTIONONE = CLIENTSTREAMARRAY[4]; 
        
        // Assigns String in array location 5 of CLIENTSTREAMARRAY to ANSWERONE
        final String ANSWERONE = CLIENTSTREAMARRAY[5];
        
        // Assigns String in array location 6 of CLIENTSTREAMARRAY to QUESTIONTWO
        final String QUESTIONTWO = CLIENTSTREAMARRAY[6]; 
        
        // Assigns String in array location 7 of CLIENTSTREAMARRAY to ANSWERTWO
        final String ANSWERTWO = CLIENTSTREAMARRAY[7]; 
        
        // Assigns String in array location 8 of CLIENTSTREAMARRAY to QUESTIONTHREE
        final String QUESTIONTHREE = CLIENTSTREAMARRAY[8];
        
        // Assigns String in array location 9 of CLIENTSTREAMARRAY to ANSWERTHREE
        final String ANSWERTHREE = CLIENTSTREAMARRAY[9];
        
        // Creates a new Key using the getKey function of SecurePhrase Class and assigns it to KEY
        final String KEY = SECUREPASSWORD.getKey(256);
        
        // Uses SHA-512 Encrytion to secure user's new password and assigns it to SPASSWORD
        final String SPASSWORD = SECUREPASSWORD.generateSecurePassword(NEWPASSWORD, KEY);
        
        // Uses SHA-512 Encrytion to secure user's security answer one and assigns it to SANSWERONE
        final String SANSWERONE = SECUREPASSWORD.generateSecurePassword(ANSWERONE, KEY);
        
        // Uses SHA-512 Encrytion to secure user's security answer two and assigns it to SANSWERTWO
        final String SANSWERTWO = SECUREPASSWORD.generateSecurePassword(ANSWERTWO, KEY);
        
        // Uses SHA-512 Encrytion to secure user's security answer three and assigns it to SANSWERTHREE
        final String SANSWERTHREE = SECUREPASSWORD.generateSecurePassword(ANSWERTHREE, KEY);
       
        try
        {
            boolean goodUserID; // Boolean used to make sure that the user's UserID is unique.
            do
            {
                userID = (int)(Math.random() * 2147483647); // Makes a random number from 0 - 2147483647
                goodUserID = true; // Mark goodUserID as true
                
                // Executes a MySQL query statement to select all UserID's currently in the Account Table.
                mySQLResults = mySQLStatement.executeQuery("SELECT " + USERIDFIELDID + " FROM " + ACCOUNTTABLENAME);
                
                // This While Loop runs throught all results of the executed MySQL query statement above
                // looking to see if there is currently exist a user ID that matches are new random userID.
                while(mySQLResults.next())
                {
                   // If there exist a user ID that matches are new random userID
                   if(userID == mySQLResults.getInt(USERIDFIELDID))
                        goodUserID = false; // Mark goodUserID as false
                }
            }while(!goodUserID); // While goodUserID is false start back over at the top of do and retry.
        }
        
        // catch to see if SQL exception fires while trying to run our try statement
        catch(SQLException e)
        {
            
        }
        
        // Initializes SQLPREPARED1 to be used in the creation on the new users Key table entry.
        final String SQLPREPARED1 = "INSERT INTO " + KEYTABLENAME + "(" +  USERIDFIELDID + "," +  KEYFIELDID + ") VALUES(?,?)";
        
        // Creates a new entry into the Key table entry
        try(final PreparedStatement KEYTABLEPRESTATEMENT = dataBase.getConnection().prepareStatement(SQLPREPARED1)) 
        {
            KEYTABLEPRESTATEMENT.setInt(1,userID);
            KEYTABLEPRESTATEMENT.setString(2, KEY);
            KEYTABLEPRESTATEMENT.executeUpdate();
        } 
        
        // catch to see if SQL exception fires while trying to run our try statement
        catch (SQLException e) 
        {
            // Output message on server with the error code only since error is coming from server-side.
            System.out.println(e.getMessage());
        }
        
        // Initializes SQLPREPARED1 to be used in the creation on the new users Recovery table entry.
        final String SQLPREPARED2 = "INSERT INTO " + RECOVERYTABLENAME + "(" +  USERIDFIELDID + ",questionOne,questionTwo,questionThree,answerOne,answerTwo,answerThree) VALUES(?,?,?,?,?,?,?)";
        
        // Creates a new entry into the Recovery table entry
        try (final PreparedStatement RECOVERYTABLEPRESTATEMENT = dataBase.getConnection().prepareStatement(SQLPREPARED2)) 
        {
            RECOVERYTABLEPRESTATEMENT.setInt(1,userID);
            RECOVERYTABLEPRESTATEMENT.setString(2, QUESTIONONE);
            RECOVERYTABLEPRESTATEMENT.setString(3, QUESTIONTWO);
            RECOVERYTABLEPRESTATEMENT.setString(4, QUESTIONTHREE);
            RECOVERYTABLEPRESTATEMENT.setString(5, SANSWERONE);
            RECOVERYTABLEPRESTATEMENT.setString(6, SANSWERTWO);
            RECOVERYTABLEPRESTATEMENT.setString(7, SANSWERTHREE);
            RECOVERYTABLEPRESTATEMENT.executeUpdate();
        } 
        
        // catch to see if SQL exception fires while trying to run our try statement
        catch (SQLException e) 
        {
                // Initializes SQLPREPAREDDEL to be used in the deletion of the Key table entry since Recovery table entry failed.
                final String SQLPREPAREDDEL = "DELETE FROM " + KEYTABLENAME + " WHERE " + USERIDFIELDID + "=?";
                
                // Removes the new Key table entry since Recovery table entry failed.
                try (final PreparedStatement KEYPRESTATEMENT = dataBase.getConnection().prepareStatement(SQLPREPAREDDEL)) 
                {
                    KEYPRESTATEMENT.setInt(1,userID);
                    KEYPRESTATEMENT.executeUpdate();
                } 
                
                // catch to see if IO exception fires while trying to run our try statement 
                catch (SQLException error) 
                {
                    // Output message on server with the error code only since error is coming from server-side only.
                    System.out.println(error.getMessage());
                }
        }
        
        // Initializes SQLPREPARED3 to be used in the creation on the new users Account table entry.
        final String SQLPREPARED3 = "INSERT INTO " + ACCOUNTTABLENAME + "(" +  USERIDFIELDID + "," + USERNAMEFIELDID + ",screenName," + PASSWORDFIELDID + ",email) VALUES(?,?,?,?,?)";
        
        // Creates a new entry into the Account table entry
        try (PreparedStatement pstmt = dataBase.getConnection().prepareStatement(SQLPREPARED3)) 
        {
            pstmt.setInt(1,userID);
            pstmt.setString(2, LOGINNAME);
            pstmt.setString(3, SCREENNAME);
            pstmt.setString(4, SPASSWORD);
            pstmt.setString(5, EMAIL);
            pstmt.executeUpdate();
        } 
        
        // catch to see if SQL exception fires while trying to run our try statement
        catch (SQLException e) 
        {
            // Initializes SQLPREPAREDDEL to be used in the deletion of the Key table entry since Recovery table entry failed.
            final String SQLPREPAREDDEL = "DELETE FROM " + KEYTABLENAME + " WHERE " + USERIDFIELDID + "=?";

            // Removes the new Key table entry since Recovery table entry failed.
            try (final PreparedStatement KEYPRESTATEMENT = dataBase.getConnection().prepareStatement(SQLPREPAREDDEL)) 
            {
                KEYPRESTATEMENT.setInt(1,userID);
                KEYPRESTATEMENT.executeUpdate();
            } 

            // catch to see if IO exception fires while trying to run our try statement 
            catch (SQLException error) 
            {
                // Output message on server with the error code only since error is coming from server-side only.
                System.out.println(error.getMessage());
            }

            // Initializes SQLPREPAREDDEL to be used in the deletion of the Key table entry since Recovery table entry failed.
            final String SQLPREPAREDDEL2 = "DELETE FROM " + RECOVERYTABLENAME + " WHERE " + USERIDFIELDID + "=?";

            // Removes the new Key table entry since Recovery table entry failed.
            try (final PreparedStatement RECOVERYPRESTATEMENT = dataBase.getConnection().prepareStatement(SQLPREPAREDDEL2)) 
            {
                RECOVERYPRESTATEMENT.setInt(1,userID);
                RECOVERYPRESTATEMENT.executeUpdate();
            } 

            // catch to see if IO exception fires while trying to run our try statement 
            catch (SQLException error) 
            {
                // Output message on server with the error code only since error is coming from server-side only.
                System.out.println(error.getMessage());
            }
        }
    }
     
    //This function allows the user to login
    public void loginProcessing(final SocketChannel ISERVERCLIENT, final String CLIENTTEXTSTREAM, final String SPLITPHRASE)
    {
        // Splits CLIENTTEXTSTREAM into a array of Strings using SPLITPHRASE as the separator.
        final String CLIENTSTREAMARRAY[] = CLIENTTEXTSTREAM.split(SPLITPHRASE);
        
        // Assigns String in array location 0 of CLIENTSTREAMARRAY to USERNAME
        final String USERNAME = CLIENTSTREAMARRAY[0];
        
        // Assigns String in array location 1 of CLIENTSTREAMARRAY to PASSWORD
        final String PASSWORD = CLIENTSTREAMARRAY[1];
        
        // Initializes USERMESSAGE.
        final String USERMESSAGE;
        
        // Checks the user's input with current information stored on the MySQL database
        if(login(ISERVERCLIENT, USERNAME, PASSWORD))
            USERMESSAGE = Integer.toString(userID);// Assigns userID as a string in USERMESSAGE
        else
            USERMESSAGE = "Username or Password Was Incorrect. Try Again";// Assigns error message for user as a string in USERMESSAGE
        
        try
        {
            byte[] message = USERMESSAGE.getBytes();
            ByteBuffer buffer = ByteBuffer.wrap(message);
            ISERVERCLIENT.write(buffer);//Sends USERMESSAGE to the user client
        }
        
        // catch to see if IO exception fires while trying to run our try statement Which would be a server-side only error
        catch(IOException e)
        {
            System.out.printf("====================\n\tServer Error:\n\t\t"
                + "%s\n====================\n", e);
        }
    }
    
    //This function checks to see if the user's input information matchs the database
    private boolean login(final SocketChannel ISERVERCLIENT, final String INPUTUSERNAME, final String INPUTPASSWORD)
    {
        try
        {   
            final String KEY;// Initializes KEY.
            final String SECUREPASSWORD;// Initializes SECUREPASSWORD.
            final SecurePhrase SPASSWORD = new SecurePhrase();// Initializes SPASSWORD with a blank SecurePhrase.
            
            // Executes a MySQL query statement that selects a user accout in the Accout's table based on the user's input.
            mySQLResults = mySQLStatement.executeQuery("SELECT * FROM " + ACCOUNTTABLENAME + " WHERE " + USERNAMEFIELDID + " ='" + INPUTUSERNAME + "'");
            
            //If results are found in the search
            if(mySQLResults.next())
            {
                userID = mySQLResults.getInt(USERIDFIELDID);// Assigns the found UserID based on the username input by the user.
                SECUREPASSWORD = mySQLResults.getString(PASSWORDFIELDID);// Assigns the found password based on the username input by the user.
            }
            
            else
                return false;
            
            // Executes a MySQL query statement that selects a key in the key's table based on the user's UserID found in the first MySQL search.
            mySQLResults = mySQLStatement.executeQuery("SELECT " +  KEYFIELDID + " FROM " + KEYTABLENAME + " WHERE " +  USERIDFIELDID + " ='" + userID + "'");
            
            //If results are found in the search
            if(mySQLResults.next())
                KEY = mySQLResults.getString(KEYFIELDID);// Assigns the found key based on the UserID found in the first search.
            
            else
                return false;
            
            return SPASSWORD.verifyUserPassword(INPUTPASSWORD, SECUREPASSWORD, KEY);// Checks the hashed password with the user's input password

        }
        
        // catch to see if SQL exception fires while trying to run our try statement
        catch(SQLException e)
        {
            try
            {
                // Output message on server starting with the error code then what Ip address had caused this code.
                System.out.println(e.getMessage()  + " Coming FROM: " + ISERVERCLIENT.getRemoteAddress().toString());
            }
            
            // catch to see if IO exception fires while trying to run our try statement 
            catch (IOException error)
            {
                // Output message on server with the error code only since error is coming from ISERVERCLIENT.getRemoteAddress().
                System.out.println(error.getMessage());
            }
            return false;
        }
    }
}
