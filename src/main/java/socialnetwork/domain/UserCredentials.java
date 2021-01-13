package socialnetwork.domain;

public class UserCredentials extends Entity<Long> {
    private String username;
    private String password;

    /**
     * Constructor that creates a new UserCredentials
     * @param username String, representing the username of the User
     * @param password String, representing the password of the User
     */
    public UserCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * @return String, representing the username of the User
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username String, representing the username of the User
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return String, representing the password of the User
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password String, representing the password of the User
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return String, representing the serialization of a UserCredentials
     */
    @Override
    public String toString() {
        return "UserCredentials{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
