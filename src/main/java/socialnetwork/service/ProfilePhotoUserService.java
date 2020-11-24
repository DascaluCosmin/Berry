package socialnetwork.service;

import socialnetwork.domain.ProfilePhotoUser;
import socialnetwork.repository.Repository;
import socialnetwork.utils.events.ChangeEventType;
import socialnetwork.utils.events.ProfilePhotoUserChangeEvent;
import socialnetwork.utils.observer.Observable;
import socialnetwork.utils.observer.Observer;

import java.util.ArrayList;
import java.util.List;

public class ProfilePhotoUserService implements Observable<ProfilePhotoUserChangeEvent> {
    Repository<Long, ProfilePhotoUser> profilePhotoUserRepository;
    List<Observer<ProfilePhotoUserChangeEvent>> observers = new ArrayList();

    /**
     * Constructor that creates a new ProfilePhotoUserService
     * @param profilePhotoUserRepository Repository<Long, ProfilePhotoUser>, representing the Repository that handles
     *                               the ProfilePhotoUser data
     */
    public ProfilePhotoUserService(Repository<Long, ProfilePhotoUser> profilePhotoUserRepository) {
        this.profilePhotoUserRepository = profilePhotoUserRepository;
    }

    /**
     * Method that adds a new ProfilePhotoUser
     * @param profilePhotoUserParam ProfilePhotoUser, representing the ProfilePhotoUser to be added
     * @return null, if the ProfilePhotoUser was added successfully
     *         non-null ProfilePhotoUser, otherwise (the ProfilePhotoUser already exists)
     */
    public ProfilePhotoUser addProfilePhotoUser(ProfilePhotoUser profilePhotoUserParam) {
        return profilePhotoUserRepository.save(profilePhotoUserParam);
    }

    /**
     * Method that deletes a ProfilePhotoUser
     * @param idProfilePhotoUser Long, representing the ID of the ProfilePhotoUser to be deleted
     * @return null, if the ProfilePhotoUser to be deleted doesn't exist
     *         non-null ProfilePhotoUser, representing the deleted ProfilePhotoUser, otherwise
     */
    public ProfilePhotoUser deleteProfilePhotoUser(Long idProfilePhotoUser) {
        return profilePhotoUserRepository.delete(idProfilePhotoUser);
    }

    /**
     * Method that updates a ProfilePhotoUser
     * @param newProfilePhotoUser ProfilePhotoUser, representing the new ProfilePhotoUser
     */
    public void updateProfilePhotoUser(ProfilePhotoUser newProfilePhotoUser) {
        deleteProfilePhotoUser(newProfilePhotoUser.getId());
        addProfilePhotoUser(newProfilePhotoUser);
        notifyAll(new ProfilePhotoUserChangeEvent(ChangeEventType.UPDATE));
    }

    /**
     * Method that gets one specific ProfilePhotoUser
     * @param idProfilePhotoUser Long, representing the ID of the ProfilePhotoUser to be selected
     * @return non-null ProfilePhotoUser, representing the selected ProfilePhotoUser (if the ID of the ProfilePhotoUser exists)
     *         null, otherwise
     */
    public ProfilePhotoUser findOne(Long idProfilePhotoUser) {
        return profilePhotoUserRepository.findOne(idProfilePhotoUser);
    }

    /**
     * Method that gets all the existing ProfilePhotoUsers
     * @return Iterable<ProfilePhotoUser>, representing all the existing ProfilePhotoUser
     */
    public Iterable<ProfilePhotoUser> getAll() {
        return profilePhotoUserRepository.findAll();
    }

    @Override
    public void addObserver(Observer<ProfilePhotoUserChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<ProfilePhotoUserChangeEvent> e) {
    }

    @Override
    public void notifyAll(ProfilePhotoUserChangeEvent profilePhotoUserChangeEvent) {
        observers.forEach(observer -> observer.update(profilePhotoUserChangeEvent));
    }
}
