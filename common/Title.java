package common;


public enum Title {
	BOSS(0, "Boss"), 
	CAPO(1, "Caporegime"), 
	SOLDIER(2, "Soldier"), 
	ASSOC(3, "Associate");

	public final int rank;
	private final String title;

	private Title(int rank, String title) {
		this.rank = rank;
		this.title = title;
	}
	
	public Title getTitle(int rank) {
		switch(rank) {
		case 0:
			return BOSS;
		case 1:
			return CAPO;
		case 2:
			return SOLDIER;
		case 3:
			return ASSOC;
		default:
			return null;
		}
	}

	@Override
	public String toString() {
		return title;
	}
}
