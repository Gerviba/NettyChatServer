package hu.gerviba.chatserver.commands;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import org.reflections.Reflections;

import hu.gerviba.chatserver.datastores.User;
import io.netty.channel.Channel;

public abstract class Command {

	private static final HashMap<String, Command> COMMANDS = new HashMap<>();

	public static boolean process(User u, Channel c, String label, String msg) {
		if (COMMANDS.containsKey(label)) {
			COMMANDS.get(label).performCommand(u, msg, c);
			return true;
		} else {
			return false;
		}
	}

	public static Command getCommand(String label) {
		return COMMANDS.get(label.toLowerCase());
	}

	public static void init() {
		Set<Class<? extends Command>> annotated = new Reflections("hu.gerviba.chatserver.commands")
				.getSubTypesOf(Command.class);
		for (Class<? extends Command> c : annotated) {
			Command cmd = null;
			try {
				cmd = c.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			if (cmd != null)
				COMMANDS.put(cmd.getLabel(), cmd);
		}
	}

	private final String label;
	private final int permission;
	private final String manual;

	protected Command(String label, int permission, String manual) {
		this.label = label;
		this.permission = permission;
		this.manual = manual;
		System.out.println("[INFO] Command '" + label + "' registered");
	}

	protected void onCommand(User u, String msg, Channel c) {
		System.out.println("Interpreter not implemented!");
	}

	public final void performCommand(User u, String msg, Channel c) {
		if (u != null && u.getPermission() < permission) {
			c.writeAndFlush("[SERVER] You don't have permission to use this command!\r\n");
		} else {
			onCommand(u, msg, c);
		}
	}

	public String getLabel() {
		return label;
	}

	public int getPermission() {
		return permission;
	}

	public String getManual() {
		return manual;
	}

	public static Collection<Command> getAll() {
		return COMMANDS.values();
	}

}
