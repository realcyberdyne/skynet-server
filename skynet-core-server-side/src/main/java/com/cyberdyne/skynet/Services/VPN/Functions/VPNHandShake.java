package com.cyberdyne.skynet.Services.VPN.Functions;

import com.cyberdyne.skynet.Services.VPN.Config.Config;
import com.cyberdyne.skynet.connection.manager.DTO.ConnectionDTO;
import com.cyberdyne.skynet.connection.manager.Models.Connectin_Models;
import com.cyberdyne.skynet.connection.manager.Statics.IpConnection_Model;
import com.cyberdyne.skynet.connection.manager.Statics.Statics;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class VPNHandShake
{

    public VPNHandShake()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    ServerSocket ThreadPortHandShakeSocket = new ServerSocket(Config.VPNTPort);
                    while (true)
                    {
                        Socket ShakedSocket = ThreadPortHandShakeSocket.accept();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                HandShakeSocket(ShakedSocket);
                            }
                        }).start();
                    }
                }
                catch (Exception e)
                {
                    System.err.println("Error Hand Shaker : "+e.getMessage());
                }
            }
        }).start();
    }


    private void HandShakeSocket(Socket ShakedSocket)
    {
        try
        {
            DataInputStream DIS = new DataInputStream(ShakedSocket.getInputStream());
            DataOutputStream DOS = new DataOutputStream(ShakedSocket.getOutputStream());

            String Hash = DIS.readUTF();
            //System.out.println("Hash : "+Hash);
            Connectin_Models Connection = new ConnectionDTO().GetConnectionByHash(Hash);

            if(Connection.getProtocol().equals("T600"))
            {
                String Ipaddress = ShakedSocket.getInetAddress().getHostAddress();

                //System.out.println("Key is : "+Connection.getKey());

                //Get add to key connection data structure
                Statics.IpConnection.put(
                    Ipaddress,
                    Connection.getKey()
                );
            }
        }
        catch (Exception e)
        {
            System.err.println("Error Hand Shaker : "+e.getMessage());
        }
    }

}
