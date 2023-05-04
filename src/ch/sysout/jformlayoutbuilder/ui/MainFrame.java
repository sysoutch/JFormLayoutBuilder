package ch.sysout.jformlayoutbuilder.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import com.jgoodies.forms.debug.FormDebugPanel;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class MainFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	private static final String TITLE = "JFormLayout Builder";

	private JButton btnAddRow;
	private JButton btnAddColumn;
	private JButton btnRemoveRow;
	private JButton btnRemoveColumn;
	private JButton btnAddComponent;

	private JSpinner selectedColumn;
	private JSpinner selectedRow;

	private JCheckBox chkShowGrid;
	private JCheckBox chkPaintRows;
	private JButton btnGridColor;

	private JSpinner spanColumns;
	private JSpinner spanRows;

	private FormLayout formLayout;
	private FormDebugPanel pnlMain;
	private LayoutComponents layoutComponents;

	private JTextArea txtPreviewLayout;

	private Component currentSelectedComp;

	// Dummy components
	private JLabel lblDummy;
	private JButton btnDummy;
	private JCheckBox chkDummy;
	private JRadioButton rdbDummy;
	private JTextField txtDummy;
	private JComboBox<String> cmbDummy;
	private JList<String> lstDummy;
	private FormDebugPanel pnlDummy;

	private List<Component> dummyComponents;

	private LayoutManager layout;

	private Color currentColor;

	protected Component currentSelectedComponentInGrid;

	protected boolean dragging;

	public MainFrame() {
		super(TITLE);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(1024, 600));
		initComponents();
		createUI();
		pack();
	}

	private void initComponents() {
		currentColor = Color.GREEN;

		btnAddRow = new JButton("Add Row");
		btnAddColumn = new JButton("Add Column");
		btnRemoveRow = new JButton("Remove Row");
		btnRemoveColumn = new JButton("Remove Column");
		btnAddComponent = new JButton("Add Component");
		selectedColumn = new JSpinner();
		selectedRow = new JSpinner();
		selectedColumn.setValue(1);
		selectedRow.setValue(1);
		chkShowGrid = new JCheckBox("Show Grid");
		chkPaintRows = new JCheckBox("Paint Rows");
		btnGridColor = new JButton(" ");
		chkShowGrid.setSelected(true);
		chkPaintRows.setSelected(true);
		spanRows = new JSpinner();
		spanColumns = new JSpinner();
		spanRows.setValue(1);
		spanColumns.setValue(1);

		layoutComponents = new LayoutComponents();


		lblDummy = new JLabel("Label");
		btnDummy = new JButton("Button");
		chkDummy = new JCheckBox("Check Box");
		rdbDummy = new JRadioButton("Radio Button");
		txtDummy = new JTextField("Text Field");
		cmbDummy = new JComboBox<String>();
		DefaultListModel<String> mdlLstDummy = new DefaultListModel<>();
		mdlLstDummy.addElement("Element 0");
		mdlLstDummy.addElement("Element 1");
		mdlLstDummy.addElement("Element 2");
		lstDummy = new JList<String>(mdlLstDummy);
		pnlDummy = new FormDebugPanel();
		dummyComponents = new ArrayList<>();
		dummyComponents.add(lblDummy);
		dummyComponents.add(btnDummy);
		dummyComponents.add(chkDummy);
		dummyComponents.add(rdbDummy);
		dummyComponents.add(txtDummy);
		dummyComponents.add(cmbDummy);
		dummyComponents.add(lstDummy);
		dummyComponents.add(pnlDummy);
		addListeners();
	}

	private void addListeners() {
		btnAddRow.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				addRow();
			}
		});
		btnAddColumn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				addColumn();
			}
		});

		btnRemoveRow.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				removeLastRow();
			}
		});

		btnRemoveColumn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				removeLastColumn();
			}
		});

		btnAddComponent.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Component comp = currentSelectedComp;
				int col = (int) selectedColumn.getValue();
				int row = (int) selectedRow.getValue();
				int width = (int) spanColumns.getValue();
				int height = (int) spanRows.getValue();
				boolean makeCopy = true;
				if (makeCopy) {
					comp = cloneSwingComponent(comp);
					currentSelectedComp = comp;
				}
				addComponent(comp, col, row, width, height, true);
				revalidateAndRepaint();
			}
		});

		chkShowGrid.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				boolean selected = chkShowGrid.isSelected();
				setGridVisible(selected);
			}
		});

		chkPaintRows.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				boolean selected = chkPaintRows.isSelected();
				setPaintRows(selected);
			}
		});

		btnGridColor.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JColorChooser chooser = new JColorChooser();
				Color color = JColorChooser.showDialog(chooser, "", Color.GREEN);
				System.out.println("color: " + color);
				btnGridColor.setBackground(color);
				setGridColor(color);
			}
		});

		for (Component comp : dummyComponents) {
			comp.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					currentSelectedComp = cloneSwingComponent(comp);
				}
			});
		}
	}

	private Component cloneSwingComponent(Component c) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(c);
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			return (Component) ois.readObject();
		} catch (IOException | ClassNotFoundException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	protected void setGridColor(Color color) {
		setGridColor(color, true);
	}

	protected void setGridColor(Color color, boolean saveCurrentColor) {
		if (saveCurrentColor) {
			currentColor = color;
		}
		pnlMain.setGridColor(color);
		revalidateAndRepaint();
	}

	protected void setGridVisible(boolean gridVisible) {
		if (gridVisible) {
			if (chkPaintRows.isSelected()) {
				setPaintRows(true);
			}
		} else {
			setPaintRows(false);
		}
		setGridColor(gridVisible ? currentColor : pnlMain.getBackground(), gridVisible);
	}

	protected void setPaintRows(boolean paintRows) {
		pnlMain.setPaintRows(paintRows);
		revalidateAndRepaint();
	}

	protected void addRow() {
		formLayout.appendRow(RowSpec.decode("fill:pref"));
		for (int i = 1; i <= formLayout.getColumnCount(); i++) {
			EmptyPanel pnlEmpty = new EmptyPanel();
			addComponent(pnlEmpty, i, formLayout.getRowCount(), false);
			addPanelSelectionListener(pnlEmpty);
			addPanelHoverListener(pnlEmpty);
		}
		revalidateAndRepaint();
		//		int rowCount = formLayout.getRowCount();
		//		int columnCount = formLayout.getColumnCount();
		//		int row = rowCount;
		//		int col = 1;
		//		if (currentSelectedComp == null) {
		//			currentSelectedComp = new JButton("Button");
		//		}
		//		Component comp = currentSelectedComp;
		//		addComponent(comp, col, row, (int) spanColumns.getValue(), (int) spanRows.getValue());
		//		currentSelectedComp = cloneSwingComponent(currentSelectedComp);
		displayLayoutPreview();
	}

	protected void addColumn() {
		formLayout.appendColumn(ColumnSpec.decode("default:grow"));
		for (int i = 1; i <= formLayout.getRowCount(); i++) {
			EmptyPanel pnlEmpty = new EmptyPanel();
			addComponent(pnlEmpty, formLayout.getColumnCount(), i, false);
			addPanelSelectionListener(pnlEmpty);
			addPanelHoverListener(pnlEmpty);
		}
		revalidateAndRepaint();
		//		int row = formLayout.getRowCount();
		//		int col = 1;
		//		if (currentSelectedComp == null) {
		//			currentSelectedComp = new JButton("Button");
		//		}
		//		Component comp = currentSelectedComp;
		//		addComponent(comp, col, row, (int) spanColumns.getValue(), (int) spanRows.getValue());
		//		currentSelectedComp = cloneSwingComponent(currentSelectedComp);
		displayLayoutPreview();
	}

	private void addPanelSelectionListener(Component comp) {
		comp.addMouseListener(new MouseAdapter() {
			private Color noBackground;

			@Override
			public void mouseEntered(MouseEvent e) {
				System.out.println( "entered. dragging? " + dragging);
				if (currentSelectedComponentInGrid != null && dragging) {
					CellConstraints sourceConstraints = formLayout.getConstraints(currentSelectedComponentInGrid);
					int sourceX = sourceConstraints.gridX;
					int sourceY = sourceConstraints.gridY;


					CellConstraints constraints = formLayout.getConstraints(comp);
					int x = constraints.gridX;
					int y = constraints.gridY;
					System.out.println("current grid X " + x);
					System.out.println("current grid Y " + y);
					for (int i = sourceX; i <= formLayout.getColumnCount(); i++) {
						if (i <= x) {
							if (i != sourceX) {
								Component c1 = getComponentAtFormLayoutGridCell(i, y);
								c1.setBackground(Color.YELLOW);
								int newSpanColumnValue = (i - sourceX) + 1;
								System.out.println("span col: " + newSpanColumnValue);
								spanColumns.setValue(newSpanColumnValue);
							}
							for (int k = sourceY+1; k <= formLayout.getRowCount(); k++) {
								if (k <= y) {
									Component c2 = getComponentAtFormLayoutGridCell(i, k);
									c2.setBackground(Color.ORANGE);
									int newSpanRowValue = (k - sourceY) + 1;
									System.out.println("span row: " + newSpanRowValue);
									spanRows.setValue(newSpanRowValue);
								} else {
									Component c2 = getComponentAtFormLayoutGridCell(i, k);
									c2.setBackground(noBackground);
								}
							}
						} else {
							for (int l = 1; l <= formLayout.getColumnCount(); l++) {
								for (int m = 1; m <= sourceY-1; m++) {
									Component c3 = getComponentAtFormLayoutGridCell(l, m);
									c3.setBackground(noBackground);
								}
							}
							Component c3 = getComponentAtFormLayoutGridCell(i, y);
							c3.setBackground(noBackground);
						}
					}
					System.out.println("mouse dragged over component ");
					pnlMain.repaint();
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				dragging = false;
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (comp instanceof EmptyPanel) {
					if (noBackground == null) {
						noBackground = comp.getBackground();
					}
					comp.setBackground(comp.getBackground().equals(noBackground.brighter()) ? noBackground : noBackground.brighter());
				}
				currentSelectedComponentInGrid = comp;

				//				Component component = pnlMain.getComponent(
				//						formLayout.getConstraints(currentSelectedEmptyCellPanel).gridY * formLayout.getColumnCount() +
				//						formLayout.getConstraints(currentSelectedEmptyCellPanel).gridX);

				CellConstraints constraints = formLayout.getConstraints(currentSelectedComponentInGrid);
				int x = constraints.gridX;
				int y = constraints.gridY;
				System.out.println("current grid X " + x);
				System.out.println("current grid Y " + y);

				boolean spanToColEnd = false;
				boolean spanToRowEnd = false;
				if (spanToColEnd) {
					if ((int) spanColumns.getValue() > formLayout.getColumnCount() - x +1) {
						spanColumns.setValue(formLayout.getColumnCount() - x +1);
					}
				} else {
					spanColumns.setValue(1);
				}
				if (spanToRowEnd) {
					if ((int) spanRows.getValue() > formLayout.getRowCount() - y +1) {
						spanRows.setValue(formLayout.getRowCount() - y +1);
					}
				} else {
					spanRows.setValue(1);
				}
				selectedColumn.setValue(x);
				selectedRow.setValue(y);
				MainFrame.this.repaint();
			}
		});
	}

	private void addPanelHoverListener(EmptyPanel pnlEmpty) {
		pnlEmpty.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent e) {

			}

			@Override
			public void mouseDragged(MouseEvent e) {
				dragging = true;
			}
		});
	}

	protected void addComponent(Component comp, int col, int row) {
		addComponent(comp, col, row, true);
	}

	private void addComponent(Component comp, int col, int row, boolean addToEmptyPanel) {
		addComponent(comp, col, row, 1, 1, addToEmptyPanel);
	}

	private void addComponent(Component comp, int col, int row, int spanColumns, int spanRows, boolean addToEmptyPanel) {
		JPanel pnlToUse = (currentSelectedComponentInGrid instanceof FormDebugPanel ? (JPanel) currentSelectedComponentInGrid : pnlMain);
		if (addToEmptyPanel && currentSelectedComponentInGrid != null) {
			//			// check the row and column properties of label1's constraints
			//			CellConstraints label1Constraints = bla(currentSelectedEmptyCellPanel);
			//			int label1Row = label1Constraints.gridX;
			//			int label1Column = label1Constraints.gridY;
			//			System.out.println("Label 1 is in row " + label1Row + ", column " + label1Column);

			CellConstraints constraints = formLayout.getConstraints(currentSelectedComponentInGrid);
			int x = constraints.gridX;
			int y = constraints.gridY;

			//			Component component = null;
			//			if (y == 1) {
			//				component = pnlMain.getComponent(x -1);
			//			} else {
			//				component = pnlMain.getComponent(x + formLayout.getColumnCount() * (y - 1));
			//			}
			//			pnlMain.remove(component);

			for (int k = y; k <= (y + (spanRows-1)); k++) {
				for (int i = 1; i <= (x + (spanColumns-1)); i++) {
					if (k == y && i <= x) {
						continue;
					}
					Component c1 = getComponentAtFormLayoutGridCell(i, k, pnlToUse);
					if (c1 != null) {
						pnlToUse.remove(c1);
					}
				}
			}
			pnlToUse.remove(currentSelectedComponentInGrid);

			//			pnlMain.remove(CC.xy(col, row));
			currentSelectedComponentInGrid = null;
		}
		pnlToUse.add(comp, CC.xywh(col, row, spanColumns, spanRows));
		addPanelSelectionListener(comp);
	}

	private Component getComponentAtFormLayoutGridCell(int x, int y, JPanel pnl) {
		Component[] comps = pnl.getComponents();
		for (Component c : comps) {
			CellConstraints constraints = formLayout.getConstraints(c);
			if (constraints.gridX != x || constraints.gridY != y) {
				continue;
			}
			return c;
		}
		return null;
	}

	private Component getComponentAtFormLayoutGridCell(int x, int y) {
		return getComponentAtFormLayoutGridCell(x, y, pnlMain);
	}

	private CellConstraints bla(Component currentSelectedEmptyCellPanel2) {
		// get the constraints for label1
		CellConstraints label1Constraints = formLayout.getConstraints(currentSelectedEmptyCellPanel2);
		return label1Constraints;
	}

	protected void removeLastRow() {
		removeRow(formLayout.getRowCount());
	}

	protected void removeRow(int rowCount) {
		//		pnlMain.remove(layoutComponents.getComponentAt(row, col));
		formLayout.removeRow(formLayout.getRowCount());
		revalidateAndRepaint();
	}

	protected void removeLastColumn() {
		removeColumn(formLayout.getColumnCount());
	}

	protected void removeColumn(int columnCount) {
		//		pnlMain.remove(layoutComponents.getComponentAt(row, col));
		formLayout.removeColumn(formLayout.getColumnCount());
		revalidateAndRepaint();
	}

	private void createUI() {
		formLayout = new FormLayout("min:grow",
				"fill:pref");
		//		formLayout.setHonorsVisibility(false);
		pnlMain = new FormDebugPanel();
		pnlMain.setGridColor(currentColor);
		pnlMain.setLayout(formLayout);
		EmptyPanel pnlEmpty = new EmptyPanel();
		addComponent(pnlEmpty, 1, 1);
		addPanelSelectionListener(pnlEmpty);
		addPanelHoverListener(pnlEmpty);
		JPanel pnlTop = createTopPanel();
		JPanel pnlLeft = createLeftPanel();
		JPanel pnlRight = createRightPanel();
		add(pnlTop, BorderLayout.NORTH);
		JScrollPane spMain = new JScrollPane(pnlMain);
		add(spMain);
		add(pnlLeft, BorderLayout.WEST);
		add(pnlRight, BorderLayout.EAST);
	}

	private JPanel createTopPanel() {
		FormLayout layout = new FormLayout("m, m, m, m, m, m, m, m, m, m, m, m, m", "f:p");
		JPanel pnl = new JPanel(layout);
		int x = 1;
		pnl.add(btnAddRow, CC.xy(x++, 1));
		pnl.add(btnAddColumn, CC.xy(x++, 1));
		pnl.add(btnRemoveRow, CC.xy(x++, 1));
		pnl.add(btnRemoveColumn, CC.xy(x++, 1));
		pnl.add(btnAddComponent, CC.xy(x++, 1));
		pnl.add(selectedColumn, CC.xy(x++, 1));
		pnl.add(selectedRow, CC.xy(x++, 1));
		pnl.add(chkShowGrid, CC.xy(x++, 1));
		pnl.add(chkPaintRows, CC.xy(x++, 1));
		pnl.add(btnGridColor, CC.xy(x++, 1));
		pnl.add(spanColumns, CC.xy(x++, 1));
		pnl.add(spanRows, CC.xy(x++, 1));
		return pnl;
	}

	private JPanel createLeftPanel() {
		FormLayout layout = new FormLayout("m:g", "f:p, f:p, f:p, f:p, f:p, f:p, f:p, f:p, f:p, m");
		JPanel pnl = new JPanel(layout);

		FormLayout dummyLayout = new FormLayout("m:g", "f:m:g");
		pnlDummy.setLayout(dummyLayout);
		pnlDummy.add(new JLabel("panel"), CC.xy(1, 1));
		pnl.add(lblDummy, CC.xy(1, 1));
		pnl.add(btnDummy, CC.xy(1, 2));
		pnl.add(chkDummy, CC.xy(1, 3));
		pnl.add(rdbDummy, CC.xy(1, 4));
		pnl.add(txtDummy, CC.xy(1, 5));
		pnl.add(cmbDummy, CC.xy(1, 6));
		pnl.add(lstDummy, CC.xy(1, 7));
		pnl.add(pnlDummy, CC.xy(1, 9));
		return pnl;
	}

	private JPanel createRightPanel() {
		FormLayout layout = new FormLayout("m:g", "f:p:g");
		JPanel pnl = new JPanel(layout);
		txtPreviewLayout = new JTextArea();
		txtPreviewLayout.setColumns(10);
		JScrollPane sp = new JScrollPane(txtPreviewLayout);
		pnl.add(sp, CC.xy(1, 1));
		return pnl;
	}

	private void revalidateAndRepaint() {
		validate();
		repaint();
	}

	private void displayLayoutPreview() {
		int rowCount = formLayout.getRowCount();
		int columnCount = formLayout.getColumnCount();
		String[] rowSpecs = new String[rowCount];
		String rowSpecsString = formLayout.getRowSpec(1).encode();
		for (int i = 1; i < rowCount; i++) {
			rowSpecs[i] = formLayout.getRowSpec(i+1).encode();
			rowSpecsString += ", " + rowSpecs[i];
		}

		String[] columnSpecs = new String[columnCount];
		String columnSpecsString = formLayout.getColumnSpec(1).encode();
		for (int i = 1; i < columnCount; i++) {
			columnSpecs[i] = formLayout.getColumnSpec(i+1).encode();
			columnSpecsString += ", " + columnSpecs[i];
		}
		txtPreviewLayout.setText("FormLayout layout = new FormLayout(\"" + columnSpecsString + "\", \"" + rowSpecsString + "\");");
	}
}
