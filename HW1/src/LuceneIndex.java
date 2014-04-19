import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

import javax.print.Doc;
import javax.swing.text.Document;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class LuceneIndex{
	
	private IndexWriter writer = null ; 
	String filePath;
	String indexPath;
	Analyzer analyzer;
	  
	public LuceneIndex(String filePath, String indexPath, Analyzer analyzer) 
	{ 
		this.filePath = filePath;
		this.indexPath = indexPath;
		this.analyzer = analyzer;
	} 
	  
	public void createIndex() throws Exception {
		FileInputStream fileInputStream = new FileInputStream(filePath);
		// cast to a data input stream
		DataInputStream inStream=new DataInputStream(fileInputStream);
		Directory fsDir = FSDirectory.getDirectory(indexPath);
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
			    org.apache.lucene.document.Document document = new org.apache.lucene.document.Document();
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
	    ramIndexWriter.close();
	        
	    //2、退出时保存
	    IndexWriter fsIndexWriter = new IndexWriter(fsDir, analyzer, true, MaxFieldLength.LIMITED);
	    fsIndexWriter.addIndexesNoOptimize(new Directory[]{ramDir});
	        
	    // 优化操作
	    fsIndexWriter.commit();
	    fsIndexWriter.optimize();
	        
	    fsIndexWriter.close();
	}
}