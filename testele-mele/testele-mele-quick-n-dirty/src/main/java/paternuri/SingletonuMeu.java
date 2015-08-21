package paternuri;

import java.io.Serializable;

public class SingletonuMeu implements Serializable {

    private static final long serialVersionUID = 11234123124513L;
    
    private volatile static SingletonuMeu instance = null;
    
    private SingletonuMeu() {
    }
    
    public static synchronized SingletonuMeu getInstance() {
        if (instance == null) {
            instance = new SingletonuMeu();
        }
        return instance;
    }
   
    protected Object readResolve() {
        System.out.println("sunt in readResolve: instanceID=" + this);
        return getInstance();
    }
    
    public int getIndividu() {
        return individu;
    }

    public void setIndividu(int individu) {
        this.individu = individu;
    }

    private int individu;
    
}
