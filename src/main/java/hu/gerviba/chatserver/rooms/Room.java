package hu.gerviba.chatserver.rooms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import hu.gerviba.chatserver.ChatServer;
import hu.gerviba.chatserver.datastores.User;

public class Room implements Comparable<Room> {

	private static final HashMap<Integer, Room> ROOMS = new HashMap<>();

	public static void registerNew(Room room) {
		ROOMS.put(room.id, room);
	}

	public static void registerNewSafe(Room room) {
		if (room.id == ChatServer.getInstance().getDefaultRoomId())
			return;
		if (ROOMS.containsKey(room.id)) {
			Room old = ROOMS.remove(room.id);
			old.moveTo(ChatServer.getInstance().getDefaultRoom());
		}
		ROOMS.put(room.id, room);
	}

	public static Room getById(int roomId) {
		return ROOMS.get(roomId);
	}

	public static Room getByName(String name) {
		for (Room r : ROOMS.values()) {
			if (r.getName().equalsIgnoreCase(name))
				return r;
		}
		return null;
	}

	public static Collection<Room> getRooms() {
		return ROOMS.values();
	}

	private final int id;
	private String name;
	private String description;
	private int visible;
	private String password;
	private ArrayList<User> users = new ArrayList<>();
	private RoomType type;
	private ChatMode mode;
	private Task join = null;

	public Room(int id, String name, String description, int visible, String password, RoomType type, ChatMode mode) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.visible = visible;
		this.password = password;
		this.type = type;
		this.mode = mode;
	}

	public void setJoinTask(Task r) {
		this.join = r;
	}

	public void join(User u, boolean message) {
		if (message)
			broadcast("[SERVER] " + u.getNickname() + " joined to your channel");
		System.out.println("[INFO] " + u.getNickname() + " is now in the '" + name + "' room");
		if (u.gui())
			u.getChannel().writeAndFlush("[GUI] ROOM " + name + "\r\n");
		users.add(u);
		u.setRoom(this);
		if (join != null)
			join.run(u);
	}

	public void leave(User u, boolean message) {
		users.remove(u);
		if (message)
			broadcast("[SERVER] " + u.getNickname() + " left from your channel");
	}

	public void quit(User u, boolean forced) {
		users.remove(u);
		if (forced)
			broadcast("[SERVER] " + u.getNickname() + " forcibly disconnected from your channel");
		else
			broadcast("[SERVER] " + u.getNickname() + " disconnected from your channel");
	}

	public void moveTo(Room r) {
		broadcast("[SERVER] You will be moved to the " + r.name + " room");
		for (User u : users) {
			leave(u, false);
			r.join(u, false);
		}
	}

	public void message(User sender, String msg) {
		if (mode == ChatMode.HIDDEN)
			return;
		if (mode.getPerm() > sender.getPermission()) {
			sender.getChannel().writeAndFlush("[SERVER] You have no permission to send message\r\n");
			return;
		}
		msg += "\r\n";
		for (User u : users) {
			if (!u.getUuid().equals(sender.getUuid()))
				u.getChannel().writeAndFlush("(" + name + "): " + sender.getNickname() + " >> " + msg);
		}
		sender.getChannel().writeAndFlush("(" + name + "): You >> " + msg);
	}

	public void broadcast(String msg) {
		msg += "\r\n";
		for (User u : users) {
			u.getChannel().writeAndFlush(msg);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getVisible() {
		return visible;
	}

	public void setVisible(int visible) {
		this.visible = visible;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public ArrayList<User> getUsers() {
		return users;
	}

	public void setUsers(ArrayList<User> users) {
		this.users = users;
	}

	public RoomType getType() {
		return type;
	}

	public void setType(RoomType type) {
		this.type = type;
	}

	public ChatMode getMode() {
		return mode;
	}

	public void setMode(ChatMode mode) {
		this.mode = mode;
	}

	public int getId() {
		return id;
	}
	
	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Room other = (Room) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public int compareTo(Room another) {
		return this.name.compareTo(another.name);
	}

}
