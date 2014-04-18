import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.lucene.index.IndexWriter;

public class LuceneIndex{
	
	  private IndexWriter writer = null ; 
	  
	  public LuceneIndex() 
	  { 
	    
	  } 
	  
	  
	  public static void main(String args[])throws IOException 
	  { 
		  int count = 0;
		  String inputWarcFile="file/08.warc";
		  FileInputStream fileInputStream = new FileInputStream("file/08.warc");
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
		        // print our data
		        System.out.println(thisTRECID + " : " + thisTargetURI);
		        System.out.println(thisContentString);
		      }
		    }
		    inStream.close();
	  } 
}