package hu.gerviba.chatserver.commands;

import hu.gerviba.chatserver.ChatServer;
import hu.gerviba.chatserver.IncomingSocketHandler;
import hu.gerviba.chatserver.datastores.User;
import io.netty.channel.Channel;

public class StopCommand extends Command {

	protected StopCommand() {
		super("/stop", 200, "Stops the server");
	}

	@Override
	protected void onCommand(User u, String msg, Channel c) {
		System.out.println(u.getNickname() + " is stopping the server");
		IncomingSocketHandler.kickAll("Stopping server");
		ChatServer.getInstance().stop();
	}

}
