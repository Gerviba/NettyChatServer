package hu.gerviba.chatserver.datastores;

public class PreUser {

	private final String username;
	private final String password;
	private final String nickname;
	private final int priviledge;
	private final String banned;

	public PreUser(String username, String password, String nickname, int priviledge, String banned) {
		this.username = username;
		this.password = password;
		this.nickname = nickname;
		this.priviledge = priviledge;
		this.banned = banned;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getNickname() {
		return nickname;
	}

	public int getPrivilege() {
		return priviledge;
	}

	public boolean isBanned() {
		return !banned.equals("null");
	}

	public String getReason() {
		return banned;
	}

}
