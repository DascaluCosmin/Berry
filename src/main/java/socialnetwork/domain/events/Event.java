package socialnetwork.domain.events;

import socialnetwork.domain.Entity;
import socialnetwork.domain.User;

import java.time.LocalDate;

public class Event extends Entity<Long> {
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private String organization;
    private String description;
    private String location;
    private String category;
    private String photoURL;
    private User organizer;

    /**
     * Constructor that creates a new Event
     * @param name String, representing the Name of the Event
     * @param startDate LocalDate, representing the Date the Event starts
     * @param endDate LocalDate, representing the Date the Event ends
     * @param organization String, representing the name of the Organization holding the Event
     * @param description String, representing the Description of the Event
     * @param location String, representing the Location the Event takes place
     * @param category String, representing the Category of the Event
     * @param photoURL String, representing the URL to the
     * @param organizer User, representing the User creating the Event
     */
    public Event(String name, LocalDate startDate, LocalDate endDate, String organization, String description, String location, String category, String photoURL, User organizer) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.organization = organization;
        this.description = description;
        this.location = location;
        this.category = category;
        this.photoURL = photoURL;
        this.organizer = organizer;
    }

    /**
     * @return String, representing the Name of the Event
     */
    public String getName() {
        return name;
    }

    /**
     * @param name String, representing the Name of the Event
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return LocalDate, representing the Date the Event starts
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * @param startDate LocalDate, representing the Date the Event starts
     */
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    /**
     * @return LocalDate, representing the Date the Event ends
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * @param endDate LocalDate, representing the Date the Event ends
     */
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    /**
     * @return String, representing the name of the Organization holding the Event
     */
    public String getOrganization() {
        return organization;
    }

    /**
     * @param organization String, representing the name of the Organization holding the Event
     */
    public void setOrganization(String organization) {
        this.organization = organization;
    }

    /**
     * @return String, representing the Description of the Event
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description String, representing the Description of the Event
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return String, representing the Location the Event takes place
     */
    public String getLocation() {
        return location;
    }

    /**
     * @param location String, representing the Location the Event takes place
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * @return String, representing the Category of the Event
     */
    public String getCategory() {
        return category;
    }

    /**
     * @param category String, representing the Category of the Event
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * @return String, representing the URL to the
     */
    public String getPhotoURL() {
        return photoURL;
    }

    /**
     * @param photoURL String, representing the URL to the
     */
    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    /**
     * @return User, representing the User creating the Event
     */
    public User getOrganizer() {
        return organizer;
    }

    /**
     * @param organizer User, representing the User creating the Event
     */
    public void setOrganizer(User organizer) {
        this.organizer = organizer;
    }

    /**
     * @return String, representing the serialization of the Event
     */
    @Override
    public String toString() {
        return "ID = " + getId() + ", " +
                "name=" + name + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", organization='" + organization + '\'' +
                ", description='" + description + '\'' +
                ", location='" + location + '\'' +
                ", category='" + category + '\'' +
                ", photoURL='" + photoURL + '\'' +
                ", organizer=" + organizer +
                '}';
    }
}
