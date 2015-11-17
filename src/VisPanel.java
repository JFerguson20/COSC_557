import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

public class VisPanel extends JPanel implements MouseListener, MouseMotionListener, ComponentListener {
	
	private int data[][];
	private float max, min;
	// defines the region inside the plot axes
	private Rectangle plotRectangle;
	// defines a border around the plot
	private int borderSize = 10; 
	// color of the data marks
	private boolean showGrid = true;
	private boolean antialiasEnabled = true;
	private Point mousePoint = new Point();
	
	// for drawing the plot offscreen for better efficiency
	private BufferedImage offscreenImage;
	private Graphics2D offscreenGraphics;
	
	private BufferedImage offscreenGrid;
	private Graphics2D gridGraphics;
	private BufferedImage resized;
	private Graphics2D resizedG2;
	private AffineTransform xform;
	
	public VisPanel (int[][] theData) {
		setData(theData);
		addComponentListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	// sets the data values and forces the panel to layout the boundaries and
	// compute data points
	public void setData(int[][] data) {
		this.data = data;
	
		//find min and max values
		max = Float.MIN_VALUE;
		min = Float.MAX_VALUE;
		for (int i = 0; i < data.length; i++) {
			for(int j = 0; j < data[0].length; j++){
				int value = data[i][j];
				if (value > max) {
					max = value;
				}
				if (value < min) {
					min = value;
				}
			}
		}
			
		
		//draw lines 
		        
		layoutPlot();
		//calculatePoints();
		repaint();
	}
	
	
	private void layoutPlot() {

		
		// forces the scatterplot to be square using the smaller dimension (width or height)
		int plotSize = getWidth();
		if (getHeight() < getWidth()) {
			plotSize = getHeight();
		}
		
		// centers the scatterplot in the middle of the panel
		int xOffset = (getWidth() - plotSize) / 2;
		int yOffset = (getHeight() - plotSize) / 2;
		
		// get the dimensions of the plot region for later use
		int left = borderSize + xOffset;
		int right = xOffset + (getWidth() - (borderSize*2));
		int bottom = yOffset + (getHeight() - (borderSize*2));
		int top = borderSize + yOffset;
		plotRectangle = new Rectangle(left, top, (right-left), (bottom-top));
		
		drawImage();
	}
	
	private void drawImage(){
		

		//make image size of data with each point being a pixel
		offscreenImage = new BufferedImage(data.length, data[0].length,
		        BufferedImage.TYPE_INT_ARGB);
		offscreenGraphics = offscreenImage.createGraphics();
		
		int cellSize = getWidth() / data.length;
		
		for (int x = 0; x < data.length; x++) {
			for (int y = 0; y < data[0].length; y++) {
				int value = data[x][y];
				//calculate what percentage of the max the value is
				double perc = value/max;
				offscreenGraphics.setColor(new Color(255, 255 -(int)(255*perc), 255-(int)(255*perc)));
				offscreenGraphics.fillRect(x,y, 1,1);
			}
		}
		//scale image here so we can reverse it.
		resized = new BufferedImage(data.length * 3, data[0].length *3,BufferedImage.TYPE_INT_ARGB);
		resizedG2 = resized.createGraphics();
		xform = AffineTransform.getScaleInstance(3.0, 3.0);
	    resizedG2.drawImage(offscreenImage, xform, null);
		
	}
	
	// converts x value to screen pixel location
	private int toScreenX(float value, float minValue, float maxValue, int offset, int plotWidth) {
		float norm = (value - minValue) / (maxValue - minValue);
		int x = offset + (int)(Math.round(norm * plotWidth));
		return x;
	}
	
	// converts y value to screen pixel location
	private int toScreenY(float value, float minValue, float maxValue, int offset, int plotHeight) {
		float normVal = 1.f - ((value - minValue) / (maxValue - minValue));
		int y = offset + (int)(Math.round(normVal * plotHeight));
		return y;
	}
	

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(getBackground());
		g2.fillRect(0, 0, getWidth(), getHeight());
		
		if (antialiasEnabled) {
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);				
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		}


		// code below draws in offscreen image for buffering and greater efficiency
	
		//offscreenImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
	    //g2.drawImage(offscreenImage, borderSize, borderSize, getWidth() - borderSize, getHeight() - borderSize, 0, 0,
	    	//	offscreenImage.getWidth(), offscreenImage.getHeight(), null);

		//Graphics2D offscreenImageGraphics = (Graphics2D)offscreenImage.getGraphics();
	    /*
		if (antialiasEnabled) {
			offscreenImageGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);				
			offscreenImageGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		}
		*/
	
		g2.drawImage(resized, borderSize, borderSize, this);
	
    
	    //g2.drawLine(50, 500, 50, 50);
		
	    if(showGrid){
		    drawVerticalLines(g2);
		    //drawHorizontalLines(g2);
	    }else{
	    	drawCrosshair(g2);
	    }
		

	}
	
	private void drawCrosshair(Graphics2D g2){
		
	}
	
	private void drawHorizontalLines(Graphics2D g2) {
		
	}
	
	private void drawVerticalLines(Graphics2D g2) {
		
	}
	
	
	public void toggleGrid() {
		showGrid = !showGrid;
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		
		if (plotRectangle != null) {
		
			mousePoint = e.getPoint();

		}
		
		//Do the inverse transform
		Point2D p = null;
		//System.out.println(mousePoint.getX());
		try {
			p = xform.inverseTransform(new Point2D.Double(mousePoint.getX() - borderSize, mousePoint.getY() - borderSize), null);
		} catch (NoninvertibleTransformException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("x="+p.getX()+" y="+p.getY());
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
		setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
	}

	@Override
	public void mouseExited(MouseEvent e) {
		setCursor(Cursor.getDefaultCursor());
	}

	@Override
	public void componentResized(ComponentEvent e) {
		
		layoutPlot();
		//calculateGrid();
		//calculatePoints();
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
