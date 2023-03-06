package ch.sysout.jformlayoutbuilder.ui;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

public class LayoutComponents {

	private List<List<Component>> components = new ArrayList<>();

	public Component getComponentAt(int row, int col) {
		return components.get(row).get(col);
	}
}
