package socialnetwork.repository.database.event;

import org.postgresql.util.PSQLException;
import socialnetwork.domain.ContentPage;
import socialnetwork.domain.events.Event;
import socialnetwork.domain.User;
import socialnetwork.repository.Repository;
import socialnetwork.repository.database.UserDBRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EventDBRepository implements Repository<Long, Event> {
    private String url;
    private String username;
    private String password;
    private UserDBRepository userRepository;

    /**
     * Constructor that creates a new EventDBRepository
     * @param url String, representing the URL of the Data Base
     * @param username String, representing the Username of the user connecting to the DB
     * @param password Password, representing the Password of the user connecting to the DB
     * @param userRepository UserDBRepository, representing the Repository handling the User data
     */
    public EventDBRepository(String url, String username, String password, UserDBRepository userRepository) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.userRepository = userRepository;
    }

    /**
     * Method that gets one specific Event
     * @param aLong Long, representing the ID of the Event
     * @return null, if the Event doesn't exist
     *      non-null Event, otherwise
     */
    @Override
    public Event findOne(Long aLong) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "SELECT * FROM events WHERE id = " + aLong;
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return getEvent(resultSet);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    /**
     * Method that gets the list of all Events
     * @return Iterable<Event>, representing the list of all Events
     */
    @Override
    public Iterable<Event> findAll() {
        List<Event> eventList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "SELECT * FROM events";
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                eventList.add(getEvent(resultSet));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return eventList;
    }

    /**
     * Method that gets the list of all Events on a specific Page
     * @param currentPage ContentPage, representing the Page containing the Events
     * @return Iterable<Event>, representing the list of Events on that Page
     */
    public Iterable<Event> findAll(ContentPage currentPage) {
        List<Event> eventList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "SELECT * FROM events" +
                    " LIMIT " + currentPage.getSizePage() + " OFFSET " + (currentPage.getNumberPage() - 1) * currentPage.getSizePage();
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                eventList.add(getEvent(resultSet));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return eventList;
    }

    /**
     * Method that gets the list of all Events that a User participates to, on a specific Page
     * @param idUser Long, representing the ID of the User participating to the Events
     * @param currentPage ContentPage, representing the Page containing the Events
     * @param eventParticipationType EventParticipationType, representing the type of Participation to the Event
     *                   the User has - HOST, PARTICIPATE or NO_PARTICIPATE
     * @return Iterable<Event>, representing the list of Events that the User participates to, on that Page
     */
    public Iterable<Event> findAll(Long idUser, ContentPage currentPage, EventParticipationType eventParticipationType) {
        List<Event> eventList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement preparedStatement = connection.prepareStatement(participationToCommand(idUser, currentPage, eventParticipationType));
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                eventList.add(getEvent(resultSet));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return eventList;
    }

    /**
     * Method that adds a new Event to the Data Base
     * @param entity Event, representing the entity to be added
     *         entity must be not null
     * @return non-null Event, if the Event was added successfully
     *      null, otherwise
     */
    @Override
    public Event save(Event entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "INSERT INTO events " +
                    "(\"Name\", \"DateStart\", \"DateEnd\", \"Organization\", \"Description\", \"Location\", \"Category\", \"PhotoURL\", \"OrganizerID\") VALUES " +
                    "('" + entity.getName() + "', " +
                    "'" + entity.getStartDate() + "', " +
                    "'" + entity.getEndDate() + "', " +
                    "'" + entity.getOrganization() + "', " +
                    "'" + entity.getDescription() + "', " +
                    "'" + entity.getLocation() + "', " +
                    "'" + entity.getCategory() + "', " +
                    "'" + entity.getPhotoURL() + "', " +
                    ""  + entity.getOrganizer().getId() + ") " +
                    "RETURNING *";
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            try {
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return getEvent(resultSet);
                }
            } catch (PSQLException e) {
                return null;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    /**
     * Method that deletes an Event from the Data Base
     * @param aLong Long, representing the ID of the Event to be deleted
     * @return null, if the Event doesn't exist
     *      non-null Event, if the Event was deleted successfully
     */
    @Override
    public Event delete(Long aLong) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "DELETE FROM events WHERE id = " + aLong + " RETURNING *";
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return getEvent(resultSet);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    /**
     * Method that updates an Event in the Data Base
     * @param entity Event, representing the new Event
     *          entity must not be null
     * @return null, if the Event was updated successfully
     *      non-null Event, otherwise
     */
    @Override
    public Event update(Event entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "UPDATE events SET " +
                    "\"Name\" = '" + entity.getName() + "', " +
                    "\"DateStart\" = '" + entity.getStartDate() + "', " +
                    "\"DateEnd\" = '" + entity.getEndDate() + "', " +
                    "\"Organization\" = '" + entity.getOrganization() + "', " +
                    "\"Description\" = '" + entity.getDescription() + "', " +
                    "\"Location\" = '" + entity.getLocation() + "', " +
                    "\"Category\" = '" + entity.getCategory() + "', " +
                    "\"PhotoURL\" = '" + entity.getPhotoURL() + "' " +
                    "WHERE id = " + entity.getId() + " " +
                    "RETURNING *";
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return null;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return entity;
    }

    /**
     * Method that gets a Photo Post from the current position of the Result Set
     * @param resultSet ResultSet, representing the Result Set
     * @return PhotoPost, representing the Photo Post built from the current position of the Result Set
     * @throws SQLException, if a field from the Data Base doesn't exist
     */
    private Event getEvent(ResultSet resultSet) throws SQLException {
        Long eventID = resultSet.getLong("id");
        String name = resultSet.getString("Name");
        LocalDate startDate = resultSet.getDate("DateStart").toLocalDate();
        LocalDate endDate = resultSet.getDate("DateEnd").toLocalDate();
        String organization = resultSet.getString("Organization");
        String description = resultSet.getString("Description");
        String location = resultSet.getString("Location");
        String category = resultSet.getString("Category");
        String photoURL = resultSet.getString("PhotoURL");
        Long organizerID = resultSet.getLong("OrganizerID");
        User organizer = userRepository.findOne(organizerID);
        Event event = new Event(
                name, startDate, endDate, organization, description, location,
                category, photoURL, organizer
        );
        event.setId(eventID);
        return event;
    }

    /**
     * Method that gets the specific SQL Command to be run, given the Type of Event Participation
     * @param idUser Long, representing the ID of the User participating to the Event
     * @param currentPage ContentPage, representing the Page containing the Events
     * @param eventParticipationType eventParticipationType EventParticipationType, representing the type of Participation to the Event
     *                   the User has - HOST, PARTICIPATE or NO_PARTICIPATE
     * @return
     */
    private String participationToCommand(Long idUser, ContentPage currentPage, EventParticipationType eventParticipationType) {
        String subQuery =  "( SELECT \"EventID\" FROM participants WHERE \"UserID\" = " + idUser + ")";
        if (eventParticipationType == EventParticipationType.HOST) {
            return "SELECT * FROM events WHERE \"OrganizerID\" = " + idUser +
                    " LIMIT " + currentPage.getSizePage() + " OFFSET " + (currentPage.getNumberPage() - 1) * currentPage.getSizePage();
        } else if (eventParticipationType == EventParticipationType.PARTICIPATE) {
            return "SELECT * FROM events WHERE id IN " + subQuery +
                    " LIMIT " + currentPage.getSizePage() + " OFFSET " + (currentPage.getNumberPage() - 1) * currentPage.getSizePage();
        }
        // EventParticipationType.NO_PARTICIPATE
        return "SELECT * FROM events WHERE id NOT IN " + subQuery +
                " LIMIT " + currentPage.getSizePage() + " OFFSET " + (currentPage.getNumberPage() - 1) * currentPage.getSizePage();
    }
}
