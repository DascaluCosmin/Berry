package socialnetwork.utils;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Constants {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter DATE_TIME_FORMATTER_SHORTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DATE_TIME_FORMATTER_MONTH_NAME = DateTimeFormatter.ofPattern("dd MMM yyyy");
    public static final List<String> months = new ArrayList<>(Arrays.asList("January", "February", "March", "April", "May", "June", "July",
                                                                    "August", "September", "October", "November", "December"));
}
