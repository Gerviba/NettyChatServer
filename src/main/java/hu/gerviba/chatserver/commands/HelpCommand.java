package hu.gerviba.chatserver.commands;

import hu.gerviba.chatserver.datastores.User;
import hu.gerviba.chatserver.utils.Util;
import io.netty.channel.Channel;

public class HelpCommand extends Command {

	protected HelpCommand() {
		super("/help", -1, "Prints this message");
	}

	@Override
	protected void onCommand(User u, String msg, Channel c) {
		c.writeAndFlush("You can use this commands:\r\n");
		Command.getAll().parallelStream().filter(x -> x.getPermission() <= u.getPermission())
				.filter(x -> x.getLabel().charAt(1) != '/').sorted((a, b) -> a.getLabel().compareTo(b.getLabel()))
				.forEachOrdered(x -> c.writeAndFlush(Util.escape(x.getLabel(), 18) + "\t" + x.getManual() + "\r\n"));
	}

}
