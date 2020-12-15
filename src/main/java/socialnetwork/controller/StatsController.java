package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.util.ResourceUtils;
import socialnetwork.domain.Friendship;
import socialnetwork.domain.User;
import socialnetwork.domain.UserDTO;
import socialnetwork.domain.messages.Message;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.MessageService;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

enum TypeReport {
    PDF, HTML
}

public class StatsController {
    private UserDTO selectedUserDTO;
    private MessageService messageService;
    private FriendshipService friendshipService;

    @FXML
    DatePicker datePickerStartDate;
    @FXML
    DatePicker datePickerEndDate;
    @FXML
    PieChart pieChartMessages;
    @FXML
    PieChart pieChartFriendships;
    @FXML
    TextField textFieldYear;

    public void setSelectedUserDTO(UserDTO selectedUserDTO) {
        this.selectedUserDTO = selectedUserDTO;
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    public void setFriendshipService(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    private void generateReport(TypeReport typeReport) {
        LocalDate dateStart = datePickerStartDate.getValue();
        LocalDate dateEnd = datePickerEndDate.getValue();
        if (!validateDates(dateStart, dateEnd)) {
            return;
        }
        List<Message> messageList = messageService.getListAllMessagesToUserTimeInterval(selectedUserDTO.getId(), dateStart, dateEnd);
        List<Friendship> friendshipList = friendshipService.getListAllFriendshipsUserTimeInterval(
                selectedUserDTO.getId(), dateStart, dateEnd
        );
        if (messageList.size() == 0) { // No messages in that Date Period
            messageList.add(new Message(new User("No messages", "No messages"), null, "No messages", LocalDateTime.now()));
        }
        try {
            File file = ResourceUtils.getFile("classpath:report.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(messageList);
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("UserReport", selectedUserDTO.getFullName() + "'s Report");
            parameters.put("StatisticsFriendsMessages", "You befriended " + friendshipList.size() +
                    " people and received " + messageList.size() + " messages");
            parameters.put("DatePeriodReport", "Date Period: " + dateStart + " - " + dateEnd);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            String pathToGenerateTo = "C:\\Users\\dasco\\" + selectedUserDTO.getFullName().replace(' ', '_');
            if (typeReport == TypeReport.PDF) {
                JasperExportManager.exportReportToPdfFile(jasperPrint, pathToGenerateTo + "Report.pdf");
            } else if (typeReport == TypeReport.HTML) {
                JasperExportManager.exportReportToHtmlFile(jasperPrint, pathToGenerateTo + "Report.html");
            }
        } catch (FileNotFoundException | JRException e) {
            e.printStackTrace();
        }
    }

    public void generatePDFReport() {
        generateReport(TypeReport.PDF);
    }

    public void generateHTMLReport() {
        generateReport(TypeReport.HTML);
    }

    private boolean validateDates(LocalDate dateStart, LocalDate dateEnd) {
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

    public void eventShowGraphs() {
        if (textFieldYear.getText().length() >= 4) {
            try {
                Integer year = Integer.parseInt(textFieldYear.getText());
                setPieChart(pieChartMessages, messageService.getMessagesToUserYear(selectedUserDTO.getId(), year), year, "messages");
                setPieChart(pieChartFriendships, friendshipService.getNewFriendsUserYear(selectedUserDTO.getId(), year), year, "friendships");
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Introduce a valid year!");
                alert.show();
            }
        } else {
            pieChartMessages.setTitle("");
            pieChartMessages.setData(FXCollections.emptyObservableList());
            pieChartFriendships.setTitle("");
            pieChartFriendships.setData(FXCollections.emptyObservableList());
        }
    }

    private void setPieChart(PieChart pieChart, Map<String, Integer> mapData, Integer year, String entitiesString) {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        mapData.entrySet().removeIf(entry -> entry.getValue() == 0); // Remove the months with 0 entities
        mapData.keySet().forEach(key -> {
            int number = mapData.get(key);
            String text = key + ": " + number + " " + entitiesString;
            pieChartData.add(new PieChart.Data(text, number));
        });
        if (mapData.keySet().size() == 0) { // Don't show the pie chart if there is no data for that year
            pieChart.setTitle("");
            pieChart.setData(FXCollections.emptyObservableList());
        } else {
            pieChart.setData(pieChartData);
            pieChart.setClockwise(true);
            pieChart.setStartAngle(180);
            pieChart.setLabelLineLength(20);
            pieChart.setTitle(entitiesString.substring(0, 1).toUpperCase() + entitiesString.substring(1) + " in " + year);
        }
    }
}
