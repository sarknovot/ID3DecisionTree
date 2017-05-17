# ID3DecisionTree
ID3 decision tree written to practice Java, namely Collections framework and basics of JUnit. Also, for first time I encoutered Maven and logging.

The API of decision tree is created by class ID3DecisionTree<T extends Comparable<T>>, namely by methods:
- ID3DecisionTree (int depth) - constructor sets maximum depth of tree
- void fit(Map<String, LinkedList<T>> data, LinkedList<T> output) - based on training data build a decision tree, only binary output is accepted
- List<T> predict(Map<String, LinkedList<T>> newData, List<T> resultOfPrediction) - classifies newData, result returns in resultOfPrediction

To read data from csv file, class ReadCSV can be used. Public methods:
- ReadCSV(String filename) - filename is name of csv file, name of features are expected in the first row of the file
- Map<String, LinkedList<String>> getParsedFile() - returns read data