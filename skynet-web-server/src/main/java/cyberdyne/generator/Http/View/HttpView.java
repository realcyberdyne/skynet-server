package cyberdyne.generator.Http.View;

import com.google.common.io.Resources;
import org.apache.commons.io.Charsets;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HttpView
{
    //Get view function start
    public static String View(String ViewName)
    {
        try
        {
            String result = loadViewFile(ViewName);

            //Get check include file
            if(result.contains("@include('"))
            {
                String lines[] = result.split("\n");
                for(int i = 0; i < lines.length; i++)
                {
                    if(lines[i].contains("@include('"))
                    {
                        int index_qoute_start = lines[i].indexOf("'") + 1;
                        int index_qoute_end = lines[i].indexOf("'", index_qoute_start + 1);

                        String includeFileName = lines[i].substring(index_qoute_start, index_qoute_end);
                        String include_value = loadViewFile(includeFileName);

                        //replace include value
                        result = result.replace("@include('" + includeFileName + "')", include_value);
                    }
                }
            }

            return result;
        }
        catch (Exception e)
        {
            return "Internal server error : " + e.getMessage();
        }
    }
    //Get view function end

    // Helper method to load view files from external directory or classpath
    private static String loadViewFile(String viewName) throws IOException
    {
        // First try external View directory (next to jar)
        Path externalPath = Paths.get("View", viewName + ".html");

        if (Files.exists(externalPath))
        {
            System.out.println("Loading view from external file: " + externalPath.toAbsolutePath());
            return new String(Files.readAllBytes(externalPath), StandardCharsets.UTF_8);
        }

        // Fallback to classpath (inside jar)
        try
        {
            URL resource = Resources.getResource("View/" + viewName + ".html");
            return Resources.toString(resource, StandardCharsets.UTF_8);
        }
        catch (IllegalArgumentException e)
        {
            throw new IOException("View file not found: " + viewName + ".html (checked both external and classpath)");
        }
    }
}