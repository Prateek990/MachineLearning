import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;


public class LR_MainThread {

	public static HashMap<String,HashMap<String, Integer> >spamFileMap = new HashMap<String, HashMap<String,Integer>>();	
	public static HashMap<String,HashMap<String, Integer> >hamFileMap = new HashMap<String, HashMap<String,Integer>>();

	public static Set<String> distinctV = new HashSet<String>();
	public static HashMap<String, Integer> spamMap = new HashMap<String, Integer>();
	public static HashMap<String, Integer> hamMap = new HashMap<String, Integer>();


	static Set<String> hamfSet = new HashSet<String>();
	static Set<String> spamfSet = new HashSet<String>();
	static Set<String> fileSet = new HashSet<String>();
	public static Set<String> sWordList = new HashSet<String>();

	public static void main(String[] args) throws Exception {

		String path = args[0];
		String filter = args[1];
		double lRate = Double.parseDouble(args[2]);
		double lambda = Double.parseDouble(args[3]);
		int numOfIter = Integer.parseInt(args[4]);
		File spamTestPath = new File(path+"/hw2_test/test/spam");
		File hamTestPath = new File(path+"/hw2_test/test/ham");  
		File spamTrainPath = new File(path+"/hw2_train/train/spam");
		File hamTrainPath = new File(path+"/hw2_train/train/ham"); 

		File stopWord = new File(path+"/stopword.txt");

		setVocabulary(spamTrainPath);
		setVocabulary(hamTrainPath);

		Scanner scn=null;
		try {
			scn = new Scanner(stopWord);
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
		while(scn.hasNext()){
			String sw = scn.next();
			sWordList.add(sw);
		}
		scn.close();

		if(filter.equals("yes")){

			for(String s : sWordList){
				if(distinctV.contains(s)){
					distinctV.remove(s);
				}
			}
		}


		computeHashmap(spamTrainPath, false);
		computeHashmap(hamTrainPath, true);

		LR_class lr = new LR_class(path, distinctV,hamFileMap,spamFileMap,hamMap,spamMap, lRate, lambda, numOfIter,fileSet,spamfSet,hamfSet);

		lr.trainAlgo();
		int spamCount = 0 ;
		int numOfFile = 0;

		for(File testfile : spamTestPath.listFiles()){
			numOfFile = numOfFile + 1;
			HashMap<String, Integer> tMap = new HashMap<String, Integer>();
			Scanner scn1 = new Scanner(testfile);
			while(scn1.hasNext()){
				String line = scn1.nextLine();
				for(String s: line.toLowerCase().trim().split(" ")){
					s = s.replaceAll("[^a-zA-Z]+", "");
					if(tMap.containsKey(s)){
						tMap.put(s, tMap.get(s)+1);
					}else{
						tMap.put(s, 1);
					}
				}	
			}
			scn1.close();
			
			if(filter.equals("yes")){
				for(String stopword: sWordList){
					stopword = stopword.replaceAll("[^a-zA-Z]+", "");
					if(tMap.containsKey(stopword)){
						tMap.remove(stopword);
					}
				}
			}
			
			int spam = lr.testAlgo(tMap);
			if(spam == 1){
				spamCount++;
			}

		}

		double sAccuracy = ( (double)spamCount / (double)numOfFile)*100;

		System.out.println("Accuracy  of spam mail: "+ (sAccuracy));

		int hamCount = 0 ;
		int numOfHamFiles = hamTestPath.listFiles().length;

		for(File testfile : hamTestPath.listFiles()){
			HashMap<String, Integer> tMap = new HashMap<String, Integer>();
			Scanner sc = new Scanner(testfile);
			while(sc.hasNext()){
				String line = sc.nextLine();
				for(String s: line.toLowerCase().trim().split(" ")){
					s = s.replaceAll("[^a-zA-Z]+", "");

					if(tMap.containsKey(s)){
						tMap.put(s, tMap.get(s)+1);
					}else{
						tMap.put(s, 1);
					}
				}	
			}
			sc.close();
			int ham = lr.testAlgo(tMap);
			if(ham == 0){
				hamCount++;

			}
		}

		double hAccuracy = ( (double)hamCount / (double)numOfHamFiles)*100;
		System.out.println("Accuracy  of Ham mail: "+ hAccuracy);
	}
	
	private static void setVocabulary(File path) throws Exception {

		for(File file: path.listFiles()){

			Scanner scn = new Scanner(file);
			while(scn.hasNext()){
				String nxtLine = scn.nextLine();
				for(String str : nxtLine.toLowerCase().trim().split(" ")){
					str = str.replaceAll("[^a-zA-Z]+", "");
					if(!str.isEmpty()){
						distinctV.add(str);
					}
				}
			}
			scn.close();
		}
	}

	private static void computeHashmap(File dir, boolean flag) throws Exception {
		if(flag == false)
			{
				for(File file: dir.listFiles()){
				HashMap<String, Integer> spamVocab = new HashMap<String, Integer>();
	
				spamfSet.add(file.getName());
				fileSet.add(file.getName());
				Scanner scn = new Scanner(file);
				while(scn.hasNext()){
					String nxtLine = scn.nextLine();
	
					for(String str: nxtLine.toLowerCase().trim().split(" ")){
						str = str.replaceAll("[^a-zA-Z]+", "");
						if(distinctV.contains(str)){
	
							if(spamMap.containsKey(str)){
								spamMap.put(str, spamMap.get(str)+1);
							}else{
								spamMap.put(str, 1);
							}
	
							if(spamVocab.containsKey(str)){
								spamVocab.put(str, spamVocab.get(str)+1);
							}
							else{
								spamVocab.put(str, 1);
							}
						}
	
						spamFileMap.put(file.getName(), spamVocab);
					}
				}
				scn.close();
			}
		}
		else
		{
			for(File file: dir.listFiles()){
				HashMap<String, Integer> hamVocab = new HashMap<String, Integer>();
				hamfSet.add(file.getName());
				fileSet.add(file.getName());

				Scanner scn = new Scanner(file);
				while(scn.hasNext()){
					String nxtLine = scn.nextLine();
					for(String str: nxtLine.toLowerCase().trim().split(" ")){
						str = str.replaceAll("[^a-zA-Z]+", "");
						if(!str.isEmpty()){

							if(distinctV.contains(str)){
								if(hamMap.containsKey(str)){
									hamMap.put(str, hamMap.get(str)+1);
								}else{
									hamMap.put(str, 1);
								}

							}	
						}	

						if(!str.isEmpty()){
							if(distinctV.contains(str)){
								if(hamVocab.containsKey(str)){
									hamVocab.put(str, hamVocab.get(str)+1);
								}else{
									hamVocab.put(str, 1);
								}
							}
						}
						hamFileMap.put(file.getName(), hamVocab);
					}
				}
				scn.close();
			}
		}

	}
}
