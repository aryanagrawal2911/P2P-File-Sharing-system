import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.net.InetAddress;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Main {
    
    private static JFrame frame = new JFrame();

    private static JLabel label;

    private static JTextField filename;

    private static JButton button;

    private static JTextArea textArea;


    public Main(){

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));
        panel.setLayout(null);

        label = new JLabel("File to request : ");
        label.setBounds(60, 60, 150, 25);
        panel.add(label);

        filename = new JTextField(20);
        filename.setBounds(200, 60, 165, 25);
        panel.add(filename);
        button = new JButton("Send");
        button.setBounds(120, 100, 100, 25);

        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e){

                try{
                    redirectSystemStreams(); // Redirect System.out and System.err
                    String fileNameToRequest = filename.getText();

                    InetAddress p1Add = InetAddress.getLocalHost();
                    int p1port = 4000;

                    InetAddress p2Add = InetAddress.getLocalHost();
                    int p2port = 5000;

                    Peer p1 = new Peer(p1Add, p1port);
                    Peer p2 = new Peer(p2Add, p2port);

                    p1.start();
                    p2.start();
                    p1.files.put("file1.txt", "file1.txt");
                    p2.files.put("file2.txt", "file2.txt");

                    p1.sendMessage(p2Add, p2port, fileNameToRequest);
                    String messageReceivedfromPeer = p2.listenRequest();

                    p2.sendFile(p1.address, p1.port, messageReceivedfromPeer);
                    String fileReceivedFromPeer = p1.listenRequest();

                    p1.receiveFile(fileReceivedFromPeer);
                    p1.closeConnection();
                    p2.closeConnection();

                }
                catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        });

        panel.add(button);
        textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);

        scrollPane.setBounds(60, 140, 400, 200);
        panel.add(scrollPane);
        frame.setSize(400, 400);
        frame.add(panel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("P2P File Sharing System");
        frame.pack();
        frame.setVisible(true);
    }


    private void redirectSystemStreams(){

        PrintStream originalOut = System.out;
        PrintStream printStream = new PrintStream(new CustomOutputStream(textArea, originalOut));
        System.setOut(printStream);

        PrintStream originalErr = System.err;
        PrintStream errorstream = new PrintStream(new CustomOutputStream(textArea, originalErr));
        System.setErr(errorstream);
    }


    public static void main(String[] args) {
        
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {new Main();}
        });
    }
}