package hu.gerviba.chatserver.database;

import java.sql.Connection;
import java.util.ArrayList;

import hu.gerviba.chatserver.ChatServer;
import hu.gerviba.chatserver.datastores.PreUser;
import hu.gerviba.chatserver.datastores.User;
import hu.gerviba.chatserver.utils.Util;

public class TestDB implements DatabaseConnector {
	
	private final ArrayList<UserRecord> USERS = new ArrayList<>();
	
	@Override
	public void init() {
		USERS.add(new UserRecord("SYSTEM", "SysAdmin", "admin@system.local", Util.encryptPassword(System.getProperty("SYSADMIN_PASS", "SYS1234")), "null", 200));
	}

	@Override
	public void query(String query) {
		System.out.println("[DATA] Function not supported: QUERY -> "+query);
	}

	@Override
	public Connection getConnection() {
		System.out.println("[DATA] Function not supported: CONNECTION");
		return null;
	}

	@Override
	public void close() {
		System.out.println("[DATA] Function not supported: CLOSE");
	}

	@Override
	public PreUser prepareUser(String name, String uuid) {
		UserRecord u = USERS.stream().filter(x -> x.getName().equalsIgnoreCase(name)).limit(1).findFirst().orElse(null);
		return u == null ? null : new PreUser(u.getName(), u.getPass(), u.getNick(), u.getPermission(), u.getBan());
	}

	@Override
	public boolean isNameUsed(String name) {
		return USERS.stream().filter(x -> x.getName().equalsIgnoreCase(name)).limit(1).count() == 1;
	}

	@Override
	public boolean isNickUsed(String name) {
		return USERS.stream().filter(x -> x.getNick().equalsIgnoreCase(name)).limit(1).count() == 1;
	}

	@Override
	public boolean isEmailUsed(String mail) {
		return USERS.stream().filter(x -> x.getEmail().equalsIgnoreCase(mail)).limit(1).count() == 1;
	}

	@Override
	public User registerUser(String name, String pass, String nick, String mail, String uuid, boolean gui) {
		USERS.add(new UserRecord(name, nick, mail, pass, "null", 0));
		return new User(uuid, name, pass, nick, ChatServer.getInstance().getFirstRoom(), 0, gui);
	}

	public String getBanned(String name) {
		return USERS.stream().filter(x -> x.getName().equalsIgnoreCase(name)).limit(1).findFirst().get().getBan();
	}
	
	public String getBannedSafe(String name) {
		if(isNameUsed(name))
			return USERS.stream().filter(x -> x.getName().equalsIgnoreCase(name)).limit(1).findFirst().get().getBan();
		else
			return "null";
	}
	
	public void setBanned(String name, String ban) {
		USERS.stream().filter(x -> x.getName().equalsIgnoreCase(name)).limit(1).findFirst().get().setBan(ban);
	}
	
	public void setPassword(String name, String hash) {
		USERS.stream().filter(x -> x.getName().equalsIgnoreCase(name)).limit(1).findFirst().get().setPass(hash);
	}

	@Override
	public UserRecord getUserRecord(String name) {
		return USERS.stream().filter(x -> x.getName().equalsIgnoreCase(name)).limit(1).findFirst().orElse(USERS.stream().filter(x -> x.getNick().equalsIgnoreCase(name)).limit(1).findFirst().orElse(null));
	}
}
