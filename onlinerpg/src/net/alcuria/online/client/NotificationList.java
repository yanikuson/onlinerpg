package net.alcuria.online.client;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class NotificationList {

	public BitmapFont noticeFont;
	public int index;
	public String[] notices;
	public float[] durations;
	
	public NotificationList(){
		
	}
	
	public void update(){
		// TODO: update all of the notifications
		// also check if a notice is invisible and clear it?
	}
	
	public void render(){
		// TODO: render the list of notices
	}
	
	public void add(String s){
		// TODO: add string s to the list of notifications
	}
	
}
