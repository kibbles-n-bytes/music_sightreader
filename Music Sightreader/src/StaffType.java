//Creates an enumerated type that determines which type of staff the note is on.
public enum StaffType {
	TREBLE    (112),
	BASS      (70), 
	TREBLE8VB (0);
	
	private int offsetConstant;
	
	private StaffType(int offsetConstant) {
		this.offsetConstant = offsetConstant;
	}
	
	public int getConstant() {
		return offsetConstant;
	}
}