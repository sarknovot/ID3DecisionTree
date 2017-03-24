

import java.lang.Math;
import java.util.List;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

public class NodeInformationGain<T extends Comparable<T>> implements  BaseNode{

	private double informationGain;
	private String splittingFeature;
	private Map<String, LinkedList<T>> data;
	private LinkedList<T> result;
	private T resultCategoryTrue;
	private T resultCategoryFalse;
	private Map<T,NodeInformationGain<T>> subNodes;
	private Map<T, Map<String, LinkedList<T>>> splittedData;
	private Map<T, LinkedList<T>> splittedResult;
	//private final NodeInformationGain parent;
	//private final NodeInformationGain leftChild;
	//private final NodeInformationGain rightChild;
	
	
	public NodeInformationGain (Map<String, LinkedList<T>> data, LinkedList<T> result){
		//this.parent = parent;
		//this.leftChild = null;
		//this.rightChild = null;
		this.data = data;
		this.result = result;
		
		this.resultCategoryTrue = result.getFirst();
		for (int i=1; i < result.size(); i++){
			this.resultCategoryFalse = result.get(i);
			if (!this.resultCategoryFalse.equals(this.resultCategoryTrue)){
				break;
			}
		}
			
		calcInformationGain();
	}
	
	public NodeInformationGain (LinkedList<T> result){
		
		this.data = null;
		this.result = result;
		this.splittingFeature = null;
		this.informationGain = 0;
	}
	
	
	class ClassOfFeatureInfo {
		private T classOfFeature;
		private int occurenceNumber;
		private int resultTrueNumber;
		private int resultFalseNumber;
		
		
		// class which holds information about individual class that a feature contains
		public ClassOfFeatureInfo(T classOfFeature,int occurenceNumber,int resultTrueNumber,int resultFalseNumber){
			this.classOfFeature = classOfFeature;
			this.occurenceNumber = occurenceNumber;
			this.resultTrueNumber = resultTrueNumber; //now many data items having values of feature equal to class have in the result vector true
			this.resultFalseNumber = resultFalseNumber;//now many data items having values of feature equal to class have in the result vector false
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
		
		public T getClassOfFeature(){
			return this.classOfFeature;
		}
		
		public int getResultTrueNumber(){
			return this.resultTrueNumber;
		}
		
		public int getResultFalseNumber(){
			return this.resultFalseNumber;
		}
		
		
    }
	

	private double  calcEntropy(double p){
		if (p != 0){
			return -p*Math.log(p)/Math.log(2);
		}
		else{
			return 0;
		}
	}
	
	//extracts information about feature - classes which the feature contains and numbers of yes/no values in result vector which correspond to a class
	private List<ClassOfFeatureInfo> getClassesOfFeature(LinkedList<T> feature){
		List<ClassOfFeatureInfo> classes = new LinkedList<ClassOfFeatureInfo>();	
		
		for (int i=0; i<feature.size(); i++){		
			int isAlreadyInClasses = 0;
			// if class already exist in classes list, the record is updated
			for (ClassOfFeatureInfo classInfo : classes) {
	               if (feature.get(i).equals(classInfo.getClassOfFeature())){
	            	   classInfo.incOccurenceNumber();
	            	   isAlreadyInClasses = 1;
	            	   if (this.result.get(i).equals(this.resultCategoryFalse)){
	            		   classInfo.incResultFalseNumber(); 
	            	   }
	            	   else if (this.result.get(i).equals(this.resultCategoryTrue)){
	            		   classInfo.incResultTrueNumber();   
	            	   }            	   
	            	   else{
	            		   throw new IllegalArgumentException("The Node can classify only into two disctinct values");  
	            	   }
	            	   	            	   
	            	   break;
	            	}
			   }
			// new class has been found	and ClassOfFeatureInfo will be instantialized for it 
			if (isAlreadyInClasses == 0)
				{
					if (this.result.get(i).equals(this.resultCategoryFalse)){ 
						classes.add(new ClassOfFeatureInfo(feature.get(i), 1 , 1, 0));					 				 
					}
					else if (this.result.get(i).equals(this.resultCategoryTrue)){
						classes.add(new ClassOfFeatureInfo(feature.get(i), 1 , 0, 1));     
					}            	   
					else{
						throw new IllegalArgumentException("The Node can classify only into two disctinct values");  
					}
				
			}
		}
		
		return classes;
	}
	
	private void calcInformationGain(){
	
			this.informationGain = 0;
			
			//calculates total entropy ( from the whole result list)
			List<ClassOfFeatureInfo> countsOfResultClasses = getClassesOfFeature(result);
			double totalEntropy = 0;
			int sizeOfResult = this.result.size();
			
			for( ClassOfFeatureInfo classInfo : countsOfResultClasses){
				totalEntropy += calcEntropy((double)(classInfo.getOccurenceNumber())/(double)(sizeOfResult));
			}
					
			if (totalEntropy==0){ // no entropy in result
				this.informationGain = 0;
				this.splittingFeature = null;
				
			}else{ // calculates information gains for individual classes of all features and saves value if it is higher that this.informationGain			
				for (String featurName : this.data.keySet()){				
					LinkedList<T> feature = this.data.get(featurName);
					List<ClassOfFeatureInfo> classesOfFeature = getClassesOfFeature(feature);

					double entropyOfFeatureTimesCount = 0;
				
					for (ClassOfFeatureInfo classInfo : classesOfFeature){
						int ClassOccurenceNumber = classInfo.getResultTrueNumber()+classInfo.getResultFalseNumber();
						entropyOfFeatureTimesCount += (double)(ClassOccurenceNumber)*(calcEntropy((double)(classInfo.getResultTrueNumber())/(double)(ClassOccurenceNumber)) + calcEntropy((double)(classInfo.getResultFalseNumber())/(double)(ClassOccurenceNumber))); 							
					}
				
					double informationGain = totalEntropy - entropyOfFeatureTimesCount/sizeOfResult;
				
					if (informationGain > this.informationGain){
						this.informationGain = informationGain;
						this.splittingFeature = featurName;
					}  
			
			
				}

		} 
	}
	
	private void splitData(){// splits data into subsets which correspond to individual classes from feature which has had the highest information gain
	  
		 LinkedList<T> splFeature = data.get(this.splittingFeature);
		 this.data.remove(this.splittingFeature);
		 HashMap<String, LinkedList<T>> emptySubData = new  HashMap<String, LinkedList<T>>();
		 for (String featureName: this.data.keySet()){
			 emptySubData.put(featureName, new LinkedList<T>());
		 }
		 		 
		 Iterator<T> splFeatureIterator = splFeature.iterator();	 
		 Iterator<T> resultIterator = this.result.iterator();
		 
		 for (String featureName: this.data.keySet()){
			 LinkedList<T> feature = this.data.get(featureName);
			 Iterator<T> featureInDataIterator  = feature.iterator();
			 	 			 
			 while (splFeatureIterator.hasNext() && featureInDataIterator.hasNext() && resultIterator.hasNext()){
				 T classOfSplittingFeature = (T) splFeatureIterator.next();				 
				 if (!splittedData.containsKey(classOfSplittingFeature)){
					 this.splittedData.put(classOfSplittingFeature, new HashMap<String, LinkedList<T>>(emptySubData));
				     this.splittedResult.put(classOfSplittingFeature, new LinkedList<T>());
				 }
				 this.splittedData.get(classOfSplittingFeature).get(featureName).add((T) featureInDataIterator.next());
				 this.splittedResult.get(classOfSplittingFeature).add((T) resultIterator.next());
			}
		 }
		 
		if (this.data.keySet().size() == 0){ // last feature - only result has to be split
			while (splFeatureIterator.hasNext() && resultIterator.hasNext()){
				T classOfSplittingFeature = (T) splFeatureIterator.next();	
				if (!splittedResult.containsKey(classOfSplittingFeature)){
				     this.splittedResult .put(classOfSplittingFeature, new LinkedList<T>());
				 }
				 this.splittedResult.get(classOfSplittingFeature).add((T) resultIterator.next());
			}				
			}
			
			
		 
	} 
	
	
	
	public void addLayer(){
		if (this.data != null){ // not leaf node of final tree
		    if (this.subNodes == null){	//leaf node of tree being built - new layer will be added 			 
		    	this.splittedData = new HashMap<T, Map<String, LinkedList<T>>>();
		    	this.splittedResult  = new HashMap<T, LinkedList<T>>();
		    	this.subNodes = new HashMap<T, NodeInformationGain<T>>();
		    	splitData();
		    	 
		    		
		    	if ( (splittedData.keySet().size() > 1) ||  (this.data.keySet().size()>0) ){ //there are more than two classes in feature with maximal information gain, so feature can be splitted and subtrees created or some features have not been splitted yet
		    		for (T classOfSplittingFeature: this.splittedData.keySet()){
		    			subNodes.put(classOfSplittingFeature,new NodeInformationGain<T>(splittedData.get(classOfSplittingFeature), splittedResult.get(classOfSplittingFeature)));
		    		}
		    	}else if (splittedResult.keySet().size() > 0){
		    		for (T classOfSplittingFeature: this.splittedResult.keySet()){
		    			subNodes.put(classOfSplittingFeature, new NodeInformationGain<T>(splittedResult.get(classOfSplittingFeature)));
		    		}
		    	}
				    	
		    	
		    }		    
		    else{//not leaf node of tree being built - next layer is called
		 		 for (NodeInformationGain<T> subNode: this.subNodes.values()){
		    		subNode.addLayer();
		    	}
		    }
		    
		}
	}
	
	
	
	public double getInformationGain(){
		return this.informationGain;
	}
	public String getSplittingFeature(){
		return this.splittingFeature;
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
		System.out.println("--" + this.toString() + "\r\n");
		if (this.subNodes != null ){
			for (NodeInformationGain<T> subnode: this.subNodes.values()){
				subnode.printSubTree();
			}
		}
	}

	private T findResultClassWithMaxOcurrence(T classWithMaxOccurence){
	// in result data of leaf node finds class (yes/no) which has the highest occurrence = is result of classification	
		if (this.result == null){
			return null;
		}
		
		Map<T,AtomicInteger> resultClasses = new HashMap<T,AtomicInteger>();
		 
		int maxOccurence = 0;
		
		for (T resClass: this.result){
			if (resultClasses.containsKey(resClass)){
				resultClasses.get(resClass).incrementAndGet();
				if (resultClasses.get(resClass).intValue()>maxOccurence){
					maxOccurence = resultClasses.get(resClass).intValue();
					classWithMaxOccurence = resClass;
				}
			}
			else{
				resultClasses.put(resClass,new AtomicInteger(1));
				if (maxOccurence == 0){
					maxOccurence = 1;
					classWithMaxOccurence = resClass;
				}
			}
		}
		
		
		return classWithMaxOccurence;
	}
	
	
	public  T predict(Map<String,T> dataRow, T finalClass){
	// for a given data row finds corresponding leaf node and returns corresponding class of result list (yes/no) 
		if (this.splittingFeature !=null){
			 if(!dataRow.containsKey(this.splittingFeature)){
		           throw new IllegalArgumentException("Data do not contain feature which has been in training data: " + this.splittingFeature);
			 }			 
			return this.subNodes.get(dataRow.get(this.splittingFeature)).predict(dataRow, finalClass);
		}else{		
		return findResultClassWithMaxOcurrence(finalClass); //TODO result of maximal occurrence is property of leaf node
		}
		
	}
	
	
}
