//Creates a Grand Staff.

import java.awt.*;
import java.util.*;
import java.io.*;

public class GrandStaff {
	
	private Staff topStaff;
	private Staff bottomStaff;
	private StaffType topStaffType;
	private StaffType bottomStaffType;
	private int gap;
	private int length;
	private File settings;

	////////////////////////////////////CONSTRUCTORS///////////////////////////////////////////////
	
	//Explicit staff constructor
	public GrandStaff(StaffType topStaffType, StaffType bottomStaffType, Point edge, 
			int length, int gap, File settings) {
		topStaff = new Staff(topStaffType, edge, length);
		bottomStaff = new Staff(bottomStaffType, new Point(edge.x, edge.y + (7 * 4 + gap)), length);
		
		this.topStaffType = topStaffType;
		this.bottomStaffType = bottomStaffType;
		this.gap = gap;
		this.length = length;
		this.settings = settings;
	}
	
	//Default staff constructor
	public GrandStaff(Point edge, int length, int gap) {
		this(StaffType.TREBLE, StaffType.BASS, edge, length, gap, new File("Default Grand Staff Settings.txt"));
	}
	
	////////////RETURNS////////////
	public Staff getTopStaff() {
		return topStaff;
	}
	
	public Staff getBottomStaff() {
		return bottomStaff;
	}
	
	public int getLength() {
		return length;
	}
	
	public int getGap() {
		return gap;
	}
	
	/*public Staff getCorrectStaff(int noteNumber) {
		return findCorrectStaff(noteNumber);
	}*/
	
	//////////////FINDING THE CORRECT STAFF///////////////////
	
	private void setSettingsFile() {
		
	}
	
	
	/*private Staff findCorrectStaff(int noteNumber) {
		Scanner input;
		String divider;
		Staff staff = new Staff();
		
		try {	
			input = new Scanner(settings);
			input.next();
			divider = input.next();
			
			//if(findAbsoluteNoteNumber(noteNumber) >= findAbsoluteNoteNumber
			
		} catch(Exception e) {
			System.out.println("Failed to load Grand Staff settings file.");
		}
		
		return staff;
	}*/
}