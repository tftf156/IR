
import java.awt.Color;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.apache.lucene.analysis.Analyzer;
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
    JFrame resultFrame;
    ArrayList<Document> docArrayList = new ArrayList<Document>();
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
		System.out.println("總共有【" + topDocs.totalHits + "】條匹配結果");
		
		resultFrame = new JFrame("result");
        resultFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        resultFrame.setSize(800, 800);
        Container container = new JPanel();
        container.setLayout(new GridBagLayout());
		int count = 1;
		// 3，打印结果
		for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
			// 文档内部编号
			int index = scoreDoc.doc; 
			// 根据编号取出相应的文档
			Document doc = indexSearcher.doc(index);
			docArrayList.add(doc);
			JButton button = new JButton();
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = count;
	        button.setText(doc.get("Title"));
	        button.setHorizontalAlignment(SwingConstants.LEFT);
	        button.setBorderPainted(false);
	        button.setOpaque(false);
	        button.setBackground(Color.WHITE);
	        button.addActionListener(new InputListener());
	        container.add(button, c);
			//System.out.println("------------------------------");
			//System.out.println("第" + count + "筆");
			//System.out.println("Title = " + doc.get("Title"));
			if (count == 5) {
				break;
			}
			count++;
		}
		JScrollPane scrollPane = new JScrollPane(container);
        resultFrame.add(scrollPane);
        resultFrame.setVisible(true);
	}
	public ArrayList<String> searchArray(String queryString) throws Exception {
		ArrayList<String> words = new ArrayList<>();
		if(!queryString.equals(""))
		{
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
				if(!doc.get("Title").equals(""))
				{
					words.add(doc.get("Title"));
				}
				/*System.out.println("size = " + NumberTools.stringToLong(doc.get("size")));
	          	System.out.println("path = " + doc.get("path"));*/
				count++;
			}
		}
		return words;
	}
	
	class InputListener implements ActionListener, CaretListener {
	    public void actionPerformed(ActionEvent event) {
	    	try {
	            JButton button = (JButton) event.getSource();
	            for (Document document : docArrayList) {
					if (button.getText() == document.get("Title")) {
						HtmlViewer htmlEditor = new HtmlViewer(document.get("html"));
						//System.out.print(document.get("html"));
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	         
	    public void caretUpdate(CaretEvent event) {
	        
	    } 
	}
}