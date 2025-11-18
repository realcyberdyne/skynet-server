package com.cyberdyne.skynet.connection.manager.Statics;

import java.util.ArrayList;

public class Statics_old
{
    public static ArrayList<IpConnection_Model> IpConnection=new ArrayList<>();


    //Get Find Key by ip
    public static String GetKey(String IP)
    {
        for(int i=0;i<IpConnection.size();i++)
        {
            if(IpConnection.get(i).Ip.equals(IP))
            {
                return IpConnection.get(i).Key;
            }
        }
        return "";
    }

}
