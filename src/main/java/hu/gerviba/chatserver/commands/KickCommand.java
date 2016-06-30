package hu.gerviba.chatserver.commands;

import hu.gerviba.chatserver.IncomingSocketHandler;
import hu.gerviba.chatserver.datastores.User;
import io.netty.channel.Channel;

public class KickCommand extends Command {

	protected KickCommand() {
		super("/kick", 50, "Kicks the given user with a reason");
	}
	
	@Override
	protected void onCommand(User u, String msg, Channel c) {
		String[] args = msg.split(" ", 3);
		if (args.length < 2) {
			c.writeAndFlush("[SERVER] Usage: /kick <User> [Reason]\r\n");
		} else {
			User given = User.getUser(args[1]);
			if (given == null) {
				c.writeAndFlush("[SERVER] The given user is not online\r\n");
			} else {
				IncomingSocketHandler.kick(u, args.length == 3 ? args[2] : "You got kicked from the server!");
			}
		}
	}
	
}
