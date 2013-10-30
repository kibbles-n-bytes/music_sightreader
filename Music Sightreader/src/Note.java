// A class representing a musical note that stores its note name and its accidental.
import java.awt.*;

public class Note implements Comparable<Note> {
	private NoteName noteName;
	private Accidental accidental;

	//Constructs a note object with the given note name and accidental.
	public Note(NoteName noteName, Accidental accidental) {
		this.noteName = noteName;
		this.accidental = accidental;
	}

	// Returns this note's name.
	public NoteName getNoteName() { return noteName; }
	
	// Returns this note's accidental.
	public Accidental getAccidental() { return accidental; }
	
	// Returns negative when note is lower, positive when higher. Does not take into account 
	// pitch, only note name.
	public int compareTo(Note other) { return noteName.compareTo(other.noteName); }
}