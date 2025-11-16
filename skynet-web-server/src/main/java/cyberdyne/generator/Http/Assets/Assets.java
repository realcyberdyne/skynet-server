package cyberdyne.generator.Http.Assets;

import com.google.common.io.Resources;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Assets
{
    public static String Asset(String AssetName)
    {
        try
        {
            String result = loadAssetContent(AssetName);
            return result;
        }
        catch (Exception e)
        {
            return "Internal server error : " + e.getMessage();
        }
    }

    public static File AssetFile(String AssetName) throws Exception
    {
        // First try external Assets directory (next to jar)
        Path externalPath = Paths.get("Assets" + AssetName);

        if (Files.exists(externalPath))
        {
            System.out.println("Loading asset from external file: " + externalPath.toAbsolutePath());
            return externalPath.toFile();
        }

        // If in debug mode or resource is directly accessible as file
        if (cyberdyne.generator.Conf.Config.Http_Debug)
        {
            try
            {
                URL resource = Resources.getResource("Assets" + AssetName);
                return new File(resource.toURI());
            }
            catch (Exception e)
            {
                System.out.println("Could not load as direct file, will extract from jar");
            }
        }

        // Extract from jar to temp file
        return extractAssetFromJar(AssetName);
    }

    // Helper method to load asset content as string
    private static String loadAssetContent(String assetName) throws IOException
    {
        // First try external Assets directory
        Path externalPath = Paths.get("Assets" + assetName);

        if (Files.exists(externalPath))
        {
            System.out.println("Loading asset content from external file: " + externalPath.toAbsolutePath());
            return new String(Files.readAllBytes(externalPath), StandardCharsets.UTF_8);
        }

        // Fallback to classpath (inside jar)
        try
        {
            URL resource = Resources.getResource("Assets" + assetName);
            return Resources.toString(resource, StandardCharsets.UTF_8);
        }
        catch (IllegalArgumentException e)
        {
            throw new IOException("Asset file not found: " + assetName + " (checked both external and classpath)");
        }
    }

    // Helper method to extract asset from jar to temp file
    private static File extractAssetFromJar(String assetName) throws IOException
    {
        InputStream inputStream = Assets.class.getClassLoader()
                .getResourceAsStream("Assets" + assetName);

        if (inputStream == null)
        {
            throw new FileNotFoundException("Asset not found in jar: Assets" + assetName);
        }

        // Create temp file
        String fileName = assetName.substring(assetName.lastIndexOf("/") + 1);
        String extension = "";
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex > 0)
        {
            extension = fileName.substring(dotIndex);
            fileName = fileName.substring(0, dotIndex);
        }

        File tempFile = File.createTempFile(fileName + "_", extension);
        tempFile.deleteOnExit();

        // Copy content to temp file
        try (FileOutputStream outputStream = new FileOutputStream(tempFile))
        {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1)
            {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
        finally
        {
            inputStream.close();
        }

        System.out.println("Extracted asset to temp file: " + tempFile.getAbsolutePath());
        return tempFile;
    }
}