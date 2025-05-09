package cyberdyne.generator;

import com.cyberdyne.skynet.Services.VPN.Functions.VPNCore;
import cyberdyne.generator.Conf.Config;
import cyberdyne.generator.Functions.RandomLoginPassword;
import cyberdyne.generator.Http.HttpServer;

public class Main
{
    public static void main(String[] args)
    {
        //Log
        System.out.println("Welcome to Cyberdyne");
        System.out.println("Cyberdyne service : get start....");

        //Get read properties values
        new Config();
        System.out.println("Cyberdyne service : Config file is loaded");

        //Get begin http server
        new HttpServer();
        System.out.println("Cyberdyne service : Http server is started");

        //Get Generate Login Password
        new RandomLoginPassword();

        //Get Config
        new com.cyberdyne.skynet.Services.Config.Config();

        //Get VPn
        new VPNCore(8090,"reza");

    }
}