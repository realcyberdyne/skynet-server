package com.cyberdyne.skynet.connection.manager.DTO;

import com.cyberdyne.skynet.connection.manager.Database.DatabaseManager;
import com.cyberdyne.skynet.connection.manager.Models.Users_Model;
import com.cyberdyne.skynet.connection.manager.Services.DateTime.DateTime;
import com.cyberdyne.skynet.connection.manager.Services.Hash.Hash;

public class UsersDTO
{


    //Get Insert new user to database
    public boolean GetInsertNewUser(Users_Model NUser)
    {
        try
        {
            String HashedPassword = Hash.hashPassword(NUser.getPassword());

            //Get current datatime
            String CurrentDateTime = DateTime.GetDateTime();

            //Get generate sql quary
            String NewUserQuary = "INSERT INTO users_model (username, password, datetime) VALUES ('" + NUser.getUseename() + "', '" + HashedPassword + "', '" + CurrentDateTime + "')";

            //Get submit on database
            return new DatabaseManager().OprationOnDatabase(NewUserQuary);
        }
        catch (Exception e)
        {
            System.out.println("Error in UserDTO : "+e.getMessage());
        }
        return false;
    }

}
