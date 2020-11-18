package socialnetwork.repository.file;

import socialnetwork.domain.User;
import socialnetwork.domain.validators.Validator;

import java.util.List;

public class UserFileRepository extends AbstractFileRepository<Long, User>{

    /**
     * Constructor that creates a new UserFileRepository
     * @param fileName String, representing the name of the file where the data is loaded from / written to
     * @param validator ValidatorService<User>, representing the validator of the UserFileRepository
     */
    public UserFileRepository(String fileName, Validator<User> validator) {
        super(fileName, validator);
    }

    /**
     * Method that extracts an User having a specified list of attributes
     * @param attributes List<String>, representing the attributes of the User to be extracted
     * @return User, representing the extracted User based on the given attributes
     */
    @Override
    public User extractEntity(List<String> attributes) {
        User user = new User(attributes.get(1),attributes.get(2));
        user.setId(Long.parseLong(attributes.get(0)));
        return user;
    }

    /**
     * Method that gets the serialization of an User
     * @param entity User, representing the User whose serialization is being determined
     * @return String, representing the serialization of the User
     */
    @Override
    protected String createEntityAsString(User entity) {
        return entity.getId()+";"+entity.getFirstName()+";"+entity.getLastName();
    }
}
