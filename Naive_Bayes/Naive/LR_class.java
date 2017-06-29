import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;


public class LR_class {

	static HashMap<String, HashMap<String, Integer>> hamMap = new HashMap<String, HashMap<String,Integer>>();
	static HashMap<String,HashMap<String,Integer>> spamMap = new HashMap<String, HashMap<String,Integer>>();
	static HashMap<String, Integer> hamWordMap = new HashMap<String, Integer>();
	static HashMap<String, Integer> spamWordMap = new HashMap<String, Integer>();
	HashMap<String, Double>sumWeights = new HashMap<String, Double>();

	double learningRate = 0.0 ;
	double in_lambda = 0; 
	int iterCount =0;
	String dir = new String();
	static Set<String> vocab = new HashSet<String>();
	static double weight0 = 0.1;
	static Set<String> hamSet = new HashSet<String>();
	static Set<String> spamSet = new HashSet<String>();
	static Set<String> set = new HashSet<String>();

	static HashMap<String, Double> w_wordMap = new HashMap<String, Double>();
	static HashMap<String, Double> nwWordMap = new HashMap<String, Double>();

	public  LR_class(String location, Set<String> d_vocab,
			HashMap<String, HashMap<String, Integer>> hMap,
			HashMap<String, HashMap<String, Integer>> sMap,
			HashMap<String, Integer> hWordMap,
			HashMap<String, Integer> sWordMap, double lr, double lambda, int numiter, Set<String> fileSet, Set<String> spamFile, Set<String> hamFile){
		
		dir = location;
		
		vocab = d_vocab;
		hamMap = hMap;
		spamMap = sMap;
		
		hamWordMap = hWordMap;
		spamWordMap = sWordMap;
		
		learningRate = lr;
		in_lambda = lambda; 
		iterCount = numiter;
	
		set = fileSet;
		spamSet = spamFile;
		hamSet = hamFile;


	}

	public void trainAlgo() {
		//Assign weights to all the words in the distinct vocabulary set

		int c = 0;
		for(String s:vocab){
			double r =  (Math.random() * (1 -(-1))) + (-1);
			w_wordMap.put(s, r); // assign random weights
		}

		for(int i =0 ; i<1; i++){

			for(String cWord : vocab){
				double temp = 0;

				for(String fName : set){
					double oClass;
					int count_currentword = getCountOfword(fName, cWord);
					if(spamSet.contains(fName)){
						oClass = 1; //spam
					}
					else{
						oClass = 0;//ham
					}
					double sigma = computeWeight(fName);
					double error = (oClass - sigma);
					temp = temp + count_currentword*error;
				}
				
				double new_weight_forWord = w_wordMap.get(cWord) + learningRate*temp -(learningRate*in_lambda*w_wordMap.get(cWord));
				w_wordMap.put(cWord, new_weight_forWord);
			}
		}
	
	}

	public int testAlgo(HashMap<String, Integer> map) {
		double sum = 0;
		for(Entry<String, Integer> entrySet :map.entrySet()){
			if(w_wordMap.containsKey(entrySet.getKey())){
				sum = sum + (w_wordMap.get(entrySet.getKey())* entrySet.getValue());
			}
		}
		sum = sum + weight0;
		if(sum >= 0){
			return 1;
		}
		else{
			return 0;
		}
	}
	
	private int getCountOfword(String name, String w) {
		int c = 0;
		if(hamSet.contains(name)){
			try {
				for(Entry<String, Integer> wCount1: hamMap.get(name).entrySet()){
					if(wCount1.getKey().equals(w)){
						c = wCount1.getValue();
						return c;
					}
				}
			} catch (Exception e) {
				
				e.printStackTrace();
			}
		}
		else if(spamSet.contains(name)){

			try {
				for(Entry<String, Integer> wordcount: spamMap.get(name).entrySet()){
					if(wordcount.getKey().equals(w)){
						c = wordcount.getValue();
						return c;
					}
				}
			} catch (Exception e) {
				
				e.printStackTrace();
			}
		}
		return 0;
	}

	
	
	private double computeWeight(String filename) {

		if(hamSet.contains(filename)){
			double wSum = weight0;
			try{
				for(Entry<String, Integer> values_map: hamMap.get(filename).entrySet()){
					wSum = wSum + w_wordMap.get( values_map.getKey() )  * values_map.getValue();
				}	
			}
			catch(Exception E){
				System.out.println( E);
				
			}
			return (ComputeSigmod(wSum) );
		}

		else{
			double wSum1 = weight0;
			try{
				for(Entry<String, Integer> values_map1: spamMap.get(filename).entrySet()){
					wSum1 = wSum1 + w_wordMap.get( values_map1.getKey() )  * values_map1.getValue();
				}	
			}
			catch(Exception e){
				System.out.println("..");
			}

			return (ComputeSigmod(wSum1) );
		}


	}

	private double ComputeSigmod(double wSum) {
		if(wSum>100){
			return 1.0;
		}
		else if(wSum<-100){

			return 0.0;
		}
		else{
			return (1.0 /(1.0+ Math.exp(-wSum)));
		}
	}

}
