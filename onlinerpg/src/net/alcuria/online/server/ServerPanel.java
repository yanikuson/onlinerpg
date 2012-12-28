package net.alcuria.online.server;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import java.util.logging.*;

public class ServerPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	public static Logger logger = Logger.getLogger("onlinerpg");
	public static JFrame frame;
	
	public ServerPanel(){
		setPreferredSize(new Dimension(600, 400));
		setLayout(new BorderLayout());
		
		add(getPlayerList(), "North");
		add(getLog(), "Center");
	}
	
	public static void create() {
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e){
			e.printStackTrace();
		}
		
		ServerPanel sp = new ServerPanel();
		frame = new JFrame("Heroes of Umbra Server");
		frame.add(sp);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
	}
	
	public JScrollPane getPlayerList(){
	
		PlayerList pl = new PlayerList();
		JScrollPane scroll = new JScrollPane(pl);
		scroll.setBorder(new TitledBorder(new EtchedBorder(), "Player List"));
		return scroll;
		
	}
	
	public JPanel getLog(){
		
		JPanel panel = new JPanel(new BorderLayout());
		JTextArea text = new JTextArea();
		logger.addHandler(new OutputHandler(text));
		JScrollPane pane = new JScrollPane(text);
		text.setEditable(false);
		
		JTextField input = new JTextField();
		
		panel.add(pane, "Center");
		panel.add(input, "South");
		panel.setBorder(new TitledBorder(new EtchedBorder(), "Log Output"));
		
		return panel;
	}

	public static void update() {
	
		
	}
	
}
