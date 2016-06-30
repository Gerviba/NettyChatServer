package hu.gerviba.chatserver.commands;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import hu.gerviba.chatserver.datastores.User;
import io.netty.channel.Channel;

public class TimeCommand extends Command {

	protected TimeCommand() {
		super("/time", -1, "Prints the server time");
	}

	@Override
	protected void onCommand(User u, String msg, Channel c) {
		LocalDateTime time = LocalDateTime.now();
		c.writeAndFlush("[SERVER] Current server time: " + time.format(DateTimeFormatter.ISO_DATE_TIME) + "\r\n");
	}

}
