package hu.gerviba.chatserver.commands;

import hu.gerviba.chatserver.IncomingSocketHandler;
import hu.gerviba.chatserver.datastores.User;
import io.netty.channel.Channel;

public class KickAllCommand  extends Command {

	protected KickAllCommand() {
		super("/kickall", 100, "Kicks everybody from the server with a reason");
	}
	
	@Override
	protected void onCommand(User u, String msg, Channel c) {
		String[] args = msg.split(" ", 2);
		if (args.length < 2) {
			c.writeAndFlush("[SERVER] Usage: /kickall <Reason>\r\n");
		} else {
			IncomingSocketHandler.kickAll(args[1]);
		}
	}
}
