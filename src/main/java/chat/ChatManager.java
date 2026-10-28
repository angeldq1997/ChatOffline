package chat;

import jakarta.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ChatManager {
    private final XMLManager xmlManager = new XMLManager();

    // Fase 1: comprobar duplicados y registrar el usuario en XML.
    public void registerUser(String name) throws JAXBException {
        User user = new User(name);
        UserList users = new UserList();
        if (!Objects.equals(xmlManager.readUsers(), new UserList()) && xmlManager.readUsers().findUser(user) != null ){
            System.out.println("The user already exists");
        } else {
            users = xmlManager.readUsers();
            if ( users.addUser(user) ){
                xmlManager.writeUsers(users);
                System.out.println("User registered on system successfully");
            } else {
                System.out.println("There was an error adding the user");
            }
        }
    }

    // Fase 2: validar usuarios, crear mensaje con fecha y persistirlo.
    public void sendMessage(String sender, String receiver, String content) throws JAXBException {
        User userSender = new User(sender);
        User userReceiver = new User(receiver);
        UserList userList = xmlManager.readUsers();
        if ( userList.findUser(userSender) == null ){
            System.out.println("The user sender doesn't exists");
        } else if ( userList.findUser(userReceiver) == null ){
            System.out.println("The user receiver doesn't exists");
        } else {
            Message message = new Message(sender, receiver, content, LocalTime.now().toString());
            MessageList messageList = xmlManager.readMessages();
            if ( messageList.addMessage(message) ){
                xmlManager.writeMessages(messageList, "messages.xml");
                System.out.println("The message was sent successfully");
            } else {
                System.out.println("There was an error sending the message");
            }
        }
    }

    // Fase 3: filtrar mensajes en ambos sentidos (A -> B y B -> A).
    public List<Message> checkConversation(String userA, String userB) throws JAXBException {
        MessageList messageList = xmlManager.readMessages();
        return collectMessages(messageList, userA, userB);
    }

    // TODO Fase 4: utilizar Stream API para contar mensajes por sender.
    public Map<String, Long> statistics(String userA, String userB) throws JAXBException {

    }

    // Fase 4: exportar la conversación a un archivo de texto.
    public void exportConversation(String userA, String userB, Path destination) throws JAXBException, IOException {
        List<Message> conversation = checkConversation(userA, userB);
        if ( !Objects.equals( conversation, new ArrayList<>() ) ){
            MessageList messageList = new MessageList();
            messageList.setMessages(conversation);
            xmlManager.writeMessages(messageList, destination.toString());
        }
    }

    // TODO Fase 5: validar extensión/tamaño, copiar con InputStream/OutputStream
    // a media/ y guardar los metadatos del adjunto en el mensaje XML.
    public void attachFile(Message message, Path sourceFile) throws IOException, JAXBException {
        throw new UnsupportedOperationException("Pendiente: adjuntarArchivo");
    }


    /* Complementary methods */
    public List<Message> collectMessages(MessageList messages , String sender, String receiver){
        List<Message> conversation = new ArrayList<>();
        for ( Message message : messages.getMessages() ){
            if ( message.getSender().equals(sender) && message.getReceiver().equals(receiver) ){
                conversation.add(message);
            }
        }
        return conversation;
    }

    public User findUser(String name) throws JAXBException {
        User user = new User(name);
        return xmlManager.readUsers().findUser(user);
    }
}