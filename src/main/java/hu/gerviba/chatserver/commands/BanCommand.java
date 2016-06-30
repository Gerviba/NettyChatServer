package hu.gerviba.chatserver.commands;

import hu.gerviba.chatserver.datastores.User;
import io.netty.channel.Channel;

public class BanCommand extends Command {

	protected BanCommand() {
		super("/ban", 100, "Bans the entered username with a reson");
	}
	
	@Override
	protected void onCommand(User u, String msg, Channel c) {
		//FIXME: Implement /BAN command
	}
	
}
