package socialnetwork.domain.validators;

import socialnetwork.domain.events.Event;


public class ValidatorEvent implements Validator<Event> {

    /**
     * Method that validates a Event upon adding
     * @param entity Event, representing the Event to be validated
     * @throws ValidationException if
     *      the Event has empty Name, Start Date, End Date, Location, Organization, Category, Description
     */
    @Override
    public void validate(Event entity) throws ValidationException {
        String errors = "";
        if (entity.getName().matches("[ ]*")) {
            errors += "Empty name\n";
        }
        if (entity.getStartDate() == null) {
            errors += "Empty First Date\n";
        }
        if (entity.getEndDate() == null) {
            errors += "Empty End Date\n";
        }
        if (entity.getLocation().matches("[ ]*")) {
            errors += "Empty Location\n";
        }
        if (entity.getOrganization().matches("[ ]*")) {
            errors += "Empty Organization\n";
        }
        if (entity.getCategory().matches("[ ]*")) {
            errors+= "Empty Category\n";
        }
        if (entity.getDescription().matches("[ ]*")) {
            errors += "Empty Description\n";
        }
        if (errors.length() > 0) {
            throw new ValidationException("Empty fields!");
        }
    }
}
