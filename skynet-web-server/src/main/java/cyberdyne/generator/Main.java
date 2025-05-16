package cyberdyne.generator;

import com.cyberdyne.skynet.Services.VPN.Functions.EncVPNCore;
import com.cyberdyne.skynet.Services.VPN.Functions.VPNCore;
import com.cyberdyne.skynet.connection.manager.DTO.ConnectionDTO;
import com.cyberdyne.skynet.connection.manager.Statics.Statics;
import cyberdyne.generator.Conf.Config;
import cyberdyne.generator.Http.HttpServer;

public class Main
{
    public static void main(String[] args)
    {
        //Log
        System.out.println("Welcome to Cyberdyne");
        System.out.println("Cyberdyne service : get start....");

//        Get Config
        new com.cyberdyne.skynet.Services.VPN.Config.Config();
        System.out.println("Vpn Config file is loaded");

//        Get read properties values
        new Config();
        System.out.println("Cyberdyne service : Config file is loaded");

//        Get begin http server
        new HttpServer();
        System.out.println("Cyberdyne service : Http server is started");

//        Get set on connections
        Statics.All_Connections = new ConnectionDTO().GetSelectConnections();

        new EncVPNCore(8085,"reza");

    }
}