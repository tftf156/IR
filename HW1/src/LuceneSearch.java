
import java.io.Console;
import java.util.Date;
import java.util.Scanner;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;

public class LuceneSearch {
  private IndexSearcher searcher = null;
  private Query query = null;
  private Analyzer analyzer = new StandardAnalyzer();
  
  public LuceneSearch()
  {
    try
    {
      searcher = new IndexSearcher(IndexReader.open("index"));
    }
    catch(Exception e)
    {
      
    }
  }
  public final Hits search(String keyword)throws Exception 
  {
    System.out.println("正在檢索關鍵字 : "+keyword);
    QueryParser qp = new QueryParser("contents",analyzer);
    query = qp.parse(keyword);
    Date start = new Date();
    Hits hits = searcher.search(query);
    Date end = new Date();
    System.out.println("檢索完成，用時"+(end.getTime()
-start.getTime())+"毫杪");
    
    return hits;
  }
  //列印結果集
  public void printResult(Hits h)
  {
    if(h.length()==0)
    {
      System.out.println("對不起！沒有您要找的資料!");
    }
    else
    {
      for(int i = 0 ; i <h.length();i++)
      {
        try
        {
          Document doc = h.doc(i);
          System.out.println("這是第"+(i+1)+"個檢索到的結果,檔案為 : "
+doc.get("path"));
          System.out.println(doc.get("contents"));
        }
        catch(Exception e)
        {
          e.printStackTrace();
        }
      }
    }
    System.out.println("---------------------------");
  }
  
  public static void main(String[] args) throws Exception
  {
	Scanner scanner = new Scanner(System.in);
    LuceneSearch test = new LuceneSearch();
    while (true) {
    	System.out.print("請輸入關鍵字：");

        String input = scanner.next();
        if (input.equals("exit")) {
        	System.out.print("結束!");
			return;
		}
        else { 
            test.printResult(test.search(input));
		}
	}
  }
  
}