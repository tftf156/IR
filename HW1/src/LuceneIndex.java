import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

public class LuceneIndex{
	
	static String filePath = "../../file/08.warc";
	static String indexPath = "../../index";
	static Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_47);
	  
	public static void main(String args[])throws IOException 
	{
		try {
			Date start = new Date();
			createIndex();
			Date end = new Date(); 
		    System.out.println("建立完成，建立索引用時"+(end.getTime()-start.getTime())+"毫杪");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
	} 
	  
	public static void createIndex() throws Exception {
		FileInputStream fileInputStream = new FileInputStream(filePath);
		// cast to a data input stream
		DataInputStream inStream=new DataInputStream(fileInputStream);
		Directory fsDir = FSDirectory.open(new File(indexPath));
	    //1、启动时读取
	    Directory ramDir = new RAMDirectory(fsDir, null);
	        
	    // 运行程序时操作ramDir
	    IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_47, analyzer);
	    IndexWriter ramIndexWriter = new IndexWriter(ramDir, iwc);
	        
		// iterate through our stream
		WarcRecord thisWarcRecord;
		while ((thisWarcRecord=WarcRecord.readNextWarcRecord(inStream))!=null) {
			// see if it's a response record
			if (thisWarcRecord.getHeaderRecordType().equals("response")) {
				// it is - create a WarcHTML record
				WarcHTMLResponseRecord htmlRecord=new WarcHTMLResponseRecord(thisWarcRecord);
				String thisTargetURI=htmlRecord.getTargetURI();
				String thisContentString = thisWarcRecord.getContentUTF8();
				org.jsoup.nodes.Document content = Jsoup.parse(thisContentString);
				//System.out.print(thisContentString);
				
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
			    org.apache.lucene.document.Document document = new org.apache.lucene.document.Document();
			    //文件名称
			    document.add(new StoredField("URL", thisTargetURI));
			    //检索到的内容
			    document.add(new TextField("Title", titleString, Store.YES));
			    //文件大小
			    document.add(new StoredField("content", bodyText));
			    document.add(new StoredField("html", thisContentString));
			    ramIndexWriter.addDocument(document);
			    thisWarcRecord = null;
				content = null;
				document = null;
				bodySplit = null;
				body = null;
				htmlRecord = null;
				doc = null;
			    //break;
		    }
		}
		inStream.close();
	    ramIndexWriter.close();
	        
	    //2、退出时保存
	    IndexWriterConfig fsIwc = new IndexWriterConfig(Version.LUCENE_47, analyzer);
	    IndexWriter fsIndexWriter = new IndexWriter(fsDir, fsIwc);
	    fsIndexWriter.addIndexes(new Directory[]{ramDir});
	        
	    // 优化操作
	    fsIndexWriter.commit();
	        
	    fsIndexWriter.close();
	}
}