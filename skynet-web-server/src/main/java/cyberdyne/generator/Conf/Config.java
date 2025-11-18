package cyberdyne.generator.Conf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class Config
{
    //Global variables
    public static String Root_Dir="/Uploads";
    public static int Http_Port=9095;
    public static boolean Http_Debug=true;
    public static boolean Http_File_Upload=false;
    public static boolean Http_Access_File=true;
    public static String Http_User_Encript_Key="http_user_encript_key";
    public static String Http_Log_File_Address="/logs.log";
    public static int FileSize=17000000;
    public static int FileSizeSpli=256;
    public static String SERVER_IP = "127.0.0.1";

    public Config()
    {
        try
        {
            Properties properties = new Properties();

            // First try external file (next to jar)
            File externalConfig = new File("Protocol.properties");

            if (externalConfig.exists()) {
                System.out.println("Loading config from external file: " + externalConfig.getAbsolutePath());
                FileInputStream fis = new FileInputStream(externalConfig);
                properties.load(fis);
                fis.close();
            } else {
                // Fallback to classpath (inside jar)
                System.out.println("External file not found, trying classpath...");
                InputStream inputStream = getClass().getClassLoader()
                        .getResourceAsStream("Protocol.properties");

                if (inputStream == null) {
                    System.out.println("Config error: Protocol.properties not found in classpath either");
                    return;
                }

                properties.load(inputStream);
                inputStream.close();
            }

            // Files repository address
            Root_Dir = properties.getProperty("root_dir");

            // Http config
            Http_Port = Integer.parseInt(properties.getProperty("http_port"));
            Http_User_Encript_Key = properties.getProperty("http_user_encript_key");
            Http_Log_File_Address = properties.getProperty("http_log_file_address");
            Http_Debug = Boolean.parseBoolean(properties.getProperty("http_debug"));
            Http_File_Upload = Boolean.parseBoolean(properties.getProperty("http_file_upload"));
            Http_Access_File = Boolean.parseBoolean(properties.getProperty("http_access_file"));

            // File size conf
            FileSize = Integer.parseInt(properties.getProperty("file_size_lim"));

            // File spli array size conf
            FileSizeSpli = Integer.parseInt(properties.getProperty("file_spli_size"));

            //Get vpn server ip address
            SERVER_IP = properties.getProperty("ip_address");

            System.out.println("Config file loaded successfully!");
            System.out.println("Root_Dir: " + Root_Dir);
            System.out.println("Http_Port: " + Http_Port);
        }
        catch (Exception e)
        {
            System.out.println("Config error : " + e.getMessage());
            e.printStackTrace();
        }
    }
}