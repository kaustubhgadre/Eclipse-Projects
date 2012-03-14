package edu.missouri.eldercare.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;

import com.swtdesigner.SWTResourceManager;

public class ApplicationView extends ViewPart implements Runnable {
	public ApplicationView() {
	}

	HashMap<Character, Canvas> buttonMap = new HashMap<Character, Canvas>();
	private HashMap<Canvas, Long> buttonState = new HashMap<Canvas, Long>();

	public synchronized HashMap<Canvas, Long> getButtonState() {
		return buttonState;
	}

	public synchronized void setButtonState(HashMap<Canvas, Long> buttonState) {
		this.buttonState = buttonState;
	}

	public synchronized Set<Canvas> getButtonKeySet() {
		return getButtonState().keySet();
	}

	public synchronized long getButtonTime(Canvas canvas) {
		return getButtonState().get(canvas);
	}

	public synchronized long setButtonTime(Canvas canvas, long time) {
		return getButtonState().put(canvas, time);
	}

	char[] row1 = new char[] { 'Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P' };
	char[] row2 = new char[] { 'A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L', ' ' };
	char[] row3 = new char[] { 'Z', 'X', 'C', 'V', 'B', 'N', 'M', ' ', ' ', ' ' };
	ArrayList<char[]> alphabets = new ArrayList<char[]>();

	@Override
	public void createPartControl(Composite parent) {
		// parent = new Shell(display);
		parent.setSize(810, 300);
		// parent.setText("Elder Care");
		parent.setLayout(new GridLayout(10, true));

		alphabets.add(row1);
		alphabets.add(row2);
		alphabets.add(row3);

		for (Iterator<char[]> iterator = alphabets.iterator(); iterator
				.hasNext();) {
			char[] row = (char[]) iterator.next();
			for (int i = 0; i < row.length; i++) {
				if (!("" + row[i]).equals(" ")) {
					Canvas canvas = new Canvas(parent, SWT.BORDER);
					canvas.setBackground(SWTResourceManager
							.getColor(SWT.COLOR_DARK_GREEN));
					canvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
							true, 1, 1));
					canvas.setLayout(new GridLayout(1, false));
					Label label = new Label(canvas, SWT.BOLD);
					label.setText("" + row[i]);
					label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,
							true, true, 1, 1));
					label.setBackground(SWTResourceManager
							.getColor(SWT.COLOR_DARK_GREEN));
					label.setFont(SWTResourceManager.getFont("Segoe UI", 14,
							SWT.BOLD));
					buttonMap.put(row[i], canvas);
					buttonState.put(canvas, 0l);
				} else
					new Label(parent, SWT.NONE);
			}

		}

		parent.addListener(SWT.KeyDown, listener);

		parent.setFocus();
		Control[] buttons = parent.getChildren();
		for (int i = 0; i < buttons.length; i++) {
			final Control selectedButton = (Control) buttons[i];
			selectedButton.addListener(SWT.KeyDown, listener);
		}
		new Thread(new ApplicationView()).start();
	}

	Listener listener = new Listener() {
		@Override
		public void handleEvent(Event e) {
			char eventChar = e.character;
			String upperCase = ("" + eventChar).toUpperCase();
			char upperCaseChar = upperCase.toCharArray()[0];
			if (buttonMap.containsKey(eventChar)
					|| buttonMap.containsKey(upperCaseChar)) {
				Canvas canvas = buttonMap.get(upperCaseChar);
				canvas
						.setBackground(SWTResourceManager
								.getColor(SWT.COLOR_RED));
				Control[] labels = canvas.getChildren();
				for (int i = 0; i < labels.length; i++) {
					labels[i].setBackground(SWTResourceManager
							.getColor(SWT.COLOR_RED));
				}
				setStatusLine("Key " + eventChar + " is selected");
				setButtonTime(canvas, System.currentTimeMillis());
			}

			Set<Character> keySet = buttonMap.keySet();
			for (Iterator<Character> iterator = keySet.iterator(); iterator
					.hasNext();) {
				Character character = (Character) iterator.next();
				if (character != upperCaseChar) {
					Canvas canvas = buttonMap.get(character);
					canvas.setBackground(SWTResourceManager
							.getColor(SWT.COLOR_DARK_GREEN));
					Control[] labels = canvas.getChildren();

					for (int i = 0; i < labels.length; i++) {
						labels[i].setBackground(SWTResourceManager
								.getColor(SWT.COLOR_DARK_GREEN));
					}
				}
			}

		}
	};

	private void setStatusLine(String message) {
		IActionBars bars = getViewSite().getActionBars();
		bars.getStatusLineManager().setMessage(message);
	}

	@Override
	public void setFocus() {
	}

	@Override
	public void run() {
		while (true) {
			Set<Canvas> allCanvas = getButtonKeySet();
			for (Iterator<Canvas> iterator = allCanvas.iterator(); iterator
					.hasNext();) {
				Canvas canvas = (Canvas) iterator.next();
				if ((System.currentTimeMillis() - getButtonTime(canvas)) >= 500) {
					canvas.setBackground(SWTResourceManager
							.getColor(SWT.COLOR_DARK_GREEN));
					Control[] labels = canvas.getChildren();

					for (int i = 0; i < labels.length; i++) {
						labels[i].setBackground(SWTResourceManager
								.getColor(SWT.COLOR_DARK_GREEN));
					}
					setButtonTime(canvas, 0l);
				}
			}
			// buttonState.clear();
		}

	}
}