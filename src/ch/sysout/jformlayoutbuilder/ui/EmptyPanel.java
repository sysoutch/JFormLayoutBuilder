package ch.sysout.jformlayoutbuilder.ui;

import java.awt.event.MouseListener;

import javax.swing.JPanel;

public class EmptyPanel extends JPanel{
	private static final long serialVersionUID = 1L;

	public EmptyPanel() {
		//		super(new BorderLayout());
	}

	public void addPanelSelectionListener(MouseListener l) {
		addMouseListener(l);
	}
}
