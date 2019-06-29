package net.minemora.reportsystem.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

import net.minemora.reportsystem.ReportSystem;
import net.minemora.reportsystem.config.ConfigMain;

public class Database {
	
	private static Database database;
	
	private String url;
	private String username;
	private String password;
	
	private Connection connection;
	
	private Database() {}
	
	public void setup() {
		this.url = ConfigMain.get().getString("database.url");
		this.username = ConfigMain.get().getString("database.username");
		this.password = ConfigMain.get().getString("database.password");
		createConnection();
		createTables();
	}
	
	private void createConnection() {
		try {
		    Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
		    e.printStackTrace();
		    return;
		}
		try {
		    this.connection = DriverManager.getConnection(url,username,password);
		} catch (SQLException e) {
		    e.printStackTrace();
		}
	}
	
	private void createTables() {
		String sql = "CREATE TABLE IF NOT EXISTS reportsystem (id int NOT NULL AUTO_INCREMENT, "
	    		+ "reporter-uuid varchar(36), "
	    		+ "reported-uuid varchar(36), "
	    		+ "reason varchar(100), "
	    		+ "date bigint(255), "
	    		+ "PRIMARY KEY (id))";
		update(sql);
	}
	
	public void addReport(UUID reporterUuid, UUID reportedUuid, String reason) {
		String sql = "INSERT INTO reportsystem (reporter-uuid, reported-uuid, reason, date) VALUES ('" 
				+ reporterUuid + "', '" + reportedUuid +"', ?, '" + System.currentTimeMillis() + "');";
		ReportSystem.getPlugin().getProxy().getScheduler().runAsync(ReportSystem.getPlugin(), new Runnable() {
			@Override
			public void run() {
				try(PreparedStatement stmt = connection.prepareStatement(sql)) {
					stmt.setString(1, reason);
					stmt.executeUpdate();
				} catch (SQLException ex) {
				    ex.printStackTrace();
				}
            }
		});
	}
	
	public void update(String sql) {
		try(PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.executeUpdate();
		} catch (SQLException ex) {
		    ex.printStackTrace();
		}
	}
	
	public void close() {
		try {
	        if (connection!=null && !connection.isClosed()) {
	            connection.close();
	        }
	    } catch(Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public static Database getDatabase() {
		if (database == null) {
			database = new Database();
        }
        return database;
	}

	public String getUrl() {
		return url;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
}