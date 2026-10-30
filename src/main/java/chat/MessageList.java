package chat;

import jakarta.xml.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "historial")
@XmlAccessorType(XmlAccessType.FIELD)
public class MessageList {
    @XmlElementWrapper(name = "messages")
    @XmlElement(name = "message", type = Message.class)
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
