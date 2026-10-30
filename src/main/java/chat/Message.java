package chat;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "message")
public class Message implements Serializable {
    private String sender;
    private String receiver;
    private String content;
    private String date; // ISO-8601, por ejemplo: 2026-10-26T13:45:00
    private String attachedPath; // Puede ser null si no hay adjunto.
    private String attachedName;
    private long attachedSize = 0;
    private String attachedType;

    public Message() { } // JAXB necesita un constructor sin argumentos.

    public Message(String sender, String receiver, String content, String date) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.date = LocalDateTime.now().toString();
    }

    public String getSender() { return sender; }

    public void setSender(String sender) { this.sender = sender; }

    public String getReceiver() { return receiver; }

    public void setReceiver(String receiver) { this.receiver = receiver; }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }

    public String getAttachedPath() { return attachedPath; }

    public void setAttachedPath(String attachedPath) { this.attachedPath = attachedPath; }

    public String getAttachedName() { return attachedName; }

    public void setAttachedName(String attachedName) { this.attachedName = attachedName; }

    public long getAttachedSize() { return attachedSize; }

    public void setAttachedSize(long attachedSize) { this.attachedSize = attachedSize; }

    public String getAttachedType() { return attachedType; }

    public void setAttachedType(String attachedType) { this.attachedType = attachedType; }

    @Override
    public String toString() {
        return "<"+this.date+"> "+this.sender +": " +this.content;
    }
}