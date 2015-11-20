import java.awt.Color;
import java.awt.Cursor;
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

	private Matrix2D wholeMatrix;
	private int max;
	// defines a border around the plot
	private int borderSize = 10;
	// color of the data marks
	private boolean showTooltip = true;
	private boolean antialiasEnabled = true;
	private Point mousePoint = new Point();
	private Point2D mouseScaled = null; // index of row and col
	// for drawing the plot offscreen for better efficiency
	private BufferedImage offscreenImage;
	private Graphics2D offscreenGraphics;
	// for scaling the heatmap
	private BufferedImage resized;
	private Graphics2D resizedG2;
	private AffineTransform xform;

	// what we are hovering over
	private int cellRow = 0;
	private int cellCol = 0;

	// can use this to keep index's of rows we selected.
	private ArrayList<Integer> selectedRows;

	// labels to show hovered over points
	JLabel valLabel;
	JLabel genomeLabel;
	JLabel pfamLabel;

	public VisPanel(Matrix2D mat) {
		wholeMatrix = mat;
		selectedRows = new ArrayList<Integer>();
		addComponentListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		max = mat.getMaxVal();
		mat.getMinVal();
		valLabel = new JLabel();
		genomeLabel = new JLabel();
		pfamLabel = new JLabel();
		add(valLabel);
		add(genomeLabel);
		add(pfamLabel);
		drawImage();
		repaint();
	}

	// call when the data changes
	private void drawImage() {

		int numCols = wholeMatrix.getNumCols();
		int numRows = wholeMatrix.getNumRows();

		// make image size of data with each point being a pixel
		offscreenImage = new BufferedImage(numCols, numCols, BufferedImage.TYPE_INT_ARGB);
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

	// call when window size is changed.
	private void scaleImage() {
		int numCols = wholeMatrix.getNumCols();
		int numRows = wholeMatrix.getNumRows();
		// scale the image
		double widthScaled = (1.0 * getWidth() - (borderSize * 2)) / (1.0 * numCols);
		double heightScaled = (1.0 * getHeight() - (borderSize * 2)) / (1.0 * numRows);

		int width = (int) (widthScaled * numCols);
		int height = (int) (heightScaled * numRows);
		if (width <= 0 || height <= 0) {
			width = 1;
			height = 1;
		}
		// scale image here so we can reverse it.
		resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		resizedG2 = resized.createGraphics();
		xform = AffineTransform.getScaleInstance(widthScaled, heightScaled);
		resizedG2.drawImage(offscreenImage, xform, null);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(getBackground());
		g2.fillRect(0, 0, getWidth(), getHeight());

		if (antialiasEnabled) {
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			resizedG2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			resizedG2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		}

		g2.drawImage(resized, borderSize, borderSize, this);

		// g2.setColor(Color.BLACK);
		// g2.drawLine(50, 500, 50, 50);

		highlightSelectRows(g2);
		// make sure mouse is on plot and showTooltip is true
		if ((cellRow >= 0 && cellCol >= 0) && cellCol < wholeMatrix.getNumCols() && cellRow < wholeMatrix.getNumRows()){
			if(showTooltip)
				drawLabels(g2);
			drawHorizontalLine(g2, cellRow);
			drawVerticalLine(g2, cellCol);
		}
			
	}

	// draw the count, genome name, and pfam when hovering over a point
	private void drawLabels(Graphics2D g2) {
		g2.setColor(Color.BLACK);
		Font labelFont = valLabel.getFont();
		// draw rectangle to put our values in
		g2.drawRect(mousePoint.x + 15, mousePoint.y - 75, 100, 55);
		g2.setColor(new Color(100, 100, 100, 50));
		g2.fillRect(mousePoint.x + 15, mousePoint.y - 75, 100, 55);
		// draw value
		valLabel.setText("Count: " + Integer.toString(wholeMatrix.getPFamCount(cellRow, cellCol)));
		valLabel.setFont(new Font(labelFont.getName(), Font.PLAIN, 15));
		valLabel.setBounds(mousePoint.x + 15, mousePoint.y - 75, 75, 15);
		// draw genome
		genomeLabel.setText(wholeMatrix.getGenomeName(cellRow));
		genomeLabel.setFont(new Font(labelFont.getName(), Font.PLAIN, 15));
		genomeLabel.setBounds(mousePoint.x + 15, mousePoint.y - 55, 100, 15);
		// draw pfam
		pfamLabel.setText(wholeMatrix.getPFamName(cellCol));
		pfamLabel.setFont(new Font(labelFont.getName(), Font.PLAIN, 15));
		pfamLabel.setBounds(mousePoint.x + 15, mousePoint.y - 35, 75, 15);
	}

	private void highlightSelectRows(Graphics2D g2) {
		for (int row : selectedRows) {
			System.out.println("HERE");
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
			bot = (int) y;
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

	@Override
	public void mouseDragged(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {

		mousePoint = e.getPoint();

		// Do the inverse transform
		mouseScaled = null;
		// System.out.println(mousePoint.getX());
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
