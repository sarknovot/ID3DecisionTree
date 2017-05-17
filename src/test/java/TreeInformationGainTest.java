import static org.junit.Assert.*;
import java.util.Map;
import java.util.logging.Logger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

public class TreeInformationGainTest {	
	private static final String CLASS_NAME = ReadCSV.class.getCanonicalName();
	private static Logger logger = Logger.getLogger(CLASS_NAME);	
	
	@Test
	public void informationGainCalc() {
		LinkedList<Integer> result = new LinkedList<Integer>(Arrays.asList(1,0,0,0));
		Map<String, LinkedList<Integer>> data = new HashMap<String, LinkedList<Integer>>();
		data.put("feature1", new LinkedList<Integer>(Arrays.asList(2,3,3,1)));
		data.put("feature2", new LinkedList<Integer>(Arrays.asList(0,0,0,0)));		
		
		NodeInformationGain<Integer> node  = new NodeInformationGain<Integer>(data, result);
		
		assertEquals(0.811, node.getInformationGain(),0.0005);
		 	 		 
	}

	@Test
	public void splittingFeatureFind() {
		
		LinkedList<Integer> result = new LinkedList<Integer>(Arrays.asList(1,0,0,0));
		Map<String, LinkedList<Integer>> data = new HashMap<String, LinkedList<Integer>>();
		data.put("feature1", new LinkedList<Integer>(Arrays.asList(2,3,3,1)));
		data.put("feature2", new LinkedList<Integer>(Arrays.asList(0,0,0,0)));
		
		NodeInformationGain<Integer> node  = new NodeInformationGain<Integer>(data, result);
		
		assertTrue( node.getSplittingFeature().equals("feature1") );
	}
 
	@Test 
	public void classifyNewData(){
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
		classificationResult = (ArrayList<String>) tree.predict(newDataMap, classificationResult);
		String[] resulArray = new String[classificationResult .size()];
		resulArray = classificationResult.toArray(resulArray);
		System.out.println(resulArray);
		String[] expectedResult = {"setosa", "setosa", "setosa2", "setosa2"};
		Assert.assertArrayEquals( expectedResult, resulArray);
	}
 	
	
}
