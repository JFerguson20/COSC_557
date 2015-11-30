import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.NoninvertibleTransformException;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;

@SuppressWarnings("serial")
public class SelectPanel extends JPanel
		implements ActionListener, MouseListener, MouseMotionListener, ComponentListener {
	private VisApp mainApp;
	private JScrollPane scrollPane;
	private JList<String> list;
	private DefaultListModel<String> listModel;
	private JButton selectBtn;
	private JButton clearBtn;
	private JButton removeBtn;
	private ArrayList<String> allGenomeNames;
	public SelectPanel(VisApp mainApp) {
		this.mainApp = mainApp;
		allGenomeNames = mainApp.getAllGenomeNames();
		listModel = new DefaultListModel<String>();
		// add(valLabel);
		list = new JList<String>(listModel);
		scrollPane = new JScrollPane(list);
		add(scrollPane);
		// buttons
		selectBtn = new JButton("Select");
		clearBtn = new JButton("Clear All");
		removeBtn = new JButton("Remove Selected");
		selectBtn.setActionCommand("select");
		clearBtn.setActionCommand("clear");
		removeBtn.setActionCommand("remove");
		selectBtn.addActionListener(this);
		clearBtn.addActionListener(this);
		removeBtn.addActionListener(this);

		add(selectBtn);
		add(clearBtn);
		add(removeBtn);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		int width = getWidth();
		int height = getHeight();

		// paint scrollPane
		// 50% down to 10% down
		int top = (int)(height * .1) + 10;
		System.out.println(top);
		
		int bot = (int)(height * .8) - 10;
		System.out.println(bot);
		scrollPane.setBounds(5, top, width - 10, bot);

		// position buttons
		// clear and remove on top of the scrollPane
		clearBtn.setBounds(5, 5, (width / 2) - 10, (int) (height * .1) - 10);
		removeBtn.setBounds(width / 2 + 5, 5, (width / 2) - 10, (int) (height * .1) - 10);
		// select on the whole bottom
		selectBtn.setBounds(5, (int) (height * .9) + 5, width - 10, height - (int) (height * .9) - 10);

	}

	public void addSelectedItem(String name) {
		listModel.addElement(name);
	}

	public void removeItem(String name) {
		listModel.removeElement(name);
	}

	public void clearSelectedItems() {
		listModel.clear();
	}

	public void actionPerformed(ActionEvent e) {
		if ("select".equals(e.getActionCommand())) {
			try {
				mainApp.select(listModel.toArray());
			} catch (NoninvertibleTransformException e1) {
				e1.printStackTrace();
			}
		} else if ("clear".equals(e.getActionCommand())) {
			mainApp.clearAll();
			listModel.removeAllElements();
		} else if ("remove".equals(e.getActionCommand())) {
			mainApp.remove(list.getSelectedValue());
			listModel.removeElement(list.getSelectedValue());
		}
	}

	@Override
	public void componentHidden(ComponentEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void componentMoved(ComponentEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void componentResized(ComponentEvent arg0) {
		System.out.println(getWidth());
		System.out.println(getHeight());

	}

	@Override
	public void componentShown(ComponentEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

}
