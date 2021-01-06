package socialnetwork.service;

import socialnetwork.domain.ContentPage;
import socialnetwork.domain.posts.PhotoPost;
import socialnetwork.repository.database.userPosts.PhotoPostDBRepository;
import socialnetwork.repository.database.userPosts.PostUserType;

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
     * Method that gets the list of Photo Posts
     * @return List<PhotoPost>, representing the list of Photo Posts
     */
    public List<PhotoPost> getListPhotoPosts() {
        List<PhotoPost> photoPostList = new ArrayList<>();
        photoPostDBRepository.findAll().forEach(photoPostList::add);
        return photoPostList;
    }

    /**
     * Method that gets the list of Photo Posts of a User
     * @param idUser Long, representing the ID of the User
     * @return List<PhotoPost>, representing the list of Photo Posts
     */
    public List<PhotoPost> getListPhotoPosts(Long idUser) {
        List<PhotoPost> photoPostList = new ArrayList<>();
        photoPostDBRepository.findAll(idUser).forEach(photoPostList::add);
        return photoPostList;
    }

    /**
     * Method that gets the list of Photo Posts of a User on a specific Page
     * @param idUser Long, representing the ID of the User
     * @param page ContentPage, representing the Page containing the Photo Posts
     * @return List<PhotoPost>, representing the list of Photo Posts on that Page
     */
    public List<PhotoPost> getListPhotoPosts(Long idUser, ContentPage page) {
        List<PhotoPost> photoPostList = new ArrayList<>();
        photoPostDBRepository.findAll(idUser, page, PostUserType.USER).forEach(photoPostList::add);
        return photoPostList;
    }

    /**
     * Method that gets the list of Photo Posts of a User's friends, on a specific Page
     * @param idUser Long, representing the ID of the User
     * @param page ContentPage, representing the Page containing the Photo Posts
     * @return List<PhotoPost>, representing the list of Photo Posts of the User's friends, on that Page
     */
    public List<PhotoPost> getListPhotoPostsFriends(Long idUser, ContentPage page) {
        List<PhotoPost> photoPostListFriends = new ArrayList<>();
        photoPostDBRepository.findAll(idUser, page, PostUserType.FRIEND).forEach(photoPostListFriends::add);
        return photoPostListFriends;
    }

    /**
     * Method that gets a specific Photo Post
     * @param idPhotoPost Long, representing the ID of the Photo Post to be selected
     * @return PhotoPost, representing the Photo Post to be selected
     */
    public PhotoPost getPhotoPost(Long idPhotoPost) {
        return photoPostDBRepository.findOne(idPhotoPost);
    }

    /**
     * Method that gets the number of Photo Posts an User has
     * @param idUser Long, representing the ID of the User
     * @return Integer, representing the number of Photo Posts the User has
     */
    public Integer getNumberPhotoPosts(Long idUser) {
        return getListPhotoPosts(idUser).size();
    }
}
