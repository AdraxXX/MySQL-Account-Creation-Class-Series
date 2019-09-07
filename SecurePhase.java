/*
* Author: Daniel Johnston
* Date uploaded: 06 Sep 2019
* Program Details: This class can be used in Java to create a SHA-512 passphrase from any input string.
*This class can also be used to checks two passphrases with one already being encrypted as well as generate a key.
*/
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

class SecurePhrase 
{
    private static final Random RANDOM = new SecureRandom(); //Creates a new secure random number
    private static final String SETVALUES = "0123456789!@#$%^&*()_+ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"; //Values usedable in KEY variable
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 512;
    
    SecurePhase()
    {
        
    }
    
    //This function will generate a key to be used when hashing the password.
    //This key is gererated by using our secure random
    public final String getKey(final int LENGTH) 
    {
        StringBuilder returnValue = new StringBuilder(LENGTH); //Initializes a empty StringBuilder with size of LENGTH
        
        //This for loop creates a StringBuilder one character at a time
        for (int i = 0; i < LENGTH; i++) 
        {
            returnValue.append(SETVALUES.charAt(RANDOM.nextInt(SETVALUES.length())));
        }
        
        return new String(returnValue);//returns the StringBuilder as a String
    }
    
    //This function will take in the current phrase (PASSWORD) as a character array
    //and the KEY as a byte array with these it will create a hash using SHA-512.
    private byte[] hash(final char[] PASSWORD, final byte[] KEY) 
    {
        PBEKeySpec spec = new PBEKeySpec(PASSWORD, KEY, ITERATIONS, KEY_LENGTH);
        Arrays.fill(PASSWORD, Character.MIN_VALUE);
        try 
        {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            return skf.generateSecret(spec).getEncoded();
        } 
        catch (NoSuchAlgorithmException | InvalidKeySpecException e) 
        {   
            throw new AssertionError(String.format("====================\n\tError:\n\t\t"
                + "%s\n====================\n", e));
        } 
        finally 
        {
            spec.clearPassword();
        }
    }
    
    //This function will take in two variable PASSWORD and KEY then with these will
    //call the hash function which will output into the securePassword byte array.
    public final String generateSecurePassword(final String PASSWORD, final String KEY) 
    {
        byte[] securePassword = hash(PASSWORD.toCharArray(), KEY.getBytes()); //Hashes the PASSWORD using 512 encryption
        securePassword = hash(Base64.getEncoder().encodeToString(securePassword).toCharArray(), KEY.getBytes()); //Hashes the hashed PASSWORD using 512 encryption
        return Base64.getEncoder().encodeToString(securePassword);
    }
    
    //This function will take the current user input and compare it with the already
    //hashed phrase using the variable KEY, PROVIDEDPASSWORD and SECUREDPASSWORD
    //PROVIDEDPASSWORD being the new user input.
    public final boolean verifyUserPassword(final String PROVIDEDPASSWORD,
            final String SECUREDPASSWORD, final String KEY)
    {
        String newSecurePassword = generateSecurePassword(PROVIDEDPASSWORD, KEY); //Runs the new input through our hashing process
        return newSecurePassword.equals(SECUREDPASSWORD);
    }
}
