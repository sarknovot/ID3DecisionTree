

//import ml.estimators.tree.NodeInfo;

public interface BaseNode {	
	double getInformationGain();
	String getSplittingFeature();
}
