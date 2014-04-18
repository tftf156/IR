
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
    System.out.println("���b�˯�����r : "+keyword);
    QueryParser qp = new QueryParser("contents",analyzer);
    query = qp.parse(keyword);
    Date start = new Date();
    Hits hits = searcher.search(query);
    Date end = new Date();
    System.out.println("�˯������A�ή�"+(end.getTime()
-start.getTime())+"�@�W");
    
    return hits;
  }
  //�C�L���G��
  public void printResult(Hits h)
  {
    if(h.length()==0)
    {
      System.out.println("�藍�_�I�S���z�n�䪺���!");
    }
    else
    {
      for(int i = 0 ; i <h.length();i++)
      {
        try
        {
          Document doc = h.doc(i);
          System.out.println("�o�O��"+(i+1)+"���˯��쪺���G,�ɮ׬� : "
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
    	System.out.print("�п�J����r�G");

        String input = scanner.next();
        if (input.equals("exit")) {
        	System.out.print("����!");
			return;
		}
        else { 
            test.printResult(test.search(input));
		}
	}
  }
  
}