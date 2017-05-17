import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.LinkedList;


public class ReadCSV {// reads table in CSV format to HashMap
	//TODO read other data types than String
	private Map<String, LinkedList<String>> parsedFile;
	
	public ReadCSV(String filename) throws FileNotFoundException, IOException
	{			
		try (FileReader fr = new FileReader(filename);
			CSVParser csvFileParser = new CSVParser(fr, CSVFormat.DEFAULT.withHeader())){
			List<CSVRecord> rec = csvFileParser.getRecords();		
			Map<String,Integer> csvRecords = csvFileParser.getHeaderMap();
			this.parsedFile = new HashMap<String, LinkedList<String>>();
				for (String feature : csvRecords.keySet()){	
					this.parsedFile.put(feature, new  LinkedList<String>());		  
					for (CSVRecord r: rec) {			    	
						this.parsedFile.get(feature).add(r.get(feature));			    	 					
					}		    
		  }
		}
	}
	
	public Map<String, LinkedList<String>> getParsedFile(){
		return this.parsedFile;
	}	
}
