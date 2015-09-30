package pachetu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Clasa {

    public static void main(String[] args) {
    	iteratorul();
    }

    private static void iteratorul() {
    	List<Integer> list = new ArrayList<>();
    	list.addAll(Arrays.asList(1,2,3));

    	Iterator<Integer> it = list.iterator();
    	int k = Integer.MIN_VALUE; // k=0 also works because there are 2 list operation/cycle
    	
    	while(it.hasNext()) {
    		System.out.println(it.next());

    		while(k<Integer.MAX_VALUE) {
    			list.remove(list.size() - 1);
    			list.add(3);
//    			System.out.println("k=" + k);
    			k++;
    		}

    		System.out.println("k1=" + k);
    		System.out.println("DONE");

    		list.remove(0);
    		list.add(4);

    		k++;
    		System.out.println("k2=" + k);
    	}
    }
    
	private static void testDisplayFriendlyDuration() {
		long millis = 23456789;
                             
        long x;
        x = millis / 1000;
        System.out.println("x=" + x);
        long seconds = x % 60;
        System.out.println("seconds=" + seconds);
        x /= 60;
        System.out.println("x=" + x);
        long minutes = x % 60;
        System.out.println("minutes=" + minutes);
        x /= 60;
        System.out.println("x=" + x);
        long hours = x % 24;
        System.out.println("hours=" + hours);
        x /= 24;
        System.out.println("x=" + x);
        long days = x;
        System.out.println("days=" + days);
        
        String result = days + "d:" + hours + "h:" + minutes + "m:" + seconds + "s";
        System.out.println(result);
	}

}
