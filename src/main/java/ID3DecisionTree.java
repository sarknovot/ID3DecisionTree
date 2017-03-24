import java.util.LinkedList;
import java.util.Map;
 


public class ID3DecisionTree<T> {
	
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
		
		
		this.root = new NodeInformationGain<T>(data, result);
		for (int i=0;i<this.depth-1;i++){
			this.root.addLayer();
		}
		
		return 1;
		
	}
	
	public void printTree(){
		if (this.root == null){
			System.out.println("The tree is empty.");
		}
		else{
			this.root.printSubTree();
		}
	}
		
	public  LinkedList<T> predict(Map<String, LinkedList<T>> data){
		LinkedList<T> result = new LinkedList<T>();
		
		return result;
	}
	
	
}
