package socialnetwork.service;

import socialnetwork.domain.Friendship;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.messages.FriendshipRequest;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.Repository;
import socialnetwork.service.validators.ValidatorFriendshipRequest;

import java.util.ArrayList;
import java.util.List;

public class FriendshipRequestService {
    private Repository<Long, FriendshipRequest> friendshipRequestRepository;
    private Repository<Tuple<Long, Long>, Friendship> friendshipRepository;
    private ValidatorFriendshipRequest validatorFriendshipRequestService = new ValidatorFriendshipRequest();

    /**
     * Constructor that creates a new FriendshipRequestService
     * @param friendshipRequestRepository Repository<Long, FriendshipRequest>, representing the Repository
     *                                    that handles the FriendshipRequest data
     * @param friendshipRepository Repository<Tuple<Long, Long>, Friendship>, representing the Repository
     *                             that handles the Friendship data
     */
    public FriendshipRequestService(Repository<Long, FriendshipRequest> friendshipRequestRepository,
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
        validatorFriendshipRequestService.validateBeforeAdding(friendshipRequestParam,
                getAll(), friendshipRepository.findAll());
        FriendshipRequest friendshipRequest = friendshipRequestRepository.save(friendshipRequestParam);
        return friendshipRequestParam;
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
        validatorFriendshipRequestService.validateDelete(friendshipRequest);
        return friendshipRequest;
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
     *      if the Friendship Request exists (the User has that pending Friendship Request)
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
}
