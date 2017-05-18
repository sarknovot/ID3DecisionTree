# ID3DecisionTree
ID3 decision tree written to practice Java, namely Collections framework and basics of JUnit. Also, for first time I encoutered Maven and logging.

The API of decision tree is created by class ID3DecisionTree<T extends Comparable<T>>, namely by methods:
- ID3DecisionTree (int depth) - constructor sets maximum depth of tree
- void fit(Map<String, LinkedList<T>> data, LinkedList<T> output) - builds a decision tree based on training data, by which feature the data will be split is decided on the basis of information gain, only binary output is accepted
- List<T> predict(Map<String, LinkedList<T>> newData, List<T> resultOfPrediction) - classifies newData, returns result in resultOfPrediction

To read data from csv file, class ReadCSV can be used. The class makes use of org.apache.commons.csv. Public methods are:
- ReadCSV(String filename) - filename is name of csv file, name of features are expected in the first row of the file
- Map<String, LinkedList<String>> getParsedFile() - returns read data
