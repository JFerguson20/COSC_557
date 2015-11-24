import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.NoninvertibleTransformException;

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
	
	boolean zoomOut    = false;
	boolean zoomIn     = false;
	
	boolean allowZoomOut = true;
	boolean allowZoomIn  = true;
	
	public boolean updateFromSlider = false;
	public boolean updateFromKeys   = false;
	
	double sliderZoomVal = 0.5f;
	
	public ZoomSliderEvent sliderMovement    = new ZoomSliderEvent();
	public VisApp.UpdateSliderEvent updateSlider;
	
	public Matrix2DVis(Matrix2D mat) throws NoninvertibleTransformException {
		imagePanel = new VisPanel(mat); 
		
		initializeScrollPane();
	}
	
	private void initializeScrollPane() {
		this.setViewportView(imagePanel);
		this.setSize(new Dimension(imagePanel.getWidth(), imagePanel.getHeight()));
		
		addKeyStrokeEvents();
		toggleKeyStrokeEvents(true);
	}
	
	private void zoomIn() {
		
		this.setPreferredSize(new Dimension(imagePanel.getWidth(), imagePanel.getHeight()));

		imagePanel.applyZoom(currZoomFac*1.1f);
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
	}
	
	private void zoomOut() {	
		
		this.setPreferredSize(new Dimension(imagePanel.getWidth(), imagePanel.getHeight()));
		
		imagePanel.applyZoom(currZoomFac*0.9f);
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
    		grabFocus();
    		/*
    		System.out.println("currVal: " + currVal);
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
			sliderZoomVal = Math.max(0.00001, sliderZoomVal + newZoom);
    		if(sliderZoomVal > 1.0f) { allowZoomIn = false; sliderZoomVal = 1.0f;}
    		else { allowZoomIn = true; }
    		
    		if(sliderZoomVal == 0.00001) { allowZoomOut = false; sliderZoomVal = 0.00001;}
    		else { allowZoomOut = true; }
    	
    		updateSlider.updateSlider(sliderZoomVal);
		}
    }
    public interface ZoomListener {
        public void zoomChangedEvent(double newZoom);
    }
}
