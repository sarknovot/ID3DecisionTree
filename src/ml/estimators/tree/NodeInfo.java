package ml.estimators.tree;

public class NodeInfo 
{
	private double informationGain;
	private int splittingFeature;
	private double splitingValue;
	
	public NodeInfo(double informationGain,int splittingFeature,double splitingValue){
		this.informationGain = informationGain;
		this.splittingFeature = splittingFeature;
		this.splitingValue = splitingValue;
	}
	
	public double getInformationGain()
	{
		return this.informationGain;
	}
	
	public float splittingFeature()
	{
		return this.splittingFeature;
	}

	public double splitingValue()
	{
		return this.splitingValue;
	}
}
 
