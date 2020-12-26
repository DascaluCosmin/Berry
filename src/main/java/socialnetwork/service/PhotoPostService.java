package socialnetwork.service;

import socialnetwork.domain.posts.PhotoPost;
import socialnetwork.repository.database.PhotoPostDBRepository;

import java.util.ArrayList;
import java.util.List;

public class PhotoPostService {
    private final PhotoPostDBRepository photoPostDBRepository;

    /**
     * Constructor that creates a new PhotoPostService
     * @param photoPostDBRepository PhotoPostDBRepository, representing the Repository that handles the Photo Posts data
     */
    public PhotoPostService(PhotoPostDBRepository photoPostDBRepository) {
        this.photoPostDBRepository = photoPostDBRepository;
    }

    /**
     * Method that adds a new Photo Post
     * @param photoPostToBeAdded PhotoPost, representing the Photo Post to be added
     * @return null, if the Photo Post was added successfully
     *      non-null PhotoPost, otherwise
     */
    public PhotoPost addPhotoPost(PhotoPost photoPostToBeAdded) {
        return photoPostDBRepository.save(photoPostToBeAdded);
    }

    /**
     * Method that deletes a Photo Post
     * @param idPhotoPost Long, representing the ID of the Photo Post to be deleted
     * @return null, if the Photo Post doesn't exist
     *      non-null Photo Post, if the Photo Post was deleted successfully
     */
    public PhotoPost deletePhotoPost(Long idPhotoPost) {
        return photoPostDBRepository.delete(idPhotoPost);
    }

    /**
     * Method that gets the list of photo posts
     * @return List<PhotoPost>, representing the list of photo posts
     */
    public List<PhotoPost> getListPhotoPosts() {
        List<PhotoPost> photoPostList = new ArrayList<>();
        photoPostDBRepository.findAll().forEach(photoPostList::add);
        return photoPostList;
    }

    /**
     * Method that gets the list of photo posts of a User
     * @param idUser Long, representing the ID of the User
     * @return List<PhotoPost>, representing the list of photo posts
     */
    public List<PhotoPost> getListPhotoPosts(Long idUser) {
        List<PhotoPost> photoPostList = new ArrayList<>();
        photoPostDBRepository.findAll(idUser).forEach(photoPostList::add);
        return photoPostList;
    }

    /**
     * Method that gets a specific Photo Post
     * @param idPhotoPost Long, representing the ID of the Photo Post to be selected
     * @return PhotoPost, representing the Photo Post to be selected
     */
    public PhotoPost getPhotoPost(Long idPhotoPost) {
        return photoPostDBRepository.findOne(idPhotoPost);
    }
}