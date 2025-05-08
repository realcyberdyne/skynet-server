package com.cyberdyne.skynet.Services.VPN.Functions;

import com.cyberdyne.skynet.Services.Encription.EncriptionCLS;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class VPNCore
{


    //Get constractor function
    public VPNCore(int Port,String EncryptionKey)
    {
        try
        {
            ServerSocket Server = new ServerSocket(Port);

            while (true)
            {
                Socket request=Server.accept();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try
                        {
                            GetHandleProxy(request,EncryptionKey);
                        }
                        catch (Exception e)
                        {

                        }
                    }
                }).start();
            }
        }
        catch (Exception e)
        {

        }
    }



    //Get handle request from socket
    public static void GetHandleProxy(Socket request,String EncryptionKey)
    {
        try
        {
            BufferedWriter BW=new BufferedWriter(new OutputStreamWriter(request.getOutputStream()));
            BufferedReader BR=new BufferedReader(new InputStreamReader(request.getInputStream()));

            System.out.println("New request...");

            //Get Decrypt Request From Client First Line HTTP Request
            String FristLine = EncriptionCLS.decrypt(BR.readLine(),EncryptionKey);

            if(FristLine==null)
            {
                request.close();
                return;
            }

            String RequestFirstLine[]=FristLine.split(" ");

            if (RequestFirstLine.length < 2 || !RequestFirstLine[0].toUpperCase().equals("CONNECT"))
            {
                System.out.println("Error 400");
                BW.write("HTTP/1.1 400 Bad Request\n\r\n\r");
                BW.flush();
                request.close();
                return;
            }

            String[] hostParts = RequestFirstLine[1].split(":");
            String UrlAddress=hostParts[0];
            int targetPort = hostParts.length > 1 ? Integer.parseInt(hostParts[1]) : 443;


            //Get connect to server
            Socket m_server=new Socket(UrlAddress,targetPort);
            InputStream IS=m_server.getInputStream();
            OutputStream OS=m_server.getOutputStream();
            System.out.println("Connected to sever");

            //Get send to sever
            BW.write("HTTP/1.1 200 Connection Established\r\n\r\n");
            BW.flush();


            // Prepare for bidirectional data transfer
            byte[] buffer = new byte[4096];
            int bytesRead;


            while (true)
            {

                if(request.getInputStream().available() > 0)
                {
                    bytesRead = request.getInputStream().read(buffer);
                    if (bytesRead == -1) break;

                    //Get Decrypt request
                    String RequestsLines = new String(buffer);
                    RequestsLines = EncriptionCLS.decrypt(RequestsLines,EncryptionKey);

                    OS.write(RequestsLines.getBytes(),0,bytesRead);
                    OS.flush();
                }

                if(m_server.getInputStream().available() > 0)
                {
                    bytesRead = m_server.getInputStream().read(buffer);
                    if (bytesRead == -1) break;

                    //Get Decrypt request
                    String ReponseLines = new String(buffer);
                    ReponseLines = EncriptionCLS.decrypt(ReponseLines,EncryptionKey);

                    request.getOutputStream().write(ReponseLines.getBytes(),0,bytesRead);
                    request.getOutputStream().flush();
                }

                // Small delay to prevent tight spinning
                //Thread.sleep(10);

            }

        }
        catch (Exception e)
        {

        }
    }


}