package hu.gerviba.chatserver.rooms;

import hu.gerviba.chatserver.datastores.User;

public interface Task {

	public void run(User u);

}
