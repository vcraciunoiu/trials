package springstuff;

import org.springframework.stereotype.Component;

@Component
public class TdnsClient {

	public void faSiTuCeva() {
		System.out.println("fac si eu ceva: arunc eroare");
//		throw new Exception("am pus-o");
		
		int i = 7;
		int j = 0;
		
		int k = i / j;
		System.out.println(k);
	}
	
}
