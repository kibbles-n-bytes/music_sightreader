//A class representing a musical staff, storing its type and the notes on it.

import java.awt.*;
import java.util.*;

public class Staff {

	private StaffType staffType;
	private NoteType noteType;
	private NoteName[] range;
	private Set<Accidental> accidentals;
	private Point location;
	private Set<Note> notes;

	// Creates a new staff object with the given staff type, note type, range,
	// acceptable accidentals, and location.
	public Staff(StaffType staffType, NoteType noteType, NoteName[] range, 
				 Set<Accidental> accidentals, Point location) {
		this.staffType = staffType;
		this.noteType = noteType;
		this.range = range;
		this.accidentals = accidentals;
		this.location = location;
		
		notes = new HashSet<Note>();
	}
	
	// Creates a new staff object with the given staff type, with default note type,
	// range, and acceptable accidentals.
	public Staff(StaffType staffType) {
		this(staffType, NoteType.SINGLE, null, null, new Point(70, 70));
		
		range = new NoteName[] {NoteName.A0, NoteName.C8};
		
		accidentals = new HashSet<Accidental>();
		accidentals.add(Accidental.FLAT);
		accidentals.add(Accidental.NATURAL);
		accidentals.add(Accidental.SHARP);
	}
	
	// Returns the type of staff.
	public StaffType getStaffType() { return staffType; }
	// Sets the type of staff.
	public void setStaffType(StaffType staffType) { this.staffType = staffType; }
	
	// Returns the type of notes allowed.
	public NoteType getNoteType() { return noteType; }
	// Sets the type of notes allowed.
	public void setNoteType(NoteType noteType) { this.noteType = noteType; }
	
	// Returns the allowed range.
	public NoteName[] getRange() { return range; }
	// Sets the allowed range.
	public void setRange(NoteName[] range) { this.range = range; }
	
	// Returns the allowed accidentals.
	public Set<Accidental> getAccidentals() { return accidentals; }
	// Sets the allowed accidentals.
	public void setAccidentals(Set<Accidental> accidentals) { this.accidentals = accidentals; }
	
	// Returns the location of the staff.
	public Point getLocation() { return location; }
	// Sets the location of the staff to the given point.
	public void setLocation(Point location) { this.location = location; }
	
	// Returns a collection of notes on the staff.
	public Set<Note> getNotes() { return notes; }
	// Adds the given note to the collection of notes on the staff.
	public void addNote(Note note) { 
		notes.add(note); 
	}
	
	// Clears the list of notes on the staff.
	public void clearNotes() { notes = new HashSet<Note>(); }
}