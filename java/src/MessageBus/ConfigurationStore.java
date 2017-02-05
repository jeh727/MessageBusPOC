package MessageBus;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ConfigurationStore {

	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/MessageBusConfig";
	static final String USER_NAME = "MessageBus";
	static final String PASSWORD = "MessageBus";

	public static void close(Connection conn, PreparedStatement stmt, ResultSet rs) throws Exception {
		Exception e = null;

		try {
			if (rs != null)
				rs.close();
		} catch (SQLException se) {
			e = new Exception(se.getMessage());
			se.printStackTrace();
		}

		try {
			if (stmt != null)
				stmt.close();
		} catch (SQLException se) {
			e = new Exception(se.getMessage());
			se.printStackTrace();
		}

		try {
			if (conn != null)
				conn.close();
		} catch (SQLException se) {
			e = new Exception(se.getMessage());
			se.printStackTrace();
		}

		if (e != null)
			throw e;
	}

	public static int createPattern() throws Exception {

		int patternId = -1;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			Class.forName("com.mysql.jdbc.Driver");

			conn = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);

			stmt = conn.prepareStatement("INSERT INTO Patterns() VALUES()", Statement.RETURN_GENERATED_KEYS);
			stmt.execute();
			rs = stmt.getGeneratedKeys();

			if (rs.next()) {
				patternId = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(conn, stmt, rs);
		}

		return patternId;
	}

	static void deletePattern(int id) throws Exception {

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			Class.forName("com.mysql.jdbc.Driver");

			conn = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);

			stmt = conn
					.prepareStatement("DELETE FROM Patterns WHERE id = ?");
			stmt.setInt(1, id);

			stmt.execute();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(conn, stmt, rs);
		}
	}
	
	public static List<Integer> getPatternIds() throws Exception {

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<Integer> patternIds = new ArrayList<Integer>();

		try {
			Class.forName("com.mysql.jdbc.Driver");

			conn = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);

			stmt = conn.prepareStatement("SELECT id FROM Patterns ORDER BY id");
			rs = stmt.executeQuery();

			while (rs.next()) {
				patternIds.add(rs.getInt("id"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(conn, stmt, rs);
		}

		return patternIds;
	}
	

	public static int createNode(int patternId, String nodeType, String inputs, String config, String outputs)
			throws Exception {

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		int id = -1;

		try {
			Class.forName("com.mysql.jdbc.Driver");

			conn = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);

			stmt = conn
					.prepareStatement("INSERT INTO NodeConfigs(patternId, nodeType, enabled, inputs, config, outputs)"
							+ " VALUES(?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			stmt.setInt(1, patternId);
			stmt.setString(2, nodeType);
			stmt.setBoolean(3, false);
			stmt.setString(4, inputs);
			stmt.setString(5, config);
			stmt.setString(6, outputs);

			stmt.execute();
			rs = stmt.getGeneratedKeys();

			if (rs.next()) {
				id = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(conn, stmt, rs);
		}

		return id;
	}

	public static int createNode(int patternId, String nodeType, JSONObject inputs, JSONObject config, JSONObject outputs)
			throws Exception {
		return createNode(patternId, nodeType, inputs.toString(), config.toString(), outputs.toString());
	}

	static void deleteNode(int id) throws Exception {

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			Class.forName("com.mysql.jdbc.Driver");

			conn = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);

			stmt = conn
					.prepareStatement("DELETE FROM NodeConfigs WHERE id = ?");
			stmt.setInt(1, id);

			stmt.execute();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(conn, stmt, rs);
		}
	}

	
	public static void setNodeEnabled(int id, boolean enabled) throws Exception {

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			Class.forName("com.mysql.jdbc.Driver");

			conn = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);

			stmt = conn
					.prepareStatement("UPDATE NodeConfigs SET enabled = ? WHERE id = ?");
			stmt.setBoolean(1, enabled);
			stmt.setInt(2, id);

			stmt.execute();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(conn, stmt, rs);
		}
	}
	
	public static void setPatternNodesEnabled(int patternId, boolean enabled) throws Exception {

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			Class.forName("com.mysql.jdbc.Driver");

			conn = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);

			stmt = conn
					.prepareStatement("UPDATE NodeConfigs SET enabled = ? WHERE patternId = ?");
			stmt.setBoolean(1, enabled);
			stmt.setInt(2, patternId);

			stmt.execute();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(conn, stmt, rs);
		}
	}
	
	public static class NodeResult {
		public int id;
		public int patternId;
		public String nodeType;
		public boolean enabled;
		public JSONObject inputs;
		public JSONObject config;
		public JSONObject outputs;

		public NodeResult(int id, int patternId, String nodeType, boolean enabled, JSONObject inputs, JSONObject config,
				JSONObject outputs) {
			super();
			this.id = id;
			this.patternId = patternId;
			this.nodeType = nodeType;
			this.enabled = enabled;
			this.inputs = inputs;
			this.config = config;
			this.outputs = outputs;
		}
	}

	public static NodeResult getNode(int id) throws Exception {

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		NodeResult result = null;

		try {
			Class.forName("com.mysql.jdbc.Driver");

			conn = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);

			stmt = conn.prepareStatement(
					"SELECT patternId, nodeType, enabled, inputs, config, outputs FROM NodeConfigs WHERE id = ?");
			stmt.setInt(1, id);
			
			rs = stmt.executeQuery();

			if (rs.next()) {
				JSONParser parser = new JSONParser();
				result = new NodeResult(id, rs.getInt("patternId"), rs.getString("nodeType"),
						rs.getBoolean("enabled"), (JSONObject)parser.parse(rs.getString("inputs")),
						(JSONObject)parser.parse(rs.getString("config")),
						(JSONObject)parser.parse(rs.getString("outputs")));
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(conn, stmt, rs);
		}

		return result;
	}
	
	public static List<NodeResult> getNodeTypeConfigs(String nodeType) throws Exception {

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		NodeResult result = null;
		List<NodeResult> results = new ArrayList<NodeResult>();
		
		try {
			Class.forName("com.mysql.jdbc.Driver");

			conn = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);

			stmt = conn.prepareStatement(
					"SELECT id, patternId, nodeType, enabled, inputs, config, outputs FROM NodeConfigs" +
					" WHERE nodeType = ? ORDER BY id");
			stmt.setString(1, nodeType);
			
			rs = stmt.executeQuery();

			while(rs.next()) {
				JSONParser parser = new JSONParser();
				results.add(new NodeResult(rs.getInt("id"), rs.getInt("patternId"), rs.getString("nodeType"),
						rs.getBoolean("enabled"), (JSONObject)parser.parse(rs.getString("inputs")),
						(JSONObject)parser.parse(rs.getString("config")),
						(JSONObject)parser.parse(rs.getString("outputs"))));
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(conn, stmt, rs);
		}

		return results;
	}
	
	public static List<NodeResult> getPatternNodeConfigs(int patternId) throws Exception {

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		NodeResult result = null;
		List<NodeResult> results = new ArrayList<NodeResult>();
		
		try {
			Class.forName("com.mysql.jdbc.Driver");

			conn = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);

			stmt = conn.prepareStatement(
					"SELECT id, patternId, nodeType, enabled, inputs, config, outputs FROM NodeConfigs" +
					" WHERE patternId = ? ORDER BY id");
			stmt.setInt(1, patternId);
			
			rs = stmt.executeQuery();

			while(rs.next()) {
				JSONParser parser = new JSONParser();
				results.add(new NodeResult(rs.getInt("id"), rs.getInt("patternId"), rs.getString("nodeType"),
						rs.getBoolean("enabled"), (JSONObject)parser.parse(rs.getString("inputs")),
						(JSONObject)parser.parse(rs.getString("config")),
						(JSONObject)parser.parse(rs.getString("outputs"))));
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(conn, stmt, rs);
		}

		return results;
	}
}
