package socialnetwork.domain;

public class UserDTO extends Entity<Long>{
    private String firstName;
    private String lastName;

    /**
     * Constructor that creates a new UserDTO
     * @param firstName String, representing the First Name of the User
     * @param lastName String, representing the Last Name of the User
     */
    public UserDTO(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * @return String, representing the First Name of the User
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @return String, representing the Last Name of the User
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @return String, representing the Full Name of the User
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * @param firstName String, representing the First Name of the User
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @param lastName String, representing the First Name of the User
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return String, representing the First Name of the User
     */
    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}
