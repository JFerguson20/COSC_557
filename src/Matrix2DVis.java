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
	double maxZoomFac  = 4.0f;
	double minZoomFac  = 1.0f / maxZoomFac;
	double currZoomFac = 1.0f;
	double prevZoomFac = 1.0f;
	double zoomPerc    = 50.0f / 100.0f; //percent we zoom in or out
	double prevSliderVal = 1.0;
	
	boolean zoomOut    = false;
	boolean zoomIn     = false;
	
	boolean allowZoomOut = true;
	boolean allowZoomIn  = true;
	
	public boolean updateFromSlider = false;
	public boolean updateFromKeys   = false;
	
	double sliderZoomVal = 0.5f;
	
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
		
		currZoomFac = 1.1;
		imagePanel.applyZoom(1.1);
		if(!updateFromSlider) {
			sliderMovement.zoomChangedEvent(0.1f);
		}
		
			
	    Point pos = this.getViewport().getViewPosition();
	    Point point = this.imagePanel.getMousePoint();
	    System.out.println("x: " + point.x + "\ty: " + point.y);

	    int newX = (int)(point.x*(1.1f - 1f) + 1.1f*pos.x);
	    int newY = (int)(point.y*(1.1f - 1f) + 1.1f*pos.y);
	    this.getViewport().setViewPosition(new Point(newX, newY));
	    this.imagePanel.setMousePoint(newX, newY);
	    
		imagePanel.revalidate();
		imagePanel.repaint();
		
		revalidate();
		repaint();
	}
	
	private void zoomOut() {	
		
		//this.setPreferredSize(new Dimension(imagePanel.getWidth(), imagePanel.getHeight()));
		currZoomFac = 0.9;
		imagePanel.applyZoom(0.9f);
		if(!updateFromSlider) {
			sliderMovement.zoomChangedEvent(-0.1f);
		}
		
		Point pos = this.getViewport().getViewPosition();
		Point point = this.imagePanel.getMousePoint();	    

	    int newX = (int)(point.x*(0.9f - 1f) + 0.9f*pos.x);
	    int newY = (int)(point.y*(0.9f - 1f) + 0.9f*pos.y);
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
				if(allowZoomIn) {
					updateFromSlider = false;
					updateFromKeys   = true;
					zoomIn();
				}
            }
        });
		this.getActionMap().put("zoom out", new AbstractAction() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) {
				allowZoomIn = true;
				if(allowZoomOut) {
					updateFromSlider = false;
					updateFromKeys   = true;
					zoomOut();
				}
            }
        });
	}
	
	public void setSliderListener(VisApp.UpdateSliderEvent listener) {
		updateSlider = listener;
	}
    public class ZoomSliderEvent implements ChangeListener, ZoomListener {
    	public void stateChanged(ChangeEvent e) {
    		//if(updateFromKeys) { return; }
    		
    		JSlider slider = (JSlider)e.getSource();
    		double currVal = (double)slider.getValue() / 100.f;
    		
    		System.out.println("currVal: " + currVal);
    		System.out.println("prev Val: " + prevSliderVal);
    		updateFromSlider = true;
    		if(prevSliderVal < currVal) 		{ zoomIn();  }
    		else if(prevSliderVal > currVal)	{ zoomOut(); }
    		
    		prevSliderVal = currVal;
    		
    		grabFocus();
    		/*
    		if(currVal > sliderZoomVal && allowZoomIn) { 
    			zoomIn(); 
    		}
    		else if(allowZoomOut) { 
    			zoomOut(); 
    		}
    		*/
    	}
		@Override
		public void zoomChangedEvent(double newZoom) {
			System.out.println("zoomChangedEvent");
			sliderZoomVal = Math.max(0.00001, sliderZoomVal + newZoom);
    		if(sliderZoomVal > 1.0f) { allowZoomIn = false; sliderZoomVal = 1.0f;}
    		else { allowZoomIn = true; }
    		
    		if(sliderZoomVal == 0.00001) { allowZoomOut = false; sliderZoomVal = 0.00001;}
    		else { allowZoomOut = true; }
    	
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

	public ArrayList<Integer> selectMatrix(Object[] selectedNames) {	
		return imagePanel.selectMatrix(selectedNames);
	}
}
