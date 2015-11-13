import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JLabel;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

public class VisApp extends JPanel implements ActionListener {
	private JFrame appFrame;
	private VisPanel visPanel;
	JComboBox<String> yAxis;
	JComboBox<String> xAxis;
	JComboBox<String> thirdVariable;
	String yString = "";
	String xString = "";
	String zString = "";
	boolean csvLoaded = false;
	private JLabel lblNewLabel_1;

	public VisApp() {
		initialize();
		appFrame.setVisible(true);
	}

	private void initialize() {
		appFrame = new JFrame();
		appFrame.setTitle("Assignment 2 -- James Ferguson");
		appFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		appFrame.setBounds(100, 100, 1000, 700);

		initializePanel();
		initializeMenu();

	}

	private void initializeMenu() {
		JMenuBar menuBar = new JMenuBar();
		appFrame.setJMenuBar(menuBar);

		JMenu menu = new JMenu("File");
		menuBar.add(menu);
		JMenuItem mi = new JMenuItem("Open CSV...", KeyEvent.VK_O);
		mi.addActionListener(this);
		mi.setActionCommand("open csv");
		mi.setEnabled(true);
		menu.add(mi);

		menu.addSeparator();

		mi = new JMenuItem("Exit", KeyEvent.VK_X);
		mi.addActionListener(this);
		mi.setActionCommand("exit");
		menu.add(mi);
	}

	private void initializePanel() {
		Random rand = new Random();
		int max = 10;
		int min = 0;
		int[][] data = new int[50][50];
		for (int i = 0; i < data.length; i++){
			for (int j = 0; j < data.length; j++){
				data[i][j] = rand.nextInt((max - min) + 1) + min;
			}
		}
		
		/*
		int[][] data = new int[][] {{1, 3, 2, 0},
			{0, 2, 0, 0},
			{1, 0 ,0, 2}};
		*/
		//Color[] colors = new Color[] {Color.green, Color.red, Color.blue, Color.white};
		visPanel = new VisPanel(data);
		JPanel mainPanel = (JPanel) appFrame.getContentPane();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(visPanel, BorderLayout.CENTER);
		visPanel.setLayout(null);
		/*
		visPanel.setBackground(Color.white);
		visPanel.setForeground(Color.darkGray);



		// Create the combo box, select item at index 4.
		// Indices start at 0, so 4 specifies the pig.
		yAxis = new JComboBox<String>();
		yAxis.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent event) {

				if (csvLoaded) {
					if (event.getStateChange() == ItemEvent.SELECTED) {
						yString = (String) yAxis.getSelectedItem();
					}
					visPanel.changeLabels(yString, xString, zString);
				}
			}
		});
		yAxis.setBounds(10, 71, 100, 25);

		xAxis = new JComboBox<String>();
		xAxis.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent event) {
				if (csvLoaded) {
					if (event.getStateChange() == ItemEvent.SELECTED) {
						xString = (String) xAxis.getSelectedItem();
					}
					visPanel.changeLabels(yString, xString, zString);
				}
			}
		});
		xAxis.setBounds(10, 127, 100, 25);

		thirdVariable = new JComboBox<String>();
		thirdVariable.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent event) {
				if (csvLoaded) {
					if (event.getStateChange() == ItemEvent.SELECTED) {
						zString = (String) thirdVariable.getSelectedItem();
					}
					visPanel.changeLabels(yString, xString, zString);
				}
			}
		});
		thirdVariable.setBounds(10, 188, 100, 25);
		// add the combo boxes
		visPanel.add(yAxis);
		visPanel.add(xAxis);
		visPanel.add(thirdVariable);
		JLabel lblYAxis = new JLabel("Y Axis: ");
		lblYAxis.setBounds(10, 46, 46, 14);
		visPanel.add(lblYAxis);

		JLabel lblNewLabel = new JLabel("X Axis: ");
		lblNewLabel.setBounds(10, 102, 46, 14);
		visPanel.add(lblNewLabel);

		lblNewLabel_1 = new JLabel("Third Variable:");
		lblNewLabel_1.setBounds(10, 163, 83, 14);
		visPanel.add(lblNewLabel_1);
		*/

	}

	public static void main(String args[]) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				VisApp app = new VisApp();
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		/*
		if (event.getActionCommand().equals("open csv")) {

			// read csv
			JFileChooser chooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV files", "csv");
			chooser.setFileFilter(filter);
			int returnVal = chooser.showOpenDialog(chooser);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				System.out.println("You chose to open this file: " + chooser.getSelectedFile().getPath());
			}

			File f = new File(chooser.getSelectedFile().getPath());
			ArrayList<ArrayList<Double>> rows = new ArrayList<ArrayList<Double>>();
			ArrayList<String> columnNames = new ArrayList<String>();
			try {
				CSVReader.readCSV(f, rows, columnNames);
			} catch (IOException e) {
				e.printStackTrace();
			}

			// Fill in comboboxs
			xAxis.removeAllItems();
			yAxis.removeAllItems();
			thirdVariable.removeAllItems();

			thirdVariable.addItem("None");
			for (String item : columnNames) {
				yAxis.addItem(item);
				xAxis.addItem(item);
				thirdVariable.addItem(item);
			}

			visPanel.setRows(rows);
			visPanel.setLabels(columnNames);

			yString = (String) yAxis.getSelectedItem();
			xString = (String) xAxis.getSelectedItem();
			zString = (String) thirdVariable.getSelectedItem();
			visPanel.changeLabels(yString, xString, zString);
			csvLoaded = true;

		} else if (event.getActionCommand().equals("exit")) {
			System.exit(0);
		}
		*/
	}
}
