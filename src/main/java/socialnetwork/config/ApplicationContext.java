package socialnetwork.config;

import java.util.Properties;

public class ApplicationContext {
    private static final Properties PROPERTIES = Config.getProperties();

    /**
     * @return an object of type Properties, representing the Properties of the application
     */
    public static Properties getPROPERTIES() {
        return PROPERTIES;
    }
}
