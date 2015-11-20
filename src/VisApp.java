import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

public class VisApp extends JPanel implements ActionListener {
	private JFrame appFrame;
	private VisPanel visPanel;

	public VisApp() {
		initialize();
		appFrame.setVisible(true);
	}

	private void initialize() {
		appFrame = new JFrame();
		appFrame.setTitle("Matrix Vis");
		appFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		appFrame.setBounds(100, 100, 1000, 700);

		initializePanel();
		initializeMenu();
	}

	private void initializeMenu() {
		JMenuBar menuBar = new JMenuBar();
		appFrame.setJMenuBar(menuBar);

		JMenu file = new JMenu("File");
		menuBar.add(file);
		JMenuItem mi = new JMenuItem("Open CSV...", KeyEvent.VK_O);
		mi.addActionListener(this);
		mi.setActionCommand("open csv");
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

		// Toggle Grid
		JMenuItem grid = new JMenuItem("Toggle Hover", KeyEvent.VK_T);
		grid.addActionListener(this);
		grid.setActionCommand("toggle hover");
		grid.setEnabled(true);
		options.add(grid);

		// Change colors
		JMenuItem colors = new JMenuItem("Change Colors", KeyEvent.VK_C);
		colors.addActionListener(this);
		colors.setActionCommand("change colors");
		colors.setEnabled(false);
		options.add(colors);
	}

	private void initializePanel() {

		// File f = new
		// File("./data/result/translated_Metabolism_PfamA.matrix.tsv");
		File f = new File("./data/result/translated_helix_turn_helix_PfamA.matrix.tsv");
		Matrix2D mat = null;
		try {
			mat = new Matrix2D(f);
			// data = new int[mat.getNumRows()][mat.getNumCols()];

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		visPanel = new VisPanel(mat);
		JPanel mainPanel = (JPanel) appFrame.getContentPane();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(visPanel, BorderLayout.CENTER);
		visPanel.setLayout(null);
		
		
		
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

		if (event.getActionCommand().equals("toggle hover")) {
			visPanel.toggleTooltip();
		} else if (event.getActionCommand().equals("change colors")) {

		} else if (event.getActionCommand().equals("exit")) {
			System.exit(0);
		}
	}
}
