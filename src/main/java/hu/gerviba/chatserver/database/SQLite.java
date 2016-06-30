package hu.gerviba.chatserver.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import hu.gerviba.chatserver.ChatServer;
import hu.gerviba.chatserver.datastores.PreUser;
import hu.gerviba.chatserver.datastores.User;

public class SQLite implements DatabaseConnector {

	private Connection c = null;

	@Override
	public void init() {
		System.out.println("[DATA] Selected database: SQLite");

		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:test.db");
			c.setAutoCommit(false);
			System.out.println("[DATA] Database connection: OK");
		} catch (ClassNotFoundException e) {
			System.err.println("[DATA] SQLite not supported");
			throw new RuntimeException("SQLite not supported");
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			throw new RuntimeException("SQLite connect failure", e);
		}
	}

	@Override
	public void query(String query) {
		try (Statement s = c.createStatement()) {
			s.executeUpdate(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Connection getConnection() {
		return c;
	}

	@Override
	public void close() {
		System.out.println("[DATA] Closing database: SQLite");
		try {
			c.close();
			System.out.println("[DATA] SQLite closed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public PreUser prepareUser(String name, String uuid) {
		ResultSet rs = null;
		try (PreparedStatement ps = c.prepareStatement("SELECT * FROM `USERS` WHERE `NAME`='?' LIMIT 1;")) {
			ps.setString(1, name);
			rs = ps.executeQuery();
			if(rs.first()) {
				return new PreUser(rs.getString("NAME"), rs.getString("PASS"), rs.getString("NICK"), rs.getInt("PERMISSION"), rs.getString("BAN"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) try { rs.close(); } catch (SQLException e) {}
		}
		return null;
	}

	@Override
	public boolean isNameUsed(String name) {
		ResultSet rs = null;
		try (PreparedStatement ps = c.prepareStatement("SELECT * FROM `USERS` WHERE `NAME`='?' LIMIT 1;")) {
			ps.setString(1, name);
			rs = ps.executeQuery();
			return rs.first();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) try { rs.close(); } catch (SQLException e) {}
		}
		return false;
	}

	@Override
	public boolean isNickUsed(String nick) {
		ResultSet rs = null;
		try (PreparedStatement ps = c.prepareStatement("SELECT * FROM `USERS` WHERE `NICK`='?' LIMIT 1;")) {
			ps.setString(1, nick);
			rs = ps.executeQuery();
			return rs.first();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) try { rs.close(); } catch (SQLException e) {}
		}
		return false;
	}

	@Override
	public boolean isEmailUsed(String mail) {
		ResultSet rs = null;
		try (PreparedStatement ps = c.prepareStatement("SELECT * FROM `USERS` WHERE `EMAIL`='?' LIMIT 1;")) {
			ps.setString(1, mail);
			rs = ps.executeQuery();
			return rs.first();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) try { rs.close(); } catch (SQLException e) {}
		}
		return false;
	}

	@Override
	public User registerUser(String name, String pass, String nick, String mail, String uuid, boolean gui) {
		ResultSet rs = null;
		try (PreparedStatement ps = c.prepareStatement("INSERT INTO `USERS` (`NAME`,`PASS`,`NICK`,`EMAIL`,`PERMISSION`,`BAN`) VALUES ('?','?','?','?','0','null');")) {
			ps.setString(1, name);
			ps.setString(2, pass);
			ps.setString(3, nick);
			ps.setString(4, mail);
			ps.executeUpdate();
			return new User(uuid, name, pass, nick, ChatServer.getInstance().getDefaultRoom(), 0, gui);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) try { rs.close(); } catch (SQLException e) {}
		}
		return null;
	}

	@Override
	public UserRecord getUserRecord(String name) {
		// TODO Implement
		return null;
	}

}
