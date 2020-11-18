package socialnetwork.domain.validators;

import socialnetwork.domain.messages.FriendshipRequest;

public class FriendshipRequestValidator implements Validator<FriendshipRequest> {

    /**
     * Method that validates a Friendship Request entity
     * @param entity FriendshipRequest, representing the Friendship Request to be validated
     * @throws ValidationException if
     *      the ID of the sender is negative,
     *      the IDs of the receivers are negative,
     *      the text of the message contains only blank spaces
     *      the status of the friendship request is not "declined", "accepted" or "pending"
     */
    @Override
    public void validate(FriendshipRequest entity) throws ValidationException {
        String errors = "";
        if (entity.getFrom().getId() != null && entity.getFrom().getId() < 0) {
            errors += "The message can't have a sender with a negative ID!\n";
        }
        if (entity.getTo().stream().anyMatch(user -> user.getId() < 0)) {
            errors += "The message can't have receivers with a negative ID!\n";
        }
        if (entity.getMessage().matches("[ ]*")) {
            errors += "The message can't contain only blank spaces\n";
        }
        if (!entity.getStatusRequest().equals("declined") && !entity.getStatusRequest().equals("accepted") &&
                !entity.getStatusRequest().equals("pending")) {
            errors += "Invalid status!\n";
        }
        if (errors.length() > 0) {
            throw new ValidationException(errors);
        }
    }
}
