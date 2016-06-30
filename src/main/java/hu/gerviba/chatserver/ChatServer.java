package hu.gerviba.chatserver;

import hu.gerviba.chatserver.database.DatabaseManager;
import hu.gerviba.chatserver.database.TestDB;
import hu.gerviba.chatserver.rooms.Room;
import hu.gerviba.chatserver.utils.ServerInitializer;
import hu.gerviba.chatserver.utils.Util;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class ChatServer {

	private static ChatServer instance = null;

	public static ChatServer getInstance() {
		return instance;
	}

	private final int port;
	private String password;
	private int defaultRoomId;
	private String servername;
	private boolean disableGuests;
	private boolean disableRegister;
	private int firstRoom;

	public ChatServer(int port, String password, int defaultRoomId, String servername, boolean disableGuests,
			boolean disableRegister, int firstRoom) {
		this.port = port;
		this.password = password == null ? null : Util.encryptPassword(password);
		this.defaultRoomId = defaultRoomId;
		this.servername = servername;
		this.disableGuests = disableGuests;
		this.disableRegister = disableRegister;
		this.firstRoom = firstRoom;

		instance = this;
	}

	public void run() {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {
			System.out.println("[INFO] Setting up bootstrap");
			ServerBootstrap bootstrap = new ServerBootstrap().group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class).childHandler(new ServerInitializer());

			DatabaseManager.init(TestDB.class);

			System.out.println("[INFO] Listening on port: " + port);
			bootstrap.bind(port).sync().channel().closeFuture().sync();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	public void stop() {
		// TODO: Save rooms
		// TODO: Save users
		// TODO: etc.
		System.exit(0);
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getDefaultRoomId() {
		return defaultRoomId;
	}

	public Room getDefaultRoom() {
		return Room.getById(defaultRoomId);
	}

	public String getServername() {
		return servername;
	}

	public void setServername(String servername) {
		this.servername = servername;
	}

	public boolean isGuestDisabled() {
		return disableGuests;
	}

	public boolean isRegisterDisabled() {
		return disableRegister;
	}

	public Room getFirstRoom() {
		return Room.getById(firstRoom);
	}

}
