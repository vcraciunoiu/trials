package basic.training.springie;

public class Taliban {

    private String victim;
    
    public Taliban(String nume) {
        this.victim = nume;
        System.out.println("OK m-am construit");
    }
    
    public void sendToAllah(String victim) {
        System.out.println("I kill you " + victim);
    }
    
    public void sendToAllah() {
        System.out.println("I kill you " + victim);
    }

}
