package com.cyberdyne.skynet.connection.manager.DTO;

import com.cyberdyne.skynet.connection.manager.Database.DatabaseManager;
import com.cyberdyne.skynet.connection.manager.Models.Users_Model;
import com.cyberdyne.skynet.connection.manager.Services.DateTime.DateTime;
import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class UsersDTO
{


    //Get Insert new user to database
    public boolean GetInsertNewUser(Users_Model NUser)
    {
        try
        {
            String HashedPassword = Hashing.sha256().hashString(NUser.getPassword(), StandardCharsets.UTF_8).toString();

            //Get current datatime
            String CurrentDateTime = DateTime.GetDateTime();

            //Get generate sql quary
            String NewUserQuary = "INSERT INTO users_tbl (username, password, datetime) VALUES ('" + NUser.getUseename() + "', '" + HashedPassword + "', '" + CurrentDateTime + "')";

            //Get submit on database
            return new DatabaseManager().OprationOnDatabase(NewUserQuary);
        }
        catch (Exception e)
        {
            System.out.println("Error in UserDTO : "+e.getMessage());
        }
        return false;
    }


    //Get Select Users from database
    public ArrayList<Users_Model> GetSelectUsers()
    {
        ArrayList<Users_Model> result=new ArrayList<>();
        try
        {
            //Get generate sql quary
            String NewUserQuary = "SELECT * FROM users_tbl";

            //Get submit on database
            ArrayList<ArrayList<String>> SELECTUser = new DatabaseManager().SelectFromDatabase(NewUserQuary);

            for(int i=0;i<SELECTUser.size();i++)
            {
                ArrayList<String> UserRow=SELECTUser.get(i);
                Users_Model UserData = new Users_Model(Long.parseLong(UserRow.get(0)),UserRow.get(1),UserRow.get(2),UserRow.get(3));
                result.add(UserData);
            }
        }
        catch (Exception e)
        {
            System.out.println("Error in UsersDTO : "+e.getMessage());
        }
        return result;
    }


    //Get Select Users from database
    public ArrayList<Users_Model> GetSelectUser(String Username,String Password)
    {
        ArrayList<Users_Model> result=new ArrayList<>();
        try
        {
            String HashedPassword = Hashing.sha256().hashString(Password, StandardCharsets.UTF_8).toString();

            //Get generate sql quary
            String NewUserQuary = "SELECT * FROM users_tbl WHERE username = '"+Username+"' AND password='"+HashedPassword+"';";

            //Get submit on database
            ArrayList<ArrayList<String>> SELECTUser = new DatabaseManager().SelectFromDatabase(NewUserQuary);

            for(int i=0;i<SELECTUser.size();i++)
            {
                ArrayList<String> UserRow=SELECTUser.get(i);
                Users_Model UserData = new Users_Model(Long.parseLong(UserRow.get(0)),UserRow.get(1),UserRow.get(2),UserRow.get(3));
                result.add(UserData);
            }
        }
        catch (Exception e)
        {
            System.out.println("Error in UsersDTO : "+e.getMessage());
        }
        return result;
    }



    //Get Update User from database
    public boolean GetSelectUser(long id,String Username,String Passoword)
    {
        try
        {
            //Get hash password
            String HashedPassword = Hashing.sha256().hashString(Passoword, StandardCharsets.UTF_8).toString();

            //Get generate sql quary
            String NewUserQuary = "UPDATE users_tbl SET username='"+Username+"',password='"+HashedPassword+"' WHERE id="+id+";";

            //Get submit on database
            return new DatabaseManager().OprationOnDatabase(NewUserQuary);

        }
        catch (Exception e)
        {
            System.out.println("Error in UsersDTO : "+e.getMessage());
        }
        return false;
    }



    //Get delete User from database
    public boolean GetSelectUser(long id)
    {
        try
        {
            //Get generate sql quary
            String NewUserQuary = "DELETE FROM users_tbl WHERE id="+id+";";

            //Get submit on database
            return new DatabaseManager().OprationOnDatabase(NewUserQuary);

        }
        catch (Exception e)
        {
            System.out.println("Error in UsersDTO : "+e.getMessage());
        }
        return false;
    }

}
