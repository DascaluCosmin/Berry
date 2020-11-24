package socialnetwork.repository.file;

import socialnetwork.domain.Friendship;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;

public class FriendshipFileRepository extends AbstractFileRepository<Tuple<Long, Long>, Friendship> {
    private Repository<Long, User> repositoryUser;

    /**
     * Constructor that creates a new FriendshipFileRepository
     * @param fileName String, representing the name of the file where the data is loaded from / stored to
     * @param validator Validator<Friendship>, representing the validator of the FriendshipFileRepository
     * @param repositoryUser Repository<Long, User>, representing a Repository of Users
     */
    public FriendshipFileRepository(String fileName, Validator<Friendship> validator, Repository<Long, User> repositoryUser) {
        super(fileName, validator);
        this.repositoryUser = repositoryUser;
        createListsFriends();
    }

    /**
     * Method that extracts a Friendship having a specified list of attributes
     * @param attributes List<String>, representing the attributes of the Friendship to be extracted
     * @return Friendship, representing the extracted Friendship based on the given attributes
     */
    @Override
    public Friendship extractEntity(List<String> attributes) {
        Friendship friendship = new Friendship(LocalDate.parse(attributes.get(0)));
        Long leftID = Long.parseLong(attributes.get(1));
        Long rightID = Long.parseLong(attributes.get(2));
        friendship.setId(new Tuple(leftID, rightID));
        return friendship;
    }

    /**
     * Method that gets the serialization of a Friendship
     * @param entity Friendship, representing the entity whose serialization is being determined
     * @return String, representing the serialization of the Friendship
     */
    @Override
    protected String createEntityAsString(Friendship entity) {
        return entity.getDate() + ";" + entity.getId().getLeft() + ";" + entity.getId().getRight();
    }

    /**
     * Method that creates the lists of friends for each User
     */
    private void createListsFriends() {
        entities.forEach((ids, element) -> {
            {
                User userLeft = repositoryUser.findOne(ids.getLeft());
                User userRight = repositoryUser.findOne(ids.getRight());
                userLeft.getFriends().add(userRight);
                userRight.getFriends().add(userLeft);
            }
        });
    }
}
