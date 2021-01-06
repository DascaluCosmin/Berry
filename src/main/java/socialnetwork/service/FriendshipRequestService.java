package socialnetwork.service;

import socialnetwork.domain.ContentPage;
import socialnetwork.domain.Friendship;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.messages.FriendshipRequest;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.Repository;
import socialnetwork.repository.database.friendshipRequests.FriendshipRequestsDBRepository;
import socialnetwork.repository.database.friendshipRequests.TypeFriendshipRequest;
import socialnetwork.service.validators.ValidatorFriendshipRequest;
import socialnetwork.utils.events.ChangeEventType;
import socialnetwork.utils.events.FriendshipRequestChangeEvent;
import socialnetwork.utils.observer.Observable;
import socialnetwork.utils.observer.Observer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FriendshipRequestService implements Observable<FriendshipRequestChangeEvent> {
    private FriendshipRequestsDBRepository friendshipRequestRepository;
    private Repository<Tuple<Long, Long>, Friendship> friendshipRepository;
    private ValidatorFriendshipRequest validatorFriendshipRequestService = new ValidatorFriendshipRequest();
    private List<Observer<FriendshipRequestChangeEvent>> observers = new ArrayList<>();

    /**
     * Constructor that creates a new FriendshipRequestService
     * @param friendshipRequestRepository FriendshipRequestsDBRepository, representing the Repository
     *                                    that handles the FriendshipRequest data
     * @param friendshipRepository Repository<Tuple<Long, Long>, Friendship>, representing the Repository
     *                             that handles the Friendship data
     */
    public FriendshipRequestService(FriendshipRequestsDBRepository friendshipRequestRepository,
                                    Repository<Tuple<Long, Long>, Friendship> friendshipRepository) {
        this.friendshipRequestRepository = friendshipRequestRepository;
        this.friendshipRepository = friendshipRepository;
    }

    /**
     * Method that adds a new FriendshipRequest
     * @param friendshipRequestParam FriendshipRequest, representing the FriendshipRequest to be added
     * @return null, if the FriendshipRequest was added successfully
     *         non-null FriendshipRequest, otherwise (the FriendshipRequest already exists)
     * @throws ValidationException if the FriendshipRequest already exists
     */
    public FriendshipRequest addFriendshipRequest(FriendshipRequest friendshipRequestParam) throws ValidationException {
//        validatorFriendshipRequestService.validateBeforeAdding(friendshipRequestParam,
//                getAll(), friendshipRepository.findAll());
        return friendshipRequestRepository.save(friendshipRequestParam);
    }

    /**
     * Method that deletes a Friendship Request
     * @param idFriendshipRequest Long, representing the ID of the FriendshipRequest to be deleted
     * @return null, if the FriendshipRequest to be deleted doesn't exist
     *         non-null FriendshipRequest, representing the deleted FriendshipRequest, otherwise
     * @throws ValidationException, if the FriendshipRequest doesn't exist
     */
    public FriendshipRequest deleteFriendshipRequest(Long idFriendshipRequest) throws ValidationException {
        FriendshipRequest friendshipRequest = friendshipRequestRepository.delete(idFriendshipRequest);
        if (friendshipRequest != null) {
            notifyAll(new FriendshipRequestChangeEvent(ChangeEventType.DELETE));
        }
        return friendshipRequest;
    }

    public void updateFriendshipRequest(FriendshipRequest friendshipRequestToBeUpdated, String newStatus) {
        FriendshipRequest friendshipRequest = deleteFriendshipRequest(friendshipRequestToBeUpdated.getId());
        friendshipRequest.setStatusRequest(newStatus);
        friendshipRequest.setDate(LocalDateTime.now());
        friendshipRequest = addFriendshipRequest(friendshipRequest);
        if (friendshipRequest == null){
            notifyAll(new FriendshipRequestChangeEvent(ChangeEventType.UPDATE));
        }
    }

    /**
     * Method that accepts a Friendship Requests
     * It changes its status from Pending to Accepted
     * @param friendshipRequestParam FriendshipRequest, the FriendshipRequest to be accepted
     * @return null, if the FriendshipRequest was accepted successfully
     *      non-null FriendshipRequest, otherwise
     */
    public FriendshipRequest acceptFriendshipRequest(FriendshipRequest friendshipRequestParam) {
        friendshipRequestParam.setDate(LocalDateTime.now());
        friendshipRequestParam.setStatusRequest("accepted");
        FriendshipRequest friendshipRequest = friendshipRequestRepository.update(friendshipRequestParam);
        if (friendshipRequest == null) {
            friendshipRepository.save(new Friendship(new Tuple<>(friendshipRequestParam.getTo().get(0).getId(), friendshipRequestParam.getFrom().getId())));
            friendshipRepository.save(new Friendship(new Tuple<>(friendshipRequestParam.getFrom().getId(), friendshipRequestParam.getTo().get(0).getId())));
        }
        return friendshipRequest;
    }

    /**
     * Method that declines a Friendship Requests
     * It changes its status from Pending to Declined
     * @param friendshipRequestParam FriendshipRequest, the FriendshipRequest to be declined
     * @return null, if the FriendshipRequest was declined successfully
     *      non-null FriendshipRequest, otherwise
     */
    public FriendshipRequest declineFriendshipRequest(FriendshipRequest friendshipRequestParam) {
        friendshipRequestParam.setDate(LocalDateTime.now());
        friendshipRequestParam.setStatusRequest("declined");
        return friendshipRequestRepository.update(friendshipRequestParam);
    }

    /**
     * Method that gets the pending Friendship Requests of some User
     * @param idUser Long, representing the ID of the User
     * @return Iterable<FriendshipRequest>, representing the pending Friendship Requests
     */
    public Iterable<FriendshipRequest> getPendingRequests(Long idUser) {
        Iterable<FriendshipRequest> listRequests = friendshipRequestRepository.findAll();
        List<FriendshipRequest> listPendingRequestsUser = new ArrayList<>();
        listRequests.forEach(friendshipRequest -> {
            if (friendshipRequest.getTo().get(0).getId().equals(idUser) &&
                   friendshipRequest.getStatusRequest().equals("pending")) {
                listPendingRequestsUser.add(friendshipRequest);
            }
        });
        return listPendingRequestsUser;
    }

    /**
     * Method that gets the list of pending Friendship Requests received by an User on a specific Page
     * @param idUser Long, representing the ID of the User receiving the Friendship Requests
     * @param page ContentPage, representing the Page containing the Friendship Requests
     * @return List<FriendshipRequest>, representing the list of pending Friendship Requests received by the User, on that Page
     */
    public List<FriendshipRequest> getListReceivedPendingRequests(Long idUser, ContentPage page) {
        List<FriendshipRequest> friendshipRequestList = new ArrayList<>();
        friendshipRequestRepository.findAll(idUser, page, TypeFriendshipRequest.RECEIVED).forEach(friendshipRequestList::add);
        return friendshipRequestList;
    }

    /**
     * Method that gets the list of pending Friendship Requests sent by an User on a specific Page
     * @param idUser Long, representing the ID of the User sending the Friendship Requests
     * @param page ContentPage, representing the Page containing the Friendship Requests
     * @return List<FriendshipRequest>, representing the list of pending Friendship Requests sent by the User, on that Page
     */
    public List<FriendshipRequest> getListSentPendingRequests(Long idUser, ContentPage page) {
        List<FriendshipRequest> friendshipRequestList = new ArrayList<>();
        friendshipRequestRepository.findAll(idUser, page, TypeFriendshipRequest.SENT).forEach(friendshipRequestList::add);
        return friendshipRequestList;
    }

    /**
     * Method that gets all the Friendship Requests of some User
     * @param idUser Long, representing the ID of the User
     * @return Iterable<FriendshipRequest>, representing the pending Friendship Requests
     */
    public List<FriendshipRequest> getFriendshipRequestsUser(Long idUser) {
        Iterable<FriendshipRequest> listFriendshipRequests = friendshipRequestRepository.findAll();
        List<FriendshipRequest> listFriendshipRequestsUser = new ArrayList<>();
        listFriendshipRequests.forEach(friendshipRequest -> {
            if (friendshipRequest.getTo().get(0).getId().equals(idUser)) {
                listFriendshipRequestsUser.add(friendshipRequest);
            }
        });
        return listFriendshipRequestsUser;
    }

    public List<FriendshipRequest> getFriendshipRequestsUserFrom(Long idUser) {
        Iterable<FriendshipRequest> listFriendshipRequests = friendshipRequestRepository.findAll();
        List<FriendshipRequest> listFriendshipRequestsUser = new ArrayList<>();
        listFriendshipRequests.forEach(friendshipRequest -> {
            if (friendshipRequest.getFrom().getId().equals(idUser)) {
                listFriendshipRequestsUser.add(friendshipRequest);
            }
        });
        return listFriendshipRequestsUser;
    }

    /**
     * Method that gets one specific Friendship Request
     * @param idFriendshipRequest Long, representing the ID of the Friendship Request to be selected
     * @return non-null FriendshipRequest, representing
     *      the selected FriendshipRequest (if the ID of the Friendship Request exists)
     *         null, otherwise
     */
    public FriendshipRequest getFriendshipRequest(Long idFriendshipRequest) {
        return friendshipRequestRepository.findOne(idFriendshipRequest);
    }

    /**
     * Method that gets a pending Friendship Request of an User
     * @param idUser Long, representing the ID of the User
     * @param idFriendshipRequest Long, representing the ID of the Friendship Request
     * @return non-null FriendshipRequest,
     *       if the Friendship Request exists (the User has that pending Friendship Request)
     *         null, otherwise
     */
    public FriendshipRequest getPendingFriendshipRequest(Long idUser, Long idFriendshipRequest) {
        FriendshipRequest friendshipRequest = friendshipRequestRepository.findOne(idFriendshipRequest);
        if (friendshipRequest == null) return null;
        if (friendshipRequest.getTo().get(0).getId().equals(idUser) &&
                friendshipRequest.getStatusRequest().equals("pending")) return friendshipRequest;
        return null;
    }

    /**
     * Method that gets all the existing Friendship Requests
     * @return Iterable<FriendshipRequest>, representing all the existing Friendship Requests
     */
    public Iterable<FriendshipRequest> getAll() {
        return friendshipRequestRepository.findAll();
    }

    @Override
    public void addObserver(Observer<FriendshipRequestChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<FriendshipRequestChangeEvent> e) {

    }

    @Override
    public void notifyAll(FriendshipRequestChangeEvent friendshipRequestChangeEvent) {
        observers.forEach(observer -> observer.update(friendshipRequestChangeEvent));
    }
}
