package hu.gerviba.chatserver.database;

import java.sql.Connection;

import hu.gerviba.chatserver.datastores.PreUser;
import hu.gerviba.chatserver.datastores.User;

public interface DatabaseConnector {
	
	public void init();
	public void query(String query);
	public Connection getConnection();
	public void close();
	
	public PreUser prepareUser(String name, String uuid);
	public boolean isNameUsed(String name);
	public boolean isNickUsed(String name);
	public boolean isEmailUsed(String mail);
	public User registerUser(String name, String pass, String nick, String mail, String uuid, boolean gui);
	public UserRecord getUserRecord(String name);
	
}
