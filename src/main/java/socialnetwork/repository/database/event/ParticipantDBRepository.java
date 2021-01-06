package socialnetwork.repository.database.event;

import org.postgresql.util.PSQLException;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.events.Participant;
import socialnetwork.repository.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParticipantDBRepository implements Repository<Tuple<Long, Long>, Participant> {
    private String url;
    private String username;
    private String password;

    /**
     * Constructor that creates a new ParticipantDBRepository
     * @param url String, representing the URL of the Data Base
     * @param username String, representing the Username of the user connecting to the DB
     * @param password Password, representing the Password of the user connecting to the DB
     */
    public ParticipantDBRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * Method that gets one specific Participant
     * @param idTuple Tuple<Long, Long>, representing the ID of the Participant
     * @return null, if the Participant doesn't exist
     *      non-null Participant, otherwise
     */
    @Override
    public Participant findOne(Tuple<Long, Long> idTuple) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "SELECT * FROM participants WHERE \"EventID\" = " + idTuple.getLeft() +
                    " AND \"UserID\" = " +  idTuple.getRight();
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return getParticipant(resultSet);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    /**
     * Method that gets the list of all Participants
     * @return Iterable<Participant>, representing the list of all Participants
     */
    @Override
    public Iterable<Participant> findAll() {
        List<Participant> listParticipants = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "SELECT * FROM participants";
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                listParticipants.add(getParticipant(resultSet));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return listParticipants;
    }

    /**
     * Method that gets the list of all Participants to an Event
     * @param idEvent Long, representing the ID of the Event
     * @return Iterable<Participant>, representing the list of all Participants to an Event
     */
    public Iterable<Participant> findAll(Long idEvent) {
        List<Participant> listParticipants = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "SELECT * FROM participants WHERE \"EventID\" = " + idEvent;
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                listParticipants.add(getParticipant(resultSet));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return listParticipants;
    }

    /**
     * Method that adds a new Participant to the Data Base
     * @param entity Participant, representing the entity to be added
     *         entity must be not null
     * @return non-null Participant, if the Participant was added successfully
     *      null, otherwise
     */
    @Override
    public Participant save(Participant entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            int isNotified = entity.getNotified() ? 1 : 0;
            String command = "INSERT INTO participants (\"EventID\", \"UserID\", \"isNotified\") VALUES " +
                    "(" + entity.getId().getLeft() + ", " + entity.getId().getRight() + ", '" + isNotified + "') " +
                    "RETURNING *";
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            try {
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return getParticipant(resultSet);
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
     * Method that deletes a Participant from the Data Base
     * @param idTuple Tuple<Long, Long>, representing the ID of the Participant to be deleted
     * @return null, if the Participant doesn't exist
     *      non-null Participant, if the Participant was deleted successfully
     */
    @Override
    public Participant delete(Tuple<Long, Long> idTuple) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "DELETE FROM participants WHERE \"EventID\" = " + idTuple.getLeft() +
                    " AND \"UserID\" = " + idTuple.getRight() + " RETURNING *";
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return getParticipant(resultSet);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    /**
     * Method that updates a Participant in the Data Base
     * @param entity Participant, representing the new Participant
     *          entity must not be null
     * @return non-null Participant, if the Participant was updated successfully
     *      null, otherwise
     */
    @Override
    public Participant update(Participant entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            int isNotified = entity.getNotified() ? 1 : 0;
            String command = "UPDATE participants SET \"isNotified\" = '" + isNotified + "' " +
                    "WHERE \"EventID\" = " + entity.getId().getLeft() + " AND \"UserID\" = " + entity.getId().getRight() +
                    " RETURNING *";
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return getParticipant(resultSet);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    /**
     * Method that gets a Participant from the current position of the Result Set
     * @param resultSet ResultSet, representing the Result Set
     * @return Participant, representing the Participant built from the current position of the Result Set
     * @throws SQLException, if a field from the Data Base doesn't exist
     */
    private Participant getParticipant(ResultSet resultSet) throws SQLException {
        Long idEvent = resultSet.getLong("EventID");
        Long idUser = resultSet.getLong("UserID");
        Boolean isNotified = resultSet.getBoolean("isNotified");
        return new Participant(idEvent, idUser, isNotified);
    }
}
