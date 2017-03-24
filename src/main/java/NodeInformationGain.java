

import java.lang.Math;
import java.util.List;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

public class NodeInformationGain<T> implements  BaseNode{

	private double informationGain;
	private String splittingFeature;
	private Map<String, LinkedList<T>> data;
	private LinkedList<T> result;
	private T resultCategoryTrue;
	private T resultCategoryFalse;
	private LinkedList<NodeInformationGain<T>> subNodes;
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
		//this.parent = parent;
		//this.leftChild = null;
		//this.rightChild = null;
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
		
		public ClassOfFeatureInfo(T classOfFeature,int occurenceNumber,int resultTrueNumber,int resultFalseNumber){
			this.classOfFeature = classOfFeature;
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
	
	private List<ClassOfFeatureInfo> getClassesOfFeature(LinkedList<T> feature){
		List<ClassOfFeatureInfo> classes = new LinkedList<ClassOfFeatureInfo>();	
		
		for (int i=0; i<feature.size(); i++){		
			int isAlreadyInClasses = 0;
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
	            		   System.out.println("The Node can classify only into two disctinct values");  
	            	   }
	            	   	            	   
	            	   break;
	            	}
			   }
			   
		if (isAlreadyInClasses == 0)
			{
				if (this.result.get(i).equals(this.resultCategoryFalse)){ 
					classes.add(new ClassOfFeatureInfo(feature.get(i), 1 , 1, 0));					 				 
         	   }
         	   else if (this.result.get(i).equals(this.resultCategoryTrue)){
         		  classes.add(new ClassOfFeatureInfo(feature.get(i), 1 , 0, 1));     
         	   }            	   
         	   else{
         		   System.out.println("The Node can classify only into two disctinct values");  
         	   }
				
			}
		}
		
		return classes;
	}
	
	private void calcInformationGain(){
	//initialization of result
			this.informationGain = 0;
			
			//calculates total entropy ( from all the result vector)
			List<ClassOfFeatureInfo> countsOfResultClasses = getClassesOfFeature(result);
			double totalEntropy = 0;
			int sizeOfResult = this.result.size();
			
			for( ClassOfFeatureInfo classInfo : countsOfResultClasses){
				totalEntropy += calcEntropy((double)(classInfo.getOccurenceNumber())/(double)(sizeOfResult));
			}
					
			if (totalEntropy!=0){
			// calculates information gains for individual classes of all features
				
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

			}else{ // no entropy in result data, decision can be made
				this.informationGain = 0;
				this.splittingFeature = null;
				
			}
	}
	
	private void splitData(){
		 if (this.splittingFeature != null){
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
				     this.splittedResult .put(classOfSplittingFeature, new LinkedList<T>());
				 }
				 this.splittedData.get(classOfSplittingFeature).get(featureName).add((T) featureInDataIterator.next());
				 this.splittedResult.get(classOfSplittingFeature).add((T) resultIterator.next());
			}
		 }
		 
		if (this.data.keySet().size() == 0){
			while (splFeatureIterator.hasNext() && resultIterator.hasNext()){
				T classOfSplittingFeature = (T) splFeatureIterator.next();	
				if (!splittedResult.containsKey(classOfSplittingFeature)){
				     this.splittedResult .put(classOfSplittingFeature, new LinkedList<T>());
				 }
				 this.splittedResult.get(classOfSplittingFeature).add((T) resultIterator.next());
			}				
			}
			
			
		}
	} 
	
	
	
	public void addLayer(){
		if (this.data != null){
		    if (this.subNodes == null){				 
		    	this.splittedData = new HashMap<T, Map<String, LinkedList<T>>>();
		    	this.splittedResult  = new HashMap<T, LinkedList<T>>();
		    	this.subNodes = new LinkedList<NodeInformationGain<T>>();
		    	splitData();
		    	
		    	if ( (splittedData.keySet().size() > 1) ||  (this.data.keySet().size()>0) ){ //there are more than two classes in feature with maximal information gain, so feature can be splitted and subtrees created or some features have not been splitted yet
		    		for (T classOfSplittingFeature: this.splittedData.keySet()){
		    			subNodes.add(new NodeInformationGain<T>(splittedData.get(classOfSplittingFeature), splittedResult.get(classOfSplittingFeature)));
		    		}
		    	}else if (splittedResult.keySet().size() > 0){
		    		for (T classOfSplittingFeature: this.splittedResult.keySet()){
		    			subNodes.add(new NodeInformationGain<T>(splittedResult.get(classOfSplittingFeature)));
		    		}
		    	}
				    	
		    	
		    }		    
		    else{
		 		 for (NodeInformationGain<T> subNode: this.subNodes){
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
			for (NodeInformationGain<T> subnode: this.subNodes){
				subnode.printSubTree();
			}
		}
	}
	
	
}
