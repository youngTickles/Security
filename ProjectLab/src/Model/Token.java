package Model;

import Helper.DbConnect;

import javax.xml.transform.Result;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Token {

    private String token;
    private String email;

    public static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    // 2048 bit keys should be secure until 2030 - https://web.archive.org/web/20170417095741/https://www.emc.com/emc-plus/rsa-labs/historical/twirl-and-rsa-key-size.htm
    public static final int SECURE_TOKEN_LENGTH = 256;

    private static final SecureRandom random = new SecureRandom();

    private static final char[] symbols = CHARACTERS.toCharArray();

    private static final char[] buf = new char[SECURE_TOKEN_LENGTH];

    /**
     * Generate the next secure random token in the series.
     */
    public static String nextToken() {
        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = symbols[random.nextInt(symbols.length)];
        return new String(buf);
    }

    public Token(String token, String email)
    {
        this.token = token;
        this.email = email;
    }

    public void deleteToken()
    {
        DbConnect db = new DbConnect();
        Connection connection = db.connect();

        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement("DELETE FROM token where gmail =?");
            preparedStatement.setString(1,this.email);
            preparedStatement.executeUpdate();
        }catch (SQLException ex) {
            System.out.println("Check failed (SQL CREATE STATEMENT FAILED)");
        }
    }

    public void addTokenToDataBase()
    {
        DbConnect db = new DbConnect();
        Connection connection = db.connect();

        PreparedStatement preparedStatement = null;

        try
        {
            preparedStatement = connection.prepareStatement("INSERT INTO token(gmail, token) VALUES (?,?)");
            preparedStatement.setString(1,this.email);
            preparedStatement.setString(2,this.token);
            preparedStatement.executeUpdate();

        }catch (SQLException ex) {
        System.out.println("Check failed (SQL CREATE STATEMENT FAILED)");
    }

    }

    public static boolean tokenExists(Token token)
    {
        //DB Connect
        // check in database
        DbConnect db = new DbConnect();
        Connection connection = db.connect();

        PreparedStatement preparedStatement = null;
        int token_count=0;
        try {
            preparedStatement = connection.prepareStatement("SELECT count(token) as token_count FROM token WHERE gmail = ? AND token =?");
            preparedStatement.setString(1, token.email);
            preparedStatement.setString(2,token.token);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next())
            {
                token_count =Integer.parseInt( resultSet.getString("token_count"));
            }


        }catch (SQLException ex) {
            System.out.println("Check failed (SQL CREATE STATEMENT FAILED)");
        }

        if (token_count ==0)
            return false;
        else
            return true;
    }




    public static void checkExpiarationDate()
    {
        //DB connect
        //If expiration date excceeded => delete

    }
}
