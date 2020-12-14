package socialnetwork.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatsController {
    private UserDTO selectedUserDTO;
    private MessageService messageService;
    private FriendshipService friendshipService;

    @FXML
    DatePicker datePickerStartDate;
    @FXML
    DatePicker datePickerEndDate;

    public void setSelectedUserDTO(UserDTO selectedUserDTO) {
        this.selectedUserDTO = selectedUserDTO;
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    public void setFriendshipService(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    public void generatePDFReport() {
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
        String pathToGenerateTo = "C:\\Users\\dasco\\" + selectedUserDTO.getFullName().replace(' ', '_') + "Report.pdf";
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
            JasperExportManager.exportReportToPdfFile(jasperPrint, pathToGenerateTo);
        } catch (FileNotFoundException | JRException e) {
            e.printStackTrace();
        }
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
}
