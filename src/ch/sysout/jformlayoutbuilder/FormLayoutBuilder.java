package ch.sysout.jformlayoutbuilder;

import javax.swing.UIManager;

import com.formdev.flatlaf.FlatDarkLaf;

import ch.sysout.jformlayoutbuilder.ui.MainFrame;

public class FormLayoutBuilder {

	public FormLayoutBuilder() {
		MainFrame view = new MainFrame();
		view.setLocationRelativeTo(null);
		view.setVisible(true);
	}

	public static void main(String[] args) {
		int value = 0;
		UIManager.put("Button.arc", value);
		UIManager.put("Component.arc", value);
		UIManager.put("CheckBox.arc", value);
		UIManager.put("ProgressBar.arc", value);
		UIManager.put("TextComponent.arc", value);
		try {
			UIManager.setLookAndFeel(new FlatDarkLaf());
		} catch (Exception ex) {
			System.err.println("Failed to initialize LaF");
		}
		new FormLayoutBuilder();
	}
}
