package pachetu;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class Clasa {

    public static void main(String[] args) throws Exception {
    	double gigi = 1234.56;
    	
//    	String numberFormatPattern = "#,##0.###";
    	
    	NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
    	String formattedNumber = numberFormat.format(gigi);
    	
    	System.out.println(formattedNumber);
    }
    
    private static void serializationShit() throws Exception{
    	 //This is the object we're going to serialize.
        String name = "bob";
 
        //We'll write the serialized data to a file "name.ser"
        FileOutputStream fos = new FileOutputStream("name.ser");
        ObjectOutputStream os = new ObjectOutputStream(fos);
        os.writeObject(name);
        os.close();
 
        //Read the serialized data back in from the file "name.ser"
        FileInputStream fis = new FileInputStream("name.ser");
        ObjectInputStream ois = new ObjectInputStream(fis);
 
        //Read the object from the data stream, and convert it back to a String
        String nameFromDisk = (String)ois.readObject();
 
        //Print the result.
        System.out.println(nameFromDisk);
        ois.close();
    }
    
    private static void restExample() {
    	RestTemplate restTemplate = new RestTemplate();
    	final String uri = "https://jgod-uas-qs-lb.v976.gmx.net/login";
    	
        HttpHeaders headers1 = new HttpHeaders();
//        headers1.setAcceptCharset(Arrays.asList(Charset.defaultCharset()));
		
		MultiValueMap<String, String> payload = new LinkedMultiValueMap<String, String>();
		payload.add("username", "test.qa");
		payload.add("password", "test123#");
		payload.add("serviceID", "cashback.webde.login");
		payload.add("partnerdata", "goto=gigi");
		
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(payload, headers1);
        
        URI postForLocation = restTemplate.postForLocation(uri, request);
        System.out.println(postForLocation);
        
        String queryString = postForLocation.getQuery();
        System.out.println(queryString); 
        
        String[] params = queryString.split("&");
        String uassesionid = params[0].substring(params[0].indexOf("=")+1);
        System.out.println(uassesionid);
        
        String goTo = params[1].substring(params[1].indexOf("=")+1);
        System.out.println(goTo);
    }
    
	private static String reverseRecursively(String str) {
		if ((null == str) || (str.length() <= 1)) {
			return str;
		}
		return reverseRecursively(str.substring(1)) + str.charAt(0);
	}

	public static void esteAnagrama(String s1, String s2) {
    	if (s1.length() != s2.length()) {
    		System.out.println("nu e");
    		return;
    	}

    	Map<Character, Integer> charFrequency1 = initializeFrequencyMap();
    	Map<Character, Integer> charFrequency2 = initializeFrequencyMap();
    	
    	char[] s1array = s1.toCharArray();
    	for (char c : s1array) {
    		Integer howManytimes = charFrequency1.get(c);
    		howManytimes = howManytimes + 1;
   			charFrequency1.put(c, howManytimes);
		}

    	char[] s2array = s2.toCharArray();
    	for (char c : s2array) {
    		Integer howManytimes = charFrequency2.get(c);
    		howManytimes = howManytimes + 1;
   			charFrequency2.put(c, howManytimes);
		}

    	for (Entry<Character, Integer> entryCharFrequency1 : charFrequency1.entrySet()) {
			Integer value1 = entryCharFrequency1.getValue();
			Integer value2 = charFrequency2.get(entryCharFrequency1.getKey());
			
			if (value1 != value2) {
				System.out.println("Nu avem egalitate pt characterul " + entryCharFrequency1.getKey() 
					+ ": " + value1 + " diferit de " +  value2);
				break;
			}
		}
    	
    	System.out.println("este");
    }
    
    public static Map<Character, Integer> initializeFrequencyMap() {
    	Map<Character, Integer> charFrequency = new HashMap<Character, Integer>();

    	charFrequency.put('a', 0);
    	charFrequency.put('b', 0);
    	charFrequency.put('c', 0);
    	charFrequency.put('d', 0);
    	charFrequency.put('e', 0);
    	charFrequency.put('f', 0);
    	charFrequency.put('g', 0);
    	charFrequency.put('h', 0);
    	charFrequency.put('i', 0);
    	charFrequency.put('j', 0);
    	charFrequency.put('k', 0);
    	charFrequency.put('l', 0);
    	charFrequency.put('m', 0);
    	charFrequency.put('n', 0);
    	charFrequency.put('o', 0);
    	charFrequency.put('p', 0);
    	charFrequency.put('q', 0);
    	charFrequency.put('r', 0);
    	charFrequency.put('s', 0);
    	charFrequency.put('t', 0);
    	charFrequency.put('u', 0);
    	charFrequency.put('v', 0);
    	charFrequency.put('x', 0);
    	charFrequency.put('y', 0);
    	charFrequency.put('w', 0);
    	charFrequency.put('z', 0);
    	
    	return charFrequency;
    }
    
    public static String getValueFromStringOfHashMap(String string, String key) {
        int start_index = string.indexOf(key) + key.length() + 1;
        int end_index = string.indexOf(",", start_index);
        if (end_index == -1) { // because last key value pair doesn't have trailing comma (,)
            end_index = string.indexOf("}");
        }
        String value = string.substring(start_index, end_index);

        return value;
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
