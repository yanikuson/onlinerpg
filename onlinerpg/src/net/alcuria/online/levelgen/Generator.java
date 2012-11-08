package net.alcuria.online.levelgen;

import java.util.Stack;


public class Generator {

	public static final int WIDTH = 10;
	public static final int HEIGHT = 10;

	Cell[][] m;
	Cell currentCell;
	Stack<Cell> locations;
	Stack<Cell> neighbors;
	int totalCells = WIDTH * HEIGHT;
	int visitedCells;

	public Generator(){

		locations = new Stack<Cell>();
		neighbors = new Stack<Cell>();
		
		// initialize the array
		m = new Cell[HEIGHT][WIDTH];
		for (int i = 0; i < HEIGHT; i++){
			for (int j = 0; j < WIDTH; j++){
				m[i][j] = new Cell(j, i);
			}
		}

		// we add borders!
		for (int i = 0; i < WIDTH; i++){
			m[0][i].border[Cell.N] = true;
			m[HEIGHT-1][i].border[Cell.S] = true;
		}

		for (int i = 0; i < HEIGHT; i++){
			m[i][0].border[Cell.W] = true;
			m[i][WIDTH-1].border[Cell.E] = true;
		}
	}

	// prints the board
	public static void print(Cell[][] m){
		System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
		for (int i = 0; i < HEIGHT; i++){
			for (int rows = 0; rows < 3; rows++){
				for (int j = 0; j < WIDTH; j++){
					if (rows == 0){
						m[i][j].printTop();
					}
					if (rows == 1){
						m[i][j].printMid();
					}
					if (rows == 2){
						m[i][j].printBot();
					}
				}
				System.out.println();

			}
		}

	}

	public static void main(String[] args){
		
		// create the mazegen and start at a random cell
		Generator g = new Generator();
		g.visitedCells = 1;
		g.currentCell = g.m[(int) (Math.random()*HEIGHT)][(int) (Math.random()*WIDTH)];
		
		while (g.visitedCells < g.totalCells){
			
			// empty the neighbors stack
			g.neighbors.clear();
			
			// find all neighbors of currentCell with all walls intact
			if (g.currentCell.y-1 >= 0 && g.m[g.currentCell.y-1][g.currentCell.x].getNumWalls() == 4){
				//System.out.println("pushing neighbor at " + g.currentCell.x + " " + (g.currentCell.y-1));
				g.neighbors.push(g.m[g.currentCell.y-1][g.currentCell.x]);
			}
			if (g.currentCell.y+1 < HEIGHT && g.m[g.currentCell.y+1][g.currentCell.x].getNumWalls() == 4){
				//System.out.println("pushing neighbor at " + g.currentCell.x + " " + (g.currentCell.y+1));
				g.neighbors.push(g.m[g.currentCell.y+1][g.currentCell.x]);
			}
			if (g.currentCell.x-1 >= 0 && g.m[g.currentCell.y][g.currentCell.x-1].getNumWalls() == 4){
				//System.out.println("pushing neighbor at " + (g.currentCell.x-1) + " " + g.currentCell.y);
				g.neighbors.push(g.m[g.currentCell.y][g.currentCell.x-1]);	
			}
			if (g.currentCell.x+1 < WIDTH && g.m[g.currentCell.y][g.currentCell.x+1].getNumWalls() == 4){
				//System.out.println("pushing neighbor at " + (g.currentCell.x+1) + " " + g.currentCell.y);
				g.neighbors.push(g.m[g.currentCell.y][g.currentCell.x+1]);
			}
			
			// if one or more neighbors found
			if (g.neighbors.size() > 0){
				
				// chose a neighbor at random
				Cell randomNeighbor = g.neighbors.get((int) (Math.random() * g.neighbors.size()));
				
				// knock down the wall between it and currentCell
				if (randomNeighbor.x > g.currentCell.x){
					g.currentCell.walls[Cell.E] = false;
					randomNeighbor.walls[Cell.W] = false;
				} else if (randomNeighbor.x < g.currentCell.x){
					g.currentCell.walls[Cell.W] = false;
					randomNeighbor.walls[Cell.E] = false;
				} else if (randomNeighbor.y > g.currentCell.y){
					g.currentCell.walls[Cell.S] = false;
					randomNeighbor.walls[Cell.N] = false;
				} else if (randomNeighbor.y < g.currentCell.y){
					g.currentCell.walls[Cell.N] = false;
					randomNeighbor.walls[Cell.S] = false;
				} else {
					System.out.println("Error: neighbor is not adjacent to currentCell");
				}
				
				// push currentCell location onto the cellStack
				g.locations.push(g.currentCell);
				
				// make the neighbor the new currentCell
				g.currentCell = randomNeighbor;
				
				// add 1 to visitedCells
				g.visitedCells++;
				
			} else {
				
				// pop the most recent cell entry off of the cell stack and make it current cell
				g.currentCell = g.locations.pop();
			}
			
			print(g.m);
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
		// print the finished maze
		print(g.m);
		
	}
}
