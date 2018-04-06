package sjsu.piroyan.cs16.project2;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;

/**
 * Class Maze models a maze.
 */
public class Maze {
	private Vertex vertexList[]; // array of vertices
	private int adjMat[][]; // adjacency matrix
	private final int size; // size of the maze
	private Random myRandGen; // random generator
	
	/**
	 * Generates a maze of a given size
	 * @param size size of the maze
	 */
	public Maze(int size) {
		this.size = size;
		
		// create vertex list
		vertexList = new Vertex[size*size];
		for (int i = 0; i < size*size; i++)
			vertexList[i] = new Vertex(' ', i);
		
		// create adjacency matrix
		adjMat = new int[size*size][size*size];
		for (int i = 0; i < size*size; i++)
			for (int j = 0; j < size*size; j++)
				adjMat[i][j] = 0;
		
		myRandGen = new Random(20); // use 20 as seed
	}
	
	// Returns a copy of the maze
	private Maze copy() {
		Maze maze = new Maze(size);
		
		for (int i = 0; i < size*size; i++)
			maze.vertexList[i] = new Vertex(vertexList[i].label, vertexList[i].index);
		
		for (int i = 0; i < size*size; i++)
			for (int j = 0; j < size*size; j++)
				maze.adjMat[i][j] = this.adjMat[i][j];
		
		return maze;
	}
	
	// Generates the maze using DFS
	public void generate() {
		Stack<Integer> cellStack = new Stack<Integer>(); // cell stack holds cell location list
		for (int i = 0; i < vertexList.length; i++)
			cellStack.push(i);
		int currentCell = 0;
		int visitedCells = 1;
		while (visitedCells < vertexList.length) {
			ArrayList<Integer> neighbors = findNeighborsWallsIntact(currentCell);
			if (!neighbors.isEmpty()) {
				// choose a random neighbor
				int rIndex = myRandGen.nextInt(neighbors.size());
				rIndex = neighbors.get(rIndex);
				
				// knock down wall
				adjMat[currentCell][rIndex] = 1;
				adjMat[rIndex][currentCell] = 1;
				
				cellStack.push(currentCell);
				currentCell = rIndex;
				visitedCells++;
			}
			else {
				currentCell = cellStack.pop();
			}
		}
	}
	
	// Solves the maze using DFS and returns the new solved maze.
	// The caller maze is unchanged.
	public Maze solveDFS() {
		Maze maze = copy();
		Stack<Vertex> vStack = new Stack<Vertex>();
		char label = '0';
		vStack.push(maze.vertexList[0]); // push start room on stack
		while (!vStack.isEmpty()) {
			Vertex v = vStack.pop();			
			
			if (!v.visited) {
				v.visited = true;
				v.label = label++;
				
				if (v.index == vertexList.length - 1) // reach end room
					break;
				
				if (v.label == '9')
					label = '0'; // reset label
				
				// push all unvisited neighbors of v on stack
				ArrayList<Vertex> unvisitedNeighbors = maze.getUnvisitedNeighbors(v.index);
				for (Vertex w : unvisitedNeighbors) {
					w.parent = v;				
					vStack.push(w);
				}
			}
		}
		return maze;
	}
	
	// Solves the maze using BFS and returns the new solved maze.
	// The caller maze is unchanged.
	public Maze solveBFS() {
		Maze maze = copy();
		Queue<Vertex> vQueue = new LinkedList<Vertex>();
		char label = '0';
		vQueue.offer(maze.vertexList[0]); // enqueue start room
		while (!vQueue.isEmpty()) {
			Vertex v = vQueue.poll();			
			
			if (!v.visited) {
				v.visited = true;
				v.label = label++;
				
				if (v.index == vertexList.length - 1) // reach end room
					break;
				
				if (v.label == '9')
					label = '0'; // reset label
				
				// enqueue all unvisited neighbors of v
				ArrayList<Vertex> unvisitedNeighbors = maze.getUnvisitedNeighbors(v.index);
				for (Vertex w : unvisitedNeighbors) {
					w.parent = v;
					vQueue.offer(w);
				}
			}
		}
		return maze;
	}
	
	// Shows the path after solved
	public void showPath() {		
		// clear labels
		for (int i = 0; i < vertexList.length; i++)
			vertexList[i].label = ' ';
		
		// traverse back from end room to start room and mark solution path
		Vertex v = vertexList[vertexList.length - 1];
		while (v.parent != null) {
			v.label = '#';
			v = v.parent;
		}
		v.label = '#';
	}
	
	// Returns a display of the maze in string
	public String toString() {
		String info = "";
		for (int r = 0; r < size; r++) {
			// draw up
			for (int c = 0; c < size; c++) {
				info += "+"; // corner
				if (cellIndex(r, c) == 0)
					info += " "; // start
				else 
					info += (isUpOpen(cellIndex(r, c)) ? " " : "-");
			}
			info += "+\n";
			
			// draw left
			for (int c = 0; c < size; c++) {
				info += (isLeftOpen(cellIndex(r, c)) ? " " : "|");
				info += vertexList[cellIndex(r, c)].label;
			}
			info += "|\n";
		}
		
		// draw bottom border
		for (int c = 0; c < size; c++) {
			info += "+"; // corner
			if (cellIndex(size - 1, c) == vertexList.length - 1)
				info += " "; // end
			else 
				info += (isDownOpen(cellIndex(size - 1, c)) ? " " : "-");
		}
		info += "+\n";
					
		return info;
	}
	
	// Returns cell index of a given row, column coordinate
	private int cellIndex(int row, int col) {
		return row * size + col;
	}
	
	// Returns true if left of the room is open
	private boolean isLeftOpen(int vIndex) {
		int row = vIndex / size;
		int col = vIndex % size;
				
		if (col == 0)
			return false;
		
		int leftNeighbor = cellIndex(row, col - 1);
		return (adjMat[vIndex][leftNeighbor] == 1);
	}
	
	// Returns true if right of the room is open
	private boolean isRightOpen(int vIndex) {
		int row = vIndex / size;
		int col = vIndex % size;
				
		if (col == size - 1)
			return false;
		
		int rightNeighbor = cellIndex(row, col + 1);
		return (adjMat[vIndex][rightNeighbor] == 1);
	}
	
	// Returns true if up of the room is open
	private boolean isUpOpen(int vIndex) {
		int row = vIndex / size;
		int col = vIndex % size;
				
		if (row == 0)
			return false;
		
		int upNeighbor = cellIndex(row - 1, col);
		return (adjMat[vIndex][upNeighbor] == 1);
	}
	
	// Returns true if down of the room is open
	private boolean isDownOpen(int vIndex) {
		int row = vIndex / size;
		int col = vIndex % size;
				
		if (row == size - 1)
			return false;
		
		int downNeighbor = cellIndex(row + 1, col);
		return (adjMat[vIndex][downNeighbor] == 1);
	}
	
	// Returns true if all walls of the room are intact
	private boolean isAllWallsIntact(int vIndex) {
		return !isLeftOpen(vIndex) && !isRightOpen(vIndex) && 
				!isUpOpen(vIndex) && !isDownOpen(vIndex);
	}
	
	// Finds all neighbors of a vertex with all alls intact
	private ArrayList<Integer> findNeighborsWallsIntact(int vIndex) {
		ArrayList<Integer> neighbors = new ArrayList<Integer>();
		int row = vIndex / size;
		int col = vIndex % size;
		int neighbor;
		
		// left
		if (col - 1 >= 0) {
			neighbor = cellIndex(row, col - 1);
			if (isAllWallsIntact(neighbor))
				neighbors.add(neighbor);
		}
		
		// right
		if (col + 1 < size) {
			neighbor = cellIndex(row, col + 1);
			if (isAllWallsIntact(neighbor))
				neighbors.add(neighbor);
		}
		
		// up
		if (row - 1 >= 0) {
			neighbor = cellIndex(row - 1, col);
			if (isAllWallsIntact(neighbor))
				neighbors.add(neighbor);
		}
		
		// down
		if (row + 1 < size) {
			neighbor = cellIndex(row + 1, col);
			if (isAllWallsIntact(neighbor))
				neighbors.add(neighbor);
		}
		
		return neighbors;
	}
	
	// Returns all unvisited neighbors of a vertex
	private ArrayList<Vertex> getUnvisitedNeighbors(int vIndex) {
		ArrayList<Vertex> unvisitedNeighbors = new ArrayList<Vertex>();
		int row = vIndex / size;
		int col = vIndex % size;
		int neighbor;
		
		// left
		if (col - 1 >= 0) {
			neighbor = cellIndex(row, col - 1);
			if (adjMat[vIndex][neighbor] == 1 && !vertexList[neighbor].visited)
				unvisitedNeighbors.add(vertexList[neighbor]);
		}
		
		// right
		if (col + 1 < size) {
			neighbor = cellIndex(row, col + 1);
			if (adjMat[vIndex][neighbor] == 1 && !vertexList[neighbor].visited)
				unvisitedNeighbors.add(vertexList[neighbor]);
		}
		
		// up
		if (row - 1 >= 0) {
			neighbor = cellIndex(row - 1, col);
			if (adjMat[vIndex][neighbor] == 1 && !vertexList[neighbor].visited)
				unvisitedNeighbors.add(vertexList[neighbor]);
		}
		
		// down
		if (row + 1 < size) {
			neighbor = cellIndex(row + 1, col);
			if (adjMat[vIndex][neighbor] == 1 && !vertexList[neighbor].visited)
				unvisitedNeighbors.add(vertexList[neighbor]);
		}
		
		return unvisitedNeighbors;
	}
	
//	// For debug only
//	private void printAdjMatrix() {
//		System.out.print("   ");
//		for (int i = 0; i < vertexList.length; i++)
//			System.out.printf("%3d", i);
//		System.out.println();
//		for (int i = 0; i < vertexList.length; i++) {
//			System.out.printf("%3d", i);
//			for (int j = 0; j < vertexList.length; j++) {
//				System.out.printf("%3d", adjMat[i][j]);
//			}
//			System.out.println();
//		}
//	}
	
	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		System.out.print("Enter maze size: ");
		int size = input.nextInt();
		
		Maze maze = new Maze(size);
		maze.generate();
		System.out.println("MAZE:");
		System.out.println(maze);
				
		System.out.println("\nDFS:");
		Maze dfs = maze.solveDFS();
		System.out.println(dfs);
		dfs.showPath();
		System.out.println(dfs);
		
		System.out.println("\nBFS:");
		Maze bfs = maze.solveBFS();
		System.out.println(bfs);
		bfs.showPath();
		System.out.println(bfs);
	}
}
