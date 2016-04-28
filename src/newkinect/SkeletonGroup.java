package newkinect;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SkeletonGroup {

    private List<UserSkeletonTimestamp> userList;
    private LocalDateTime timestamp;

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
     * 
     */
    private void checkRep() {
        Set<String> userIDSet = new HashSet<String>();
        List<String> userIDList = new ArrayList<>();
        for (UserSkeletonTimestamp user : this.userList) {
            userIDSet.add(user.getUserID());
            userIDList.add(user.getUserID());
            if (!(user.getDateTime().isEqual(this.timestamp))) {
                System.out.println(this.getTimeOfGroup() + " compared to new: " + user.getDateTime());
            }
            assert (user.getDateTime().isEqual(this.timestamp));
        }
        assert (userIDSet.size() == userIDList.size());
    }

    /**
     * 
     * @return
     */
    public LocalDateTime getTimeOfGroup() {
        return timestamp;
    }

    /**
     * 
     * @return
     */
    public List<UserSkeletonTimestamp> getUsers() {
        List<UserSkeletonTimestamp> users = new ArrayList<>();
        users.addAll(this.userList);
        checkRep();
        return users;
    }
    
    public UserSkeletonTimestamp getSingleUser(int id) {
        return this.getUsers().get(id);
    }
    
    /**
     * Finds the number of people in the group
     * @return an int representing the number of people in the group
     */
    public int getSize() {
        return this.userList.size();
    }

    /**
     * 
     * @param newUser
     * @return
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
