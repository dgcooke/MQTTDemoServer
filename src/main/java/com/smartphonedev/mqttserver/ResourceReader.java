package com.smartphonedev.mqttserver;
import com.smartphonedev.mqttserver.exceptions.InvalidResourceFileException;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;


/*protected*/ final class ResourceReader
{
    private static final  Logger LOGGER = Logger.getLogger(ResourceReader.class.getName());
    private static final String CA_CERTIFICATE_PATH = "caCertificateFilePath";
    private static final String CLIENT_CERTIFICATE_PATH = "clientCertificateFilePath";
    private static final String CLIENT_KEY_PATH = "clientKeyFilePath";
    private static final String SERVER_HOST = "serverHost";
    private static final String SERVER_HOST_USER_NAME = "userName";
    private static final String SERVER_HOST_PASSWORD = "password";

    public String getCACertificateFilePath()
    {
        var jsonObject = readResourceFileAsJsonObject();
        if (jsonObject.isPresent())
        {
            if (jsonObject.get().containsKey(CA_CERTIFICATE_PATH))
            {
                return jsonObject.get().getString(CA_CERTIFICATE_PATH);
            }
        }
        return "";
    }

    public String getClientCertificatePath()
    {
        var jsonObject = readResourceFileAsJsonObject();
        if (jsonObject.isPresent())
        {
            if (jsonObject.get().containsKey(CLIENT_CERTIFICATE_PATH))
            {
                return jsonObject.get().getString(CLIENT_CERTIFICATE_PATH);
            }
        }
        return "";
    }

    public String getClientKeyPath()
    {
        var jsonObject = readResourceFileAsJsonObject();
        if (jsonObject.isPresent())
        {
            if (jsonObject.get().containsKey(CLIENT_KEY_PATH))
            {
                return jsonObject.get().getString(CLIENT_KEY_PATH);
            }
        }
        return "";
    }

    private Optional<JsonObject> readResourceFileAsJsonObject()
    {
        try
        {
           return getJSONStringAsJsonObject(getFileContentsLineByLineAsString(getClientSettingsFile()));

        } catch (Exception e)
        {
            System.out.println("Exception thrown (" + e.getClass().getName() + ") reading the configuration file");
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public String getServerHostname()
    {
        var jsonObject = readResourceFileAsJsonObject();
        if (jsonObject.isPresent())
        {
            if (jsonObject.get().containsKey(SERVER_HOST))
            {
                return jsonObject.get().getString(SERVER_HOST);
            }
        }
        return "";
    }

    public String getPassword()
    {
        var jsonObject = readResourceFileAsJsonObject();
        if (jsonObject.isPresent())
        {
            if (jsonObject.get().containsKey(SERVER_HOST_PASSWORD))
            {
                return jsonObject.get().getString(SERVER_HOST_PASSWORD);
            }
        }
        return "";
    }

    public String getUserName()
    {
        var jsonObject = readResourceFileAsJsonObject();
        if (jsonObject.isPresent())
        {
            if (jsonObject.get().containsKey(SERVER_HOST_USER_NAME))
            {
                return jsonObject.get().getString(SERVER_HOST_USER_NAME);
            }
        }
        return "";
    }


    private File getClientSettingsFileOld() throws URISyntaxException, InvalidResourceFileException
    {
       final  Optional<URL> resource = Optional.ofNullable(getClass().getClassLoader().getResource("client_settings.json"));
        if (resource.isPresent())
        {
            return new File(resource.get().toURI());
        }
        throw new InvalidResourceFileException("Resource file not loaded");
    }

    private File getClientSettingsFileNewer() throws URISyntaxException, InvalidResourceFileException
    {
        final String configFileName = "client_settings.json";
        ClassLoader classLoader = getClass().getClassLoader();

        URL resource = classLoader.getResource(configFileName);
        if (resource == null) {
            throw new IllegalArgumentException("file not found! " + configFileName);
        } else
        {
            return new File(resource.toURI());
        }
    }

    private InputStream getClientSettingsFile() throws URISyntaxException, InvalidResourceFileException
    {
        final String configFileName = "client_settings.json";
        ClassLoader classLoader = getClass().getClassLoader();
        var inputStream = classLoader.getResourceAsStream(configFileName);

        // the stream holding the file content
        if (inputStream == null) {
            throw new IllegalArgumentException("file not found! " + configFileName);
        } else {
            return inputStream;
        }
    }

    private String getFileContentsLineByLineAsString(final InputStream configFileInputStream)
    {
        final var  contentBuilder = new StringBuilder();
        final var inputStreamReader = new InputStreamReader(configFileInputStream);
        final var bufferedReader = new BufferedReader(inputStreamReader);
        bufferedReader.lines().forEach(contentBuilder::append);
        try
        {
            bufferedReader.close();
            inputStreamReader.close();
            configFileInputStream.close();
        } catch (IOException ioe)
        {
            System.out.println("Exception thrown closing resourced");
        }
        return contentBuilder.toString();
    }

    private Optional<JsonObject> getJSONStringAsJsonObject(final String jsonString)
    {
        if ((jsonString != null) && (!jsonString.isEmpty()))
        {
            try
            {
                JsonReader reader = Json.createReader(new StringReader(jsonString));
                JsonObject json = reader.readObject();
                reader.close();
                return Optional.of(json);
            } catch (Exception e)
            {
                LOGGER.log(Level.SEVERE, "Exception thrown deserialising the json String: " + jsonString);
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }
}
