
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NB {
	
	Set<String> vHam = new HashSet<>();
    Set<String> vSpam = new HashSet<>();
    Set<String> vHamTestSet = new HashSet<>();
    Set<String> vSpamTestSet = new HashSet<>();
    Set<String> vSet = new HashSet<>();
    
    HashMap hamMap = new HashMap();
    HashMap spamMap = new HashMap();
    HashMap hamMapTest = new HashMap();
    HashMap spamMapTest = new HashMap();
    HashMap pOfHam = new HashMap();
    HashMap pOfSpam = new HashMap();
    
    List<String> filterStr = new ArrayList<>();	
    String filePath;
    
    double pHam = 0.73;
    double pSpam = 1 - pHam;
    double pHamDefault = 1, pSpamDefault = 1;
    
    int countHam =0, countSpam = 0;
    
    void read(String fPath) throws IOException{
        filePath = fPath;
        File f = new File(fPath);
        FilenameFilter filter;
    
        filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".txt");
            }
        };

        File[] files = f.listFiles(filter);
        for (File file : files) {
            filterWords(file.getPath());
        }
    }

    private void filterWords(String path) throws FileNotFoundException, IOException {
       
    	for(String line : Files.readAllLines(Paths.get(path) , StandardCharsets.ISO_8859_1)){		
            for(String str : line.split(" ")){
                Pattern p = Pattern.compile("[^A-Za-z0-9]");
                Matcher match = p.matcher(str);
                boolean flag = match.find();
                if (flag == false){
                    if(filePath.contains("train"))
                        filterStr.add(str);
                    else {
                        filterStr.add(str);
                    }      
                }
            }
      }
      
    if(filePath.contains("train")){
    	generateVocabList(filterStr);
    	if(filePath.contains("ham"))
    	  countWords(vHam, "ham", filterStr);
    	else
    	  countWords(vSpam, "spam", filterStr);
      }
      else {
    	generateVocabList(filterStr);
        if(filePath.contains("ham"))
          countWordsInTest(vHamTestSet, "ham", filterStr);
        else
          countWordsInTest(vSpamTestSet, "spam", filterStr);
        vHamTestSet.clear();
        vSpamTestSet.clear();
        filterStr.clear();
      }
    }

    private void generateVocabList(List<String> filterStr) {
        
    	Iterator itr = filterStr.listIterator();
        
        if(filePath.contains("train")){
            while(itr.hasNext()){
                if(filePath.contains("ham"))
                    vHam.add((String) itr.next());
                else
                    vSpam.add((String) itr.next());
            }
        }
        else {
            while(itr.hasNext()){
                if(filePath.contains("ham"))
                    vHamTestSet.add((String) itr.next());
                else
                    vSpamTestSet.add((String) itr.next());
            }
        }
    }
    
    private void totalVocab() {
        vSet.addAll(vHam);
        vSet.addAll(vSpam);        
    }

    void countWords(Set<String> voc, String type, List<String> filterStr) {
        
    	Iterator i = voc.iterator();
        
    	if(type.equals("ham")){
	        while(i.hasNext()){
	            hamMap.put((String) i.next(), 0);
	        }
	        
	        Iterator i1 = filterStr.listIterator();
	        while(i1.hasNext()){
	            String key = (String) i1.next();
	            int v =  (int) hamMap.get(key);
	            hamMap.put(key, v+1);           
	            
	        }
        }
        
        if(type.equals("spam")){
	        while(i.hasNext()){
	            spamMap.put((String) i.next(), 0);
	        }
	        Iterator i1 = filterStr.listIterator();
	        while(i1.hasNext()){
	            String key = (String) i1.next();
	            int v = (int) spamMap.get(key);
	            spamMap.put(key, v+1);
	        }
        }
    }
    
    void countWordsInTest(Set<String> vocab, String mailCat, List<String> strList) {
        
    	Iterator i = vocab.iterator();
        hamMapTest.clear();
        spamMapTest.clear();
        
        if(mailCat.equals("ham")){
	        while(i.hasNext()){
	            hamMapTest.put((String) i.next(), 0);
	        }
	        Iterator i1 = strList.listIterator();
	        while(i1.hasNext()){
	            String key = (String) i1.next();
	            int v =  (int) hamMapTest.get(key);
	            hamMapTest.put(key, v+1);           
	            
	        }
	        
	        if(!(hamMapTest.containsKey("Subject")))
	            hamMapTest.put("Subject", 1);
	        else {
	            int v = (int) hamMapTest.get("Subject");
	            hamMapTest.put("Subject", v+1);
	        }
        }
        
        if(mailCat.equals("spam")){
	        while(i.hasNext()){
	            spamMapTest.put((String) i.next(), 0);
	        }
	        Iterator i1 = strList.listIterator();
	        while(i1.hasNext()){
	            String key = (String) i1.next();
	            int v = (int) spamMapTest.get(key);
	            spamMapTest.put(key, v+1);
	        }
	        if(!(spamMapTest.containsKey("Subject")))
	            spamMapTest.put("Subject", 1);
	        else {
	            int v = (int) spamMapTest.get("Subject");
	            spamMapTest.put("Subject", v+1);
	        }
        }
        
        double probHam = 0.0, probSpam = 0.0;
        
        if(mailCat.equals("ham")){
            probHam = Math.log10(pHam) + searchHamSpam(hamMapTest, "ham");
            probSpam = Math.log10(pSpam) + searchHamSpam(hamMapTest, "spam");
            if(probHam > probSpam)
                countHam++;     
            }
        else {
            probHam = Math.log10(pHam) + searchHamSpam(spamMapTest, "ham");
            probSpam = Math.log10(pSpam) + searchHamSpam(spamMapTest, "spam");
            if(probHam < probSpam)
                countSpam++;
        }
        
    }

    private void displayMaps() {
    	
        int v = (int) hamMap.get("subject");
        hamMap.put("subject", v + 340);
        int v1 = (int) spamMap.get("subject");
        spamMap.put("subject", v1 + 123);
    }
    
    public void calcNaiveBayes(){
	    double denominator2 = vSet.size(), denominator1 = 0;
	    
	    Set entrySet = hamMap.entrySet();
	    Iterator i = entrySet.iterator();
	    while(i.hasNext()){
	        Map.Entry m = (Map.Entry) i.next();
	        denominator1 = denominator1 + (int) m.getValue();
	    }
	    double denominator = denominator1 + denominator2;
	    pHamDefault = 1 / denominator;
	    pOfHam.putAll(hamMap);
	    pOfHam.replaceAll((k , v) -> ((((int) v) + 1) / denominator));
	    
	    denominator1 = 0;
	    Set set1 = spamMap.entrySet();
	    Iterator i2 = set1.iterator();
	    while(i2.hasNext()){
	        Map.Entry m1 = (Map.Entry) i2.next();
	        denominator1 = denominator1 + (int) m1.getValue();
	    }
	    
	    double sum = denominator1 + denominator2;
	    pSpamDefault = 1 / sum;
	    pOfSpam.putAll(spamMap);
	    pOfSpam.replaceAll((k , v) -> ((((int) v) + 1) / sum));
}

    void predict(String hPath, String sPath) throws IOException{
    filterStr.clear();
    read(hPath);
    read(sPath);
    System.out.println("Number of Ham mails correctly identified = " + countHam);
    System.out.println("Number of Spam mails correctly identified = " + countSpam);
    double hAccuracy = ((double)(countHam)/348)*100;
    double sAccuracy = ((double)(countSpam)/130)*100;
    double Accuracy = ((double)(countHam + countSpam)/478)*100;
    System.out.println("Ham accuracy = " + hAccuracy + "\nSpam accuracy = " + sAccuracy + "\nOverall Accuracy = " + Accuracy);
    
}

    private double searchHamSpam(HashMap test, String mailType) {
        double probability = 0;
        Set entrySet = test.entrySet();
        Iterator i = entrySet.iterator();
        if(mailType.equals("ham")){
            while(i.hasNext()){
                Map.Entry m = (Map.Entry) i.next();
                if(pOfHam.containsKey(m.getKey()))
                	probability = probability + (int)m.getValue() * Math.log10((double)pOfHam.get(m.getKey()));
                else
                	probability = probability + (int)m.getValue() * Math.log10(pHamDefault);
            }
        }
        else {
            while(i.hasNext()){
                Map.Entry m = (Map.Entry) i.next();
                if(pOfSpam.containsKey(m.getKey()))
                	probability = probability + (int)m.getValue() * Math.log10((double)pOfSpam.get(m.getKey()) );
                else
                	probability = probability + (int)m.getValue() * Math.log10(pSpamDefault);
            }
        }
        return probability;
    }
    
    public static void main(String[] args) throws IOException  {
    	String hamTrainPath = args[0];
    	String spamTrainPath = args[1];
    	String hamTestPath = args[2];
    	String spamTestPath = args[3];
        NB NaiveBayes = new NB();
 
        NaiveBayes.read(hamTrainPath);
        NaiveBayes.read(spamTrainPath);
        NaiveBayes.totalVocab();
        NaiveBayes.calcNaiveBayes();
        NaiveBayes.displayMaps();
        NaiveBayes.predict(hamTestPath, spamTestPath);
    }
}
