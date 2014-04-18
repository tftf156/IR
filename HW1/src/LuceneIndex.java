import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import javax.print.Doc;
import javax.swing.text.Document;

import org.apache.lucene.index.IndexWriter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class LuceneIndex{
	
	  private IndexWriter writer = null ; 
	  
	  public LuceneIndex() 
	  { 
	    
	  } 
	  
	  
	  public static void main(String args[])throws IOException 
	  { 
		  int count = 0;
		  FileInputStream fileInputStream = new FileInputStream("../../file/08.warc");
		    // open our gzip input stream
		    //GZIPInputStream gzInputStream=new GZIPInputStream(new FileInputStream(inputWarcFile));
		    
		    // cast to a data input stream
		    DataInputStream inStream=new DataInputStream(fileInputStream);
		    
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
		        System.out.println(thisTRECID + " : " + thisTargetURI);
		        System.out.println(titleString);
		        
			    org.jsoup.nodes.Document doc = Jsoup.parseBodyFragment(thisContentString);
			    Element body = doc.body();
			    String bodyTextString = body.text();
			    String[] bodySplit = bodyTextString.split(" Content-Length: ");
			    String bodyText = bodySplit[1];
			    bodySplit = bodySplit[1].split(" ");
			    bodyText = bodyText.substring(bodySplit[0].length() + 1, bodyText.length());
			    System.out.println(bodyText);
		      break;
		      }
		    }
		    inStream.close();
		    
	  } 
}