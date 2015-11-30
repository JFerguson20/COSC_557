import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class VisPanel extends JPanel implements MouseListener, MouseMotionListener, ComponentListener {

	private VisApp mainApp; // so we can call functions from visApp
	private Matrix2D wholeMatrix;

	// defines a border around the plot
	private int borderSize = 15;
	// color of the data marks
	private boolean showTooltip = true;
	private boolean antialiasEnabled = true;
	private Point mousePoint = new Point();
	private Point2D mouseScaled = null; // index of row and col
	
	// for drawing the plot offscreen for better efficiency
	private BufferedImage offscreenImage;
	private Graphics2D offscreenGraphics;

	//transform for inverting coordinates for the mouse
	private AffineTransform xform;

	// what we are hovering over
	private int cellRow = 0;
	private int cellCol = 0;
	

	private double zoomPerc = 1.0f;
	private double zoomPercPrev = 1.0f;
	private double zoomWidth = 1.0f;
	private double zoomHeight = 1.0f;
	
	private Dimension prevPrefSize = new Dimension();

	// can use this to keep index's of rows we selected.
	private ArrayList<Integer> selectedRows;

	// labels to show hovered over points
	JLabel valLabel;
	JLabel genomeLabel;
	JLabel pfamLabel;

	public VisPanel(Matrix2D mat, VisApp mainApp) throws NoninvertibleTransformException {

		//intialize big matrix and array for smaller matrices
		wholeMatrix = mat;
		selectedRows = new ArrayList<Integer>();
		
		//init listeners
		addComponentListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		
		//create labels for visualization
		valLabel = new JLabel();
		genomeLabel = new JLabel();
		pfamLabel = new JLabel();
		
		//add labels
		add(valLabel);
		add(genomeLabel);
		add(pfamLabel);
		
		drawImage();
		repaint();
		
		//init link back to main app
		this.mainApp = mainApp;
	}

	// call when the data changes
	private void drawImage() throws NoninvertibleTransformException {

		int numCols = wholeMatrix.getNumCols();
		int numRows = wholeMatrix.getNumRows();
		
		int max = wholeMatrix.getMaxVal();
		
		// make image size of data with each point being a pixel
		offscreenImage = new BufferedImage(numCols, numRows, BufferedImage.TYPE_INT_ARGB);
		offscreenGraphics = offscreenImage.createGraphics();

		for (int i = 0; i < numCols; i++) {
			for (int j = 0; j < numRows; j++) {
				int value = wholeMatrix.getPFamCount(j, i);

				// calculate what percentage of the max the value is
				double perc = (value * 1.0) / (1.0 * max);
				offscreenGraphics.setColor(new Color(255, 255 - (int) (255 * perc), 255 - (int) (255 * perc)));
				offscreenGraphics.fillRect(i, j, 1, 1);
			}
		}
		scaleImage();
	}
	
	public double getZoomScale() {
		return zoomPerc;
	}
	
	public void applyZoom(double zoomPercentage) {
		zoomPerc = zoomPercentage;
		scaleImage();
		zoomPercPrev = zoomPercentage;
	}

	private double getWidthScaleFactor() {
		int numCols = wholeMatrix.getNumCols();
		
		// scale the width
		double widthScaled = (1.0 * getWidth() - (borderSize * 2)) / (1.0 * numCols);
		return widthScaled;
		
	}
	
	private double getHeightScaleFactor() {
		int numRows = wholeMatrix.getNumRows();
		
		// scale the width
		double heightScaled = (1.0 * getHeight() - (borderSize * 2)) / (1.0 * numRows);		
		return heightScaled;
	}
	
	private double getAspectRatio() {
		double widthScale  = getWidthScaleFactor();
		double heightScale = getHeightScaleFactor();
		
		int numCols = wholeMatrix.getNumCols();
		int numRows = wholeMatrix.getNumRows();
		
		if(numRows < 10) {
			if(numRows > numCols) { return widthScale; }
			else				  { return heightScale;  }
		}
		else {
			if(widthScale < heightScale) { return heightScale; }
			else						 { return widthScale;  }			
		}
	}
	
	// call when window size is changed.
	private void scaleImage()  {
		
		//get the aspect ratio
		double ratio = getAspectRatio() * zoomPerc;

		//get scaled dimensions
		int width  = (int) (ratio * (double)wholeMatrix.getNumCols()) ;
		int height = (int) (ratio * (double)wholeMatrix.getNumRows()) ;
		
		//weird but seems to be necessary
		if (width <= 0 || height <= 0) { width = 1; height = 1;	}
		zoomWidth  = width;
		zoomHeight = height;
		zoomPerc = 1.0;
		
		//set the transform for inverting mouse points
		xform = AffineTransform.getScaleInstance(ratio, ratio);

		this.setPreferredSize(new Dimension((int)zoomWidth + (borderSize*2), (int)zoomHeight + (borderSize*2)));
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(getBackground());
		g2.fillRect(0, 0, getWidth(), getHeight());

		if (antialiasEnabled) {
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		}

		g2.drawImage(offscreenImage, borderSize, borderSize, (int)zoomWidth, (int)zoomHeight, null);

		highlightSelectRows(g2);
		// make sure mouse is on plot and showTooltip is true
		if ((cellRow >= 0 && cellCol >= 0) && cellCol < wholeMatrix.getNumCols() && cellRow < wholeMatrix.getNumRows()){
			if(showTooltip)
				drawLabels(g2);
			drawHorizontalLine(g2, cellRow);
			drawVerticalLine(g2, cellCol);
		}
		else
		{
			valLabel.setText("");			
			// draw genome
			genomeLabel.setText("");			
			// draw pfam
			pfamLabel.setText("");

		}
			
	}

	// draw the count, genome name, and pfam when hovering over a point
	private void drawLabels(Graphics2D g2) {
		g2.setColor(Color.BLACK);
		Font labelFont = valLabel.getFont();
		
		int x = mousePoint.x + 15;
		
		int rectY = mousePoint.y - 75;
		int valY = mousePoint.y - 75;
		int genomeY = mousePoint.y - 55;
		int pfamY = mousePoint.y - 35;
		
		//if on right side
		if(mousePoint.x + 120 > getWidth())
		{
			x = mousePoint.x - 115;
		}
		//if on top side
		if(mousePoint.y - 80 < 0) 
		{
			rectY = mousePoint.y + 15;
			valY = mousePoint.y + 15;
			genomeY = mousePoint.y + 35;
			pfamY = mousePoint.y + 55;
			System.out.println("Top Side");
		}
			
		// draw rectangle to put our values in
		g2.drawRect(x, rectY, 100, 55);
		g2.setColor(new Color(100, 100, 100, 50));
		g2.fillRect(x, rectY, 100, 55);
		
		// draw value
		valLabel.setText("Count: " + Integer.toString(wholeMatrix.getPFamCount(cellRow, cellCol)));
		valLabel.setFont(new Font(labelFont.getName(), Font.PLAIN, 15));
		valLabel.setBounds(x, valY, 75, 15);
		
		// draw genome
		genomeLabel.setText(wholeMatrix.getGenomeName(cellRow));
		genomeLabel.setFont(new Font(labelFont.getName(), Font.PLAIN, 15));
		genomeLabel.setBounds(x, genomeY, 100, 15);
		
		// draw pfam
		pfamLabel.setText(wholeMatrix.getPFamName(cellCol));
		pfamLabel.setFont(new Font(labelFont.getName(), Font.PLAIN, 15));
		pfamLabel.setBounds(x, pfamY, 75, 15);

	}
	
	public Point getMousePoint() {
		Point p = new Point(cellCol, cellRow);
		return p;
	}
	
	public void setMousePoint(int x, int y) {
		cellCol = x;
		cellRow = y;
		revalidate();
		repaint();
	}

	private void highlightSelectRows(Graphics2D g2) {
		for (int row : selectedRows) {
			drawHorizontalLine(g2, row);
		}
	}

	private void drawHorizontalLine(Graphics2D g2, int row) {
		Point2D inv = null;
		boolean foundTop = false;
		boolean foundBot = false;
		int top = 0;
		int bot = 0;
		
		try {
			double x = 0;
			double y = xform.transform(new Point2D.Double(x, row), null).getY() + borderSize;
			
			// go up until we get a different cell.
			while (!foundTop) {
				inv = xform.inverseTransform(new Point2D.Double(x - borderSize, y - borderSize), null);

				int newRow = (int) inv.getY();

				if (row != newRow)
					foundTop = true;
				else
					--y;
			}
			top = (int) y;
			y = xform.transform(new Point2D.Double(x, row), null).getY() + borderSize;
			while (!foundBot) {
				inv = xform.inverseTransform(new Point2D.Double(x - borderSize, y - borderSize), null);

				int newRow = (int) inv.getY();
				if (row != newRow)
					foundBot = true;
				else
					++y;
			}
			bot = (int) y; if(bot == top) { bot++; }
		} catch (NoninvertibleTransformException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		g2.setColor(Color.BLACK);
		g2.drawLine(borderSize, top, getWidth() - borderSize, top);
		g2.drawLine(borderSize, bot, getWidth() - borderSize, bot);
		
	}

	private void drawVerticalLine(Graphics2D g2, int col) {
		Point2D inv = null;
		boolean foundLeft = false;
		boolean foundRight = false;
		int left = 0;
		int right = 0;
		
		try {
			double x = mousePoint.getX();
			double y = 0.0;

			// go up until we get a different cell.
			while (!foundLeft) {
				inv = xform.inverseTransform(new Point2D.Double(x - borderSize, y - borderSize), null);

				int newCol = (int) inv.getX();

				if (cellCol != newCol)
					foundLeft = true;
				else
					--x;
			}
			left = (int) x;
			x = mousePoint.getX();
			while (!foundRight) {
				inv = xform.inverseTransform(new Point2D.Double(x - borderSize, y - borderSize), null);

				int newCol = (int) inv.getX();
				if (cellCol != newCol)
					foundRight = true;
				else
					++x;
			}
			right = (int) x;
			if(right == left) { right++; }
		} catch (NoninvertibleTransformException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		g2.setColor(Color.BLACK);
		g2.drawLine(left, borderSize, left, getHeight() - borderSize);
		g2.drawLine(right, borderSize, right, getHeight() - borderSize);
	}

	public void toggleTooltip() {
		showTooltip = !showTooltip;
		valLabel.setText(null);
		genomeLabel.setText(null);
		pfamLabel.setText(null);
	}
	
	//called from main from selectedPanel
	public ArrayList<Integer> selectMatrix(Object[] selectedNames) {	
		//return rows that are selected
		return selectedRows;
	}
	//called from main from selectedPanel
	public void clearAll() {
		selectedRows.clear();
		repaint();
	}
	
	//called from main from selectedPanel
	public void remove(String nameToRemove) {
		for(int i = 0; i < selectedRows.size(); i++){
			String genomeName = wholeMatrix.getGenomeName(selectedRows.get(i));
			if(genomeName.equals(nameToRemove)){
				selectedRows.remove(i);
			}
		}
		repaint();
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {

		mousePoint = e.getPoint();

		// Do the inverse transform
		mouseScaled = null;
		//System.out.println(mousePoint.getX());
		try {
			mouseScaled = xform.inverseTransform(
					new Point2D.Double(mousePoint.getX() - borderSize, mousePoint.getY() - borderSize), null);
		} catch (NoninvertibleTransformException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// System.out.println("x="+mouseScaled.getX()+" y="+mouseScaled.getY());
		cellRow = (int) mouseScaled.getY();
		cellCol = (int) mouseScaled.getX();

		// may need to remove this later so we arent always redrawing
		repaint();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		String genomeName = null;
		if(!selectedRows.remove(Integer.valueOf(cellRow))){
			selectedRows.add(cellRow);
			genomeName = wholeMatrix.getGenomeName(cellRow);
			mainApp.rowSelected(genomeName);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// Transparent 16 x 16 pixel cursor image.
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

		// Create a new blank cursor.
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
		    cursorImg, new Point(0, 0), "blank cursor");
		setCursor(blankCursor);
		//setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
	}

	@Override
	public void mouseExited(MouseEvent e) {
		setCursor(Cursor.getDefaultCursor());
		
	}

	@Override
	public void componentResized(ComponentEvent e) {
		scaleImage();
		repaint();
		revalidate();
	}

	@Override
	public void componentMoved(ComponentEvent e) {
	}

	@Override
	public void componentShown(ComponentEvent e) {
	}

	@Override
	public void componentHidden(ComponentEvent e) {
	}





}
