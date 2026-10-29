package chat;

import jakarta.xml.bind.JAXBException;

import java.io.*;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.*;

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
        List<Message> messagesToCount = checkConversation(userA, userB);
        Map<String, Long> map = new HashMap<>();
        return map;
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
    public void attachFile(Message message, Path sourceFile) throws IOException, JAXBException, InputMismatchException {
        File file = new File(sourceFile.toUri());
        //We check if the file exists and if it isn't a directory
        if (file.exists() && !file.isDirectory() ){
            //Then we check the file extension
            byte[] bytesRead = readBytes(file, 8);
            String hexBytes = transformByteHexadecimal(bytesRead);
            String imageType = verifyImageType(hexBytes);
            //Now if everything is right we set the attachment data
            if (!imageType.isEmpty()){
                message.setAttachedType(imageType);
                throw new InputMismatchException("ERROR: INVALID FILE");
            }
            //Here we see if the file isn't bigger than 4MB
            if (file.length() > 0 && file.length() < 4000000L){
                message.setAttachedSize(file.length());
                throw new InputMismatchException("ERROR: FILE TOO BIG");
            }
            message.setAttachedPath(String.valueOf(sourceFile));
            message.setAttachedName(String.valueOf(sourceFile.getFileName()));
            File imageToStore = new File("/media");
            FileInputStream fileInputStream = new FileInputStream(file);
            FileOutputStream fileOutputStream = new FileOutputStream(imageToStore);
            fileOutputStream.write(fileInputStream.readAllBytes());
            xmlManager.writeMessages(xmlManager.readMessages(), "/");
        }
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

    public Message getLastMessage(String sender, String receiver) throws JAXBException {
        return collectMessages(xmlManager.readMessages(), sender, receiver).getLast();
    }

    public User findUser(String name) throws JAXBException {
        User user = new User(name);
        return xmlManager.readUsers().findUser(user);
    }

    public static byte[] readBytes(File f, int numberOfBytes) {
        byte[] buffer = new byte[numberOfBytes];
        try (FileInputStream fileInputStream = new FileInputStream(f)) {
            int numBytesRead = fileInputStream.read(buffer);
            if (numBytesRead < numberOfBytes) {
                byte[] bytesReadArray = new byte[numBytesRead];
                System.arraycopy(buffer, 0, bytesReadArray, 0, numBytesRead);
                return bytesReadArray;
            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
        return buffer;
    }

    public static String transformByteHexadecimal(byte[] bytes) {
        StringBuilder hexArray = new StringBuilder();
        for (byte aByte : bytes) {
            hexArray.append(String.format("%02X", aByte));
        }
        return hexArray.toString();
    }

    public static String verifyImageType(String hex) {
        String imageType="";
        if (hex.startsWith("FFD8FF")) {
            imageType="JPEG";
        } else if (hex.startsWith("89504E47")) {
            imageType="PNG";
        } else if (hex.startsWith("47494638")) {
            imageType="GIF";
        } else if (hex.startsWith("424D")) {
            imageType="BMP";
        } else if (hex.startsWith("00000100")) {
            imageType="ICO";
        }else {
            imageType="Not recognised format";
        }
        return imageType;
    }
}