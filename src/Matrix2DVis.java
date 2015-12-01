import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.NoninvertibleTransformException;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

@SuppressWarnings("serial")
public class Matrix2DVis extends JScrollPane {

	private VisPanel imagePanel;
	
	// internal zoom values

	double currZoomInFac  = 1.1f;
	double currZoomOutFac = 0.9f;
	
	boolean zoomOut    = false;
	boolean zoomIn     = false;
	
	boolean allowZoomOut = true;
	boolean allowZoomIn  = true;
	
	public boolean updateFromSlider = false;
	public boolean updateFromKeys   = false;
	
	//slider specific variable
	double sliderZoomVal = 0.0;
	double prevSliderVal = 0.0;	
	double sliderIncrement = 0.1;
	double minSliderVal	   = 0.0;
	double maxSliderVal	   = 2.0;
	
	public ZoomSliderEvent sliderMovement    = new ZoomSliderEvent();
	public VisApp.UpdateSliderEvent updateSlider;
	
	public Matrix2DVis(Matrix2D mat, VisApp mainApp) throws NoninvertibleTransformException {
		imagePanel = new VisPanel(mat, mainApp); 		
		initializeScrollPane();
	}
	
	private void initializeScrollPane() {
		this.setViewportView(imagePanel);
		this.setSize(new Dimension(imagePanel.getWidth(), imagePanel.getHeight()));
		
		addKeyStrokeEvents();
		toggleKeyStrokeEvents(true);
	}
	
	private void zoomIn() {
		
		if(!allowZoomIn) { return; }
		
		imagePanel.applyZoom(currZoomInFac);
		
		sliderMovement.zoomChangedEvent(sliderIncrement);
		
	    Point pos = this.getViewport().getViewPosition();
	    Point point = this.imagePanel.getMousePoint();

	    int newX = (int)(point.x*(currZoomInFac - 1f) + currZoomInFac*pos.x);
	    int newY = (int)(point.y*(currZoomInFac - 1f) + currZoomInFac*pos.y);
	    this.getViewport().setViewPosition(new Point(newX, newY));
	    this.imagePanel.setMousePoint(newX, newY);
	    
		imagePanel.revalidate();
		imagePanel.repaint();
		
		revalidate();
		repaint();
	}
	
	private void zoomOut() {	
		
		if(!allowZoomOut) { return; }
		
		imagePanel.applyZoom(currZoomOutFac);
		sliderMovement.zoomChangedEvent(-sliderIncrement);
		
		Point pos = this.getViewport().getViewPosition();
		Point point = this.imagePanel.getMousePoint();	    

	    int newX = (int)(point.x*(currZoomOutFac - 1f) + currZoomOutFac*pos.x);
	    int newY = (int)(point.y*(currZoomOutFac - 1f) + currZoomOutFac*pos.y);
	    this.getViewport().setViewPosition(new Point(newX, newY));
	    this.imagePanel.setMousePoint(newX, newY);
	    
		imagePanel.revalidate();
		imagePanel.repaint();
		
		revalidate();
		repaint();
	}
	
	private void toggleKeyStrokeEvents(Boolean flag) {
		InputMap inputMap = this.getInputMap();
		
		//add events
		if(flag == true) {
			//key stroke for zooming in
			inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, InputEvent.CTRL_MASK), "zoom in");
			inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, InputEvent.CTRL_MASK), "zoom out");
		}
		//remove events
		else {
			inputMap.remove(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, InputEvent.CTRL_MASK));
			inputMap.remove(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, InputEvent.CTRL_MASK));
		}
	}
	
	private void addKeyStrokeEvents() {
		this.getActionMap().put("zoom in", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				allowZoomOut = true;
				updateFromSlider = false;
				updateFromKeys   = true;
				zoomIn();
            }
        });
		this.getActionMap().put("zoom out", new AbstractAction() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) {
				allowZoomIn = true;
				updateFromSlider = false;
				updateFromKeys   = true;
				zoomOut();
            }
        });
	}
	
	public void setSliderListener(VisApp.UpdateSliderEvent listener) {
		updateSlider = listener;
	}
    public class ZoomSliderEvent implements ChangeListener, ZoomListener {
    	public void stateChanged(ChangeEvent e) {
    		
    		JSlider slider = (JSlider)e.getSource();
    		double currVal = (double)slider.getValue() / 100.f;
    		
    		//indicate that we are getting an update from slider movement itself
    		updateFromSlider = true;
    		
    		double amntChanged = Math.abs(currVal - prevSliderVal);
    		boolean doZoom = (amntChanged > sliderIncrement);
    		
    		//check if we need to zoom in or out
    		if(doZoom) {
    			if( (prevSliderVal < currVal)) 		{ zoomIn();  }
    			else if( (prevSliderVal > currVal))	{ zoomOut(); }
 
    			prevSliderVal = currVal;
    			sliderZoomVal = currVal;
    		}
    		
    		//set focus back to ctrl+ and ctrl- still work for zooming
    		grabFocus();
    	}

    	//this functions updates the slider when ctrl+ or ctrl- is pressed
		public void zoomChangedEvent(double newZoom) {
			//if(!updateFromSlider)
				sliderZoomVal =  sliderZoomVal + newZoom;
			
			//make sure we have a valid zoom
    		if(sliderZoomVal > maxSliderVal) { allowZoomIn = false; sliderZoomVal = maxSliderVal;}
    		else { allowZoomIn = true; }
    		
    		//make sure we have a valid zoom
    		if(sliderZoomVal < minSliderVal) { allowZoomOut = false; sliderZoomVal = minSliderVal;}
    		else { allowZoomOut = true; }
    	
    		//we don't want to continually update the slider if the slider sent this update here
    		if(!updateFromSlider)
    			updateSlider.updateSlider(sliderZoomVal);
		}
    }
    public interface ZoomListener {
        public void zoomChangedEvent(double newZoom);
    }
	public void clearAll() {
		imagePanel.clearAll();
	}

	public void remove(String selectedValue) {
		imagePanel.remove(selectedValue);
	}
	
	public void add(int idx) {
		imagePanel.add(idx);
	}

	public ArrayList<Integer> selectMatrix(Object[] selectedNames) {	
		return imagePanel.selectMatrix(selectedNames);
	}
}
