package hu.gerviba.chatserver.database;

public class DatabaseManager {

	private static DatabaseConnector db = null;
	
	public static void init(Class<? extends DatabaseConnector> database) throws InstantiationException, IllegalAccessException {
		if(db != null) {
			System.err.println("[DATA] Database is already initialized");
			return;
		}
		db = database.newInstance();
		db.init();
	}
	
	public static DatabaseConnector getDatabase() {
		return db;
	}
	
}
