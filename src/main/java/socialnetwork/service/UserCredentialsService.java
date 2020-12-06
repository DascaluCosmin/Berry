package socialnetwork.service;

import socialnetwork.domain.UserCredentials;
import socialnetwork.repository.database.UserCredentialsDBRepository;

public class UserCredentialsService {
    private UserCredentialsDBRepository userCredentialsRepository;

    public UserCredentialsService(UserCredentialsDBRepository userCredentialsRepository) {
        this.userCredentialsRepository = userCredentialsRepository;
    }

    public UserCredentials addUserCredentials(UserCredentials userCredentialsParam) {
        return userCredentialsRepository.save(userCredentialsParam);
    }

    public Iterable<UserCredentials> getAll() {
        return userCredentialsRepository.findAll();
    }

    public UserCredentials findOne(Long idUser) {
        return userCredentialsRepository.findOne(idUser);
    }

    public UserCredentials findOne(String username) {
        return userCredentialsRepository.findOne(username);
    }
}
