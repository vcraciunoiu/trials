package observatii;

import static java.lang.System.out;

import java.util.Observable;
import java.util.Observer;

public class MyApp {

    public static void main(String[] args) {
        out.println("Enter Text >");
        EventSource eventSource = new EventSource();
 
        eventSource.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                out.println("\nSunt observatorul 1111, am primit: " + arg);
            }
        });
 
        eventSource.addObserver((Observable obj, Object arg) -> { 
            out.println("\nSunt observatorul 2222, am primit: " + arg);
        });
 
        new Thread(eventSource).start();
    }

}
