package DataAccess;

import chat.MessageList;
import chat.UserList;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;

public class XMLManager {
    /// Method that stores users on an XML
    /// @param users that want to be stored
    /// @throws JAXBException when cannot write the users on the users.xml
    public void writeUsers(UserList users) throws JAXBException {
        try{
            JAXBContext context = JAXBContext.newInstance(UserList.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.marshal(users, new File("users.xml"));
        }catch (JAXBException e){
            System.out.println("Error " + e.getMessage());
        }
    }

    /// Method that reads the users of an XML and get them on a Java List
    /// @return the list of users
    /// @throws JAXBException when cannot read the users on the users.xml
    public UserList readUsers() throws JAXBException {
        UserList users = new UserList();
        File file = new File("users.xml");
        if ( file.exists() && !file.isDirectory() ){
            try {
                JAXBContext context = JAXBContext.newInstance(UserList.class);
                Unmarshaller reader = context.createUnmarshaller();
                users = (UserList) reader.unmarshal(file);
            } catch (JAXBException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        return users;
    }

    /// Method that writes the messages on an XML
    /// @param messages that want to be stored
    /// @param path of the destination to save the XML
    /// @throws JAXBException when it can't copy the data from the XML to Java
    public void writeMessages(MessageList messages, String path) throws JAXBException {
        try{
            JAXBContext context = JAXBContext.newInstance(MessageList.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.marshal(messages, new File(path));
        }catch (JAXBException e){
            System.out.println("Error " + e.getMessage());
        }
    }

    /// Method that read from an XML a list of messages
    /// @return a list with all the messages
    /// @throws JAXBException when there is an error reading the XML
    public MessageList readMessages() throws JAXBException {
        MessageList messages =  new MessageList();
        File file = new File("messages.xml");
        if ( file.exists() && !file.isDirectory() ){
            try {
                JAXBContext context = JAXBContext.newInstance(MessageList.class);
                Unmarshaller reader = context.createUnmarshaller();
                messages = (MessageList) reader.unmarshal(file);
            } catch (JAXBException e) {
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return messages;
    }
}