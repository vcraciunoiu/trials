package paternuri;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ClasaMea {

    public static void main(String[] args) throws Exception {
        
        SingletonuMeu instantza1 = SingletonuMeu.getInstance();
        instantza1.setIndividu(10);
        System.out.println(instantza1);

        FileOutputStream fileOut = new FileOutputStream("/home/vlad/gigi.ser");
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(instantza1);
        out.close();
        fileOut.close();
        System.out.println("am serializat singletonul meu pe disk.");

        //////////////////////////////////////////
        
        FileInputStream fileIn = new FileInputStream("/home/vlad/gigi.ser");
        ObjectInputStream in = new ObjectInputStream(fileIn);
        SingletonuMeu instantza2 = (SingletonuMeu) in.readObject();
        in.close();
        fileIn.close();
        System.out.println("am deserializat de pe disk: " + instantza2 + ". i=" + instantza2.getIndividu());
    }

}
