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
import java.util.logging.Logger;

public class ReadCSV {
	private static final String CLASS_NAME = ReadCSV.class.getCanonicalName();
    //private static final String PKG_NAME = ReadCSV.class.getPackage().getName();
    private static Logger logger = Logger.getLogger(CLASS_NAME);
	
	
	private Map<String, LinkedList<String>> parsedFile;
	
	public ReadCSV(String filename)throws FileNotFoundException 
	{	
		FileReader fr = new FileReader("zkouska.csv");
		try (CSVParser csvFileParser = new CSVParser(fr, CSVFormat.DEFAULT.withHeader())){
			List<CSVRecord> rec = csvFileParser.getRecords();		
			Map<String,Integer> csvRecords = csvFileParser.getHeaderMap();
			this.parsedFile = new HashMap<String, LinkedList<String>>();
				for (String feature : csvRecords.keySet()){	
					this.parsedFile.put(feature, new  LinkedList<String>());		  
					for (CSVRecord r: rec) {			    	
						this.parsedFile.get(feature).add(r.get(feature));			    	 
			
					
					}
		    
		  }
	}catch (IOException e){
		logger.severe(e.getMessage());
		 
	}
				  
	  
	}	
	
	public Map<String, LinkedList<String>> getParsedFile(){
		return this.parsedFile;
	}
	
	
}
