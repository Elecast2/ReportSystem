package net.minemora.reportsystem.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.minemora.reportsystem.Report;
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
		String sql = "CREATE TABLE IF NOT EXISTS rs_reports (id int NOT NULL AUTO_INCREMENT, "
	    		+ "reporter_uuid varchar(36), "
	    		+ "reported_uuid varchar(36), "
	    		+ "reporter_name varchar(32), "
	    		+ "reported_name varchar(32), "
	    		+ "reason varchar(100), "
	    		+ "date bigint(255), "
	    		+ "PRIMARY KEY (id));";
		update(sql);
		sql = "CREATE TABLE IF NOT EXISTS rs_bans (uuid varchar(36) NOT NULL PRIMARY KEY);";
		update(sql);
	}
	
	public void addReport(UUID reporterUuid, UUID reportedUuid, String reporterName, String reportedName, String reason) {
		String sql = "INSERT INTO rs_reports (reporter_uuid, reported_uuid, reporter_name, reported_name, reason, date) VALUES ('" 
				+ reporterUuid + "', '" + reportedUuid +"', '" + reporterName +"', '" + reportedName +"', ?, '" 
				+ System.currentTimeMillis() + "');";
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
	
	public void addBan(UUID uid) {
		String sql = "INSERT INTO rs_bans (uuid) VALUES ('" + uid + "') ON DUPLICATE KEY UPDATE uuid = '" + uid + "'";
		ReportSystem.getPlugin().getProxy().getScheduler().runAsync(ReportSystem.getPlugin(), new Runnable() {
			@Override
			public void run() {
				try(PreparedStatement stmt = connection.prepareStatement(sql)) {
					stmt.executeUpdate();
				} catch (SQLException ex) {
				    ex.printStackTrace();
				}
            }
		});
	}
	
	public void removeBan(UUID uid) {
		String sql = "DELETE FROM rs_bans WHERE uuid = '" + uid + "';";
		ReportSystem.getPlugin().getProxy().getScheduler().runAsync(ReportSystem.getPlugin(), new Runnable() {
			@Override
			public void run() {
				try(PreparedStatement stmt = connection.prepareStatement(sql)) {
					stmt.executeUpdate();
				} catch (SQLException ex) {
				    ex.printStackTrace();
				}
            }
		});
	}
	
	public UUID getUuid(String playerName) {
		String sql = "SELECT uuid FROM users WHERE username LIKE " + playerName + ";";
		UUID uid = null;
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			try (ResultSet result = stmt.executeQuery()) {
				while (result.next()) {
					uid = UUID.fromString(result.getString("uuid"));
				}
			}
		} catch (SQLException e) {
			 e.printStackTrace();
		}
		return uid;
	}
	
	public Set<UUID> getBannedUuids() {
		String sql = "SELECT * FROM rs_bans;";
		Set<UUID> bannedUuids = new HashSet<>();
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			try (ResultSet result = stmt.executeQuery()) {
				while (result.next()) {
					UUID uid = UUID.fromString(result.getString("uuid"));
				    bannedUuids.add(uid);
				}
			}
		} catch (SQLException e) {
			 e.printStackTrace();
		}
		return bannedUuids;
	}
	
	public Map<Report, Long> getReporterHistory(UUID uid, String name) {
		String sql = "SELECT reported_name,reason,date FROM rs_reports WHERE reporter_uuid = '" + uid + "';";
		Map<Report, Long> history = new HashMap<>();
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			try (ResultSet result = stmt.executeQuery()) {
				while (result.next()) {
					String reportedName = result.getString("reported_name");
					String reason = result.getString("reason");
					long time = result.getLong("date");
				    Report report = new Report(name, reportedName, reason);
				    history.put(report, time);
				}
			}
		} catch (SQLException e) {
			 e.printStackTrace();
		}
		return history;
	}
	
	public Map<Report, Long> getReportedHistory(UUID uid, String name) {
		String sql = "SELECT reporter_name,reason,date FROM rs_reports WHERE reported_uuid = '" + uid + "';";
		Map<Report, Long> history = new HashMap<>();
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			try (ResultSet result = stmt.executeQuery()) {
				while (result.next()) {
					String reporterName = result.getString("reporter_name");
					String reason = result.getString("reason");
					long time = result.getLong("date");
				    Report report = new Report(reporterName, name, reason);
				    history.put(report, time);
				}
			}
		} catch (SQLException e) {
			 e.printStackTrace();
		}
		return history;
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