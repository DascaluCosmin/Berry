package socialnetwork.utils.passwordEncryption;

public class PasswordCrypt {
    private static int WORKLOAD = 12; // The workload used to generate the hashed password. 10-31 represents a valid value

    /**
     * Method that encrypts a password using the BCrypt Encryption Method
     * @param passwordToBeEncrypted String, representing the Password to be encrypted
     * @return String, representing the encrypted password (hash)
     */
    public static String encryptPassword(String passwordToBeEncrypted) {
        String salt = BCrypt.gensalt(WORKLOAD);
        return BCrypt.hashpw(passwordToBeEncrypted, salt);
    }

    /**
     * Method that checks if the hashed password and the User's password match
     * @param userPassword String, representing the Password the User provides
     * @param hashedPassword String, representing the stored hashed password
     * @return Boolean, true, if the two passwords do match (the User can log in)
     *                  false, otherwise
     * @throws IllegalArgumentException, if the Password the User provides is null or
     *              if the hashedPassword is not valid - to be valid it has to start with $2a$
     */
    public static boolean checkPassword(String userPassword, String hashedPassword) {
        // The prefix $2a$ means the Hash is a BCrypt Hash
        if (userPassword == null || !hashedPassword.startsWith("$2a$")) { //
            throw new IllegalArgumentException("Invalid hash provided for comparison");
        }
        return BCrypt.checkpw(userPassword, hashedPassword);
    }
}
