import javax.imageio.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;

public class MusicDrawingPanel extends JPanel {

	public final int STAFF_LENGTH = 100;
	private Map<MusicImage, BufferedImage> images;
	private Staff[] staves;
	public static final int GAP = 50;

	// Creates a new MusicDrawingPanel object with the given dimensions and background color.
	public MusicDrawingPanel(int width, int height, Color color) {
		setPreferredSize(new Dimension(width, height));
		setBackground(color);
		
		staves = new Staff[4];
		
		//Loads the images into memory.
		images = new HashMap<MusicImage, BufferedImage>();
		try {
			images.put(MusicImage.TREBLE, ImageIO.read(new File("Images/Treble Clef.png")));
			images.put(MusicImage.BASS, ImageIO.read(new File("Images/Bass Clef.png")));
			images.put(MusicImage.FLAT, ImageIO.read(new File("Images/Flat.png")));
			images.put(MusicImage.SHARP, ImageIO.read(new File("Images/Sharp.png")));
			images.put(MusicImage.WHOLE_NOTE, ImageIO.read(new File("Images/Whole Note.png")));
		} catch(Exception e) {
			System.out.println("Couldn't load images from file.");
		}
	}
	
	// Adds the given staff to this MusicDrawingPanel object at the given index.
	public void addStaff(int i, Staff staff) { staves[i] = staff; }
	// Removes the staff at the given index from this MusicDrawingPanel object.
	public void removeStaff(int i) { staves[i] = null; }
	// Returns the staves on the MusicDrawingPanel.
	public Staff[] getStaves() { return staves; }
	
	// Draws the staves and their appropriate notes onto the panel.
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		int staffCount = 0;
		for(Staff staff: staves) {
			if(staff != null) {
				drawStaff(staff, g);
				
				for(Note note: staff.getNotes())
					drawWholeNote(note, staff, g);
				
				staffCount++;
			}
		}
		
		if(staffCount > 1)
			drawBrace(g);
	}
	
	// Draws the brace that connects the multiple staves.
	private void drawBrace(Graphics g) {
		// Find top and bottom staves
		int topIndex = -1;	
		int bottomIndex = -1;
		
		boolean topFlag = true;
		for(int i = 0; i < staves.length; i++) {
			if(staves[i] != null) {
				if(topFlag) {
					topIndex = i;
					topFlag = false;
				}
				
				bottomIndex = i;
			}
		}
		
		Point edge = staves[topIndex].getLocation();
		int height = ((7 * 4) * (bottomIndex + 1)) + (GAP * bottomIndex);
		
		//Draws the connector.
		g.drawLine(edge.x, edge.y, edge.x, edge.y + height);
		g.fillRect(edge.x - 6, edge.y, 3, height + 1);
	}
	
	//Draws the given Staff object.
	private void drawStaff(Staff staff, Graphics g) {
		Point staffLocation = staff.getLocation();
		
		for(int i = 0; i < 5; i++) //draws 5 staff lines, each 7 pixels apart
			g.drawLine(staffLocation.x, 7 * i + staffLocation.y, staffLocation.x + STAFF_LENGTH, 7 * i + staffLocation.y);
		
		StaffType staffType = staff.getStaffType();
		
		if(staffType == StaffType.TREBLE)
			g.drawImage(images.get(MusicImage.TREBLE), staffLocation.x + 6, staffLocation.y - 9, null);	
		else // staffType == StaffType.BASS
			g.drawImage(images.get(MusicImage.BASS), staffLocation.x + 6, staffLocation.y, null);
	}
		
	//Draws a whole note.
	private void drawWholeNote(Note note, Staff staff, Graphics g) {
		Point noteEdge = findNoteEdge(note, staff);
		
		g.drawImage(images.get(MusicImage.WHOLE_NOTE), noteEdge.x, noteEdge.y, null);
		drawAccidental(note, noteEdge, g);
		
		if(onLedgerLine(note, staff))
			drawLedgerLines(note, staff, g);
	}
		
	//Draws ledger lines (if necessary).
	private void drawLedgerLines(Note note, Staff staff, Graphics g) {
		// Initializing ledger line ranges
		int bottomOfLower = -1;
		int topOfLower = -1;
		int bottomOfUpper = -1;
		int topOfUpper = -1;
		
		bottomOfLower = 0;     //A0
		topOfUpper = 51;       //C8
		
		switch(staff.getStaffType()) {
			case TREBLE:
				topOfLower = 23;    //C4
				bottomOfUpper = 35; //A5
				break;
			case TREBLE8VB:
				break;
			case BASS:
				topOfLower = 11;    //E2
				bottomOfUpper = 23; //C4
				break;
		}
		
		int noteNumber = note.getNoteName().getNumber();

		if((noteNumber >= bottomOfLower && noteNumber <= topOfLower) || (noteNumber >= bottomOfUpper 
			&& noteNumber <= topOfUpper)) {					//if the note is off of staff
			
			if(noteNumber >= bottomOfUpper) {				//if the note is above the staff
				for(int i = bottomOfUpper; i <= noteNumber; i += 2) {
					Point ledgerLineEdge = findNoteEdge(new Note(NoteName.getNoteName(i), null), staff);
					g.drawLine(ledgerLineEdge.x - 4, ledgerLineEdge.y + 4, ledgerLineEdge.x + 13, 
						ledgerLineEdge.y + 4);
				}
			}else if(noteNumber <= topOfLower) {			//if the note is below the staff
				for(int i = topOfLower; i >= noteNumber; i -= 2) {
					Point ledgerLineEdge = findNoteEdge(new Note(NoteName.getNoteName(i), null), staff);
					g.drawLine(ledgerLineEdge.x - 4, ledgerLineEdge.y + 4, ledgerLineEdge.x + 13, 
						ledgerLineEdge.y + 4);
				}
			}else
				throw new IllegalArgumentException("Illegal ledger line attempt made.");
		}
	}
		
	//Draws the appropriate accidental when given the note's edge and the desired color.
	private void drawAccidental(Note note, Point noteEdge, Graphics g) {
		Accidental accidental = note.getAccidental();
		
		switch(accidental) {
			case FLAT:
				g.drawImage(images.get(MusicImage.FLAT), noteEdge.x - 8, noteEdge.y - 9, null);
				break;
			case SHARP:
				g.drawImage(images.get(MusicImage.SHARP), noteEdge.x - 10, noteEdge.y - 7, null);
				break;
			case NATURAL:
				break;
			case DOUBLE_FLAT:
				break;
			case DOUBLE_SHARP:
				break;
		}
	}
	
	//Finds the top-left edge of the given note.
	public static Point findNoteEdge(Note note, Staff staff) {
		int noteNumber = note.getNoteName().getNumber();
	 	Point edge = new Point(staff.getLocation());
		
		edge.setLocation(edge.x + 50, (-3.5) * noteNumber + (edge.y + 
			staff.getStaffType().getConstant()) - (noteNumber % 2));
			
		return edge;
	}
	
	//Tests if the note is on a ledger line for the given staff.
	private boolean onLedgerLine(Note note, Staff staff) {
		StaffType staffType = staff.getStaffType();
		int noteNumber = note.getNoteName().getNumber();
		
		if(staffType == StaffType.TREBLE && (noteNumber >= 24 && noteNumber <= 34)) //Treble, between D4 and G5
			return false;
		else if(staffType == StaffType.BASS && (noteNumber >= 12 && noteNumber <= 22)) //Bass, between F2 and B3
			return false;
		else
			return true;
	}
}