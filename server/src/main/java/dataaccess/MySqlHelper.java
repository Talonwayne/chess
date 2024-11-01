package dataaccess;

import com.google.gson.Gson;

import java.sql.ResultSet;
import java.sql.SQLException;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySqlHelper {
    private String[] createStatements;
    private static final Gson GSON = new Gson();
    public MySqlHelper(String[] cs){
        createStatements = cs;
        try {
            configureDatabase();
        }catch (DataAccessException e){
            System.out.println("Error making DB");
        }
    }

    public static <T> T fromJson(String json, Class<T> classIWant) {
        return GSON.fromJson(json, classIWant);
    }

    public static String toJson(Object obj) {
        return GSON.toJson(obj);
    }

    public ResultSet executeQuery(String statement, Object... params) throws DataAccessException {
        try  {
            var conn = DatabaseManager.getConnection();
            var ps = conn.prepareStatement(statement);
            for (var i = 0; i < params.length; i++) {
                var param = params[i];
                if (param instanceof String p) {
                    ps.setString(i + 1, p);
                } else if (param instanceof Integer p) {
                    ps.setInt(i + 1, p);
                } else if (param == null) {
                    ps.setNull(i + 1, NULL);
                }
            }
            return ps.executeQuery();
        } catch (SQLException e) {
            throw new DataAccessException("unable to execute query: " + statement + ", " + e.getMessage());
        }
    }

    public int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p){
                        ps.setString(i + 1, p);
                    } else if (param instanceof Integer p) {
                        ps.setInt(i + 1, p);
                    } else if (param == null) {
                        ps.setNull(i + 1, NULL);
                    }
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException("unable to update database: %s, %s" + statement + e.getMessage());
        }
    }
    
    public void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to configure database: %s" + ex.getMessage());
        }
    }
}
