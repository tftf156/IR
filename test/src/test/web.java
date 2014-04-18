package test;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumberTools;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

public class web {
    // 数据源路径
	static String[] dspath = {"test.txt", "test1.txt"};
    //存放索引文件的位置，即索引库
	static String indexpath = "../luceneIndex";
    //分词器
	static Analyzer analyzer = new StandardAnalyzer();
      
    /**
     * 创建索引(推荐)
     * 
     * IndexWriter 用来操作（增、删、改）索引库的
     */
    public static void createIndex2() throws Exception {
    	int count = 0;
		FileInputStream fileInputStream = new FileInputStream("../../file/08.warc");
		// cast to a data input stream
	    DataInputStream inStream=new DataInputStream(fileInputStream);
	    Directory fsDir = FSDirectory.getDirectory("../../index");
        //1、启动时读取
        Directory ramDir = new RAMDirectory(fsDir);
        
        // 运行程序时操作ramDir
        IndexWriter ramIndexWriter = new IndexWriter(ramDir, analyzer, MaxFieldLength.LIMITED);
        
	    // iterate through our stream
	    WarcRecord thisWarcRecord;
	    while ((thisWarcRecord=WarcRecord.readNextWarcRecord(inStream))!=null) {
		      // see if it's a response record
		      if (thisWarcRecord.getHeaderRecordType().equals("response")) {
		        // it is - create a WarcHTML record
		        WarcHTMLResponseRecord htmlRecord=new WarcHTMLResponseRecord(thisWarcRecord);
		        // get our TREC ID and target URI
		        String thisTRECID=htmlRecord.getTargetTrecID();
		        String thisTargetURI=htmlRecord.getTargetURI();
		        String thisContentString = thisWarcRecord.getContentUTF8();
		        org.jsoup.nodes.Document content = Jsoup.parse(thisContentString);

		        String titleString = content.title();
		        
		        // print our data
		        //System.out.println(thisTRECID + " : " + thisTargetURI);
		        //System.out.println(titleString);
		        
			    org.jsoup.nodes.Document doc = Jsoup.parseBodyFragment(thisContentString);
			    Element body = doc.body();
			    String bodyTextString = body.text();
			    String[] bodySplit = bodyTextString.split(" Content-Length: ");
			    String bodyText = bodySplit[1];
			    bodySplit = bodySplit[1].split(" ");
			    bodyText = bodyText.substring(bodySplit[0].length() + 1 + titleString.length() + 1, bodyText.length());
			    //System.out.println(bodyText);
			    
			    // 添加 Document
		        Document document = new Document();
		        //文件名称
		        document.add(new Field("URL", thisTargetURI, Store.YES, Index.ANALYZED));
		        //检索到的内容
		        document.add(new Field("Title", titleString, Store.YES, Index.ANALYZED));
		        //文件大小
		        document.add(new Field("content", bodyText, Store.YES, Index.ANALYZED));
		        ramIndexWriter.addDocument(document);
		      //break;
		      }
		    }
		    inStream.close();
        
        /*for (int i=0;i<2;i++)
        {
        //数据源
	        File file = new File(dspath[i]);
	        // 添加 Document
	        Document doc = new Document();
	        //文件名称
	        doc.add(new Field("name", file.getName(), Store.YES, Index.ANALYZED));
	        //检索到的内容
	        doc.add(new Field("content", readFileContent(file), Store.YES, Index.ANALYZED));
	        //文件大小
	        doc.add(new Field("size", NumberTools.longToString(file.length()), Store.YES, Index.NOT_ANALYZED));
	        //检索到的文件位置
	        doc.add(new Field("path", file.getAbsolutePath(), Store.YES, Index.NOT_ANALYZED));
	        ramIndexWriter.addDocument(doc);
        }*/
        ramIndexWriter.close();
        
        //2、退出时保存
        IndexWriter fsIndexWriter = new IndexWriter(fsDir, analyzer, true, MaxFieldLength.LIMITED);
        fsIndexWriter.addIndexesNoOptimize(new Directory[]{ramDir});
        
        // 优化操作
        fsIndexWriter.commit();
        fsIndexWriter.optimize();
        
        fsIndexWriter.close();
    }
        
    /**
     * 搜索
     * 
     * IndexSearcher 用来在索引库中进行查询
     */
    public static void search(String queryString) throws Exception {
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

        // 3，打印结果
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            // 文档内部编号
            int index = scoreDoc.doc; 
            // 根据编号取出相应的文档
            Document doc = indexSearcher.doc(index);
            System.out.println("------------------------------");
            System.out.println("URL = " + doc.get("URL"));
            System.out.println("Title = " + doc.get("Title"));
            /*System.out.println("size = " + NumberTools.stringToLong(doc.get("size")));
            System.out.println("path = " + doc.get("path"));*/
        }
    }

    /**
     * 读取文件内容
     */
    public static String readFileContent(File file) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            StringBuffer content = new StringBuffer();
            for (String line = null; (line = reader.readLine()) != null;) {
                content.append(line).append("\n");
            }
            reader.close();
            return content.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static void main(String args[])throws IOException 
	  {
    	try {
    		Date start = new Date();
			createIndex2();
			Date end = new Date(); 
		    System.out.println("建立索引用時"+(end.getTime()-start.getTime())+"毫杪");
			search("Adult");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	System.out.println("end");
	  }
	  

}