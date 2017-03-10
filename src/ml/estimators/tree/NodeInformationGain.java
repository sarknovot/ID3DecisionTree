package ml.estimators.tree;

import java.lang.Math;
import java.util.List;
import java.util.ArrayList;

import ml.estimators.tree.BaseNode;
import ml.estimators.tree.NodeInfo;

public class NodeInformationGain implements  BaseNode{

	private double informationGain;
	private int splittingFeature;
	private double splitingValue;
	private List<float[]> data;
	private float[] result;
	private final NodeInformationGain parent;
	private final NodeInformationGain leftChild;
	private final NodeInformationGain rightChild;
	
	
	public NodeInformationGain(NodeInformationGain parent, List<float[]> data, float[] result)
	{
		this.parent = parent;
		this.leftChild = null;
		this.rightChild = null;
		this.data = data;
		this.result = result;
		calcInformationGain();
	}
	
	private double  calcEntropy(double p)
	{
		if (p != 0)
		{
			return -p*Math.log(p)/Math.log(2);
		}
		else
		{
			return 0;
		}
	}
	
	private List<float[]> getClassesOfFeature(float[] feature)
	{
		 
		List<float[]> classes = new ArrayList<float[]>();
		
		
		for (int i=0; i<feature.length; i++)
		{		int isAlreadyInClasses = 0;
			   for (float[] j : classes) {
	               if (feature[i] == j[0])
	            	{
	            	   j[1]++;
	            	   isAlreadyInClasses = 1;
	            	   if (this.result[i] == 0)
	            	   {
	            		   j[2]++;   
	            	   }
	            	   else if (this.result[i] == 1)
	            	   {
	            		   j[3]++;   
	            	   }            	   
	            	   else
	            	   {
	            		   System.out.println("The tree can classify only into 0's and 1's");  
	            	   }
	            	   
	            	   j[4]++; 
	            	   
	            	   break;
	            	}
			   }
			   
			if (isAlreadyInClasses == 0)
			{
				if (this.result[i] == 0)
         	   {
					classes.add(new float[]{feature[i] , 1 , 1, 0 , 1});
         	   }
         	   else if (this.result[i] == 1)
         	   {
         		  classes.add(new float[]{feature[i] , 1 , 0, 1 , 1});  
         	   }            	   
         	   else
         	   {
         		   System.out.println("The tree can classify only into 0's and 1's");  
         	   }
				
			}
		}
		
		return classes;
	}
	
	private void calcInformationGain(){
	//initialization of result
			this.informationGain = 0;
			
			//calculates total entropy ( from all the result vector)
			List<float[]> countsOfResultClasses = getClassesOfFeature(result);
			double totalEntropy = 0;
			int sizeOfResult = this.result.length;
			for (float[] resultClass: countsOfResultClasses)
			{
				totalEntropy += calcEntropy(resultClass[2]/sizeOfResult);
			}
			
			// calculates information gains for individual classes of all features
			List<List<float[]>> countsOfClasses = new ArrayList<List<float[]>>();
			int indexOfFeature = -1;
			for (float[] dataColumn: data){		
				indexOfFeature++;
				List<float[]> feature = getClassesOfFeature(dataColumn);
				countsOfClasses.add(getClassesOfFeature(dataColumn));		  
				for (float[] fclass: feature)
				{
					double entropyWithoutClass = 0;
					
					for (int i = 2; i<fclass.length - 1; i++)
					{	
						double sizeOfResultWithoutClass = fclass[fclass.length-1];
						entropyWithoutClass += calcEntropy(fclass[i]/sizeOfResultWithoutClass);
						double informationGain = totalEntropy - sizeOfResultWithoutClass/sizeOfResult*entropyWithoutClass;
						if (informationGain > this.informationGain)
						{
							this.informationGain = informationGain;
							this.splittingFeature = indexOfFeature;
							this.splitingValue = fclass[0];
						}
							
					}
									
					
				}
				
			}
	
	}
	
	
	
	public NodeInfo getSplittinInfo()
	{	
				return new NodeInfo(this.informationGain,this.splittingFeature,this.splitingValue);
	}
	
	
	
}
