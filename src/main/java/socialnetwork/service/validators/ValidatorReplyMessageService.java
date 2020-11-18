package socialnetwork.service.validators;

import socialnetwork.domain.messages.ReplyMessage;
import socialnetwork.domain.validators.ValidationException;

public class ValidatorReplyMessageService implements ValidatorService<ReplyMessage> {

    /**
     * Method that validates a ReplyMessage upon adding
     * @param entity ReplyMessage, representing the ReplyMessage to be validated
     * @throws ValidationException, if
     *      the ReplyMessage is not null, i.e. the ReplyMessage was not added successfully because it already exists
     */
    @Override
    public void validateAdd(ReplyMessage entity) throws ValidationException {
        if (entity != null) {
            throw new ValidationException("The reply message already exists!");
        }
    }

    /**
     * Method that validates a ReplyMessage upon deletion
     * @param entity ReplyMessage, representing the ReplyMessage to be validated
     * @throws ValidationException, if
     *      the ReplyMessage is null, i.e. the ReplyMessage to be deleted doesn't exist
     */
    @Override
    public void validateDelete(ReplyMessage entity) throws ValidationException {
        if (entity == null) {
            throw new ValidationException("The reply message to be deleted doesn't exist!");
        }
    }
}
