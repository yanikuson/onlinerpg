package net.alcuria.online.server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Properties {
	
	public File optionsFile;
	
	public Properties(){
		optionsFile = new File("server.txt");
		
		if (!optionsFile.exists()){
			if (!createOptions()) throw new RuntimeException("Error creating options file");
		}
	}

	private boolean createOptions() {
		
		try {
			PrintWriter print = new PrintWriter(new FileWriter(optionsFile));
			print.println();
			
		} catch (IOException e) {
			e.printStackTrace();
			
		}
		return true;
	}

}
