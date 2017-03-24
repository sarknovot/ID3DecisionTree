

import static org.junit.Assert.*;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Arrays;

import org.junit.Test;

public class TreeInformationGainTest {
/*
	@Test
	public void test() {
		LinkedList<Integer> result = new LinkedList<Integer>(Arrays.asList(1,0,0,0));
		Map<String, LinkedList<Integer>> data = new HashMap<String, LinkedList<Integer>>();
		data.put("feature1", new LinkedList<Integer>(Arrays.asList(2,3,3,1)));
		
		NodeInformationGain<Integer> node  = new NodeInformationGain(data, result);

		assertEquals(0.311, node.getInformationGain(),0.0005);		 
	 		 
	}
	*/
	//List<Double> temp1 = new LinkedList<Double>(Arrays.asList(1.0, 2.0));
	
	
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
		
		assertTrue( node.getSplittingFeature().equals("feature2") );
	}

	@Test 
	public void treeDepth(){
		LinkedList<Integer> result = new LinkedList<Integer>(Arrays.asList(1,0,0,0));
		Map<String, LinkedList<Integer>> data = new HashMap<String, LinkedList<Integer>>();
		data.put("feature1", new LinkedList<Integer>(Arrays.asList(2,3,3,1)));
		data.put("feature2", new LinkedList<Integer>(Arrays.asList(0,0,0,0)));
		
		ID3DecisionTree<Integer> tree =  new ID3DecisionTree<Integer>(4);
		int output = tree.fit(data, result);
		tree.printTree();
		assertEquals(1, output);
		 
	
		
	}
	
	
}
