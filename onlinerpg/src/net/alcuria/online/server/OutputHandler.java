package net.alcuria.online.server;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import javax.swing.JTextArea;

public class OutputHandler extends Handler {

	public JTextArea textArea;
	
	public OutputHandler(JTextArea area){
		this.textArea = area;
	}
	
	public void close() throws SecurityException {


	}

	public void flush() {


	}

	public void publish(LogRecord e) {
		textArea.append(format(e));
		textArea.setCaretPosition(textArea.getDocument().getLength());
	}

	private String format(LogRecord e) {
		
		StringBuilder builder = new StringBuilder();

		// append the log level
		builder.append('[');
		builder.append(e.getLevel().toString());
		builder.append(']');
		
		// append the actual log message
		builder.append(e.getMessage());
		builder.append('\n');
		
		// check if an error was thrown 
		Throwable t = e.getThrown();
		if (t != null){
			StringWriter writer = new StringWriter();
			t.printStackTrace(new PrintWriter(writer));
			builder.append(writer.toString());
		}
		
		return builder.toString();
	}

}
