import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.LinkedHashMap;

public class PreprocessCSV {

	private LinkedHashMap<String, String> parsedFile;
	
	public PreprocessCSV(FileReader fr)throws IOException
	{		
		CSVParser csvFileParser = new CSVParser(fr, CSVFormat.DEFAULT.withHeader());
		List<CSVRecord> rec = csvFileParser.getRecords();
		Map<String,Integer> csvRecords = csvFileParser.getHeaderMap();
		
		this.parsedFile = new LinkedHashMap<String, String>();
		  for (String key : csvRecords.keySet()){	
		    for (CSVRecord r: rec) {			    	
		    	this.parsedFile.put(key, r.get(key));		    	  		    	 
			}
		  }
		
	  csvFileParser.close();
	}	
	
	public LinkedHashMap<String, String> getParsedFile(){
		return this.parsedFile;
	}
	
	
}
