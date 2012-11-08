package net.alcuria.online.levelgen;


public class Cell {

	public int x;
	public int y;
	
	public static final byte W = 0;
	public static final byte S = 1;
	public static final byte E = 2;
	public static final byte N = 3;
	
	boolean[] backtrack;
	boolean[] solution;
	boolean[] border;
	boolean[] walls;
	
	public Cell(int x, int y){
		this.x = x;
		this.y = y;
		
		backtrack = new boolean[4];
		solution = new boolean[4];
		border = new boolean[4];
		walls = new boolean[4];
		
		for (int i = 0; i < walls.length; i++){
			backtrack[i] = false;
			solution[i] = false;
			border[i] = false;
			walls[i] = true;
		}
	}

	public int getNumWalls(){
		int numWalls = 0;
		for (int i = 0; i < walls.length; i++){
			if (walls[i]){
				numWalls++;
			}
		}
		return numWalls;
	}
	
	public void printTop() {
		
		if (border[N] || walls[N]){
			System.out.print("+ - +");
		} else {
			System.out.print("+   +");
		}
		
	}
	
	public void printBot() {
		
		if (border[S] || walls[S]){
			System.out.print("+ - +");
		} else {
			System.out.print("+   +");
		}
		
	}
	
	public void printMid(){
		
		if (border[W] || walls[W]){
			System.out.print("|");
		} else {
			System.out.print(" ");
		}
		if (!walls[N] || !walls[E] || !walls[S] || !walls[W]){
			System.out.print("   ");
		} else {
			System.out.print("###");
		}
		
		if (border[E] || walls[E]){
			System.out.print("|");
		} else {
			System.out.print(" ");
		}
	}
	
}
