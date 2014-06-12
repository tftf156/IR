import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.regex.Pattern;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

/**
 * A complete Java class that demonstrates how to create an HTML viewer with styles,
 * using the JEditorPane, HTMLEditorKit, StyleSheet, and JFrame.
 * 
 * @author alvin alexander, devdaily.com.
 *
 */
public class HtmlViewer
{
	
	public HtmlViewer(final String htmlString)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				// create jeditorpane
				JEditorPane jEditorPane = new JEditorPane();
        
				// make it read-only
				jEditorPane.setEditable(false);
        
				// create a scrollpane; modify its attributes as desired
				JScrollPane scrollPane = new JScrollPane(jEditorPane);
        
				// add an html editor kit
				HTMLEditorKit kit = new HTMLEditorKit();
				jEditorPane.setEditorKit(kit);
        
				// add some styles to the html
				StyleSheet styleSheet = kit.getStyleSheet();
				styleSheet.addRule("body {color:#000; font-family:times; margin: 4px; }");
				styleSheet.addRule("h1 {color: blue;}");
				styleSheet.addRule("h2 {color: #ff0000;}");
				styleSheet.addRule("pre {font : 10px monaco; color : black; background-color : #fafafa; }");

				// create some simple html as a string
				// create a document, set it on the jeditorpane, then add the html
				Document doc = kit.createDefaultDocument();
				jEditorPane.setDocument(doc);
				System.out.print(htmlString);
				jEditorPane.setText(htmlString);
				// now add it all to a frame
				JFrame j = new JFrame("HtmlViewer");
				j.getContentPane().add(scrollPane, BorderLayout.CENTER);
				j.setSize(500, 500);
				// make it easy to close the application
				j.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				// display the frame
        
				// pack it, if you prefer
				//j.pack();
        
				// center the jframe, then make it visible
				j.setLocationRelativeTo(null);
				j.setVisible(true);
			}
		});
	}
	
	
}