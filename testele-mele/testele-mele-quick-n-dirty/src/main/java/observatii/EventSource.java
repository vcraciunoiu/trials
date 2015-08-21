package observatii;

import java.util.Observable;
import java.util.Scanner;

public class EventSource extends Observable implements Runnable {

    @Override
    public void run() {
        while (true) {
            String terminalul = new Scanner(System.in).next();
            setChanged();
            notifyObservers(terminalul);
        }
    }

}
