package DataAccess;

import chat.Message;
import chat.MessageList;
import chat.UserList;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;

public class XMLManager {
    // TODO Fase 1: save users.xml using JAXB (Marshaller).
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

    // TODO Fase 1: recover users.xml usando JAXB (Unmarshaller).
    // Si no existe el archivo, devolver una ListaUsuarios vacía.
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

    // TODO Fase 2: write messages.xml usando JAXB.
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

    // TODO Fase 2: recover messages.xml usando JAXB.
    // Si no existe el archivo, devolver una ListaMensajes vacía.
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