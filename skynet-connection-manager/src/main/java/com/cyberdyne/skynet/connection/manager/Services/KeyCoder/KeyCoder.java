package com.cyberdyne.skynet.connection.manager.Services.KeyCoder;

import com.cyberdyne.skynet.connection.manager.Models.Connectin_Models;
import netscape.javascript.JSObject;
import org.json.JSONObject;

import java.net.InetAddress;
import java.util.Base64;

public class KeyCoder
{

    public static String KeyCode(Connectin_Models connection,String fn)
    {
        JSONObject result=new JSONObject();

        result.put("id",connection.getId());
        result.put("key",connection.getKey());
        result.put("protocol",connection.getProtocol());
        result.put("ip",GetIpAddress());
        result.put("fn",fn);

        String JsonResult = result.toString();
        String Base64JsonResult = Base64.getEncoder().encodeToString(JsonResult.getBytes());

        return Base64JsonResult;
    }


    //Get Ip address
    private static String GetIpAddress()
    {
        try
        {
            InetAddress localHost = InetAddress.getLocalHost();
            //System.out.println("Local IP Address: " + localHost.getHostAddress());
            return localHost.getHostAddress();
        }
        catch (Exception e)
        {
            System.err.println("Cannot get IP address: " + e.getMessage());
        }
        return "0.0.0.0";
    }

}
