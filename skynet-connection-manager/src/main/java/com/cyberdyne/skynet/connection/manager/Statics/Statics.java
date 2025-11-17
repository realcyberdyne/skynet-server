package com.cyberdyne.skynet.connection.manager.Statics;

import com.cyberdyne.skynet.connection.manager.Models.Connectin_Models;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class Statics
{
    public static ConcurrentHashMap<String, String> IpConnection = new ConcurrentHashMap<>();

    public static String GetKey(String IP)
    {
        System.out.println("Looking for IP: " + IP);
        System.out.println("Available IPs: " + IpConnection.keySet());

        String key = IpConnection.getOrDefault(IP, "");

        if (key.isEmpty()) {
            System.err.println("WARNING: No key found for IP: " + IP);
        }

        return key;
    }

    public static void AddKey(String IP, String Key)
    {
        IpConnection.put(IP, Key);
        System.out.println("Added key for IP: " + IP);
    }
}