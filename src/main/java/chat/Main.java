package chat;

import jakarta.xml.bind.JAXBException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner keyboard = new Scanner(System.in);
        ChatManager chat = new ChatManager();
        int option = 0;
        do {
            System.out.println("---MENU---\n1. Register user\n2. Send message\n3. See conversation\n4. Statistics\n5. Export conversation\n6. Add file\n0. EXIT PROGRAM");
            System.out.println("Enter an option (0-6): ");
            option = keyboard.nextInt();
            try{
                if (option < 0 || option > 6){
                    System.out.println("Enter a integer between 0 and 6, not a negative nor a number bigger than 6");
                }
            } catch (InputMismatchException e){
                System.out.println("Error, enter a number between 0 and 6");
            }

            switch (option){
                //Exit
                case 0 -> {
                    System.out.println("Closing program, thank you for your time");
                }

                //Register user
                case 1 -> {
                    try {
                        keyboard.nextLine();
                        System.out.println("Enter name for the new user: ");
                        String userName = keyboard.next();
                        chat.registerUser(userName);
                    } catch (JAXBException e) {
                        System.out.println(e.getMessage());
                    }
                }

                //Send message
                case 2 -> {
                    sendMessage(keyboard, chat);
                }

                //See conversation
                case 3 -> {
                    seeConversation(keyboard, chat);
                }

                //Statistics
                case 4 -> {
                    statistics(keyboard, chat);
                }

                //Export conversation
                case 5 -> {
                    exportConversation(keyboard, chat);
                }

                //Add file
                case 6 -> {
                    addFile(keyboard, chat);
                }
                //ERROR for trying to evade user errors
                default -> {
                    System.out.println("ERROR, please select a correct option (0-6)");
                }
            }
        }while (option != 0);
        keyboard.close();
    }

    private static void statistics(Scanner keyboard, ChatManager chat) {
        try {
            System.out.println("Enter the username of the sender: ");
            String sender = keyboard.next();
            System.out.println("Enter the username of the receiver: ");
            String receiver = keyboard.next();
            if (chat.findUser(sender) == null ){
                System.out.println("The sender doesn't exists");
            } else if ( chat.findUser(receiver) == null ) {
                System.out.println("The receiver entered doesn't exists");
            } else {
                long total = chat.statistics(sender, receiver);
                System.out.println("The total of messages is: "+total);
            }
        } catch ( JAXBException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void addFile(Scanner keyboard, ChatManager chat) {
        try {
            System.out.println("Enter your username: ");
            String sender = keyboard.next();
            System.out.println("Enter the username of the receiver: ");
            String receiver = keyboard.next();
            if (chat.findUser(sender) == null ){
                System.out.println("Your username doesn't exists");
            } else if ( chat.findUser(receiver) == null ) {
                System.out.println("The receiver entered doesn't exists");
            } else {
                keyboard.nextLine();
                System.out.println("Introduce the path to read the image to attach:");
                String pathOfImage = keyboard.nextLine();
                Message message = chat.getLastMessage(sender, receiver);
                chat.attachFile(message, Path.of(pathOfImage));
            }
        } catch (JAXBException | InputMismatchException e){
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println("The path entered isn't valid");
        }
    }

    private static void sendMessage(Scanner keyboard, ChatManager chat) {
        try{
            System.out.println("Enter a sender");
            String sender = keyboard.next();
            keyboard.nextLine();
            System.out.println("Enter a receiver");
            String receiver = keyboard.next();
            if (chat.findUser(sender) == null ){
                System.out.println("The sender entered doesn't exists");
            } else if ( chat.findUser(receiver) == null ) {
                System.out.println("The receiver entered doesn't exists");
            } else {
                keyboard.nextLine();
                System.out.println("Introduce the message to send: ");
                String content = keyboard.nextLine();
                chat.sendMessage(sender, receiver, content);
            }
        } catch (JAXBException e){
            System.out.println(e.getMessage());
        }
    }

    private static void exportConversation(Scanner keyboard, ChatManager chat) {
        try {
            System.out.println("Enter your username: ");
            String sender = keyboard.next();
            System.out.println("Enter the username of the receiver: ");
            String receiver = keyboard.next();
            if (chat.findUser(sender) == null ){
                System.out.println("Your username doesn't exists");
            } else if ( chat.findUser(receiver) == null ) {
                System.out.println("The receiver entered doesn't exists");
            } else {
                keyboard.nextLine();
                System.out.println("Introduce the path to save the conversation:");
                String path = keyboard.nextLine();
                chat.exportConversation(sender, receiver, Path.of(path));
            }
        }catch (JAXBException e){
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println("The path entered isn't valid");
        }
    }

    private static void seeConversation(Scanner keyboard, ChatManager chat) {
        try {
            System.out.println("Enter your username: ");
            String sender = keyboard.next();
            keyboard.nextLine();
            System.out.println("Enter the username of the receiver: ");
            String receiver = keyboard.next();
            if (chat.findUser(sender) == null ){
                System.out.println("Your username doesn't exists");
            } else if ( chat.findUser(receiver) == null ) {
                System.out.println("The receiver entered doesn't exists");
            } else {
                List<Message> conversation = chat.checkConversation(sender, receiver);
                conversation.forEach(System.out::println);
            }
        }catch (JAXBException e){
            System.out.println(e.getMessage());
        }
    }
}