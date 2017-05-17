import java.lang.Math;
import java.util.List;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *Represents node of ID3 decision tree. Only binary targets are accepted.
 *
 *@author  Sarka
* @version 1.0
 */
public class NodeInformationGain<T extends Comparable<T>>{
	private double informationGain;
	private String splittingFeature;
	private Map<String, LinkedList<T>> data;
	private Set<String> dataKeySet;
	private LinkedList<T> result;
	private T resultCategoryTrue;
	private T resultCategoryFalse;
	private Map<T,NodeInformationGain<T>> subNodes;
	private Map<T, Map<String, LinkedList<T>>> splitData;
    private Map<T, LinkedList<T>> splitResult;	
	 /*
	  * Constructor.
	  * 
	  * @param data Training data.
	  * @param output Training outputs.
	  */
	protected NodeInformationGain (Map<String, LinkedList<T>> data, LinkedList<T> result){
		this.data = data;
		this.result = result;
		this.dataKeySet = new HashSet<String>(data.keySet());
		
		this.resultCategoryTrue = result.getFirst();
		for (int i=1; i < result.size(); i++){
			this.resultCategoryFalse = result.get(i);
			if (!this.resultCategoryFalse.equals(this.resultCategoryTrue)){
				break;
			}
		}		
		for (int i=1; i < result.size(); i++){
			if (!this.resultCategoryFalse.equals(result.get(i)) && !this.resultCategoryTrue.equals(result.get(i))){
				throw new IllegalArgumentException("Result vector contains three different values. "
						+ "Only binary targets accepted");	
			}
		}		
		findMaxInformationGain();
	}
	
	private NodeInformationGain (LinkedList<T> result){		
		this.data = null;
		this.result = result;
		this.splittingFeature = null;
		this.informationGain = 0;
	}
	
	 /*
	  * Class represents information about individual values of certain feature, namely:
	  *  - number of occurence of particular value 
	  *  - number of true values in result vector which correspond to the feature value
	  *  - number of false values in result vector which correspond to the feature value	  * 
	  */
	class FeatureValueInfo {
		private T featureValue; //feature value to which data in this object relates
		private int occurenceNumber; //how many times the values occcurs in feature vector
		private int resultTrueNumber; //number of true values in result vector which correspond to the value of feature
		private int resultFalseNumber; //number of false values in result vector which correspond to the value of feature
		
		public FeatureValueInfo(T featureValue,int occurenceNumber,int resultTrueNumber,int resultFalseNumber){
			this.featureValue = featureValue;
			this.occurenceNumber = occurenceNumber;
			this.resultTrueNumber = resultTrueNumber; 
			this.resultFalseNumber = resultFalseNumber;
		}
        
		public void incOccurenceNumber(){
			this.occurenceNumber++;
		}
		
		public void incResultTrueNumber(){
			this.resultTrueNumber++;
		}
		
		public void incResultFalseNumber(){
			this.resultFalseNumber++;
		}
		
		public int getOccurenceNumber(){
			return this.occurenceNumber;
		}
		
		public T getValueOfFeature(){
			return this.featureValue;
		}
		
		public int getResultTrueNumber(){
			return this.resultTrueNumber;
		}
		
		public int getResultFalseNumber(){
			return this.resultFalseNumber;
		}	
    }
	
	//Calculates entropy - used in calculation of information gain
	private double calcEntropy(double p){
		if (p != 0){
			return -p*Math.log(p)/Math.log(2);
		}
		else{
			return 0;
		}
	}
	
	/*
	 * Extracts information about a feature - values which the feature contains and numbers of yes/no values in result vector which correspond to the values.
	 * Stores extracted data in LinkedList of FeatureValueInfo objects.
	 * 
	 * @param Data representing one feature, that is one column of the whole dataset.
	 */
	private List<FeatureValueInfo> getValuesOfFeature(LinkedList<T> feature){
		List<FeatureValueInfo> values = new LinkedList<FeatureValueInfo>();	
		
		for (int i=0; i<feature.size(); i++){//go trough data column and for every entry either create new item in values list or update an existing item		
			int valueNoticed = 0;
			// if value already exists in values list, the record is updated
			for (FeatureValueInfo valueInfo: values){
	               if (feature.get(i).equals(valueInfo.getValueOfFeature())){
	            	   valueInfo.incOccurenceNumber();
	            	   valueNoticed = 1;
	            	   if (this.result.get(i).equals(this.resultCategoryFalse)){
	            		   valueInfo.incResultFalseNumber(); 
	            	   }
	            	   else if (this.result.get(i).equals(this.resultCategoryTrue)){
	            		   valueInfo.incResultTrueNumber();   
	            	   }            	   
	            	   else{
	            		   throw new IllegalArgumentException("The Node can classify only into two disctinct values");  
	            	   }            	   	            	   
	            	   break;
	            	}
			   }
			// new class was found	and FeatureValueInfo should be instantialized for it 
			if (valueNoticed == 0){
					if (this.result.get(i).equals(this.resultCategoryFalse)){ 
						values.add(new FeatureValueInfo(feature.get(i), 1 , 0, 1));					 				 
					}
					else if (this.result.get(i).equals(this.resultCategoryTrue)){
						values.add(new FeatureValueInfo(feature.get(i), 1 , 1, 0));     
					}            	   
			}
		}		
		return values;
	}
	
	/*
	 * Finds which feature has maximum information gain, saves feature name to this.splittingFeature and value of the information gain to this.informationGain
	 */
	private void findMaxInformationGain(){
			this.informationGain = 0;
			
			//calculates total entropy ( from the whole result list)
			List<FeatureValueInfo> countsOfResultValues = getValuesOfFeature(result);
			double totalEntropy = 0;
			int sizeOfResult = this.result.size();
			
			for( FeatureValueInfo valueInfo : countsOfResultValues){
				System.out.println((double)(valueInfo.getOccurenceNumber())/(double)(sizeOfResult));
				totalEntropy += calcEntropy((double)(valueInfo.getOccurenceNumber())/(double)(sizeOfResult));
			}
					
			if (totalEntropy==0){ // no entropy in result - no need for decision treee
				this.informationGain = 0;
				this.splittingFeature = null;				
			}else{ // calculates information gains for individual values of all features, this.informationGain is updated if any of here calculated information gains exceed it	
				for (String featurName : this.data.keySet()){				
					LinkedList<T> feature = this.data.get(featurName);
					List<FeatureValueInfo> valueOfFeature = getValuesOfFeature(feature);

					double entropyOfFeatureTimesCount = 0;	
					for (FeatureValueInfo valueInfo : valueOfFeature){
						int valueOccurence = valueInfo.getResultTrueNumber()+valueInfo.getResultFalseNumber();
						entropyOfFeatureTimesCount += (double)(valueOccurence)*(calcEntropy((double)(valueInfo.getResultTrueNumber())/(double)(valueOccurence)) + 
								calcEntropy((double)(valueInfo.getResultFalseNumber())/(double)(valueOccurence))); 	
						System.out.println("valueOccurence: " + valueOccurence);
						System.out.println("valueInfo.getResultTrueNumber : " + valueInfo.getResultTrueNumber());
						System.out.println("valueInfo.getResultFalseNumber : " + valueInfo.getResultFalseNumber());
					}
				
					double informationGain = totalEntropy - entropyOfFeatureTimesCount/sizeOfResult;			
					if (informationGain > this.informationGain){
						this.informationGain = informationGain;
						this.splittingFeature = featurName;
					}  	
				}
		} 
	}
	
	/*
	 * Splits data into subsets which correspond to individual values from feature which has the highest information gain
	 * Splitted data are saved in this.splittedData which is Map<T, Map<String, LinkedList<T>>> object. Keys T in the Map represent
	 * all the values of feature with highest information gain. Map<String, LinkedList<T>> represents new data set.
	 */
	private int splitData(){	
		if(this.informationGain != 0){//if information gain == 0, no entropy no need to split
	    	this.splitData = new HashMap<T, Map<String, LinkedList<T>>>();
	    	this.splitResult  = new HashMap<T, LinkedList<T>>();
	    	this.subNodes = new HashMap<T, NodeInformationGain<T>>();			
			LinkedList<T> splFeature = data.get(this.splittingFeature);
			this.data.remove(this.splittingFeature);
			//split result vector
			Iterator<T> resultIterator = this.result.iterator();
			Iterator<T> splFeatureIterator = splFeature.iterator();
			while (splFeatureIterator.hasNext() && resultIterator.hasNext()){
				T valueOfSplittingFeature = (T) splFeatureIterator.next();
				if (!splitResult.containsKey(valueOfSplittingFeature)){
					this.splitResult.put(valueOfSplittingFeature, new LinkedList<T>());	
				}
				this.splitResult.get(valueOfSplittingFeature).add((T) resultIterator.next());
			}			
			if (this.splitResult.keySet().size() == 1){//last feature - only result split
				return 0;
			}else{
				//split data
				for (String featureName: this.data.keySet()){
					LinkedList<T> feature = this.data.get(featureName);
					Iterator<T> featureInDataIterator  = feature.iterator();
					splFeatureIterator = splFeature.iterator();
					while (splFeatureIterator.hasNext() && featureInDataIterator.hasNext()){
						T valueOfSplittingFeature = (T) splFeatureIterator.next();				 
						if (!splitData.containsKey(valueOfSplittingFeature)){//there is not any data set in this.splittedData which correspond to valueOfSplittingFeature
							this.splitData.put(valueOfSplittingFeature, new HashMap<String, LinkedList<T>>());
							for (String fn: this.data.keySet()){
								splitData.get(valueOfSplittingFeature).put(fn, new LinkedList<T>());
							}						
						}//data structure for feature value already exists, data can be added
						this.splitData.get(valueOfSplittingFeature).get(featureName).add((T) featureInDataIterator.next());				
					}
				}
				return 1;//data and result split
			}
		}else{
			this.data = null; //creates leaf node from current node	
			return -1;
		}
	} 	

	/*
	 * Extends current tree by finding leaf nodes and splitting them
	 */
	protected void addLayer(){
		if (this.data != null){ // if this.data == null tree cannot be further extended
		    if (this.subNodes == null){	//we are in leaf node - new layer can be added 			 
		    	int splittype = splitData();		    
		    			    	
		    	if (splittype == 0){// no more data, only result will be split
		    		for (T classOfSplittingFeature: this.splitResult.keySet()){
		    			subNodes.put(classOfSplittingFeature, new NodeInformationGain<T>(splitResult.get(classOfSplittingFeature)));
		    		}    	
		    	}else if(splittype == 1){//subnodes with new data are created
		    		for (T classOfSplittingFeature: this.splitData.keySet()){
		    				subNodes.put(classOfSplittingFeature,new NodeInformationGain<T>(splitData.get(classOfSplittingFeature), splitResult.get(classOfSplittingFeature)));   			
		    		} 
		    	}
		    }		    
		    else{//not leaf node - next layer of tree is called
		 		 for (NodeInformationGain<T> subNode: this.subNodes.values()){
		    		subNode.addLayer();
		    	}
		    }
		}
	}
		
	public String getSplittingFeature(){
		return this.splittingFeature;
	}
	
	public double getInformationGain(){
		return this.informationGain;
	}
	
	
	@Override
    public String toString() {
		if (this.splittingFeature != null){
			return "Node{" + "feature:" + this.splittingFeature + ",information gain: " + this.informationGain+ "}";
		}
		else{
			return "Final Node";
		}
    }
 	
	protected void printSubTree(){
		System.out.println(this);
		if (this.subNodes != null ){
			for (NodeInformationGain<T> subnode: this.subNodes.values()){
				subnode.printSubTree();
			}
		}
	}
	
	/*
	 * Helper method for predict(). Goes trough result vector in leaf node and calculates occurences of its values.
	 * It is rather overkill because the NodeInformationGain class supports only binary classification, this method can
	 * be used for multiclass classification.
	 */
	private T findResultClassWithMaxOcurrence(T classWithMaxOccurence){	
		if (this.result == null){
			return null;
		}
				 
		int maxOccurence = 0;
 
		Map<T,AtomicInteger> resultValues = new HashMap<T,AtomicInteger>();
		for (T resValue: this.result){
			if (resultValues.containsKey(resValue)){
				resultValues.get(resValue).incrementAndGet();
				if (resultValues.get(resValue).intValue()>maxOccurence){
					maxOccurence = resultValues.get(resValue).intValue();
					classWithMaxOccurence = resValue;
				}
			}
			else{
				resultValues.put(resValue,new AtomicInteger(1));
				if (maxOccurence == 0){
					maxOccurence = 1;
					classWithMaxOccurence = resValue;
				}
			}
		}		
		return classWithMaxOccurence; //TODO returns reference to object in data hashmap, copy should be created instead, but T already extends comparable
	}
	
	/*
	 * For a given data row finds corresponding leaf node and returns value which prevails in result vector in that leaf node,
	 * that is returns result of classification.
	 */
	protected T predict(Map<String,T> dataRow, T finalClass){
		if (this.splittingFeature !=null){
			 if(!dataRow.containsKey(this.splittingFeature)){
		           throw new IllegalArgumentException("Data do not contain feature which has been in training data: " + this.splittingFeature);
			 }			 
			return this.subNodes.get(dataRow.get(this.splittingFeature)).predict(dataRow, finalClass);
		}else{		
		return findResultClassWithMaxOcurrence(finalClass); 
		}		
	}
	
	protected Set<String> getDataKeySet(){		 
		return this.dataKeySet;		 
	}
}
