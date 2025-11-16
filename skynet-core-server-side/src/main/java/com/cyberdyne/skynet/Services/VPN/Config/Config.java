package com.cyberdyne.skynet.Services.VPN.Config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class Config
{
    //Global variables
    public static int VPNPort = 8085;
    public static int VPNTPort = 8089;
    public static String VT600Enc = "ZR8Mag$H?k{giJLg}8B0bcJS";

    //Get constructor
    public Config()
    {
        try
        {
            Properties properties = new Properties();

            // First try external file (next to jar)
            File externalConfig = new File("Protocol.properties");

            if (externalConfig.exists()) {
                System.out.println("Loading VPN config from external file: " + externalConfig.getAbsolutePath());
                FileInputStream fis = new FileInputStream(externalConfig);
                properties.load(fis);
                fis.close();
            } else {
                // Fallback to classpath (inside jar)
                System.out.println("External VPN config not found, trying classpath...");
                InputStream inputStream = getClass().getClassLoader()
                        .getResourceAsStream("Protocol.properties");

                if (inputStream == null) {
                    System.out.println("VPN Config error: Protocol.properties not found in classpath either");
                    System.out.println("Using default values...");
                    return;
                }

                properties.load(inputStream);
                inputStream.close();
            }

            // Proxy internal port repository address
            String vpnPortStr = properties.getProperty("VPNPort");
            if (vpnPortStr != null) {
                VPNPort = Integer.parseInt(vpnPortStr);
            }

            // Proxy Thread port repository address
            String vpnTPortStr = properties.getProperty("VPNTPort");
            if (vpnTPortStr != null) {
                VPNTPort = Integer.parseInt(vpnTPortStr);
            }

            // Vpn T600 Protocol Encryption Key
            String vt600EncStr = properties.getProperty("VT600Enc");
            if (vt600EncStr != null) {
                VT600Enc = vt600EncStr;
            }

            System.out.println("Vpn Config file is loaded");
            System.out.println("VPNPort: " + VPNPort);
            System.out.println("VPNTPort: " + VPNTPort);

        }
        catch (Exception e)
        {
            System.out.println("Config error 2: " + e.getMessage());
            e.printStackTrace();
        }
    }
}