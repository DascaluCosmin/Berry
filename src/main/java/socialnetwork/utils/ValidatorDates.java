package socialnetwork.utils;

import javafx.scene.control.Alert;

import java.time.LocalDate;

public class ValidatorDates {

    /**
     * Method that validates two Dates
     * @param dateStart LocalDate, representing the first Date
     * @param dateEnd LocalDate, representing the second Date
     * @return true, if the two dates are not null and the first Date is before the second Date
     *         false, otherwise
     */
    public static boolean validateDates(LocalDate dateStart, LocalDate dateEnd) {
        if (dateStart == null || dateEnd == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please introduce the Date Period");
            alert.show();
            return false;
        }
        if (dateEnd.compareTo(dateStart) < 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please introduce a valid Date Period");
            alert.show();
            return false;
        }
        return true;
    }
}
