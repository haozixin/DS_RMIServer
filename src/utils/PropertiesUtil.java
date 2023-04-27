package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Author:  Zixin Hao
 * Student ID: 1309180
 */
public class PropertiesUtil {
    private static final Properties props = new Properties();

    public static final String SERVER_CONFIG_PROPERTIES = "serverConfig.properties";


    private static void loadConfig(String path) {
        try {
            InputStream inputStream = PropertiesUtil.class.getClassLoader().getResourceAsStream(path);
            props.load(inputStream);
        } catch (IOException ex) {
            System.out.println("Hey! Error loading config file: " + ex.getMessage());
        }
    }


    public static String getConfig(String key, String configPath){
        loadConfig(configPath);
        return props.getProperty(key);
    }

}