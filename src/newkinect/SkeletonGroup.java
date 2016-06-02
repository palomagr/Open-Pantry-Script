package newkinect;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A type representing a group of users detected by the Kinect in the same round of detection. Each user is represented by an instance of
 * UserSkeletonTimestamp and though each instance might actually have a time stamp that differs by a couple of milliseconds, the SkeletonGroup
 * gives each UserSkeletonTimestamp the time stamp of the first user detected in the group.
 *
 */

public class SkeletonGroup {

    private List<UserSkeletonTimestamp> userList;
    private LocalDateTime timestamp;

    // Abstraction Function
    //      Takes a list of UserSkeletonTimestamps and represents a group of users detected at once by the Kinect. Each user is represented
    //      by a skeleton in one of the UserSkeletonTimestamps. They are all assigned the time stamp of the first user in the group.
    //
    // Representation Invariant
    //      Every UserSkeletonTimestamp shares the same time stamp: the time stamp of the first user's UerSkeltonTimestamp
    //      Each user in the group is unique
   
    
    public SkeletonGroup(List<UserSkeletonTimestamp> userList) {
        this.userList = userList;
        if (userList.isEmpty()) {
            this.timestamp = LocalDateTime.MIN;
        } else {
            this.timestamp = userList.get(0).getDateTime();
        }
        checkRep();

    }

    /**
     * Make sure representation is valid
     */
    private void checkRep() {
        Set<String> userIDSet = new HashSet<String>();
        List<String> userIDList = new ArrayList<>();
        for (UserSkeletonTimestamp user : this.userList) {
            userIDSet.add(user.getUserID());
            userIDList.add(user.getUserID());
            assert (user.getDateTime().isEqual(this.timestamp));
        }
        assert (userIDSet.size() == userIDList.size());
    }

    /**
     * Get the time stamp of the group
     * @return the time stamp of the group's detection
     */
    public LocalDateTime getTimeOfGroup() {
        return timestamp;
    }

    /**
     * Get the users in the group, given by UserSkeletonTimestamps
     * @return the UserSkeletonTimestamps representing users in the group
     */
    public List<UserSkeletonTimestamp> getUsers() {
        List<UserSkeletonTimestamp> users = new ArrayList<>();
        users.addAll(this.userList);
        checkRep();
        return users;
    }
    
    /**
     * Get one of the UserSkeletonTimestamps representing a single user in the group
     * @param id the user ID of the user in the group
     * @return the UserSkeletonTimestamp of the single user in the group determined by their user ID
     */
    public UserSkeletonTimestamp getSingleUser(int id) {
        return this.getUsers().get(id);
    }
    
    /**
     * Find the number of people in the group
     * @return an int representing the number of people in the group
     */
    public int getSize() {
        return this.userList.size();
    }

    /**
     * Add a user to the group and make updates to the group time stamp or to the time stamps of individuals in the group if necessary
     * @param newUser the new user's userSkeletonTimestamp
     * @return a new SkeletonGroup with the new user added
     */
    public SkeletonGroup addUser(UserSkeletonTimestamp newUser) {
        List<UserSkeletonTimestamp> updatedUserList = new ArrayList<>();

        if (this.userList.isEmpty()) {
            updatedUserList.add(newUser);
            return new SkeletonGroup(updatedUserList);
        } else {
            LocalDateTime timeOfGroup = this.getTimeOfGroup();
            UserSkeletonTimestamp updatedTimeNewUser = newUser.updateTime(timeOfGroup);
            updatedUserList = this.getUsers();
            updatedUserList.add(updatedTimeNewUser);
            SkeletonGroup augmentedGroup = new SkeletonGroup(updatedUserList);
            augmentedGroup.checkRep();
            return augmentedGroup;
        }
    }

    @Override
    public String toString() {
        StringBuilder returnValue = new StringBuilder();
        returnValue.append("[");
        for (UserSkeletonTimestamp user : this.userList) {
            returnValue.append(" | " + user + " | ");
        }
        returnValue.append("]");
        return returnValue.toString();
    }
}
