package hu.gerviba.chatserver.commands;

import hu.gerviba.chatserver.datastores.User;
import hu.gerviba.chatserver.rooms.Room;
import hu.gerviba.chatserver.utils.Util;
import io.netty.channel.Channel;

public class RoomsCommand extends Command {

	protected RoomsCommand() {
		super("/rooms", -1, "Prints the available rooms");
	}

	@Override
	protected void onCommand(User u, String msg, Channel c) {
		c.writeAndFlush("Available rooms:\r\n");
		if (u.getPermission() >= 200)
			Room.getRooms().parallelStream().filter(x -> x.getVisible() <= u.getPermission()).sorted()
					.forEachOrdered(x -> c.writeAndFlush(x.getId() + "\t" + Util.escape(x.getName(), 33) + x.getDescription() + "\r\n"));
		else
			Room.getRooms().parallelStream().filter(x -> x.getVisible() <= u.getPermission()).sorted()
					.forEachOrdered(x -> c.writeAndFlush(Util.escape(x.getName(), 33) + x.getDescription() + "\r\n"));
	}

}
