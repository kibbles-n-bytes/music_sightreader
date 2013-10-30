import java.awt.*;

public class NoteCalculations {

	//Returns an int value that is equivalent to the given note, rejecting the note if it is
	//invalid or out of the range of the piano (Flat notes: -51 to -1; Natural notes: 0 to 51;
	//Sharp notes: 52 to 102).
	public static int findNoteNumber(String note) {
		char noteLetterChar, octaveNumberChar;
		int noteLetterNumber, octaveNumber, accidental = 0;
		
		if(note.length() != 2 & note.length() != 3)
			throw new IllegalArgumentException("Invalid note.");
		
		//Captures the note letter.
		if(note.toLowerCase().charAt(0) >= 'a' && note.toLowerCase().charAt(0) <= 'g')
			noteLetterChar = note.toLowerCase().charAt(0);
		else
			throw new IllegalArgumentException("Invalid note letter.");
		
		//Captures the accidental and octave number.
		if(note.charAt(1) >= '0' && note.charAt(1) <= '8') {
			octaveNumberChar = note.charAt(1);
		}else if(note.charAt(2) >= '0' && note.charAt(2) <= '8') {
			octaveNumberChar = note.charAt(2);
			if (note.charAt(1) == '#')
				accidental = 1;
			else if(note.charAt(1) == 'b')
				accidental = -1;
			else
				throw new IllegalArgumentException("Invalid accidental.");
		}else
			throw new IllegalArgumentException("Invalid octave number.");
			
		//Calculates the number the given note is.
		if(noteLetterChar == 'a' || noteLetterChar == 'b')
			noteLetterNumber = noteLetterChar - 97 + 5;
		else
			noteLetterNumber = noteLetterChar - 99;
			
		octaveNumber = octaveNumberChar - 48;
		
		int noteNumber = noteLetterNumber + (octaveNumber * 7) - 5;
		
		if(noteNumber < 0 || noteNumber > 51)
			throw new IllegalArgumentException("Note out of range of piano.");
			
		if(accidental == -1)
			noteNumber -= 52;
		else if(accidental == 1)
			noteNumber += 52;
			
		if(noteNumber == -52 || noteNumber == 103)
			throw new IllegalArgumentException("Note out of range of piano.");
		
		return noteNumber;
	}	
	
	//Finds the top-left edge of the note that is being drawn.
	public static Point findNoteEdge(int noteNumber, Staff staff) {
		if(noteNumber <= findNoteNumber("Bb0") || noteNumber >= findNoteNumber("B#7"))
			throw new IllegalArgumentException("Note edge cannot be calculated; given note not valid.");
		
		int absoluteNoteNumber = noteNumber;
		
		if(!(noteNumber >= findNoteNumber("A0") && noteNumber <= findNoteNumber("C8")))
			absoluteNoteNumber = findAbsoluteNoteNumber(noteNumber);
	
		Point edge = new Point(staff.getEdge().x, staff.getEdge().y);
			
		if(staff.getStaffType() == StaffType.TREBLE) 
			edge.setLocation(edge.x + 50, (-3.5) * absoluteNoteNumber + (edge.y + 112) - 
				(absoluteNoteNumber % 2));
		else if(staff.getStaffType() == StaffType.BASS)
			edge.setLocation(edge.x + 50, (-3.5) * absoluteNoteNumber + (edge.y + 70) - 
				(absoluteNoteNumber % 2));
		
		return edge;
	}
	
	//Finds the note range with given bottom and top notes.
	public static int findNoteInRange(String bottomNote, String topNote) {
		Random rand = new Random();
		
		int bottom = Note.findNoteNumber(bottomNote);
		int top = Note.findNoteNumber(topNote);
		
		int accidental = rand.nextInt(3) - 1;
		int note = rand.nextInt((top + 1) - bottom) + bottom;
		
		if(accidental == -1)
			note -= 52;
		else if(accidental == 1)
			note += 52;
		
		return note;
	}

}