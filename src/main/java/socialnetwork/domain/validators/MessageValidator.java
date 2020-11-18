package socialnetwork.domain.validators;

import socialnetwork.domain.messages.Message;

public class MessageValidator implements Validator<Message> {

    /**
     * Method that validates an Message entity
     * @param entity Message, representing the Message to be validated
     * @throws ValidationException if
     *      the ID of the sender is negative,
     *      the IDs of the receivers are negative,
     *      the text of the message contains only blank spaces
     */
    @Override
    public void validate(Message entity) throws ValidationException {
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
        if (errors.length() > 0) {
            throw new ValidationException(errors);
        }
    }
}
