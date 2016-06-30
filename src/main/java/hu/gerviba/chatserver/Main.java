package hu.gerviba.chatserver;

import hu.gerviba.chatserver.commands.Command;
import hu.gerviba.chatserver.datastores.User;
import hu.gerviba.chatserver.rooms.ChatMode;
import hu.gerviba.chatserver.rooms.Room;
import hu.gerviba.chatserver.rooms.RoomType;
import hu.gerviba.chatserver.rooms.Task;

public class Main {

	public static void main(String[] args) {
		System.out.println("[INFO] Setting up server...");

		int port = 4000;
		String password = null;
		int droomid = 0, froomid = 0;
		String servername = "Unknown";
		boolean disGuests = false;
		boolean disReg = false;

		for (String s : args) {
			if (s.startsWith("--port=")) {
				port = Integer.parseInt(s.substring(7));
			} else if (s.startsWith("--password=")) {
				password = s.substring(11);
			} else if (s.startsWith("--droom=")) {
				droomid = Integer.parseInt(s.substring(8));
			} else if (s.startsWith("--name=")) {
				servername = s.substring(7);
			} else if (s.startsWith("--disableGuests")) {
				disGuests = true;
			} else if (s.startsWith("--disableRegister")) {
				disReg = true;
			} else if (s.startsWith("--froom=")) {
				froomid = Integer.parseInt(s.substring(8));
			}
		}

		Command.init();
		System.out.println("[INFO] Commands are ready!");

		Room defa = new Room(0, "Entrypoint", "This is the default room", -1, null, RoomType.PERMANENT,
				ChatMode.HIDDEN);
		defa.setJoinTask(new Task() {
			@Override
			public void run(User u) {
				u.getChannel().writeAndFlush("# Welcome " + u.getNickname() + "!\r\n");
				u.getChannel().writeAndFlush("# This is the " + ChatServer.getInstance().getServername()
						+ " server and your informations are:\r\n");
				u.getChannel().writeAndFlush("# Name:\t\t" + u.getUsername() + "\r\n");
				u.getChannel().writeAndFlush("# Alias:\t" + u.getNickname() + "\r\n");
				u.getChannel().writeAndFlush("# IP:\t\t" + u.getUuid() + "\r\n");
				u.getChannel().writeAndFlush("# Permission:\t" + u.getPermission() + "\r\n");
				u.getChannel().writeAndFlush("# You're now in the Entrypoint. You can not chat here.\r\n");
				u.getChannel().writeAndFlush("# Use /room <RoomName> command to switch room!\r\n");
				u.getChannel().writeAndFlush("---\r\n");

			}
		});
		Room.registerNew(defa);
		Room r1 = new Room(1, "Room1", "Test room", -1, null, RoomType.PERMANENT, ChatMode.EVERYBODY);
		Room.registerNew(r1);

		System.out.println("[INFO] Default room is ready!");

		ChatServer cs = new ChatServer(port, password, droomid, servername, disReg, disGuests, froomid);
		cs.run();
	}

}
