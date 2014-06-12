import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;


public class Main{
	
	static String indexPath = "../../index";
	static LuceneSearch luceneSearch;
	static Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_47);
	private static JFrame frame;

	static JButton searchButton = new JButton("Search");
	static JTextField searchTextField = new JTextField();
	static JLabel keywordRankingListJLabel = new JLabel("");
	static ArrayList<KeywordRanking> keywordRankingArrayList = new ArrayList<KeywordRanking>();
	static AutoSuggestor autoSuggestor;
	
	class KeywordRanking  {  
	       String keyword;  
	       int count;  
	}  
	
	public static void main(String args[])throws IOException { 
		final Main main = new Main();
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				luceneSearch = new LuceneSearch(indexPath, analyzer);
				frame = new JFrame();
				frame.setSize(600, 600);
		        frame.setLayout(new GridBagLayout());
		        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		          
		        
		        GridBagConstraints c1 = new GridBagConstraints();
		        c1.gridx = 0;
		        c1.gridy = 0;
		        c1.gridwidth = 5;
		        c1.gridheight = 1;
		        c1.weightx = 1;
		        c1.weighty = 0;
		        c1.fill = GridBagConstraints.BOTH;
		        c1.anchor = GridBagConstraints.WEST;
		        frame.add(searchTextField, c1);
		        GridBagConstraints c2 = new GridBagConstraints();
		        c2.gridx = 3;
		        c2.gridy = 1;
		        c2.gridwidth = 2;
		        c2.gridheight = 1;
		        c2.weightx = 0;
		        c2.weighty = 0;
		        c2.fill = GridBagConstraints.NONE;
		        c2.anchor = GridBagConstraints.CENTER;
		        frame.add(searchButton, c2);
		        GridBagConstraints c3 = new GridBagConstraints();
		        c3.gridx = 0;
		        c3.gridy = 2;
		        c3.gridwidth = 2;
		        c3.gridheight = 1;
		        c3.weightx = 0;
		        c3.weighty = 0;
		        c3.fill = GridBagConstraints.NONE;
		        c3.anchor = GridBagConstraints.CENTER;
		        frame.add(keywordRankingListJLabel, c3);
		        
		        searchButton.addActionListener(main.new InputListener());
		        frame.setVisible(true);
		        
		        autoSuggestor = new AutoSuggestor(searchTextField, frame, null, Color.WHITE.brighter(), Color.BLUE, Color.RED, 0.75f) {
		            @Override
		            boolean wordTyped(String typedWord) {

		                //create list for dictionary this in your case might be done via calling a method which queries db and returns results as arraylist
		            	try {
		            		ArrayList<String> words = luceneSearch.searchArray(typedWord);
		                    setDictionary(words);
		    			} catch (Exception e) {
		    				// TODO Auto-generated catch block
		    				e.printStackTrace();
		    			}
		                
		                //addToDictionary("bye");//adds a single word

		                return super.wordTyped(typedWord);//now call super to check for any matches against newest dictionary
		            }
		        };
			}
		});
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void ranking(String keywordString) {
		if (keywordRankingArrayList == null) {
			KeywordRanking keyword = new KeywordRanking();
			keyword.keyword = keywordString;
			keyword.count = 1;
			keywordRankingArrayList.add(keyword);
		}
		else {
			if (checkKeywordIsRepeat(keywordString) == -1) {
				KeywordRanking keyword = new KeywordRanking();
				keyword.keyword = keywordString;
				keyword.count = 1;
				keywordRankingArrayList.add(keyword);
			}
			else {
				keywordRankingArrayList.get(checkKeywordIsRepeat(keywordString)).count ++;
			}
		}
		Collections.sort(keywordRankingArrayList, new Comparator(){
			@Override
			public int compare(Object o1, Object o2) {
				// TODO Auto-generated method stub
				KeywordRanking keyword1 = (KeywordRanking)o1;
				KeywordRanking keyword2 = (KeywordRanking)o2;
				return keyword2.count - keyword1.count;
			}
		});
	}
	
	public int  checkKeywordIsRepeat(String keywordString) {
		for (int i = 0; i < keywordRankingArrayList.size(); i++) {
			if (keywordString.equals(keywordRankingArrayList.get(i).keyword)) {
				return i;
			}
		}
		return -1;
	}
	
	public void listKeywordRanking() {
		String keywordRankingString = "¼öªù·j´M¡G";
		for (int i = 0; i < keywordRankingArrayList.size(); i++) {
			keywordRankingString += keywordRankingArrayList.get(i).keyword;
			keywordRankingString += "  ";
		}
	    keywordRankingListJLabel.setText(keywordRankingString);
	}
	
	class InputListener implements ActionListener, CaretListener {
		String keyword = new String();
	    public void actionPerformed(ActionEvent event) {
	    	try {
	            keyword = searchTextField.getText();
	            ranking(keyword);
	            listKeywordRanking();
				luceneSearch.search(keyword);
				//luceneSearch.doPagingSearch(in, searcher, query, hitsPerPage, raw, interactive)
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	         
	    public void caretUpdate(CaretEvent event) {
	        
	    } 
	}
	
	
}
