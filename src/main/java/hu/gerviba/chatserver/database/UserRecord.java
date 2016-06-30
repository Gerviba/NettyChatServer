package hu.gerviba.chatserver.database;

public class UserRecord {

	private final String name;
	private String nick, email, pass, ban;
	private int permission;

	public UserRecord(String name, String nick, String email, String pass, String ban, int permission) {
		this.name = name;
		this.nick = nick;
		this.email = email;
		this.pass = pass;
		this.ban = ban;
		this.permission = permission;
	}

	public int getPermission() {
		return permission;
	}

	public void setPermission(int permission) {
		this.permission = permission;
	}

	public String getName() {
		return name;
	}

	public String getNick() {
		return nick;
	}

	public String getEmail() {
		return email;
	}

	public String getPass() {
		return pass;
	}

	public String getBan() {
		return ban;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public void setBan(String ban) {
		this.ban = ban;
	}
	
}
