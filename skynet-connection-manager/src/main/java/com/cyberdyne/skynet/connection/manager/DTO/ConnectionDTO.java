package com.cyberdyne.skynet.connection.manager.DTO;

import com.cyberdyne.skynet.connection.manager.Database.DatabaseManager;
import com.cyberdyne.skynet.connection.manager.Models.Connectin_Models;
import com.cyberdyne.skynet.connection.manager.Models.Users_Model;
import com.cyberdyne.skynet.connection.manager.Services.DateTime.DateTime;
import com.cyberdyne.skynet.connection.manager.Services.KeyGenerator.KeyGenerator;

import java.util.ArrayList;

public class ConnectionDTO
{
    
    //Get Insert new connection to database
    public boolean GetInsertNewConnection(Connectin_Models NConnection)
    {
        try
        {
            //Generate Key
            NConnection.setKey(KeyGenerator.GetGenerateKey());

            //Get generate sql quary
            String Quary = "INSERT INTO connection_tbl (key,create_user_id,protocol,status) VALUES ('"+NConnection.getKey()+"',"+NConnection.getCreate_user_id()+",'"+NConnection.getProtocol()+"',"+NConnection.isStatus()+")";

            //Get submit on database
            return new DatabaseManager().OprationOnDatabase(Quary);
        }
        catch (Exception e)
        {
            System.out.println("Error in ConnectionDTO : "+e.getMessage());
        }
        return false;
    }



    //Get All Connections
    public ArrayList<Connectin_Models> GetSelectConnections()
    {
        ArrayList<Connectin_Models> result=new ArrayList<>();
        try
        {
            //Get generate sql quary
            String Quary = "SELECT * FROM connection_tbl;";

            //Get submit on database
            ArrayList<ArrayList<String>> SELECTUser = new DatabaseManager().SelectFromDatabase(Quary);

            for(int i=0;i<SELECTUser.size();i++)
            {
                ArrayList<String> UserRow=SELECTUser.get(i);
                try {
                    long id = Long.parseLong(UserRow.get(0));
                    String key = UserRow.get(1);
                    String protocol = UserRow.get(2);
                    long user_id = Long.parseLong(UserRow.get(3));
                    boolean status = Boolean.parseBoolean(UserRow.get(4));

                    Connectin_Models ConnectionData = new Connectin_Models(id, key, protocol, user_id, status);
                    result.add(ConnectionData);
                }
                catch (Exception e)
                {
                    System.out.println("Error : "+e.getMessage());
                }
            }
        }
        catch (Exception e)
        {
            System.out.println("Error in ConnectionDTO : "+e.getMessage());
        }
        return result;
    }


    //Get All Connections
    public Connectin_Models GetConnection(long id)
    {
        Connectin_Models result=new Connectin_Models();
        try
        {
            //Get generate sql quary
            String Quary = "SELECT * FROM connection_tbl WHERE id = "+id+";";

            //Get submit on database
            ArrayList<ArrayList<String>> SELECTUser = new DatabaseManager().SelectFromDatabase(Quary);

            if(SELECTUser.size()>=1)
            {
                long connection_id = Long.parseLong(SELECTUser.get(0).get(0));
                String key = SELECTUser.get(0).get(1);
                String protocol = SELECTUser.get(0).get(2);
                long user_id = Long.parseLong(SELECTUser.get(0).get(3));
                boolean status = Boolean.parseBoolean(SELECTUser.get(0).get(4));

                result.setId(connection_id);
                result.setKey(key);
                result.setProtocol(protocol);
                result.setCreate_user_id(user_id);
                result.setStatus(status);
            }
        }
        catch (Exception e)
        {
            System.out.println("Error in ConnectionDTO : "+e.getMessage());
        }
        return result;
    }


    //Get Remove new connecton to database
    public boolean GetRemoveConnection(long id)
    {
        try
        {
            //Get generate sql quary
            String Quary = "DELETE FROM connection_tbl WHERE id = "+id+";";

            //Get submit on database
            return new DatabaseManager().OprationOnDatabase(Quary);
        }
        catch (Exception e)
        {
            System.out.println("Error in ConnectionDTO : "+e.getMessage());
        }
        return false;
    }


    //Get update connection in database
    public boolean GetUpdateConnection(Connectin_Models UConnection)
    {
        try
        {
            //Get generate sql quary
            String Quary = "UPDATE connection_tbl SET key='"+UConnection.getKey()+"',status="+UConnection.isStatus()+",protocol='"+UConnection.getProtocol()+"' WHERE id = "+UConnection.getId()+";";

            //Get submit on database
            return new DatabaseManager().OprationOnDatabase(Quary);
        }
        catch (Exception e)
        {
            System.out.println("Error in ConnectionDTO : "+e.getMessage());
        }
        return false;
    }


}
