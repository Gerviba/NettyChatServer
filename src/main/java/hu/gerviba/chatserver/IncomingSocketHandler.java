package hu.gerviba.chatserver;

import java.io.IOException;

import hu.gerviba.chatserver.commands.Command;
import hu.gerviba.chatserver.datastores.User;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class IncomingSocketHandler extends SimpleChannelInboundHandler<String> {

	private static final ChannelGroup CHANNELS = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

	public static Channel getChannel(User u) {
		for (Channel c : CHANNELS) {
			if (c.remoteAddress().toString().equals(u.getUuid()))
				return c;
		}
		return null;
	}

	public static void kickAll(String reason) {
		for (Channel c : CHANNELS) {
			c.writeAndFlush("[KICK] " + reason + "\r\n");
			CHANNELS.remove(c);
			c.close();
		}
	}

	public static void kick(User u, String reason) {
		Channel c = u.getChannel();
		c.writeAndFlush("[KICK] " + reason + "\r\n");
		CHANNELS.remove(c);
		c.close();
	}

	public static int online() {
		return CHANNELS.size();
	}

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		Channel incoming = ctx.channel();
		System.out.println("[INFO] " + incoming.remoteAddress() + " is starting to connect");
		CHANNELS.add(incoming);
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		Channel incoming = ctx.channel();
		System.out.println("[INFO] " + incoming.remoteAddress() + " is starting to disconnnect");
		incoming.writeAndFlush("[SERVER] Bye!\r\n");
		CHANNELS.remove(incoming);

		User u = User.getUser(incoming.remoteAddress().toString());
		if (u == null) {
			System.out.println("[INFO] Disconnect without handshake from " + incoming.remoteAddress().toString());
		} else {
			u.getRoom().quit(u, true);
			System.out.println(
					"[INFO] " + u.getUsername() + " (" + u.getNickname() + ", " + u.getUuid() + ") left the server");
			User.removeUser(incoming.remoteAddress().toString());
		}
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		Channel incoming = ctx.channel();

		User u = User.getUser(incoming.remoteAddress().toString());
		if (u == null && !msg.startsWith("//HANDSHAKE//"))
			return;

		if (msg.startsWith("/")) {
			System.out.println("[_CMD] " + incoming.remoteAddress() + ": " + msg);
			if (!Command.process(u, incoming,
					msg.substring(0, msg.indexOf(' ') == -1 ? msg.length() : msg.indexOf(' ')).toLowerCase(), msg)) {
				incoming.writeAndFlush("[SERVER] Command not found!\r\n");
			}
		} else {
			u.getRoom().message(u, msg);
			System.out.println("[_MSG] (" + u.getRoom().getName() + "): " + u.getNickname() + u.getUuid() + ": " + msg);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		if (cause instanceof IOException) {
			System.out.println(
					"[INFO] " + ctx.channel().remoteAddress().toString() + ": Client forcibly closed the connection");
			if (System.getProperty("debug", "false").equalsIgnoreCase("true"))
				cause.printStackTrace();
		} else {
			System.err.println("[ERROR] " + ctx.channel().remoteAddress().toString() + ": Unnown exception -> "
					+ cause.getMessage());
			if (System.getProperty("debug", "false").equalsIgnoreCase("true"))
				cause.printStackTrace();
		}
		ctx.close();
	}

}
