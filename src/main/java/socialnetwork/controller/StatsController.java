package socialnetwork.controller;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.util.ResourceUtils;
import socialnetwork.domain.Friendship;
import socialnetwork.domain.UserDTO;
import socialnetwork.domain.messages.Message;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.MessageService;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatsController {
    private UserDTO selectedUserDTO;
    private MessageService messageService;
    private FriendshipService friendshipService;

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
        Iterable<Message> messageIterable = messageService.getAllMessagesToUser(selectedUserDTO.getId());
        List<Message> messageList = new ArrayList<>();
        messageIterable.forEach(messageList::add);
        Iterable<Friendship> friendshipIterable = friendshipService.getAllFriendshipsUser(selectedUserDTO.getId());
        List<Friendship> friendshipList = new ArrayList<>();
        friendshipIterable.forEach(friendshipList::add);
        String pathToGenerateTo = "C:\\Users\\dasco\\" + selectedUserDTO.getFullName().replace(' ', '_') + "Report.pdf";
        try {
            File file = ResourceUtils.getFile("classpath:report.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(messageList);
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("UserReport", selectedUserDTO.getFullName() + "'s Report");
            parameters.put("StatisticsFriendsMessages", "You befriended " + friendshipList.size() +
                    " people and received " + messageList.size() + " messages");
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            JasperExportManager.exportReportToPdfFile(jasperPrint, pathToGenerateTo);
        } catch (FileNotFoundException | JRException e) {
            e.printStackTrace();
        }
    }
}
