import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
/**
 *Represents ID3 decision tree - creates tree for given data of type <T extends COmparable<T>>, fits data,
 *makes prediction with new data and prints tree
 *
 *@author  Sarka
* @version 1.0
 */
public class ID3DecisionTree<T extends Comparable<T>> {	
	private NodeInformationGain<T> root;
	private int depth;	
	
	 /**
	  * Constructor.
	  * 
	  * @param depth Maximum depth of tree - small value prevets from overfitting, small value prevets from high bias.
	  */
	public ID3DecisionTree (int depth){	
		if (depth < 1){				
		        throw new IllegalArgumentException("depth must be greater than zero");
		    }		
		    this.depth = depth;		
	}
	
	 /**
	  * Method creates a decision tree based on training data.
	  * 
	  * @param data Training data.
	  * @param output Training outputs.
	  * @exception llegalArgumentException If data is inconsist with output.
	  */
	public void fit(Map<String, LinkedList<T>> data, LinkedList<T> output){	
		for (String key:  data.keySet()){
			if (data.get(key).size() != output.size()){				
				throw new IllegalArgumentException("Lists of all features have to have the same length as result list.");
			}
		}				
		this.root = new NodeInformationGain<T>(data, output);
		for (int i=0; i<this.depth-1; i++){
			this.root.addLayer();
		}	
	}
	
	 /**
	  * Method prints tree
	  * 
	  */
	public void printTree(){
		//TODO indentation for individual layers of the tree
		if (this.root == null){
			System.out.println("The tree is empty.");
		}
		else{
			this.root.printSubTree();
		}
	}
	
	/**
	  * Method predict outputs for given data
	  * 
	  * @param newData Data for which output should be estimated.
	  * @exception llegalArgumentException No data or Lists representing data columns having different lenghts.
	  * @return ArrayList conaining predicted values.
	  */
	public  ArrayList<T> predict(Map<String, LinkedList<T>> newData, ArrayList<T> resultOfPrediction){
		//TODO write test for predict
		
		if (newData == null){		
			throw new IllegalArgumentException("Data for prediction has to be given.");	
		}
		//test wheter the features are the same as in training set
		Set<String> newDataKeySet = newData.keySet();
		Set<String> rootDataKeySet = this.root.getDataKeySet();
		if(!newDataKeySet.equals(rootDataKeySet)){
			throw new IllegalArgumentException("New data are inconsistent with training data.");	
		}
		//test wheter all tha features vectors have the same length 
		 Iterator<String> keyDataIterator = newDataKeySet.iterator();
		 int dataLength = newData.get(keyDataIterator.next()).size();		 
		 while (keyDataIterator.hasNext()) {
		    if (newData.get(keyDataIterator.next()).size() != dataLength){		
		    	throw new IllegalArgumentException("For all features lists have to have the same length");	
		    }
		 }		 		 
		 
		/*creates iterators for individual features which will be used for reading of rows of data 
		that will be storded in dataRow HashMap	*/	 
		 Map<String, T> dataRow = new HashMap<String, T>();
		 Map<String, Iterator<T>> featureIterators =  new HashMap<String, Iterator<T>>();		 
		 for(String feature: newData.keySet() ){
			 featureIterators.put(feature,  newData.get(feature).iterator());
		 }
		 
		 for (int i=0; i<dataLength; i++){//goes trough all rows of data
			 for(String feature: newData.keySet() ){//goes trough all columns from data and copies one row to dataRow
				 dataRow.put(feature, featureIterators.get(feature).next());
			 }
			 resultOfPrediction.set(i, this.root.predict(dataRow,resultOfPrediction.get(i)));
		 }		 	
		return resultOfPrediction;
	}	
}
