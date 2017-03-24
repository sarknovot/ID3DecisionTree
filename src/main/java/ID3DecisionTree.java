import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;



public class ID3DecisionTree<T extends Comparable<T>> {
	
	private NodeInformationGain<T> root;
	private int depth;
	 
	
	public ID3DecisionTree (int depth)
	{	
		if (depth < 1){
				
		        throw new IllegalArgumentException("depth must be greater than zero");
		    }
		
		    this.depth = depth;
		
	}
	
	
	public int fit(Map<String, LinkedList<T>> data, LinkedList<T> result){
	// based on training data and corresponding result values builds a decision tree 	
		for (String key:  data.keySet()){
			if (data.get(key).size() != result.size()){				
				throw new IllegalArgumentException("Lists of all features have to have the same length as result list.");
			}
		}		
		
		this.root = new NodeInformationGain<T>(data, result);
		for (int i=0;i<this.depth-1;i++){
			this.root.addLayer();
		}
		
		return 1;
		
	}
	
	//TODO indentation for individual layers of the tree
	public void printTree(){
		if (this.root == null){
			System.out.println("The tree is empty.");
		}
		else{
			this.root.printSubTree();
		}
	}
	
	//TODO write test for predict
	public  LinkedList<T> predict(Map<String, LinkedList<T>> data, LinkedList<T> result){
		
		if (data == null){		
			throw new IllegalArgumentException("Data for prediction has to be given.");	
		}
		
		if (result == null){		
			throw new IllegalArgumentException("List where result will be saved has to be given.");	
		}
		
		 Set<String> keySet = data.keySet();
		 Iterator<String> keyDataIterator = keySet.iterator();
		 int dataLength = data.get(keyDataIterator.next()).size();
		 
		 while (keyDataIterator.hasNext()) {
		    if (data.get(keyDataIterator.next()).size() != dataLength){		
		    	throw new IllegalArgumentException("For all features lists have to have the same length");	
		    }
		 }
		 
		 if(result.size() != dataLength){
			 throw new IllegalArgumentException("Resul list and data lists have different lenghts");			 
		 }

		 Map<String, T> dataRow = new HashMap<String, T>();
		 Map<String, Iterator<T>> featureIterators =  new HashMap<String, Iterator<T>>();
		 
		 
		 for(String feature: data.keySet() ){//creates iterators for individual features which will be used for reading of rows of data
			 featureIterators.put(feature,  data.get(feature).iterator());
		 }
		 
		 for (T res: result ){		 
			 for(String feature: data.keySet() ){
				 dataRow.put(feature, featureIterators.get(feature).next());
			 }				 
			 res = this.root.predict(dataRow,res);			 			  
		 }
		 	
		return result;
	}
	
	
}
