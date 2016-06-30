package hu.gerviba.chatserver.commands;

import java.util.ArrayList;

import hu.gerviba.chatserver.IncomingSocketHandler;
import hu.gerviba.chatserver.datastores.User;
import hu.gerviba.chatserver.rooms.Room;
import hu.gerviba.chatserver.utils.Util;
import io.netty.channel.Channel;

public class ListCommand extends Command {

	protected ListCommand() {
		super("/list", -1, "Lists the online players");
	}

	@Override
	protected void onCommand(User u, String msg, Channel c) {
		if (u.getPermission() < 100) {
			c.writeAndFlush("[SERVER] Users in your channel: " + Util.pack(u.getRoom().getUsers()) + "\r\n");
		} else {
			c.writeAndFlush("[SERVER] Users in your channel: " + Util.pack(u.getRoom().getUsers()) + "\r\n");
			c.writeAndFlush("[SERVER] Users in other channels: " + packAllExcept(u.getRoom()) + "\r\n");
			c.writeAndFlush("[SERVER] Online count: " + IncomingSocketHandler.online() + "\r\n");
		}
	}

	private String packAllExcept(Room except) {
		ArrayList<User> temp = new ArrayList<>();
		Room.getRooms().forEach(x -> temp.addAll(x.getUsers()));
		return Util.pack(temp);
	}

}
