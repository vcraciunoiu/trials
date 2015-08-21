package basic.training.springie;

import java.util.ArrayList;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ClasaMea {

    public static void main(String[] args) {

//      Taliban taliban = new Taliban("roman");
//      taliban.sendToAllah();
        
        String configLocation = "application-context-1.xml";
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(configLocation);

//        Taliban talibanumeu = (Taliban) ctx.getBean("untaliban");
//        talibanumeu.sendToAllah();
        
        OrganizatieTerorista alqaida = (OrganizatieTerorista) ctx.getBean("alqaida");
        ArrayList<Taliban> fiiMei = alqaida.getFiiMei();
        for (Taliban taliban : fiiMei) {
            taliban.sendToAllah();
        }

        ctx.close();
    }

}
