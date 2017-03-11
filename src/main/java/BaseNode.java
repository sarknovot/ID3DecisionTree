

//import ml.estimators.tree.NodeInfo;

public interface BaseNode {	
	double getInformationGain();
	int getSplittingFeature();
	double getSplitingValue();
}
