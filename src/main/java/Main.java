import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import java.util.logging.Logger;

public class Main {
	private static final String CLASS_NAME = ReadCSV.class.getCanonicalName();
	private static Logger logger = Logger.getLogger(CLASS_NAME);

	public static void main(String[] args){
	
	String dataFileName = "irisdata.csv";
	String resultFileName = "irisdata_result.csv";
	String newDataFileName = "irisnewdata.csv";
	ReadCSV trainData = null;
	ReadCSV trainResult = null;
	ReadCSV newData = null;
	
	//read data for training
	try{
		trainData = new ReadCSV(dataFileName );
	}catch(FileNotFoundException e){
		logger.severe("File " + dataFileName  + " was not found.");
	}catch (IOException e){
		logger.severe(e.getMessage());		 
	}

	try{
		trainResult = new ReadCSV(resultFileName);
	}catch(FileNotFoundException e){
		logger.severe("File " + resultFileName  + " was not found.");
	}catch (IOException e){
		logger.severe(e.getMessage());		 
	}	
	Map<String, LinkedList<String>> dataMap = trainData.getParsedFile();
	Map<String, LinkedList<String>> resultMap =  trainResult.getParsedFile();
	
	//train model
	ID3DecisionTree<String> tree =  new ID3DecisionTree<String>(4);
	tree.fit(dataMap, resultMap.get("species"));
	tree.printTree();//TODO print clearer tree
	
	//read data for prediction
	try{
		newData = new ReadCSV(newDataFileName);
	}catch(FileNotFoundException e){
		logger.severe("File " + newDataFileName  + " was not found.");
	}catch (IOException e){
		logger.severe(e.getMessage());		 
	}	
	Map<String, LinkedList<String>> newDataMap = newData.getParsedFile();
	Iterator<String> keyDataIterator = newDataMap.keySet().iterator();
	 int dataLength = newDataMap.get(keyDataIterator.next()).size();	
	ArrayList<String> classificationResult = new ArrayList<>(dataLength);
	for (int i=0; i<dataLength; i++){
		 classificationResult.add(new String());
	}
	//classify new data and print result
	classificationResult = tree.predict(newDataMap, classificationResult);
	for(String res: classificationResult){
		System.out.println(res);
	}	 
}
}

 
	 	
