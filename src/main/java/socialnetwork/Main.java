package socialnetwork;

import socialnetwork.GUI.MainFX;
import socialnetwork.GUI.MainFXTest;
import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.Friendship;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.User;
import socialnetwork.domain.messages.FriendshipRequest;
import socialnetwork.domain.messages.Message;
import socialnetwork.domain.messages.ReplyMessage;
import socialnetwork.domain.validators.*;
import socialnetwork.repository.Repository;
import socialnetwork.repository.database.UserDBRepository;
import socialnetwork.repository.file.*;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.MessageService;
import socialnetwork.service.ReplyMessageService;
import socialnetwork.service.UserService;
import socialnetwork.service.FriendshipRequestService;
import socialnetwork.ui.UI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException {
        MainFX.main(args);
    }
}


