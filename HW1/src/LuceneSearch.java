
import java.io.Console;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;


public class LuceneSearch {


	String indexpath;
    //分词器
	Analyzer analyzer;
  
	public LuceneSearch(String indexPath, Analyzer analyzer)
	{
		this.indexpath = indexPath;
		this.analyzer = analyzer;
	}

	/**
	* 搜索
   	* 
   	* IndexSearcher 用来在索引库中进行查询
   	*/
	public void search(String queryString) throws Exception {
		//请求字段
		//String queryString = "document";

		// 1，把要搜索的文本解析为 Query
		String[] fields = { "Title", "content" };
		QueryParser queryParser = new MultiFieldQueryParser(fields, analyzer);
		Query query = queryParser.parse(queryString);

		// 2，进行查询，从索引库中查找
		IndexSearcher indexSearcher = new IndexSearcher(indexpath);
		Filter filter = null;
		TopDocs topDocs = indexSearcher.search(query, filter, 10000);
		System.out.println("总共有【" + topDocs.totalHits + "】条匹配结果");

		int count = 1;
		// 3，打印结果
		for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
			// 文档内部编号
			int index = scoreDoc.doc; 
			// 根据编号取出相应的文档
			Document doc = indexSearcher.doc(index);
			System.out.println("------------------------------");
			System.out.println("第" + count + "筆");
			System.out.println("URL = " + doc.get("URL"));
			System.out.println("Title = " + doc.get("Title"));
			/*System.out.println("size = " + NumberTools.stringToLong(doc.get("size")));
          	System.out.println("path = " + doc.get("path"));*/
			count++;
		}
	}
	public ArrayList<String> searchArray(String queryString) throws Exception {
		ArrayList<String> words = new ArrayList<>();
		//请求字段
		//String queryString = "document";

		// 1，把要搜索的文本解析为 Query
		String[] fields = { "Title", "content" };
		QueryParser queryParser = new MultiFieldQueryParser(fields, analyzer);
		Query query = queryParser.parse(queryString);

		// 2，进行查询，从索引库中查找
		IndexSearcher indexSearcher = new IndexSearcher(indexpath);
		Filter filter = null;
		TopDocs topDocs = indexSearcher.search(query, filter, 10);

		int count = 1;
		// 3，打印结果
		for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
			// 文档内部编号
			int index = scoreDoc.doc; 
			// 根据编号取出相应的文档
			Document doc = indexSearcher.doc(index);
			words.add(doc.get("Title"));
			/*System.out.println("size = " + NumberTools.stringToLong(doc.get("size")));
          	System.out.println("path = " + doc.get("path"));*/
			count++;
		}
		return words;
	}
}