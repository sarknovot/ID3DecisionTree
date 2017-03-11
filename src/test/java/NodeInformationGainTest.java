

import static org.junit.Assert.*;
import java.util.List;
import java.util.ArrayList;

import org.junit.Test;

public class NodeInformationGainTest {

	@Test
	public void test() {
		float[] result = new float[]{1,0,0,0};
		float[] feature = new float[]{2,3,3,1}; 
		List<float[]> data = new ArrayList<float[]>();
		data.add(feature);
		
		NodeInformationGain node  = new NodeInformationGain(data, result);

		assertEquals(0.311, node.getInformationGain(),0.00001);		 
	 		 
	}
	
	@Test
	public void test2() {
		float[] result = new float[]{1,0,0,0};
		float[] feature1 = new float[]{2,3,3,1}; 
		float[] feature2 = new float[]{0,0,0,0}; 
		List<float[]> data = new ArrayList<float[]>();
		data.add(feature2);
		data.add(feature1);
		
		NodeInformationGain node  = new NodeInformationGain(data, result);

		assertEquals(0.311, node.getInformationGain(),0.0005);
		 	 		 
	}

	@Test
	public void test3() {
		float[] result = new float[]{1,0,0,0};
		float[] feature1 = new float[]{2,3,3,1}; 
		float[] feature2 = new float[]{0,0,0,0}; 
		List<float[]> data = new ArrayList<float[]>();
		data.add(feature2);
		data.add(feature1);
		
		NodeInformationGain node  = new NodeInformationGain(data, result);

		assertEquals(1, node.getSplittingFeature());		 
	 		 
	}
	
	@Test
	public void test4() {
		float[] result = new float[]{1,0,0,0};
		float[] feature1 = new float[]{2,3,3,1}; 
		float[] feature2 = new float[]{0,0,0,0}; 
		List<float[]> data = new ArrayList<float[]>();
		data.add(feature2);
		data.add(feature1);
		
		NodeInformationGain node  = new NodeInformationGain(data, result);

		assertEquals(2, node.getSplitingValue(), 0.0001);
		 
	 		 
	}
}
