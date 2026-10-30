package chat;

import DataAccess.XMLManager;
import jakarta.xml.bind.JAXBException;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class ChatManager {
    private final XMLManager xmlManager = new XMLManager();

    /// Method that registers a user on an XML
    /// @param name of the user to be stored
    /// @throws JAXBException when there is an error storing the user
    public void registerUser(String name) throws JAXBException {
        User user = new User(name);
        UserList users = new UserList();
        if (!Objects.equals(xmlManager.readUsers(), new UserList()) && xmlManager.readUsers().findUser(user) != null) {
            System.out.println("The user already exists");
        } else {
            users = xmlManager.readUsers();
            if (users.addUser(user)) {
                xmlManager.writeUsers(users);
                System.out.println("User registered on system successfully");
            } else {
                System.out.println("There was an error adding the user");
            }
        }
    }

    /// Method that receives a sender, a receiver, a content for a message and "sends it" with the current time
    /// @param sender the user that wants to send the message
    /// @param receiver the user that will receive the message
    /// @param content the content of the message itself
    /// @throws JAXBException when there is an error reading an XML
    public void sendMessage(String sender, String receiver, String content) throws JAXBException {
        User userSender = new User(sender);
        User userReceiver = new User(receiver);
        UserList userList = xmlManager.readUsers();
        if (userList.findUser(userSender) == null) {
            System.out.println("The user sender doesn't exists");
        } else if (userList.findUser(userReceiver) == null) {
            System.out.println("The user receiver doesn't exists");
        } else {
            Message message = new Message(sender, receiver, content, "TEST");
            MessageList messageList = xmlManager.readMessages();
            if (messageList.addMessage(message)) {
                xmlManager.writeMessages(messageList, "messages.xml");
                System.out.println("The message was sent successfully");
            } else {
                System.out.println("There was an error sending the message");
            }
        }
    }

    /// Method that takes messages from userA to userB and viceversa
    /// @param userA one user of the conversation
    /// @param userB the other user of the conversation
    /// @return the list of messages ordered by date
    /// @throws JAXBException when there is an error reading an XML
    public List<Message> checkConversation(String userA, String userB) throws JAXBException {
        MessageList messageList = xmlManager.readMessages();
        List<Message> messages = new ArrayList<Message>();
        Collection<Message> collection = new ArrayList<>();
        collection.addAll(collectMessages(messageList, userA, userB));
        collection.addAll(collectMessages(messageList, userB, userA));
        return collection.stream().sorted(Comparator.comparing(Message::getDate)).toList();
    }

    /// Method that counts the messages between user A and user B
    /// @param userA first user (normally the sender)
    /// @param userB second user (normally the receiver)
    /// @return the number of messages
    /// @throws JAXBException when the XML couldn't be read
    public long statistics(String userA, String userB) throws JAXBException {
        Collection<Message> collection = collectMessages(xmlManager.readMessages(), userA, userB);
        return collection.size();
    }

    /// Method that saves on an XML the conversation between two users
    /// @param userA sender of messages
    /// @param userB receiver of messages
    /// @param destination path of destination for the XML with the conversation
    /// @throws JAXBException when the XML can't be read
    /// @throws IOException when writing on the destination isn't possible
    public void exportConversation(String userA, String userB, Path destination) throws JAXBException, IOException {
        List<Message> conversation = checkConversation(userA, userB);
        if (!Objects.equals(conversation, new ArrayList<>())) {
            MessageList messageList = new MessageList();
            messageList.setMessages(conversation);
            xmlManager.writeMessages(messageList, destination.toString());
        }
    }

    /// Method that attach an image to a message
    /// @param message Message to attach the image
    /// @param sourceFile the path of the image to attach
    /// @throws IOException when the file isn't found
    /// @throws JAXBException when the operation of reading or writing an XML gives an error
    /// @throws InputMismatchException when the file is too big or isn't in the right format
    public void attachFile(Message message, Path sourceFile) throws IOException, JAXBException, InputMismatchException {
        File file = new File(sourceFile.toUri());
        //We check if the file exists and if it isn't a directory
        if (file.exists() && !file.isDirectory()) {
            //Then we check the file extension
            byte[] bytesRead = readBytes(file, 8);
            String hexBytes = transformByteHexadecimal(bytesRead);
            String imageType = verifyImageType(hexBytes);
            //Now if everything is right we set the attachment data
            if (!imageType.isEmpty() && !imageType.equals("Not recognised format")) {
                message.setAttachedType(imageType);
            } else {
                throw new InputMismatchException("ERROR: INVALID FILE");
            }
            //Here we see if the file isn't bigger than 4MB
            if (file.length() > 0 && file.length() < 4000000L) {
                message.setAttachedSize(file.length());
            } else{
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

    /// Method that gathers the messages from a sender to a receiver
    /// @param messages list of messages
    /// @param sender user that sends the messages
    /// @param receiver user that receives the messages
    /// @return the complete list of messages
    public List<Message> collectMessages(MessageList messages, String sender, String receiver) {
        List<Message> conversation = new ArrayList<>();
        for (Message message : messages.getMessages()) {
            if (message.getSender().equals(sender) && message.getReceiver().equals(receiver)) {
                conversation.add(message);
            }
        }
        return conversation;
    }

    /// Method that get the last message of a certain sender and receiver
    /// @param sender the user that sent the last message
    /// @param receiver the user that received the last message
    /// @return the last message from the sender to the receiver
    /// @throws JAXBException when the XML of messages gives an error
    public Message getLastMessage(String sender, String receiver) throws JAXBException {
        return collectMessages(xmlManager.readMessages(), sender, receiver).getLast();
    }

    /// Method that finds a user with a name on an XML
    /// @param name of the user to find
    /// @return the User already found or null if it doesn't appear
    /// @throws JAXBException when the read of users on the XML gives an error
    public User findUser(String name) throws JAXBException {
        User user = new User(name);
        return xmlManager.readUsers().findUser(user);
    }

    /// Method that readBytes from a file
    /// @param f file to be read
    /// @param numberOfBytes number of bytes that want to be read
    /// @return the array of bytes of the file in particular
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

    /// Method that transform byte to hexadecimal
    /// @param bytes array of bytes to be transformed
    /// @return the hex string of all the bytes
    public static String transformByteHexadecimal(byte[] bytes) {
        StringBuilder hexArray = new StringBuilder();
        for (byte aByte : bytes) {
            hexArray.append(String.format("%02X", aByte));
        }
        return hexArray.toString();
    }

    /// Method that checks the extension of an image
    /// @param hex hexadecimal string to check the extension
    /// @return the format of the string
    public static String verifyImageType(String hex) {
        String imageType = "";
        if (hex.startsWith("FFD8FF")) {
            imageType = "JPEG";
        } else if (hex.startsWith("89504E47")) {
            imageType = "PNG";
        } else if (hex.startsWith("47494638")) {
            imageType = "GIF";
        } else if (hex.startsWith("424D")) {
            imageType = "BMP";
        } else if (hex.startsWith("00000100")) {
            imageType = "ICO";
        } else {
            imageType = "Not recognised format";
        }
        return imageType;
    }
}