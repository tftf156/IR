import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;


public class Main {
	
	static String filePath = "../../file/08.warc";
	static String indexPath = "../../index";
	static Analyzer analyzer = new StandardAnalyzer();
	
	public static void main(String args[])throws IOException 
	{ 
		LuceneSearch luceneSearch = new LuceneSearch(indexPath, analyzer);
		LuceneIndex luceneIndex = new LuceneIndex(filePath, indexPath, analyzer);
		String keyword = new String();
		
		try {
			Date start = new Date();
			luceneIndex.createIndex();
			Date end = new Date(); 
		    System.out.println("�إߧ����A�إ߯��ޥή�"+(end.getTime()-start.getTime())+"�@�W");
		    BufferedReader br = new BufferedReader (new InputStreamReader(System.in));
		    System.out.print("�п�J�n�j�M���r:");
		    System.out.flush(); 
		    keyword=br.readLine();		    		
			luceneSearch.search(keyword);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
	} 
}
