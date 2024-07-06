import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import javax.swing.JTextArea;

public class CustomOutputStream extends OutputStream {
    
    private JTextArea textArea;

    private PrintStream originalPrintStream;

    public CustomOutputStream(JTextArea textArea, PrintStream originalPrintStream){
        this.textArea = textArea;
        this.originalPrintStream = originalPrintStream;
    }

    @Override
    public void write(int b) throws IOException{

        //Redirect data to the text area
        textArea.append(String.valueOf((char) b));

        //Scroll the text area to the end so the latest output is visible
        textArea.setCaretPosition(textArea.getDocument().getLength());

        //Also, write to the original print stream (console)
        originalPrintStream.write(b);
    }
}
