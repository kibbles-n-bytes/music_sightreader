//Michael Kibbe
//Started 11/1/11
//This program creates a music staff and draws random notes on the staff.


/*THINGS LEFT TO BE DONE:
	
	o Allow the gap to shift if the note is forced treble or bass.
	
	o Allow for force-bass and force-treble combinations.
	
	o Fix findNoteInRange to include accidentals in bounds.
	
	o Find ways to make the code neater and more efficient (load into array instead of calculating every
	  time?); time bar not effective at the moment.
	
	o Make it read from a settings.txt file.
		
	o Make the program capable of doing more than one note at a time (with proper shifting of accidentals
	  and note positions).
	
	o Incorperate different note values (including rests).
	
	o Allow for cohesive measures.
	
	EVENTUALLY:
		   o Add patterns for training chord progressions and randomly generating harmony.
			o Allow for song input to train on actual sheet music, using a text input file.
			o Allow for note editing within the program.
			o Incorperate dynamic/expression markings.
*/

//VERSION 2.0; CAN BE IMPLEMENTED IN A GUI!


import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.*;
import java.awt.*;
import java.io.*;
import javax.imageio.*;
import java.util.*;

public class Music extends JFrame {
	
	private MusicDrawingPanel musicDrawingPanel;
	private JPanel timerSettings;
	private JTextField timerSpeed;
	private JButton speedSetButton;
	private JButton startStopButton;
	private Timer timer;
	private JTabbedPane staffTabs;
	private JPanel[][] staffTabContents;
	private int currentTab;
	private static final Point TOP_CORNER = new Point(10, 70);
	
	public Music() {
		super("Music Sightreader");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		/////The panel that displays the images /////
		musicDrawingPanel = new MusicDrawingPanel(500, 400, Color.WHITE);
		/////////////////////////////////////////////
		
		///// Panel on the side with timer settings /////
		timer = new Timer(1000, new TimerListener());
		
		timerSpeed = new JTextField("1", 3);
		speedSetButton = new JButton("Set");
		speedSetButton.addActionListener(new SpeedSetListener());
		JPanel timerSpeedAndSet = new JPanel();
		timerSpeedAndSet.add(timerSpeed);
		timerSpeedAndSet.add(speedSetButton);
		
		startStopButton = new JButton("Start");
		startStopButton.addActionListener(new StartStopListener());
		
		timerSettings = new JPanel(new GridLayout(3,1));
		timerSettings.add(new JLabel("Timer:"));
		timerSettings.add(timerSpeedAndSet);
		timerSettings.add(startStopButton);
		/////////////////////////////////////////////////
		
		///// Bottom tabbed pane with staff settings /////
		staffTabs = new JTabbedPane();
		staffTabs.addChangeListener(new TabListener());
		staffTabContents = new JPanel[4][5];
		
		for(int i = 0; i < 4; i++) {
			JPanel staffTab = new JPanel(new GridLayout(5,1));
			
			// on/off radio buttons
			JRadioButton onRadio = new JRadioButton();
			JRadioButton offRadio = new JRadioButton();
			
			onRadio.setActionCommand("On");
			offRadio.setActionCommand("Off");
			
			onRadio.addActionListener(new OnOffListener());
			offRadio.addActionListener(new OnOffListener());
			
			ButtonGroup onOff = new ButtonGroup();
			onOff.add(onRadio);
			onOff.add(offRadio);
			
			JPanel onOffPanel = new JPanel();
			onOffPanel.add(new JLabel("On"));
			onOffPanel.add(onRadio);
			onOffPanel.add(new JLabel("Off"));
			onOffPanel.add(offRadio);
			
			// Clef radio buttons
			JRadioButton trebleRadio = new JRadioButton();
			JRadioButton treble8vbRadio = new JRadioButton();
			JRadioButton bassRadio = new JRadioButton();
			trebleRadio.setSelected(true);
			
			trebleRadio.setActionCommand("Treble");
			treble8vbRadio.setActionCommand("Treble8vb");
			bassRadio.setActionCommand("Bass");
			
			trebleRadio.addActionListener(new ClefTypeListener());
			treble8vbRadio.addActionListener(new ClefTypeListener());
			bassRadio.addActionListener(new ClefTypeListener());
			
			ButtonGroup clefType = new ButtonGroup();
			clefType.add(trebleRadio);
			clefType.add(treble8vbRadio);
			clefType.add(bassRadio);
			
			JPanel clefTypePanel = new JPanel();
			clefTypePanel.add(new JLabel("Clef Type:"));
			clefTypePanel.add(new JLabel("Treble"));
			clefTypePanel.add(trebleRadio);
			clefTypePanel.add(new JLabel("Treble(8vb)"));
			clefTypePanel.add(treble8vbRadio);
			clefTypePanel.add(new JLabel("Bass"));
			clefTypePanel.add(bassRadio);
			
			// Note Type radio buttons
			JRadioButton singleRadio = new JRadioButton();
			JRadioButton multipleRadio = new JRadioButton();
			JRadioButton chordRadio = new JRadioButton();
			singleRadio.setSelected(true);
			
			singleRadio.setActionCommand("Single");
			multipleRadio.setActionCommand("Multiple");
			chordRadio.setActionCommand("Chord");
			
			singleRadio.addActionListener(new NoteTypeListener());
			multipleRadio.addActionListener(new NoteTypeListener());
			chordRadio.addActionListener(new NoteTypeListener());
			
			ButtonGroup noteType = new ButtonGroup();
			noteType.add(singleRadio);
			noteType.add(multipleRadio);
			noteType.add(chordRadio);
			
			JPanel noteTypePanel = new JPanel();
			noteTypePanel.add(new JLabel("Note Type:"));
			noteTypePanel.add(new JLabel("Single"));
			noteTypePanel.add(singleRadio);
			noteTypePanel.add(new JLabel("Multiple"));
			noteTypePanel.add(multipleRadio);
			noteTypePanel.add(new JLabel("Chord"));
			noteTypePanel.add(chordRadio);
			
			// Range dropdown boxes
			Vector<NoteName> noteNames = new Vector<NoteName>();
			
			for(NoteName n: NoteName.values())
				noteNames.add(0, n); // List of notes (highest on top)
			
			JComboBox<NoteName> bottomNote = new JComboBox<NoteName>(noteNames);
			JComboBox<NoteName> topNote = new JComboBox<NoteName>(noteNames);
			
			bottomNote.setSelectedIndex(noteNames.size() - 1);
			topNote.setSelectedIndex(0);
			
			bottomNote.addActionListener(new RangeListener());
			topNote.addActionListener(new RangeListener());
			
			JPanel rangePanel = new JPanel();
			rangePanel.add(new JLabel("Range:"));
			rangePanel.add(bottomNote);
			rangePanel.add(new JLabel("to"));
			rangePanel.add(topNote);
			
			// Accidental check boxes
			JCheckBox doubleFlatCheckBox = new JCheckBox();
			JCheckBox flatCheckBox = new JCheckBox();
			JCheckBox naturalCheckBox = new JCheckBox();
			JCheckBox sharpCheckBox = new JCheckBox();
			JCheckBox doubleSharpCheckBox = new JCheckBox();
			flatCheckBox.setSelected(true);
			naturalCheckBox.setSelected(true);
			sharpCheckBox.setSelected(true);
			
			doubleFlatCheckBox.addItemListener(new AccidentalListener());
			flatCheckBox.addItemListener(new AccidentalListener());
			naturalCheckBox.addItemListener(new AccidentalListener());
			sharpCheckBox.addItemListener(new AccidentalListener());
			doubleSharpCheckBox.addItemListener(new AccidentalListener());
			
			JPanel accidentalPanel = new JPanel();
			accidentalPanel.add(new JLabel("Accidentals: "));
			accidentalPanel.add(new JLabel("Double Flat"));
			accidentalPanel.add(doubleFlatCheckBox);
			accidentalPanel.add(new JLabel("Flat"));
			accidentalPanel.add(flatCheckBox);
			accidentalPanel.add(new JLabel("Natural"));
			accidentalPanel.add(naturalCheckBox);
			accidentalPanel.add(new JLabel("Sharp"));
			accidentalPanel.add(sharpCheckBox);
			accidentalPanel.add(new JLabel("Double Sharp"));
			accidentalPanel.add(doubleSharpCheckBox);
			
			// Store contents in staffTabContents and add it to the appropriate tab
			staffTabContents[i][0] = onOffPanel;
			staffTabContents[i][1] = clefTypePanel;
			staffTabContents[i][2] = noteTypePanel;
			staffTabContents[i][3] = rangePanel;
			staffTabContents[i][4] = accidentalPanel;
			
			for(Component c: staffTabContents[i])
				staffTab.add(c);
				
			
			staffTabs.addTab("Staff " + (i + 1), staffTab);
		}
		
		// Initialize all staves as "off"
		for(int i = 0; i < staffTabContents.length; i++) {
			currentTab = i;
			JPanel onOffPanel = staffTabContents[i][0];
			Component[] onOffComponents = onOffPanel.getComponents();
			JRadioButton offRadio = (JRadioButton) onOffComponents[3];
			offRadio.doClick();
		}
		
		currentTab = 0;
		//////////////////////////////////////////////////
		
		// Add everything to frame
		this.setLayout(new BorderLayout());
	
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.add(musicDrawingPanel, BorderLayout.CENTER);
		topPanel.add(timerSettings, BorderLayout.EAST);
		
		add(topPanel, BorderLayout.CENTER);
		add(staffTabs, BorderLayout.SOUTH);
		pack();
		setVisible(true);
	}
	
	// Makes a random note and puts it on the given staff.
	public static void addRandomNote(Staff staff) {
		Random rand = new Random();
		
		// Get acceptable range for note
		NoteName[] rangeNames = staff.getRange();
		int range = rangeNames[1].compareTo(rangeNames[0]) + 1;
		int noteNumber = rand.nextInt(range) + rangeNames[0].getNumber();
		
		// Get acceptable accidental
		Set<Accidental> accidentals = staff.getAccidentals();
		int accidentalNumber = rand.nextInt(accidentals.size());
		Accidental accidental = Accidental.NATURAL;
		
		Iterator<Accidental> iter = accidentals.iterator();
		for(int i = 0; i <= accidentalNumber; i++)
			accidental = iter.next();
		
		// Add note
		staff.addNote(new Note(NoteName.getNoteName(noteNumber), accidental));
	}
	
	// Callback class for the timer speed set button.
	class SpeedSetListener implements ActionListener {
		// Sets the speed of the timer to the speed (in seconds) written in the speed text field.
		public void actionPerformed(ActionEvent e) {
			try {
				int speedNum = (int) Math.round(Double.parseDouble(timerSpeed.getText()) * 1000);
				timer.setDelay(speedNum);
			} catch(Exception exc) {
					System.out.println("Could not understand given speed.");
			}
		}
	}
	
	// Callback class for the start/stop timer button.
	class StartStopListener implements ActionListener {
		// Starts or stops the timer for refreshing the notes on the music drawing panel.
		public void actionPerformed(ActionEvent e) {
			if(timer.isRunning()) {
				timer.stop();
				startStopButton.setText("Start");
			} else {
				timer.start();
				startStopButton.setText("Stop");
			}
		}
	}
	
	// Callback class for timer.
	class TimerListener implements ActionListener {
		//  Clears all notes on the staves and adds a random note to each.
		public void actionPerformed(ActionEvent e) {
			for(Staff staff: musicDrawingPanel.getStaves()) {
				if(staff != null) {
					staff.clearNotes();
					addRandomNote(staff);
				}
			}
			
			musicDrawingPanel.repaint();
		}
	}
	
	// Callback class for on/off radio buttons.
	class OnOffListener implements ActionListener {
		// Turns on settings and adds the staff to the music drawing panel if turned on,
		// turns off settings and removes the staff from the music drawing panel if turned off.
		public void actionPerformed(ActionEvent e) {
			String buttonString = e.getActionCommand(); // "On" or "Off"
			
			if(buttonString.equals("On")) {
				// Allows setting changes
				for(int i = 1; i < staffTabContents[currentTab].length; i++) {
					for(Component component: staffTabContents[currentTab][i].getComponents())
						component.setEnabled(true);
				}
				
				// Adds a staff with the current settings to the music drawing panel
				musicDrawingPanel.addStaff(currentTab, createStaff());
				musicDrawingPanel.repaint();
			} else {
				// Disables setting changes
				for(int i = 1; i < staffTabContents[currentTab].length; i++) {
					for(Component component: staffTabContents[currentTab][i].getComponents())
						component.setEnabled(false);
				}
				
				// Removes the staff from the music drawing panel
				musicDrawingPanel.removeStaff(currentTab);
				musicDrawingPanel.repaint();
			}
		}
		
		// Creates a staff with the current tab's properties.
		public Staff createStaff() {
			StaffType staffType = findCurrentStaffType();
			NoteType noteType = findCurrentNoteType();
			NoteName[] range = findCurrentRange();
			Set<Accidental> accidental = findCurrentAccidentals();
			Point location = findCurrentLocation();
			
			return new Staff(staffType, noteType, range, accidental, location);
		}
	}
	
	// Callback class for note type radio buttons.
	class NoteTypeListener implements ActionListener {
		// Changes the appropriate staff's note type to the one selected.
		public void actionPerformed(ActionEvent e) {
			musicDrawingPanel.getStaves()[currentTab].setNoteType(findCurrentNoteType());
		}
	}
	
	// Callback class for clef change radio buttons.
	class ClefTypeListener implements ActionListener {
		// Changes the appropriate staff to the type selected.
		public void actionPerformed(ActionEvent e) {
			musicDrawingPanel.getStaves()[currentTab].setStaffType(findCurrentStaffType());
			musicDrawingPanel.repaint();
		}
	}
	
	// Callback class for when the tab is changed.
	class TabListener implements ChangeListener {
		// Figures out and changes the current tab.
		public void stateChanged(ChangeEvent e) {
			JTabbedPane staffPane = (JTabbedPane) e.getSource();
			currentTab = staffPane.getSelectedIndex();
		}
	}
	
	// Callback class for accidental checkboxes.
	class AccidentalListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			musicDrawingPanel.getStaves()[currentTab].setAccidentals(findCurrentAccidentals());
		}
	}
	
	// Callback class for range dropdown boxes.
	class RangeListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			musicDrawingPanel.getStaves()[currentTab].setRange(findCurrentRange());
		}
	}
	
	// Returns the type of staff the current tab is set as.
	public StaffType findCurrentStaffType() {
		JPanel clefPanel = staffTabContents[currentTab][1];
		Component[] clefComponents = clefPanel.getComponents();
		
		JRadioButton trebleRadio = (JRadioButton) clefComponents[2];
		JRadioButton treble8vbRadio = (JRadioButton) clefComponents[4];
		JRadioButton bassRadio = (JRadioButton) clefComponents[6];
		
		if(trebleRadio.isSelected())
			return StaffType.TREBLE;
		else if(treble8vbRadio.isSelected())
			return StaffType.TREBLE8VB;
		else if(bassRadio.isSelected())
			return StaffType.BASS;
		else
			return null;
	}
	
	// Returns the type of note the current tab is set as.
	public NoteType findCurrentNoteType() {
		JPanel notePanel = staffTabContents[currentTab][2];
		Component[] noteComponents = notePanel.getComponents();
		
		JRadioButton singleRadio = (JRadioButton) noteComponents[2];
		JRadioButton multipleRadio = (JRadioButton) noteComponents[4];
		JRadioButton chordRadio = (JRadioButton) noteComponents[6];
		
		if(singleRadio.isSelected())
			return NoteType.SINGLE;
		else if(multipleRadio.isSelected())
			return NoteType.MULTIPLE;
		else if(chordRadio.isSelected())
			return NoteType.CHORD;
		else
			return null;
	}
	
	// Returns the current tab's set range.
	public NoteName[] findCurrentRange() {
		JPanel rangePanel = staffTabContents[currentTab][3];
		Component[] rangeComponents = rangePanel.getComponents();
		JComboBox[] rangeBoxes = { (JComboBox<NoteName>) rangeComponents[1],
								   (JComboBox<NoteName>) rangeComponents[3]};
		
		NoteName topNote = (NoteName) rangeBoxes[0].getSelectedItem();
		NoteName bottomNote = (NoteName) rangeBoxes[1].getSelectedItem();
		
		return new NoteName[] {topNote, bottomNote};
	}
	
	// Returns the current tab's set accidentals.
	public Set<Accidental> findCurrentAccidentals() {
		JPanel accidentalPanel = staffTabContents[currentTab][4];
		Component[] accidentalComponents = accidentalPanel.getComponents();
		
		// Gets references to all accidental checkboxes of current tab
		JCheckBox[] accidentalBoxes = new JCheckBox[5];
		for(int i = 0; i < accidentalBoxes.length; i++)
			accidentalBoxes[i] = (JCheckBox) accidentalComponents[2 + 2 * i];
		
		Set<Accidental> accidentals = new HashSet<Accidental>();
		
		// Figures out the boxes checked
		if(accidentalBoxes[0].isSelected()) // Double Flat
			accidentals.add(Accidental.DOUBLE_FLAT);
		if(accidentalBoxes[1].isSelected()) // Flat
			accidentals.add(Accidental.FLAT);
		if(accidentalBoxes[2].isSelected()) // Natural
			accidentals.add(Accidental.NATURAL);
		if(accidentalBoxes[3].isSelected()) // Sharp
			accidentals.add(Accidental.SHARP);
		if(accidentalBoxes[4].isSelected())// Double Sharp
			accidentals.add(Accidental.DOUBLE_SHARP);
		
		return accidentals;
	}
	
	// Returns where the current tab's staff's top corner should be.
	public Point findCurrentLocation() {
		Point location = (Point) TOP_CORNER.clone();
		location.translate(0, currentTab * (MusicDrawingPanel.GAP + (7*4)));
		
		return location;
	}
	
	public static void main(String[] args) { Music gui = new Music(); }
}
