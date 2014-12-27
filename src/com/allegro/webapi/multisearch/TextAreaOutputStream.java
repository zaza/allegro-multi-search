package com.allegro.webapi.multisearch;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 * From: http://stackoverflow.com/questions/10872951/how-to-bind-swing-jtextarea-to-printstream-to-accept-data
 *
 */
public class TextAreaOutputStream extends OutputStream {

	private final JTextArea textArea;
	private final StringBuilder sb = new StringBuilder();

	public TextAreaOutputStream(final JTextArea textArea) {
		this.textArea = textArea;
	}

	@Override
	public void flush() {
	}

	@Override
	public void close() {
	}

	@Override
	public void write(int b) throws IOException {
		if (b == '\r')
			return;
		if (b == '\n') {
			final String text = sb.toString() + "\n";
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					textArea.append(text);
				}
			});
			sb.setLength(0);
			return;
		}
		sb.append((char) b);
	}
}