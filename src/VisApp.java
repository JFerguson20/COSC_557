import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.NoninvertibleTransformException;
import java.io.File;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

public class VisApp extends JPanel implements ActionListener {
	private JFrame appFrame;
	private Matrix2D wholeMatrix;
	private JFrame selectFrame;
	private VisPanel visPanel;
	private SelectPanel selectPanel;
    private Matrix2DVis zoomPanel;
    private JComboBox<Object> combobox;
    JMenuItem selection;
    public UpdateSliderEvent updateSlider = new UpdateSliderEvent();
    JSlider zoomSlider;
    
    private int minSliderVal     = 0;
    private int maxSliderVal     = 200;
    private int sliderTickIncr   = 20;
    private int initialSliderVal = minSliderVal;
    
    
	public VisApp() throws NoninvertibleTransformException {
		initialize();
		appFrame.setVisible(true);
		
	}

	private void initialize() throws NoninvertibleTransformException {
		appFrame = new JFrame();
		appFrame.setTitle("Matrix Vis");
		appFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		appFrame.setBounds(100, 100, 1000, 700);
		
		initializeMenu();
	}

	private void initializeMenu() {
		appFrame.revalidate();
		JMenuBar menuBar = new JMenuBar();
		appFrame.setJMenuBar(menuBar);

		JMenu file = new JMenu("File");
		menuBar.add(file);
		JMenuItem mi = new JMenuItem("Open TSV...", KeyEvent.VK_O);
		mi.addActionListener(this);
		mi.setActionCommand("open tsv");
		mi.setEnabled(true);
		file.add(mi);

		file.addSeparator();

		mi = new JMenuItem("Exit", KeyEvent.VK_X);
		mi.addActionListener(this);
		mi.setActionCommand("exit");
		file.add(mi);

		// Options menu
		JMenu options = new JMenu("Options");
		menuBar.add(options);
		
		selection = new JMenuItem("Selection Tool");
		selection.addActionListener(this);
		selection.setActionCommand("selection");
		selection.setEnabled(false);
		options.add(selection);
		
		// Toggle Grid
		JMenuItem grid = new JMenuItem("Toggle Hover", KeyEvent.VK_T);
		grid.addActionListener(this);
		grid.setActionCommand("toggle hover");
		grid.setEnabled(false);
		options.add(grid);

		// Change colors
		JMenuItem colors = new JMenuItem("Change Colors", KeyEvent.VK_C);
		colors.addActionListener(this);
		colors.setActionCommand("change colors");
		colors.setEnabled(false);
		options.add(colors);
		
		//add empty combo box that we populate later
		combobox = new JComboBox<Object>();
		appFrame.getJMenuBar().add(combobox);
		//combobox.addActionListener(this);
		//combobox.setActionCommand("search input");
		combobox.setVisible(false);
	    combobox.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() 
	    {
	        public void keyPressed(KeyEvent evt)
	        {
	            if(evt.getKeyCode() == KeyEvent.VK_ENTER)
	            {
	    			String genome = combobox.getSelectedItem().toString();
	    			int idx = wholeMatrix.getAllGenomeNames().indexOf(genome);
	    			if(!zoomPanel.add(idx)) { selectPanel.removeItem(genome); }
	    			zoomPanel.revalidate();
	    			zoomPanel.repaint();
	    			appFrame.revalidate();
	    			appFrame.repaint();
	            }
	        }
	    });
	}

	private void initializePanel(File f) throws NoninvertibleTransformException {
		wholeMatrix = null;
		try {
			wholeMatrix = new Matrix2D(f);
			// data = new int[mat.getNumRows()][mat.getNumCols()];

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//visPanel = new VisPanel(mat);
		zoomPanel = new Matrix2DVis(wholeMatrix, this);
		zoomPanel.setSliderListener(updateSlider);
		JPanel mainPanel = (JPanel) appFrame.getContentPane();
		mainPanel.removeAll();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(zoomPanel, BorderLayout.CENTER);
		
		zoomSlider = new JSlider(minSliderVal, maxSliderVal);
		zoomSlider.setLayout(new FlowLayout(FlowLayout.TRAILING));
		zoomSlider.setMajorTickSpacing(sliderTickIncr);
		zoomSlider.setValue(initialSliderVal);
		zoomSlider.setPaintTicks(true);
		zoomSlider.setSize(200, 200);
		zoomSlider.setVisible(true);
		zoomSlider.addChangeListener(zoomPanel.sliderMovement);
		mainPanel.add(zoomSlider, BorderLayout.PAGE_END);
		
		mainPanel.setVisible(true);
		
		createSelectWindow();
		initializeSearchBox();
		
	}

	public void initializeSearchBox() {
		combobox.removeAllItems();
		ArrayList<String> search = new ArrayList<String>(wholeMatrix.getAllGenomeNames());
		search.sort(null);
		DefaultComboBoxModel genomeNames = new DefaultComboBoxModel( search.toArray() );
		combobox.setModel(genomeNames);
	    AutoCompleteDecorator.decorate(combobox);
	    combobox.setVisible(true);
	    combobox.setEnabled(true);
	    combobox.revalidate();
	    combobox.repaint();
	}
	public static void main(String args[]) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VisApp app = new VisApp();
				} catch (NoninvertibleTransformException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getActionCommand().equals("selection")){
			selectFrame.setVisible(true);
		}
		else if (event.getActionCommand().equals("toggle hover")) {
			visPanel.toggleTooltip();
		} else if (event.getActionCommand().equals("change colors")) {

		} else if (event.getActionCommand().equals("exit")) {
			System.exit(0);
		} else if (event.getActionCommand().equals("search input")) {
			String genome = combobox.getSelectedItem().toString();
			int idx = wholeMatrix.getAllGenomeNames().indexOf(genome);
			zoomPanel.add(idx);
			appFrame.revalidate();
		}
		else if(event.getActionCommand().equals("open tsv")) {
			
			 //Create a file chooser
			 final JFileChooser tsvFileChooser = new JFileChooser();
			 int result = tsvFileChooser.showOpenDialog(appFrame);
			
			 if (result != JFileChooser.APPROVE_OPTION) {
		 		 JOptionPane.showMessageDialog(appFrame, "Not a valid file. Please try again.");
				 return;
			 }
				
		     // user selects a file
			 File tsvFile = tsvFileChooser.getSelectedFile();			 
			 if(!tsvFile.getAbsolutePath().endsWith(".tsv")) {
				 JOptionPane.showMessageDialog(appFrame, "Not a valid .tsv file. Please try again.");
			 }
			 else {
				 try {
					initializePanel(tsvFile);
					appFrame.revalidate();
				} catch (NoninvertibleTransformException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 }
		}
	}
	
    public class UpdateSliderEvent implements SliderListener {
		public void  updateSlider(double newVal) {
			zoomSlider.setValue((int)(newVal * 100.f));
		}
    }
    
    public interface SliderListener {
        public void updateSlider(double newVal);
    }
    
    private void createSelectWindow(){
    	selection.setEnabled(true);
    	//bring up selection window unless its already active.
		selectFrame = new JFrame();
		//selectFrame.setVisible(false);
		selectFrame.setTitle("Selection");
		//menu bar
		JMenuBar menuBar = new JMenuBar();
		JMenu file = new JMenu("File");
		menuBar.add(file);
		JMenuItem mi = new JMenuItem("Import Selections", KeyEvent.VK_I);
		mi.addActionListener(this);
		mi.setActionCommand("open csv");
		mi.setEnabled(true);
		file.add(mi);
		selectFrame.setJMenuBar(menuBar);
		//end menu bar
		selectFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		selectFrame.setBounds(100,100,500, 500);
		
	    selectFrame.addWindowListener(new WindowAdapter() {
	        public void windowClosing(WindowEvent event) {
	            selectExitProcedure();
	        }
	    });
		selectFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	
		selectPanel = new SelectPanel(this);
		JPanel mainPanel = (JPanel)selectFrame.getContentPane();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(selectPanel, BorderLayout.CENTER);
		selectPanel.setLayout(null);
        //mainPanel.add(scrollPane, BorderLayout.SOUTH);

		selectFrame.setVisible(false);
    }
    
    private void selectExitProcedure(){
    	selectFrame.setVisible(false);
    }
    
    //called from Select Panel
	public void select(Object[] selectedNames) throws NoninvertibleTransformException {
		ArrayList<Integer> selectedRows = zoomPanel.selectMatrix(selectedNames);
		Matrix2D smallerMat = new Matrix2D(wholeMatrix, selectedRows);
		//create the new window with the selected matrix
		createSelectedMatrixFrame(smallerMat);
	}
	
	//called from Select Panel
	public void clearAll() {
		zoomPanel.clearAll();
	}
	
	//called from Select Panel
	public void remove(String selectedValue) {
		zoomPanel.remove(selectedValue);
	}
	
	//called from vispanel, updates selected on select panel
	public void rowSelected(String genomeName) {
		selectPanel.addSelectedItem(genomeName);
	}
	
	//called from vispanel, updates selected on select panel
	public void removeRowSelected(String genomeName) {
		selectPanel.removeItem(genomeName);
	}
	
	public ArrayList<String> getAllGenomeNames(){
		return wholeMatrix.getAllGenomeNames();
	}
	
	private void createSelectedMatrixFrame(Matrix2D smallMat) throws NoninvertibleTransformException{
		JFrame selectionMatFrame = new JFrame();
		selectionMatFrame.setVisible(true);
		selectionMatFrame.setTitle("Selection");
		selectionMatFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		selectionMatFrame.setBounds(100, 100, 1000, 700);
		
		Matrix2DVis zoomPanel1 = new Matrix2DVis(smallMat, this);
		zoomPanel1.setSliderListener(updateSlider);
		JPanel mainPanel = (JPanel) selectionMatFrame.getContentPane();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(zoomPanel1, BorderLayout.CENTER);
		JSlider zoomSlider1 = new JSlider(minSliderVal, maxSliderVal);
		zoomSlider1.setLayout(new FlowLayout(FlowLayout.TRAILING));
		zoomSlider1.setValue(initialSliderVal);
		zoomSlider1.setMajorTickSpacing(sliderTickIncr);
		zoomSlider1.setPaintTicks(true);
		zoomSlider1.setSize(200, 200);
		zoomSlider1.setVisible(true);
		zoomSlider1.addChangeListener(zoomPanel1.sliderMovement);
		mainPanel.add(zoomSlider1, BorderLayout.PAGE_END);
		
		mainPanel.setVisible(true);
	}
    
}
