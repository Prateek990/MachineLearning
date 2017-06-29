package buildDecisionTree;

import java.util.*;
import java.io.*;

// Tree class
class Node{

	Node l;
	Node r;
	double entropy;
	double gain;
	boolean visited;
	HashMap<Integer,String> attrib;
	HashSet<Integer> indexes;
	String targetAttrib;
	int zeroCount;
	int oneCount;
	int cValue;
	String clf;

	// Class Constructor
	// Initializing class member
	Node(Node node){
	this.entropy = node.entropy;
	this.gain = node.gain;
	this.visited = node.visited;
	this.attrib = new HashMap<Integer,String>(node.attrib);
	this.indexes = new HashSet<Integer>(node.indexes);
	this.targetAttrib = node.targetAttrib;
	this.zeroCount = node.zeroCount;
	this.oneCount = node.oneCount;
	this.cValue = node.cValue;
	this.clf = node.clf;

	}

	Node(){

		cValue = -1;
		entropy = 0;
		gain = 0;
		l = null;
		visited = false;
		r = null;
		zeroCount = 0;
		oneCount = 0;
		clf = "";
		attrib = new HashMap<Integer,String>();
		indexes = new HashSet<Integer>();
		targetAttrib = "";

	}

	void setVisited(boolean val){

		visited=val;
	}

	void setIndex(HashSet<Integer> index){

		indexes=new HashSet<Integer>(index);
	}

	HashSet<Integer> getIndexes(){

		return indexes;
	}

	void setAttrib(HashMap<Integer,String> name){

		attrib.putAll(name);
	}

	HashMap<Integer,String> getAttrib(){

		return attrib;
	}

	String getAttribValue(int key){

		return attrib.get(key);
	}

	boolean isAttribEmpty(){

		if((attrib.size()-1)>0){
			return false;
		}
		else{
			return true;
		}
	}

	void setcValue(int value){

		cValue = value;
	}

	int getcValue(){

		return cValue;
	}

	String getClf(){

		return clf;
	}

	String getTargetAttrib(){

		return targetAttrib;
	}

	void setTargetAttrib(String target){

		targetAttrib = target;
	}
	
	void setClf(String attr){

		clf = attr;
	}

	int getClassCount(int value){

		if(value == 0)
			return zeroCount; 
		else
			return oneCount;
		
	}

	void removeAttrib(int a){

		attrib.remove(a);
	}

	void setClassRootCount(HashMap<Integer,HashMap<String,Integer>> r){

		zeroCount = oneCount = 0;

		Iterator itr = r.entrySet().iterator();

		HashMap<String,Integer> innerMap;
	
		while(itr.hasNext()){

			Map.Entry pair = (Map.Entry)itr.next();			

			innerMap=new HashMap<String,Integer>();

			int rowNum = (Integer)pair.getKey();

			innerMap = (HashMap<String,Integer>)pair.getValue();

			indexes.add(rowNum);
		
			if(innerMap.get(getTargetAttrib()) == 0){

				zeroCount++;
			}
			else{

				oneCount++;
			}

		}

	}


	void setIandC(HashMap<Integer,HashMap<String,Integer>> r,String attr, int value){

		zeroCount=oneCount=0;
		Iterator itr=getIndexes().iterator();
		HashSet<Integer> newSet=new HashSet<Integer>();
		HashMap<String,Integer> tempHash;

		while(itr.hasNext()){

			int rowNum=(Integer)itr.next();
			tempHash=new HashMap<String,Integer>(r.get(rowNum));

			if(tempHash.get(attr)==value){
				newSet.add(rowNum);
				if(tempHash.get(getTargetAttrib())==0) {
					zeroCount++;
				}
				else{
					oneCount++;
				}
			}
		}

		indexes.clear();
		indexes=new HashSet<Integer>(newSet);
	}

	void calculateEntropy(){

		int total=(zeroCount+oneCount);
		
		if(total==0){
			entropy=0;
		}
		else{
			double prob0=(double)zeroCount/total;
			double prob1=(double)oneCount/total;
			double product0=(prob0*(log(prob0,2))*-1);
			double product1=(prob1*(log(prob1,2))*-1);

			entropy=(double)product0+product1;
		}

	}

	static double log(double x, int base){
		if(x!=0)
    		return (Math.log(x) / Math.log(base));
    	else
    		return 0;
	}

	void calculateGain(){

		if(l == null && r == null){

 			gain=1.0;

		}	
		else{

			double entropyLeft;
			double entropyRight;

			l.calculateEntropy();
			entropyLeft = l.getEntropy();

			r.calculateEntropy();
			entropyRight = r.getEntropy();

			int sumLeft = l.getClassCount(0) + l.getClassCount(1);
			int sumRight = r.getClassCount(0) + r.getClassCount(1);

			calculateEntropy();

			int total = zeroCount + oneCount;
			double probLeft = (double)sumLeft/total;
			double probRight = (double)sumRight/total;
			double p1 = (double)probLeft* entropyLeft;
			double p2= (double)probRight* entropyRight;
			gain=(double)entropy-( p1 + p2);

		}
	}

	Node getLNode(){
		
		return l;
	}

	void setLNode(Node node){

		l = node;
	}

	void setRNode(Node node){

		r = node;
	}
	
	Node getRNode(){

		return r;
	}

	void setEntropy(double value){

		entropy = value;
	}
	
	double getEntropy(){

		return entropy;
	}

	void setGain(double value){

		gain=value;
	}

	double getGain(){

		return gain;
	}
}

// Decision Tree class

class DTreeId3{

	Node root;	
	LinkedList<Node> nList;
	int size;

	DTreeId3(){
		
		root = null;
		size = 0;
	}

	DTreeId3(DTreeId3 orig){

		root = new Node(orig.getRoot());
		root.setLNode(null);
		root.setRNode(null);

		nList = new LinkedList<Node>(orig.nList);
		copyNode(orig.getRoot(),root);
	}

	void copyNode(Node origNode, Node currNode){

		if(origNode == null || currNode == null){
			return;
		}

		if(origNode.getLNode() != null){

			Node newNode=new Node(origNode.getLNode());	
			currNode.setLNode(newNode);
		}

		if(origNode.getRNode() != null){

			Node newNode=new Node(origNode.getRNode());	
			currNode.setRNode(newNode);
		}

		copyNode(origNode.getLNode(),currNode.getLNode());
		copyNode(origNode.getRNode(),currNode.getRNode());
		
	}

	DTreeId3(instanceDB trainSet){
	
	nList = new LinkedList<Node>();
	root = null;
	size = 0;

	root = new Node();

	root.setAttrib(trainSet.getAttribNames());
	HashMap<Integer,String> tempHash=root.getAttrib();
	root.setTargetAttrib(tempHash.get(tempHash.size()-1));
	
	root.setClassRootCount(trainSet.getRecords());

	root.calculateEntropy();

	if(trainSet.getSize() == root.getClassCount(1)){
		root.setClf("Root");
		root.setcValue(1);
		return;
	}

	if(trainSet.getSize() == root.getClassCount(0)){
		root.setClf("Root");
		root.setcValue(0);
		return;
	}

	if(root.isAttribEmpty()){
		
		if(root.getClassCount(0)>root.getClassCount(1)){

			root.setClf("Root");
			root.setcValue(0);
		}
		else{

			root.setClf("Root");
			root.setcValue(1);
		}
		return;
	}

	root.setLNode(null);
	root.setRNode(null);

	createTree( trainSet, root );
	}

	void printTree(Node node, int level){

		if(node==null){
			return;
		}

		level++;

		if(node.getLNode() != null){
			
			Node left = node.getLNode();
			int i = 0;
			while(i<level){

				System.out.print("| ");
				i++;
			}

			if(left.getcValue() == -1){
				
				System.out.println(node.getClf()+" = 0 :");
			}
			else{

				System.out.println(node.getClf()+" = 0 : "+left.getcValue());
			}
				printTree(left,level);
		}

		if(node.getRNode() != null){
			
			Node right=node.getRNode();
			int i=0;

			while(i<level){

				System.out.print("| ");
				i++;
			}

			if(right.getcValue() == -1){
				
				System.out.println(node.getClf()+" = 1 :");
			}
			else{

				System.out.println(node.getClf()+" = 1 : "+right.getcValue());
			}

			printTree(right, level);
		}
	}

	void createTree(instanceDB trainSet, Node node){
	
		if(node == null){
			return;
		}

		if(node.getClassCount(0) == (node.getClassCount(0) + node.getClassCount(1))){
				node.setcValue(0);
				return;
		}

		if(node.getClassCount(1) == (node.getClassCount(0) + node.getClassCount(1))){
			node.setcValue(1);
			return;
		}

		if(node.isAttribEmpty()){
			
			if(node.getClassCount(0)>node.getClassCount(1)){
				node.setcValue(0);
			}
			else{
				node.setcValue(1);	
			}
			
			return;
		}

		int attrVal=bestAttrib(trainSet,node);
		node.setClf(node.getAttribValue(attrVal));
		node.calculateEntropy();

		HashMap<Integer,String> tempHashNode=new HashMap<Integer,String>();
		tempHashNode.putAll(node.getAttrib());

		Node left=new Node();
		left.setAttrib(tempHashNode);
		left.setTargetAttrib(node.getTargetAttrib());
		left.setIndex(node.getIndexes());
		left.setIandC(trainSet.getRecords(), left.getAttribValue(attrVal), 0);
		left.calculateEntropy();
		left.removeAttrib(attrVal);

		Node right=new Node();
		right.setAttrib(tempHashNode);
		right.setTargetAttrib(node.getTargetAttrib());
		right.setIndex(node.getIndexes());
		right.setIandC(trainSet.getRecords(), right.getAttribValue(attrVal), 1);
		right.calculateEntropy();
		right.removeAttrib(attrVal);
		
		if(left.getIndexes().size()>0){
			node.setLNode(left);
			createTree(trainSet,left);
		}
	
		if(right.getIndexes().size()>0){
			node.setRNode(right);
			createTree(trainSet,right);
		}
	}

	int bestAttrib(instanceDB instance, Node node){

		double max = Double.NEGATIVE_INFINITY;
		int maxIndex = -1, key = -1;
		String temp = "";

		Iterator itr = sort(node.getAttrib()).entrySet().iterator();

		while(itr.hasNext()){
			Map.Entry pair = (Map.Entry)itr.next();
			key = (Integer)pair.getKey();
			String value = (String)pair.getValue();

			if(node.getTargetAttrib() != value){
				double k = buildAndCalcGain(instance,value,node);
				if(k > max){
					max = k;
					maxIndex = key;
					temp = value;
				}
			}
		}

	node.setLNode(null);
	node.setRNode(null);
	node.setGain(max);

	return maxIndex;
} 

	double buildAndCalcGain(instanceDB instance, String attrVal,Node node){

		Node left = new Node();		
		left.setTargetAttrib(node.getTargetAttrib());
		left.setIndex(node.getIndexes());
		left.setIandC(instance.getRecords(), attrVal,0);
		left.calculateEntropy();

		Node right = new Node();		
		right.setTargetAttrib(node.getTargetAttrib());
		right.setIndex(node.getIndexes());
		right.setIandC(instance.getRecords(), attrVal,1);
		right.calculateEntropy();
		node.setLNode(left);
		node.setRNode(right);
		node.calculateGain();

		return node.getGain();
	}


	int getSize(){
		return size;
	}

	Node getRoot(){
		return root;
	}

public static void main(String[] args) throws IOException{

	int L = 0, K = 0;
	String print = "";
	String trainingSet = "";
	String validationSet = "";
	String testingSet = "";

	if(args.length >= 6){
	
		if(args[0].length()>0 && args[1].length()>0 && args[2].length()>0 && args[3].length()>0 && args[4].length()>0 && args[5].length()>0)
		{
			L = Integer.parseInt(args[0]);
			K = Integer.parseInt(args[1]);
			trainingSet = args[2];
			validationSet = args[3];
			testingSet = args[4];
			print = args[5];
		}
		else{
			System.out.println("Input arguments order:");
			System.out.println("<L> <K> <training-set> <validation-set> <test-set> <to-print>");
			System.out.println("L: integer (used in the post-pruning algorithm)");
			System.out.println("K: integer (used in the post-pruning algorithm)");
			System.out.println("to-print:{yes,no}");
			return;
		}
	}
	else{
		System.out.println("Input arguments order:");
		System.out.println("<L> <K> <training-set> <validation-set> <test-set> <to-print>");
		System.out.println("L: integer (used in the post-pruning algorithm)");
		System.out.println("K: integer (used in the post-pruning algorithm)");
		System.out.println("to-print:{yes,no}");
		return;
	}

	instanceDB trainSet = new instanceDB(trainingSet);
	DTreeId3 tree = new DTreeId3(trainSet);
	instanceDB vSet = new instanceDB(validationSet);
	instanceDB testSet = new instanceDB(testingSet);
	System.out.println("Decision Tree constructed successfully using Training set<"+trainingSet+">");	

	double accuracy = (double) Math.round(tree.calculateAccuracy(testSet)*10000)/100;
	System.out.println("Accuracy of decision tree before pruning = "+accuracy+"%");
	DTreeId3 copyTree = new DTreeId3(tree);
	DTreeId3 prunedTree = tree.prune(L,K,vSet);

	accuracy = (double) Math.round(prunedTree.calculateAccuracy(testSet)*10000)/100;
	System.out.println("Accuracy of pruned decision tree = "+accuracy+"%");
	if(print.equals("yes")){
		prunedTree.printTree(prunedTree.getRoot(),-1);
	}
	}

	double calculateAccuracy(instanceDB instances){

		Iterator itr = instances.getRecords().entrySet().iterator();
		HashMap<String,Integer> tempHash;
		int correctCounter = 0, total = 0;

		while(itr.hasNext()){

			Map.Entry entry = (Map.Entry) itr.next();
			tempHash = new HashMap<String,Integer>((HashMap<String,Integer>)entry.getValue());
			int predictedVal = getPredictedValue(root,tempHash);
			int actualVal = tempHash.get(root.getTargetAttrib());

			if(predictedVal == actualVal){

				correctCounter++;
			}

			total++;
		}

		return (double)correctCounter/total;
	}


	int getPredictedValue(Node node, HashMap<String,Integer> tempHash){

			if(node == null){

				return -1;
			}

			if(node.getLNode() == null && node.getRNode() == null){

				return node.getcValue();
			}

			if(tempHash.get(node.getClf()) == 0){

				if(node.getLNode() != null){

					return getPredictedValue(node.getLNode(), tempHash);
				}
				
			}

			if(tempHash.get(node.getClf()) == 1){

				if(node.getRNode() != null){

					return getPredictedValue(node.getRNode(), tempHash);
				}	
			}

			return -1;
	}

void initList(){

	nList=new LinkedList<Node>();

}


DTreeId3 prune(int L, int K, instanceDB instances){

	DTreeId3 bestTree = new DTreeId3(this);

	double bestAccuracy = bestTree.calculateAccuracy(instances);

	double accuracy = 0;

	for(int i=1;i<=L;i++){

		DTreeId3 currentTree = new DTreeId3(this);

		Random randomNum = new Random();
		int M = randomNum.nextInt(K)+1;

		for(int j = 1; j <= M; j++){

			LinkedList<Node> list = new LinkedList<Node>();
			list.add(currentTree.getRoot());
			currentTree.initList();
			currentTree.levelOrder(list);

			int N = currentTree.nList.size();
			Random randomNum2 = new Random();
			int P = randomNum2.nextInt(N);

			if(P == 0){
				continue;
			}

			if(!currentTree.nList.get(P).visited){

				Node node = currentTree.nList.get(P);
				Node newNode = new Node();
				node.setVisited(true);
				node.setLNode(null);
				node.setRNode(null);
				node.setLNode(newNode);

				if(node.getClassCount(0) > node.getClassCount(1)){
					newNode.setcValue(0);
				}
				else{
					newNode.setcValue(1);
				}
			}
			else{
				continue;
			}
		}

		accuracy = currentTree.calculateAccuracy(instances);

		if(accuracy > bestAccuracy){
			bestAccuracy = accuracy;
			bestTree = new DTreeId3(currentTree);
		}
	}

return bestTree;
}

void displayList(){

	for(int i=0; i < nList.size(); i++){

		if(i == nList.size()-1)
			System.out.print(nList.get(i).getClassCount(0)+":"+nList.get(i).getClassCount(1));
		else	
			System.out.print(nList.get(i).getClassCount(0)+":"+nList.get(i).getClassCount(1)+"->");
	}
	System.out.println(" ");
}

private static HashMap sort(HashMap map) { 
       List list = new LinkedList(map.entrySet());
       Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
               return ((Comparable) ((Map.Entry) (o1)).getValue())
                  .compareTo(((Map.Entry) (o2)).getValue());
            }
       });

       HashMap sortedHashMap = new LinkedHashMap();
       for (Iterator it = list.iterator(); it.hasNext();) {
              Map.Entry entry = (Map.Entry) it.next();
              sortedHashMap.put(entry.getKey(), entry.getValue());
       } 
       return sortedHashMap;
  }

void levelOrder(LinkedList<Node> list){

		if(list.size() == 0){
			return;
		}
		LinkedList<Node> newList = new LinkedList<Node>();
		for(int i = 0; i<list.size(); i++){
			if(list.get(i).getLNode() == null && list.get(i).getRNode() == null){
					continue;
			}
			else{
				if(list.get(i).getLNode() != null){
					newList.addLast(list.get(i).getLNode());
				}
				if(list.get(i).getRNode() != null){
					newList.addLast(list.get(i).getRNode());
				}

				nList.addLast(list.get(i));
			}
		}
		levelOrder(newList);
	}
}

class instanceDB{
	
	HashMap<Integer,String> attributeNames;
	int size;
	HashMap<Integer,HashMap<String,Integer>> records;

	instanceDB(){
		attributeNames = new HashMap<Integer,String>();
		records = new HashMap<Integer,HashMap<String,Integer>>();		
		size = 0;
	}

	HashMap<Integer,HashMap<String,Integer>> getRecords(){

		return records;
	}

	HashMap<Integer,String> getAttribNames(){
		return attributeNames;
	}

	int getSize(){
		return size;
	}

	instanceDB(String fileLocation) throws IOException{

		BufferedReader reader=new BufferedReader(new FileReader(fileLocation));
		size = 0;
		String line = "";
		int i = 0;
		attributeNames = new HashMap<Integer,String>();
		records = new HashMap<Integer,HashMap<String,Integer>>();
		boolean first = true;

		while((line = reader.readLine()) != null && line.length() != 0){

			if(first){
				addAttribNames(line);
				first=false;
			}
			else{
				addToHashMap(line,i);
				size++;
				i++;
			}
		}
		reader.close();
	}

	void addAttribNames(String line){

		String[] array = splitCSVArray(line);
		for(int i = 0; i<array.length; i++){
			attributeNames.put(i,array[i]);
		}
	}

	void addToHashMap(String line,int row){

		HashMap<String,Integer> attribute;
		attribute = new HashMap<String,Integer>();

		for(int j=0, i=0; i<attributeNames.size(); j = j+2,i++){

			attribute.put(attributeNames.get(i),Character.getNumericValue(line.charAt(j)));

		}
		records.put(row,attribute);
	}

	String[] splitCSVArray(String line){
		return line.split(",");
	}
}