import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import com.inet.jortho.FileUserDictionary;
import com.inet.jortho.SpellChecker;

import com.sam.FontChooser.FontChooser;
import com.sam.LineNumbers.TextLineNumber;

 
/**
 * @author Sam Dowling | T00171832
 * @version 1.0
 * @since 2014-12-08
 */

public class Notepad extends JFrame implements ActionListener{
	/** Menubar */
	JMenu menu;
	
	/** main textarea */
	JTextArea area;
	
	/** bottom statusbar */
	JPanel statusBar;
	
	/** status bar filename */
	JLabel statusLabel;
	
	/** statusbar caret info */
	JLabel statusCaret;
	
	/** file open info */
	String fileOpen;
	
	/** font chooser instance */
	FontChooser fc;
	
	/** scroll pane */
	JScrollPane sp;
	
	/** textlinenumber instance */
	TextLineNumber tln;
	
	/** edited flag */
	boolean edited;

    public Notepad(){
    	Container cPane;
    	
    	setTitle     ("Notepad");
        setSize      (700,500);
        setResizable (true);
        setLocation  (250,200);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        /** custom close dialog */
        addWindowListener(new WindowAdapter(){
   			public void windowClosing(WindowEvent evt){
     			quit();
   			}
  		});
        ImageIcon img = new ImageIcon("note.png");
        setIconImage(img.getImage());
        fc = new FontChooser(this);
        
        
        /**
         * Setup the menubar on the main window
         */
         
        JMenuBar menuBar = new JMenuBar();
        createFileMenu();
        menuBar.add(menu);
        
        createEditMenu();
        menuBar.add(menu);
        
        createFormatMenu();
        menuBar.add(menu);
        
        createViewMenu();
        menuBar.add(menu);
        
        menuBar.setBackground(new Color(230,230,230));
        setJMenuBar(menuBar);
        
        
        /** Create the main textarea */
        area = new JTextArea();
        
        
        /** anonymous caret listener method */
        area.addCaretListener(new CaretListener(){
	        public void caretUpdate(CaretEvent e){
			    JTextArea editArea = (JTextArea)e.getSource();
			
			    int linenum = 1;
			    int columnnum = 1;
			
			    try {
			        int caretpos = editArea.getCaretPosition();
			        linenum = editArea.getLineOfOffset(caretpos);
			        columnnum = caretpos-editArea.getLineStartOffset(linenum)+1;
			        linenum += 1;
			    }
			    catch(Exception ex) { }
			    updateStatus(linenum,columnnum);
			}	
        });

		/** anonymous document changed listener method */
        area.getDocument().addDocumentListener(new DocumentListener(){
 			public void changedUpdate(DocumentEvent e){
    			changed();
  			}
  			public void removeUpdate(DocumentEvent e){
    			changed();
  			}
  			public void insertUpdate(DocumentEvent e){
    			changed();
  			}
  			private void changed(){
    			setTitle("Notepad*");
    			edited = true;
    		}
		});
        
        
        
        sp = new JScrollPane(area);
        
        JPanel content = new JPanel();
        
        content.setLayout(new BorderLayout());
        content.add(sp);
        
        /** configure textlinenumber */
        tln = new TextLineNumber(area);
        tln.setBorderGap(0);
        tln.setCurrentLineForeground(Color.BLUE);
        tln.setMinimumDisplayDigits(3);
        
        /** create statusbar */
        statusBar = new JPanel();
		content.add(statusBar, BorderLayout.SOUTH);
        statusBar.setPreferredSize(new Dimension(content.getWidth(), 16));
        statusBar.setLayout(new BoxLayout(statusBar,BoxLayout.X_AXIS));
        
		statusLabel = new JLabel("New File");
		statusBar.add(statusLabel);
		
		
		statusCaret = new JLabel("");
		statusBar.add(statusCaret);
		
		statusBar.setVisible(true);
		updateStatus(1,1);
		
		/** instanciate spell checker */
		SpellChecker.setUserDictionaryProvider(new FileUserDictionary());
		SpellChecker.registerDictionaries(null,"en");	
		SpellChecker.register(area);
		
		fileOpen="";
		edited = false;
    	setContentPane(content);
    	
    	
    }

	/** action listener method */
    public void actionPerformed(ActionEvent event) {
        String  menuName;
        menuName = event.getActionCommand();
        
        
        switch(menuName){
        	
        	/** action linsteners for file tab */
        	case "New":
        		area.setText("");
        		statusLabel.setText("New File");
        		fileOpen = "";
        		break;
        	case "Open":
        		open();
        		break;
        	case "Save":
        		save();
        		break;
        	case "Save As":
        		saveAs();
        		break;
        	case "Quit":
        		quit();
        		break;
        		
        	/** action linsteners for edit tab */
        	case "Copy":
        		area.copy();
        		break;
        	case "Cut":
				area.cut();
        		break;
        	case "Paste":
				area.paste();
        		break;
        	case "Select All":
        		area.selectAll();
        		break;
        		
        	/** action linsteners for format tab */
        	case "Font...":
        		setFont();
        		break;
        }
    }
    
    /** create file menu layout in file menubar tab */
    private void createFileMenu() {
    	JMenuItem item;
        menu = new JMenu("File");
        
        item = new JMenuItem("New");
        item.addActionListener( this );
        item.setMnemonic('N');
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK, false));
        menu.add( item );
        
        item = new JMenuItem("Open");
        item.addActionListener( this );
        item.setMnemonic('O');
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK, false));
        menu.add( item );
        
        item = new JMenuItem("Save");
        item.addActionListener( this );
        item.setMnemonic('S');
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK, false));
        menu.add( item );
        
        item = new JMenuItem("Save As");
        item.addActionListener( this );
        item.setMnemonic('A');
        menu.add( item );
        
        menu.addSeparator();
        
        item = new JMenuItem("Quit");
        item.addActionListener( this );
        item.setMnemonic('Q');
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Event.CTRL_MASK, false));
        menu.add( item );
    }
    
    /** create edit menu layout in edit menubar tab */
    private void createEditMenu() {
    	JMenuItem item;
        menu = new JMenu("Edit");
        
        item = new JMenuItem("Copy");
        item.addActionListener( this );
        item.setMnemonic('C');
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, Event.CTRL_MASK, false));
        menu.add( item );
        
        item = new JMenuItem("Cut");
        item.addActionListener( this );
        item.setMnemonic('X');
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, Event.CTRL_MASK, false));
        menu.add( item );
        
        item = new JMenuItem("Paste");
        item.addActionListener( this );
        item.setMnemonic('V');
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, Event.CTRL_MASK, false));
        menu.add( item );
        
        item = new JMenuItem("Select All");
        item.addActionListener( this );
        item.setMnemonic('A');
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, Event.CTRL_MASK, false));
        menu.add( item );
    }
    
    /** create format menu layout in format menubar tab */
    private void createFormatMenu(){
    	menu = new JMenu("Format");
    	JMenuItem item;
    	
    	/** create anonymous word wrap method */
    	JCheckBoxMenuItem wrap = 
        	new JCheckBoxMenuItem("Word Wrap", false);
      	wrap.addItemListener(new ItemListener(){
        	public void itemStateChanged(ItemEvent e){
            	if(wrap.getState()){
            		area.setLineWrap(true);
    				area.setWrapStyleWord(true);
            	}else{
            		area.setLineWrap(false);
    				area.setWrapStyleWord(false);
            	}
        	}
      	});
        menu.add(wrap);
        
        item = new JMenuItem("Font...");
        item.addActionListener( this );
        menu.add( item );
    }
    
    /** create view menu layout for view menubar */
    private void createViewMenu() {
    	menu = new JMenu("View");
    	
    	/** toggle statusbar visibility anonymous method  */
    	JCheckBoxMenuItem status = new JCheckBoxMenuItem("Show StatusBar", true);
      	status.addItemListener(new ItemListener() {
        	public void itemStateChanged(ItemEvent e){
            	if(status.getState()) {
            		statusBar.setVisible(true);
            	}else{
            		statusBar.setVisible(false);
            	}
        	}
      	});
        menu.add(status);
        
        /** toggle line numbers visibility anonymous method */
        JCheckBoxMenuItem line = new JCheckBoxMenuItem("Show Line Numbers", false);
      	line.addItemListener(new ItemListener() {
        	public void itemStateChanged(ItemEvent e) {
            	if(line.getState()){
            		sp.setRowHeaderView(tln);
            	}else{
            		sp.setRowHeaderView(null);
            	}
        	}
      	});
        menu.add( line );
    }
    
    /** confirm close dialog */
    private void quit(){
    	if(edited){
    		int i = JOptionPane.showConfirmDialog(null,"File Has Not Been Saved.\nWould You Like To Save It Before Closing?","Notice", JOptionPane.YES_NO_OPTION);
    		if(i==JOptionPane.YES_OPTION)
    			save();
    	}
    	System.exit(0);
    }
    
    /** save file dialog method */
    private void save(){
    	if(fileOpen.equals("")){
	    	saveAs();
    	}else{
    		try{
				File file = new File(fileOpen);
				file.createNewFile();
				FileWriter writer = new FileWriter(file);
				area.write(writer);
	      		writer.close();
	      		statusLabel.setText(fileOpen);
	      		setTitle("Notepad");
	      	}
	      	catch(Exception e){
	        	JOptionPane.showMessageDialog(null,"Proble, Saving File","Notice",JOptionPane.INFORMATION_MESSAGE);
	      	}
    	}	
    }
    
    /** saveas dialog method */
    private void saveAs(){
    	String fileName = null;
    	JFileChooser choose = new JFileChooser();
    	choose.addChoosableFileFilter(new FileNameExtensionFilter("Text Document (.txt)", "txt"));
    	choose.addChoosableFileFilter(new FileNameExtensionFilter("Java Source File (.java)", "java"));
    	choose.addChoosableFileFilter(new FileNameExtensionFilter("SQL Source File (.sql)", "sql"));
		int val=choose.showSaveDialog(this);
		if (val == JFileChooser.APPROVE_OPTION)
			fileName = choose.getSelectedFile().getAbsolutePath();
    	try{
			File file = new File(fileName);
			file.createNewFile();
			FileWriter writer = new FileWriter(file);
			area.write(writer);
      		writer.close();
      		statusLabel.setText(fileName);
      		fileOpen = fileName;
      		setTitle("Notepad");
      	}
      	catch(Exception e){
        	JOptionPane.showMessageDialog(null,"Problem Saving File","Notice",JOptionPane.INFORMATION_MESSAGE);
      	}
    }
    
    /** open file dialog method */
    private void open(){
    	String fileName = null;
    	JFileChooser choose = new JFileChooser();
    	choose.addChoosableFileFilter(new FileNameExtensionFilter("Text Document (.txt)", "txt"));
    	choose.addChoosableFileFilter(new FileNameExtensionFilter("Java Source File (.java)", "java"));
    	choose.addChoosableFileFilter(new FileNameExtensionFilter("SQL Source File (.sql)", "sql"));
		int val=choose.showOpenDialog(this);
		if (val == JFileChooser.APPROVE_OPTION)
			fileName = choose.getSelectedFile().getAbsolutePath();
    	
	    try(BufferedReader br = new BufferedReader(new FileReader(fileName))) {
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();
	
	        while (line != null) {
	            sb.append(line);
	            sb.append(System.lineSeparator());
	            line = br.readLine();
	        }
	        area.setText(sb.toString());
	        statusLabel.setText(fileName);
	        fileOpen = fileName;
	        setTitle("Notepad");
    	}
    	catch(Exception e){
    		JOptionPane.showMessageDialog(null,"Problem Opening File","Notice",JOptionPane.INFORMATION_MESSAGE);
    	}
    }
    
    /** set font method */
    private void setFont(){
    	fc.setVisible(true);
    	area.setFont(fc.getNewFont());
    	area.setForeground(fc.getNewColor());
    }
    
    /** updates the caret label on caret movement  */
    private void updateStatus(int linenumber, int columnnumber) {
        statusCaret.setText(" : " + "Line: " + linenumber + ", Column: " + columnnumber);
    }  
}