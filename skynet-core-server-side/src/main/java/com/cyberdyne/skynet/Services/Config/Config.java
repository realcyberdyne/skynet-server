package com.cyberdyne.skynet.Services.Config;

import java.io.FileReader;
import java.util.Properties;

public class Config
{

    //Global variables
    public static int VPNPort;

    public static String VT600Enc;


    //Get constractor
    public Config()
    {

        //Get read file
        try
        {
            FileReader reader = new FileReader("Protocol.properties");
            Properties properties = new Properties();
            properties.load(reader);

            //Proxy internal port repository address
            VPNPort=Integer.parseInt(properties.getProperty("VPNPort").toString());

            //Vpn T600 Protocol Encryption Key
            VT600Enc=properties.getProperty("VT600Enc").toString();

        }
        catch (Exception e)
        {
            //Print error
            System.out.println("Config error : "+e.getMessage());
        }


    }


}
