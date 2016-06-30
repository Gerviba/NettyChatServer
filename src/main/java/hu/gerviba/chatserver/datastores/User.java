package hu.gerviba.chatserver.datastores;

import java.util.concurrent.ConcurrentHashMap;

import hu.gerviba.chatserver.IncomingSocketHandler;
import hu.gerviba.chatserver.rooms.Room;
import io.netty.channel.Channel;

public final class User implements Comparable<User> {

	private static final ConcurrentHashMap<String, User> USERS = new ConcurrentHashMap<>();

	public static void removeUser(String uuid) {
		USERS.remove(uuid);
	}

	public static User getUser(String uuid) {
		return USERS.get(uuid);
	}
	
	public static User getByNick(String nick) {
		return USERS.values().stream().filter(x -> x.nickname.equalsIgnoreCase(nick)).findFirst().get();
	}

	public static boolean contains(String name) {
		return USERS.values().parallelStream()
				.anyMatch(u -> u.username.equalsIgnoreCase(name) || u.nickname.equalsIgnoreCase(name));
	}

	private final String uuid;
	private String username;
	private String password;
	private String nickname;
	private Room room;
	private int permission;
	private boolean gui;

	public User(String uuid, String username, String password, String nickname, Room room, int privilege, boolean gui) {
		this.uuid = uuid;
		this.username = username;
		this.password = password;
		this.nickname = nickname;
		this.room = room;
		this.permission = privilege;
		this.gui = gui;
		USERS.put(uuid, this);
	}

	public User(String uuid, PreUser pu, Room r, boolean gui) {
		this.uuid = uuid;
		this.username = pu.getUsername();
		this.password = pu.getPassword();
		this.nickname = pu.getNickname();
		this.room = r;
		this.permission = pu.getPrivilege();
		this.gui = gui;
		USERS.put(uuid, this);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public int getPermission() {
		return permission;
	}

	public void setPermission(int permission) {
		this.permission = permission;
	}

	public String getUuid() {
		return uuid;
	}

	public Channel getChannel() {
		return IncomingSocketHandler.getChannel(this);
	}

	public boolean gui() {
		return gui;
	}

	@Override
	public String toString() {
		return "User [uuid=" + uuid + ", username=" + username + ", password=<HIDDEN>, nickname=" + nickname + ", room="
				+ room + ", permission=" + permission + ", gui=" + gui + "]";
	}

	@Override
	public int hashCode() {
		return uuid.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}

	@Override
	public int compareTo(User another) {
		return this.nickname.compareTo(another.nickname);
	}

}
