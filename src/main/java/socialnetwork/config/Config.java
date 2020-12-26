package socialnetwork.config;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Config {

    public static String CONFIG_LOCATION = Config.class.getClassLoader()
            .getResource("config.properties").getFile();
    public static String IMAGES_LOCATION_PATH = Config.class.getClassLoader()
            .getResource("images").getPath();


    public static Properties getProperties() {
        Properties properties=new Properties();
        try {
            properties.load(new FileReader(CONFIG_LOCATION));
            return properties;
        } catch (IOException e) {
            throw new RuntimeException("Cannot load config properties");
        }
    }

    public static String getImagesLocation() {
        return IMAGES_LOCATION_PATH;
    }
}
