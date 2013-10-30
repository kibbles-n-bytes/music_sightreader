//Creates an enumerated type for the possible accidentals.

public enum Accidental {
	DOUBLE_FLAT (-2),
	FLAT (-1),
	NATURAL (0), 
	SHARP (1), 
	DOUBLE_SHARP (2);
	
	private int value;
	
	// Creates an accidental with the given value.
	private Accidental(int value) { this.value = value; }
	
	//Get the value of the accidental.
	public int getValue() { return value; }
	
	//Gives the corresponding accidental with the given value.
	public static Accidental getCorrespondingAccidental(int value) {
		for(Accidental a: values()) {
			if(a.getValue() == value)
				return a;
		}
		return null;
	}
}