package chat;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "users")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserList {
    // TODO: Programar la lista de usuarios.
    @XmlElement(name = "user")
    private List<User> userList = new ArrayList<>();

    public UserList() { }

    public List<User> getUsers() { return userList; }
    public void setUsers(List<User> users) { this.userList = users; }
    public boolean addUser (User user){
        return this.userList.add(user);
    }
    public boolean removeUser (User user){
        return this.userList.remove(user);
    }
    public User findUser (User userToFind){
        for (User user : userList){
            if (user.equals(userToFind)){
                return user;
            }
        }
        return null;
    }

}
