import static org.junit.Assert.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import java.util.Arrays;
import java.util.LinkedList;

public class ReadCSVTest {
	
	@Test
	public void test() throws IOException{
	ReadCSV prCSV = new ReadCSV("zkouska.csv");
	Map<String, LinkedList<String>> readMap =  prCSV.getParsedFile();
	
	Map<String, LinkedList<String>> testMap = new HashMap<String, LinkedList<String>>();
	testMap.put("Survived", new LinkedList<String>(Arrays.asList("1","0","0")));
	testMap.put("Sex", new LinkedList<String>(Arrays.asList("Z","M","Z")));
	testMap.put("Cabin", new LinkedList<String>(Arrays.asList("1","2","3")));
	
	for (String key : readMap.keySet()) {
		assertTrue(  readMap.get(key).equals(testMap.get(key) )) ; 
		 
	}
		 	
	 
	
	}
}
