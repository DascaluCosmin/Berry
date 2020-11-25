package socialnetwork.domain.getter;

import socialnetwork.domain.UserDTO;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

public class GetterUserDTO implements Getter<UserDTO> {
    @Override
    public String get(UserDTO object, String property) {
        PropertyDescriptor propertyDescriptor;
        try {
            propertyDescriptor = new PropertyDescriptor(property, object.getClass());
            return propertyDescriptor.getReadMethod().invoke(object).toString();
        } catch (IntrospectionException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
