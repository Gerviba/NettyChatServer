package hu.gerviba.chatserver.commands;

import hu.gerviba.chatserver.datastores.User;
import hu.gerviba.chatserver.rooms.Room;
import hu.gerviba.chatserver.utils.Util;
import io.netty.channel.Channel;

public class UsersCommand extends Command {

	protected UsersCommand() {
		super("/users", 100, "Prints some informations about all online users");
	}

	@Override
	protected void onCommand(User u, String msg, Channel c) {
		Room.getRooms().stream().sorted().forEachOrdered(x -> {
			c.writeAndFlush(x.getName() + ":\r\n");
			x.getUsers().stream().sorted().forEachOrdered(
					user -> c.writeAndFlush("|- " + Util.escape(user.getNickname(), 17) + user.getUsername() + "\r\n"));
		});
	}

}
