import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;


public class Peer extends Thread {
    
    HashMap <String, String> files;
    ServerSocket machine;
    InetAddress address;
    int port;

    static int i = 0;

    Peer(InetAddress address, int port){
        this.address = address;
        this.port = port;
        this.files = new HashMap <String, String>();
    }


    public void run(){

        try{
            this.machine = new ServerSocket(port);
            System.out.println("Listening..... at" + this.port + " on " + this.address + ".");

        }
        catch(Exception e){
            System.out.println("Error while starting server.....");
            e.printStackTrace();
        }
    }


    void receiveFile(String file){

        try{
            // File receivedFile = new File("newFile " + i + ".txt");

            ++i;
            String path = "newFile" + i + ".txt";
            FileOutputStream receivedFile = new FileOutputStream(path);
            
            receivedFile.write(file.getBytes());
            receivedFile.flush();
            this.files.put(path, path);
            receivedFile.close();
        }
        catch(Exception e){
            System.out.println("Error while writing file onto the machine.");
            e.printStackTrace();
        }

    }


    void sendFile(InetAddress address, int port, String filename){

        try{
            Socket socket = new Socket(address, port);
            String filePath = this.files.get(filename);
            DataOutputStream outToPeer = new DataOutputStream(socket.getOutputStream());

            File fileToSend = new File(filePath);
            BufferedReader br = new BufferedReader(new FileReader(fileToSend));

            String fileData = "";
            String s;

            while ((s = br.readLine()) != null) {
                fileData += s;
            }
            
            outToPeer.writeBytes(fileData);
            outToPeer.flush();

            System.out.println("File sent");
            br.close();
            outToPeer.close();
            socket.close();
        }
        catch(Exception e){
            System.out.println("Error while handling file");
            e.printStackTrace();
        }

    }


    void sendMessage(InetAddress address, int port, String message){

        try{
            Socket outSocket = new Socket(address, port);
            DataOutputStream dos = new DataOutputStream(outSocket.getOutputStream());

            dos.writeBytes(message);
            dos.flush();
            outSocket.close();
        }
        catch(Exception e){
            System.out.println("Error while sending message");
            e.printStackTrace();
        }

    }


    String listenRequest(){

        String request = "";

        try{
            Socket socket = machine.accept();
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            request = br.readLine();
            System.out.println("Request from peer " + socket.getInetAddress() + " is : " + request);
            socket.close();
        }
        catch(Exception e){
            System.out.println("Error while listening to Request");
            e.printStackTrace();
        }

        return request;
    }


    void closeConnection() throws Exception{
        machine.close();
    }

}
