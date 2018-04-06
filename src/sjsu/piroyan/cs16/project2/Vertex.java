package sjsu.piroyan.cs16.project2;

/**
 * Class Vertex represents a room in a maze.
 */
public class Vertex {
	public char label; // the label of the vertex
	public boolean visited; // true: the vertex was visited
	public final int index; // index of the vertex in list
	public Vertex parent; // help to show solution path

	public Vertex(char label, int index) // constructor
	{
		this.label = label;
		this.index = index;
		visited = false;
		parent = null;
	}
}
