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
    public static Users_Model UserData=new Users_Model();



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
                //System.out.println(cookie);
                // Return true if "auth=" exists in the cookie string
                return cookie.contains(AuthCookieName+"=");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace(); // Optional: print stack trace for debugging
        }
        return false;
    }



    //Auth Header check in cookies
    public boolean AuthCheckLogined(JSONObject Header)
    {
        try
        {
            if(AuthCheckExist(Header))
            {
                String Token = getAuthValueFromCookie(Header);
                if(!Token.equals(""))
                {
                    for (int i = 0; i < AuthCodes.size(); i++) {
                        ArrayList<String> Auths = AuthCodes.get(i);

                        if (Auths != null && Auths.size() >= 4)
                        {
                            if (Auths.get(3).equals(Token))
                            {
                                String []getid = Auths.toString().split(",");
                                String id = getid[0].replace("[","");

                                UserData.setId(Long.parseLong(id));
                                UserData.setUseename(Auths.get(1));
                                UserData.setDatetime(Auths.get(2));

                                return true;
                            }
                        }
                        else
                        {
                            System.out.println("Invalid Auths entry at index " + i);
                        }
                    }

                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace(); // Optional: print stack trace for debugging
        }
        return false;
    }



    //Get auth value
    private String getAuthValueFromCookie(JSONObject Header) {
        try {
            if (Header.has("Cookie")) {
                String cookie = Header.getString("Cookie");
//                System.out.println("Cookies: " + cookie);

                // Split the cookie string by semicolons
                String[] cookies = cookie.split(";");
                for (String c : cookies) {
                    c = c.trim(); // remove leading/trailing whitespace
                    if (c.startsWith(AuthCookieName+"=")) {
                        return c.substring((AuthCookieName+"=").length()); // return the value after "auth="
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // Optional: print stack trace for debugging
        }
        return null; // auth not found
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
