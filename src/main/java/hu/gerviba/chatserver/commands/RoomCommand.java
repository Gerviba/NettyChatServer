package hu.gerviba.chatserver.commands;

import hu.gerviba.chatserver.datastores.User;
import hu.gerviba.chatserver.rooms.Room;
import io.netty.channel.Channel;

public class RoomCommand extends Command {

	protected RoomCommand() {
		super("/room", -1, "Change room command");
	}

	@Override
	protected void onCommand(User u, String msg, Channel c) {
		String[] args = msg.split(" ");
		if (args.length == 1) {
			c.writeAndFlush("[SERVER] Usage: /room <RoomName>\r\n");
			return;
		}

		Room r = Room.getByName(args[1]);
		if (r == null) {
			c.writeAndFlush("[SERVER] Room not found\r\n");
		} else if (r.getVisible() > u.getPermission()) {
			c.writeAndFlush("[SERVER] You don't have permission to join\r\n");
		} else {
			u.getRoom().leave(u, true);
			r.join(u, true);
			c.writeAndFlush("[SERVER] You are now in the '" + u.getRoom().getName() + "' room\r\n");
		}

	}

}
