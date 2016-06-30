package hu.gerviba.chatserver.rooms;

public enum ChatMode {
	HIDDEN(-1), 
	FULL_DENY(Integer.MAX_VALUE), //NOBODY
	MIN_0(0), //REGISTERED
	MIN_1(1), //HIGHER PRIVIEDGE
	MIN_50(50), //MODERATOR
	MIN_100(100), //OPERATOR
	ONLY_200(200), //SUPER ADMIN
	EVERYBODY(-1);

	private final int perm;

	private ChatMode(int perm) {
		this.perm = perm;
	}

	public int getPerm() {
		return perm;
	}
	
}
