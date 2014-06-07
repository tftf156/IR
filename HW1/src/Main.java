import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;


public class Main{
	
	static String filePath = "../../file/08.warc";
	static String indexPath = "../../index";
	static Analyzer analyzer = new StandardAnalyzer();
	private static JFrame frame;
	static JButton searchButton = new JButton("Search");
	static JTextField searchTextField = new JTextField();
	
	public static void main(String args[])throws IOException 
	{ 
		Main main = new Main();
		
		frame = new JFrame();
		frame.setSize(600, 600);
        frame.setLayout(new GridBagLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
          
        
        GridBagConstraints c3 = new GridBagConstraints();
        c3.gridx = 0;
        c3.gridy = 0;
        c3.gridwidth = 5;
        c3.gridheight = 1;
        c3.weightx = 1;
        c3.weighty = 0;
        c3.fill = GridBagConstraints.BOTH;
        c3.anchor = GridBagConstraints.WEST;
        frame.add(searchTextField, c3);
        GridBagConstraints c5 = new GridBagConstraints();
        c5.gridx = 3;
        c5.gridy = 1;
        c5.gridwidth = 2;
        c5.gridheight = 1;
        c5.weightx = 0;
        c5.weighty = 0;
        c5.fill = GridBagConstraints.NONE;
        c5.anchor = GridBagConstraints.CENTER;
        frame.add(searchButton, c5);
        searchButton.addActionListener(main.new InputListener());
        frame.setVisible(true);
        
		
       
		LuceneIndex luceneIndex = new LuceneIndex(filePath, indexPath, analyzer);
		try {
			Date start = new Date();
			luceneIndex.createIndex();
			Date end = new Date(); 
		    System.out.println("建立完成，建立索引用時"+(end.getTime()-start.getTime())+"毫杪");
		    BufferedReader br = new BufferedReader (new InputStreamReader(System.in));
		    System.out.print("請輸入要搜尋的字:");	    		
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
	}
	class InputListener implements ActionListener, CaretListener {
		String keyword = new String();
		 LuceneSearch luceneSearch = new LuceneSearch(indexPath, analyzer);
	    public void actionPerformed(ActionEvent event) {
	    	try {
	            keyword = searchTextField.getText();
				luceneSearch.search(keyword);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	         
	    public void caretUpdate(CaretEvent event) {
	        
	    } 
	}
}
