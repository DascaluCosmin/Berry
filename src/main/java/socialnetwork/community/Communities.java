package socialnetwork.community;

import socialnetwork.domain.Friendship;
import socialnetwork.domain.Tuple;

import java.awt.image.AreaAveragingScaleFilter;
import java.lang.reflect.Array;
import java.util.*;

public class Communities {
    private ArrayList<ArrayList<Long>> adjacentList = new ArrayList<ArrayList<Long>>();
    private Iterable<Friendship> listFriendships;
    private Map<Long, Long> visited = new HashMap<>();
    private int V = 100;

    /**
     * Constructor that creates a new Communities, based on the list of friendships
     * @param listFriendships An Iterable object of type Friendship, representing the list of friendships
     *                        used to create the Communities
     */
    public Communities(Iterable<Friendship> listFriendships) {
        this.listFriendships = listFriendships;
        for (int i = 0; i < V; i++) {
            adjacentList.add(new ArrayList<Long>());
        }
        listFriendships.forEach(friendship -> {
            adjacentList.get(friendship.getId().getLeft().intValue()).add(friendship.getId().getRight());
            adjacentList.get(friendship.getId().getRight().intValue()).add(friendship.getId().getLeft());
            this.resetVisited();
        });
    }

    /**
     * Method that does a DFS Traversal on the Communities
     * @param vertex Long, representing the current visited User's id
     * @return int, representing the number of friendships the current visited User has
     */
    private int DFS(Long vertex) {
        visited.put(vertex, 1L);
        int numberOfFriendships = 1;
        for (Long child : adjacentList.get(vertex.intValue())) {
            if (visited.get(child) == 0)
                numberOfFriendships = numberOfFriendships + DFS(child);
        }
        return numberOfFriendships;
    }

    /**
     * Method that resets the visited User nodes
     */
    private void resetVisited() {
        listFriendships.forEach(friendship -> {
            visited.put(friendship.getId().getLeft(), 0L);
            visited.put(friendship.getId().getRight(), 0L);
        });
    }

    /**
     * Method that prints the number of communities
     */
    public void printNumberOfCommunities() {
        int numberOfCommunities = 0;
        for (Long vertex : visited.keySet()) {
            if (visited.get(vertex) == 0) {
                DFS(vertex);
                numberOfCommunities++;
            }
        }
        System.out.println("The number of communities is equal to " + numberOfCommunities);
    }

    /**
     * Method that prints the most sociable community - the one with the most users
     */
    public void printTheMostSociableCommunity() {
        int numberOfPeopleMax = 0;
        long representMaxCommunity = 0;
        for (Long vertex : visited.keySet()) {
            if (visited.get(vertex) == 0) {
                int numberOfPeople = DFS(vertex);
                if (numberOfPeopleMax < numberOfPeople) {
                    numberOfPeopleMax = numberOfPeople;
                    representMaxCommunity = vertex;
                }
            }
        }
        resetVisited();
        DFS(representMaxCommunity);
        System.out.println("The most sociabile community has " + numberOfPeopleMax + " members: ");
        for(Long vertex : visited.keySet()) {
            if (visited.get(vertex) == 1) {
                System.out.print(vertex + " ");
            }
        }
        System.out.println();
    }
}
