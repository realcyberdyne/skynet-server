package cyberdyne.generator.Http.Auth;

import com.cyberdyne.skynet.connection.manager.DTO.UsersDTO;
import com.cyberdyne.skynet.connection.manager.Models.Users_Model;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class Auth
{

    public static String AuthCookieName="auth";

    public static ArrayList<ArrayList<String>> AuthCodes=new ArrayList<>();



    //Get Login
    public String GetLoginCheckByUsernamePassword(String username,String password)
    {
        List<Users_Model> Users = new UsersDTO().GetSelectUser(username,password);

        if(Users.size()>0)
        {
            String Token = GenerateAuthToken();
            ArrayList NAuth=new ArrayList();

            NAuth.add(Users.get(0).getId());
            NAuth.add(Users.get(0).getUseename());
            NAuth.add(Users.get(0).getDatetime());
            NAuth.add(Token);

            AuthCodes.add(NAuth);

            return Token;
        }
        else
        {
            return "";
        }

    }



    //Get AUth Check
    public boolean AuthCheck(String Token)
    {
        for(int i=0;i<AuthCodes.size();i++)
        {
            ArrayList Item = AuthCodes.get(i);
            if(Item.get(3).equals(Token))
            {
                return true;
            }
        }
        return false;
    }




    //Auth Header check in cookies
    public boolean AuthCheckExist(JSONObject Header)
    {
        try
        {
            if (Header.has("Cookie"))
            {
                String cookie = Header.getString("Cookie");
                System.out.println(cookie);
                // Return true if "auth=" exists in the cookie string
                return cookie.contains("auth=");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace(); // Optional: print stack trace for debugging
        }
        return false;
    }



    //Get Generate User Token
    private String GenerateAuthToken()
    {
        // Define characters to use in the key
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder key = new StringBuilder();

        // Create a secure random number generator
        SecureRandom random = new SecureRandom();

        // Generate a 24-character key
        for (int i = 0; i < 24; i++) {
            int randomIndex = random.nextInt(characters.length());
            key.append(characters.charAt(randomIndex));
        }

        return key.toString();
    }




}
