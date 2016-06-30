package hu.gerviba.chatserver.commands;

import hu.gerviba.chatserver.database.DatabaseManager;
import hu.gerviba.chatserver.database.UserRecord;
import hu.gerviba.chatserver.datastores.User;
import io.netty.channel.Channel;

public class WhoisCommand extends Command {

	protected WhoisCommand() {
		super("/whois", 100, "Prints informations about the given user");
	}

	@Override
	protected void onCommand(User u, String msg, Channel c) {
		String[] args = msg.split(" ", 3);
		if (args.length < 3) {
			c.writeAndFlush("[SERVER] Usage: /whois <User>\r\n");
		} else {
			User given = User.getByNick(args[1]);
			if (given == null) {
				c.writeAndFlush("[SERVER] The given user is not online. Retriving user data...\r\n");
				UserRecord ud = DatabaseManager.getDatabase().getUserRecord(args[1]);
				if (ud == null) {
					c.writeAndFlush("[SERVER] User not found.\r\n");
				} else {
					c.writeAndFlush("[SERVER] Result:\r\n");
					c.writeAndFlush("         - Username....: " + ud.getName() + "\r\n");
					c.writeAndFlush("         - Nickname....: " + ud.getNick() + "\r\n");
					c.writeAndFlush("         - Email.......: " + ud.getEmail() + "\r\n");
					c.writeAndFlush("         - Permission..: " + ud.getPermission() + "\r\n");
					c.writeAndFlush("         - Room........: OFFLINE\r\n");
				}
			} else {
				// FIXME: Implement this part of the /WHOIS command
			}
		}
	}

}
