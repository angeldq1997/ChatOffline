package chat;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "messages")
@XmlAccessorType(XmlAccessType.FIELD)
public class MessageList {
    @XmlElement(name = "message")
    private List<Message> messages = new ArrayList<>();

    public MessageList() { }

    public List<Message> getMessages() { return messages; }
    public void setMessages(List<Message> messages) { this.messages = messages; }
    public boolean addMessage (Message message){
        return this.messages.add(message);
    }
    public boolean removeMessage (Message message){
        return this.messages.remove(message);
    }
}
