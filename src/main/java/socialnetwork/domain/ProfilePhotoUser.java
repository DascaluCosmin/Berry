package socialnetwork.domain;

public class ProfilePhotoUser extends Entity<Long>{
    String pathProfilePhoto;

    public ProfilePhotoUser() {
        pathProfilePhoto = "C:\\Users\\dasco\\IdeaProjects\\proiect-lab-schelet\\src\\main\\resources\\images\\noProfilePhoto.png";
    }

    public ProfilePhotoUser(String pathProfilePhoto) {
        this.pathProfilePhoto = pathProfilePhoto;
    }

    public String getPathProfilePhoto() {
        return pathProfilePhoto;
    }

    public void setPathProfilePhoto(String pathProfilePhoto) {
        this.pathProfilePhoto = pathProfilePhoto;
    }

    @Override
    public String toString() {
        return getId() + " " + getPathProfilePhoto();
    }
}
