package hu.gerviba.chatserver.commands;

import hu.gerviba.chatserver.ChatServer;
import hu.gerviba.chatserver.database.DatabaseManager;
import hu.gerviba.chatserver.datastores.PreUser;
import hu.gerviba.chatserver.datastores.User;
import hu.gerviba.chatserver.rooms.Room;
import hu.gerviba.chatserver.utils.Util;
import io.netty.channel.Channel;

public class HandShake extends Command {

	private static long guestId = 1;

	// TODO: Receive client version
	protected HandShake() {
		super("//handshake//", -1, "Used in the auth");
	}

	@Override
	protected void onCommand(User u, String msg, Channel c) {
		if (u != null) {
			System.out.println("[WARN] Duplicated handshake from " + u.getUuid());
			return;
		}

		try {
			String[] args = msg.split(" ");

			String name = null;
			String pass = null;
			String serverPass = null;
			String room = null;
			boolean gui = false;
			boolean register = false; // TODO: implement
			String mail = null;
			String nick = null;

			for (String s : args) {
				switch (s.substring(0, 2)) {
				case "u:":
					name = s.substring(2);
					if (name.length() == 0)
						name = null;
					break;
				case "p:":
					pass = s.substring(2);
					if (pass.length() == 0)
						pass = null;
					break;
				case "r:":
					room = s.substring(2);
					if (room.length() == 0)
						room = null;
					break;
				case "s:":
					serverPass = s.substring(2);
					if (serverPass.length() == 0)
						serverPass = null;
					break;
				case "m:":
					mail = s.substring(2);
					if (mail.length() == 0)
						mail = null;
					break;
				case "n:":
					nick = s.substring(2);
					if (nick.length() == 0)
						nick = null;
					break;
				case "-g":
					gui = true;
					break;
				case "-r":
					register = true;
					break;
				}

			}

			if (ChatServer.getInstance().getPassword() != null) {
				if (serverPass == null) {
					if (gui) {
						c.writeAndFlush("[GUI] HANDSHAKE_SERVERPASS_NEEDED\r\n");
					} else {
						c.writeAndFlush("[SERVER] This server is password protected!\r\n");
					}
					return;
				}
				if (!ChatServer.getInstance().getPassword().equals(serverPass)) {
					if (gui) {
						c.writeAndFlush("[GUI] HANDSHAKE_SERVERPASS_INVALID\r\n");
					} else {
						c.writeAndFlush("[SERVER] Password of the server is invalid!\r\n");
					}
					System.out.println("[HAND] Invalid server password!");
					c.close();
					return;
				}
			}

			if (register) {
				if (name == null) {
					if (gui) {
						c.writeAndFlush("[GUI] HANDSHAKE_REG_NO_NAME\r\n");
					} else {
						c.writeAndFlush("[SERVER] Please include a username to register!\r\n");
					}
				} else if (pass == null) {
					if (gui) {
						c.writeAndFlush("[GUI] HANDSHAKE_REG_NO_PASS\r\n");
					} else {
						c.writeAndFlush("[SERVER] Please include a pasword to register!\r\n");
					}
				} else if (mail == null) {
					if (gui) {
						c.writeAndFlush("[GUI] HANDSHAKE_REG_NO_MAIL\r\n");
					} else {
						c.writeAndFlush("[SERVER] Please include an email address to register!\r\n");
					}
				} else if (nick == null) {
					if (gui) {
						c.writeAndFlush("[GUI] HANDSHAKE_REG_NO_NICK\r\n");
					} else {
						c.writeAndFlush("[SERVER] Please include a nickname to register!\r\n");
					}
				} else if (name.length() < 3 || name.length() > 16) {
					if (gui) {
						c.writeAndFlush("[GUI] HANDSHAKE_REG_WRONG_NAME\r\n");
					} else {
						c.writeAndFlush("[SERVER] Invalid name format! (2-32 alphanumeric chars)\r\n");
					}
				} else if (nick.length() < 3 || nick.length() > 16) {
					if (gui) {
						c.writeAndFlush("[GUI] HANDSHAKE_REG_WRONG_NICK\r\n");
					} else {
						c.writeAndFlush("[SERVER] Invalid nickname format! (3-16 alphanumeric chars)\r\n");
					}
				} else if (!mail.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
					if (gui) {
						c.writeAndFlush("[GUI] HANDSHAKE_REG_WRONG_EMAIL\r\n");
					} else {
						c.writeAndFlush("[SERVER] Invalid email format!\r\n");
					}
				} else if (!Util.validHash(pass)) {
					if (gui) {
						c.writeAndFlush("[GUI] HANDSHAKE_REG_WRONG_HASH\r\n");
					} else {
						c.writeAndFlush("[SERVER] Invalid password hash!\r\n");
					}
				} else if (DatabaseManager.getDatabase().isNameUsed(name)) {
					if (gui) {
						c.writeAndFlush("[GUI] HANDSHAKE_REG_USED_NAME\r\n");
					} else {
						c.writeAndFlush("[SERVER] Username used!\r\n");
					}
				} else if (DatabaseManager.getDatabase().isNickUsed(name)) {
					if (gui) {
						c.writeAndFlush("[GUI] HANDSHAKE_REG_USED_NICK\r\n");
					} else {
						c.writeAndFlush("[SERVER] Nickname used!\r\n");
					}
				} else if (DatabaseManager.getDatabase().isEmailUsed(mail)) {
					if (gui) {
						c.writeAndFlush("[GUI] HANDSHAKE_REG_USED_MAIL\r\n");
					} else {
						c.writeAndFlush("[SERVER] Email address used!\r\n");
					}
				} else {
					User user = DatabaseManager.getDatabase().registerUser(name, pass, nick, mail,
							c.remoteAddress().toString(), gui);
					if (gui) {
						c.writeAndFlush("[GUI] HANDSHAKE_OK\r\n");
					}

					Room reqRoom = Room.getById(Util.parseInt(room, 0));
					if (reqRoom.getVisible() <= user.getPermission()) {
						reqRoom.join(user, true);
					} else {
						ChatServer.getInstance().getDefaultRoom().join(user, true);
					}
					return;
				}

				System.out.println("[HAND] " + c.remoteAddress().toString() + ": Invalid parameter(s)! (name:" + name
						+ ", nick:" + nick + ", mail:" + mail + ", pass:" + pass + ")");
				return;
			}

			if (name == null) {
				if (ChatServer.getInstance().isGuestDisabled()) {
					if (gui) {
						c.writeAndFlush("[GUI] HANDSHAKE_NO_GUESTS\r\n");
					} else {
						c.writeAndFlush("[SERVER] Guest users are disabled in this server!\r\n");
					}
					return;
				}

				User user = new User(c.remoteAddress().toString(), "Guest" + guestId, null, "Guest" + (guestId++),
						ChatServer.getInstance().getDefaultRoom(), -1, gui);

				if (gui) {
					c.writeAndFlush("[GUI] HANDSHAKE_OK\r\n");
				}

				Room reqRoom = Room.getById(Util.parseInt(room, 0));
				if (reqRoom.getVisible() <= user.getPermission()) {
					reqRoom.join(user, true);
				} else {
					ChatServer.getInstance().getDefaultRoom().join(user, true);
				}
				System.out.println("[HAND] " + c.remoteAddress().toString() + ": Completed as Guest!");
			} else {
				PreUser pu = DatabaseManager.getDatabase().prepareUser(name, c.remoteAddress().toString());

				if (pu == null) {
					if (gui) {
						c.writeAndFlush("[GUI] HANDSHAKE_INVALID_PASSWORD\r\n");
					} else {
						c.writeAndFlush("[SERVER] Invalid password!\r\n");
					}
				}

				if (pu.isBanned()) {
					if (gui) {
						c.writeAndFlush("[GUI] BAN " + pu.getReason() + "\r\n");
					} else {
						c.writeAndFlush("[SERVER] You're banned from this server! Reason: " + pu.getReason() + "\r\n");
					}
				}

				if (pass == null && pu.getPassword() != null) {
					c.writeAndFlush("[SERVER] Please send password!\r\n");
					System.out.println("[HAND] " + c.remoteAddress().toString() + ": No password found!");
					c.close();
					return;
				}

				if (User.contains(name)) {
					if (gui) {
						c.writeAndFlush("[GUI] HANDSHAKE_ALREADY_ONLINE\r\n");
					} else {
						c.writeAndFlush("[SERVER] Already online!\r\n");
					}
					System.out.println("[HAND] " + c.remoteAddress().toString() + ": Already online!");
					c.close();
					return;
				}

				if (pu.getPassword().equals(pass)) {
					User user = new User(c.remoteAddress().toString(), pu, ChatServer.getInstance().getDefaultRoom(),
							gui);

					Room reqRoom = room == null ? ChatServer.getInstance().getDefaultRoom()
							: Room.getById(Util.parseInt(room, 0));
					if (gui) {
						c.writeAndFlush("[GUI] HANDSHAKE_OK\r\n");
					}

					if (reqRoom != null && reqRoom.getVisible() <= user.getPermission()) {
						reqRoom.join(user, true);
					} else {
						ChatServer.getInstance().getDefaultRoom().join(user, true);
					}
					System.out.println("[HAND] " + c.remoteAddress().toString() + ": Completed!");
				} else {
					if (gui) {
						c.writeAndFlush("[GUI] HANDSHAKE_INVALID_PASSWORD\r\n");
					} else {
						c.writeAndFlush("[SERVER] Invalid password!\r\n");
					}
					System.out.println("[HAND] " + c.remoteAddress().toString() + ": Invalid password!");
					c.close();
				}
			}

		} catch (Exception e) {
			System.out.println("[HAND] " + c.remoteAddress().toString() + ": Invalid handshake");
			e.printStackTrace();
			c.close();
		}
	}

}
