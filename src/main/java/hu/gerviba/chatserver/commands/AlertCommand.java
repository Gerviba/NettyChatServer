package hu.gerviba.chatserver.commands;

import hu.gerviba.chatserver.datastores.User;
import io.netty.channel.Channel;

public class AlertCommand extends Command {

	protected AlertCommand() {
		super("/alert", 0, "Sends a message to the given user");
	}
	
	@Override
	protected void onCommand(User u, String msg, Channel c) {
		String[] args = msg.split(" ", 3);
		if (args.length < 3) {
			c.writeAndFlush("[SERVER] Usage: /alert <User> <Message>\r\n");
		} else {
			User given = User.getByNick(args[1]);
			if (given == null) {
				c.writeAndFlush("[SERVER] The given user is not online\r\n");
			} else if (u.getPermission() < 200 && !u.getRoom().getUsers().contains(given)) {
				c.writeAndFlush("[SERVER] You can not send alert to the given user\r\n");
			} else {
				given.getChannel().writeAndFlush("[ALERT] "+u.getNickname()+": "+args[2]+"\r\n");
			}
		}
	}
	
}
