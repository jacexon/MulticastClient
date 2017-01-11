import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class MulticastClient {

    private Storage st;
    private MulticastSocket mrec;

    public MulticastClient(String groupIP, String keyString) throws Exception {
        st = new Storage();
        mrec = new MulticastSocket(3456);
        InetAddress group = InetAddress.getByName(groupIP);
        mrec.joinGroup(group);
        sendRequestForData(group);
        Scanner sc = new Scanner(System.in);
        int option = 0;
        byte[] encodedKey = keyString.getBytes("ASCII");
        SecretKey originalKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        Cipher cipher = Cipher.getInstance("AES");


        Runnable listener = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        byte[] messageBuffer = new byte[1000];
                        DatagramPacket messagePack = new DatagramPacket(messageBuffer, messageBuffer.length);
                        mrec.receive(messagePack);
                        String lol1 = new String(messagePack.getData(), 0, messagePack.getLength());
                        if(lol1.equals("Hello")){
                            DatagramPacket pack;
                            cipher.init(Cipher.ENCRYPT_MODE, originalKey);
                            if(!st.getStore().isEmpty()){
                                for(String s : st.getStore()){
                                    String sEncrypted = "HelloRes " + AES.encrypt(s, cipher);
                                    pack = new DatagramPacket(sEncrypted.getBytes(), sEncrypted.length(), group, 3456);
                                    mrec.send(pack);
                                }
                            }
                        }

                        else if (lol1.contains("HelloRes ")){
                            cipher.init(Cipher.DECRYPT_MODE, originalKey);
                            String splittedMessage = lol1.split("\\s+")[1];
                            String decryptedMessage = AES.decrypt(splittedMessage, cipher);
                            st.addToStore(decryptedMessage);
                            st.store = st.getStore().stream().distinct().collect(Collectors.toList());
                        }

                        else {
                            cipher.init(Cipher.DECRYPT_MODE, originalKey);
                            System.err.println("Zaszyfrowana wiadomość odebrana: " + lol1);
                            lol1 = AES.decrypt(lol1, cipher);
                            st.addToStore(lol1);
                            System.err.println("Wiadomość: " + lol1);
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        };


        new Thread(listener).start();

        do {
            switch (option) {
                case 0: {
                    System.out.println("MULTICAST CLIENT INTERFACE");
                    System.out.println("Select an option: ");
                    System.out.println("1: Sending");
                    System.out.println("2: Displaying objects");
                    System.out.println("9: Exit");
                    System.out.print("Option: ");
                    option = sc.nextInt();
                    sc.nextLine();
                    System.out.println("\n\n\n");
                    System.out.flush();
                    break;
                }

                case 1: {
                    System.out.print("Send a string: ");
                    String message = sc.nextLine();
                    cipher.init(Cipher.ENCRYPT_MODE, originalKey);
                    message = AES.encrypt(message, cipher);
                    System.out.println("Zaszyfrowana wiadomość: " + message);
                    DatagramPacket pack = new DatagramPacket(message.getBytes(), message.length(), group, 3456);
                    mrec.send(pack);
                    System.out.println("Wysłałem pakiet!");
                    System.out.println("\n\n\n");
                    System.out.flush();
                    option = 0;
                    break;
                }

                case 2: {
                    System.out.println(st.getStore());
                    option = 0;
                    break;
                }
            }
        } while (option != 9);
    }


    public void sendRequestForData(InetAddress group){
        try {
            String welcomeMessage = "Hello";
            System.out.println("Wysłalem hellooo");
            DatagramPacket pack = new DatagramPacket(welcomeMessage.getBytes(), welcomeMessage.length(), group, 3456);
            mrec.send(pack);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws Exception {
        MulticastClient mc = new MulticastClient("224.1.1.1", "CzescCzescCzescC");
    }
}

