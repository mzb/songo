package songo.db;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class Database {
  Properties config;
  Connection connection;

  public Database(String configFileName) throws Database.Error {
    try {
      FileInputStream configFile = null;
      try {
        configFile = new FileInputStream(configFileName);
        config = new Properties();
        config.load(configFile);
      } catch (FileNotFoundException e) {
        throw new Database.Error(e);
      } catch (IOException e) {
        throw new Database.Error(e);
      } finally {
        configFile.close();
      }
    } catch (IOException e) {
      throw new Database.Error(e);
    }
  }
  
  public void connect() throws Database.Error {
    try {
      Class.forName("com.mysql.jdbc.Driver").newInstance();
      final String url = String.format("%s://%s/%s", getAdapter(), getHost(), getName());
      connection = DriverManager.getConnection(url, getUsername(), getPassword());
    } catch (Exception e) {
      throw new Database.Error(e);
    }
  }
  
  public void disconnect() throws Database.Error {
    try {
      if (connection != null) {
        connection.close();
      }
    } catch (SQLException e) {
      throw new Database.Error(e);
    }
  }
  
  public QueryResults query(String sql, Object... bindings) throws Database.Error {
    PreparedStatement stmt = preparedStatement(sql, bindings);
    try {
      return new QueryResults(stmt.executeQuery());
    } catch (SQLException e) {
      throw new Database.Error(e);
    }
  }
  
  public void update(String sql, Object... bindings) throws Database.Error {
    PreparedStatement stmt = preparedStatement(sql, bindings);
    try {
      stmt.executeUpdate();
    } catch (SQLException e) {
      throw new Database.Error(e);
    }
  }
  
  public long insert(String sql, Object... bindings) throws Database.Error {
    PreparedStatement stmt = preparedStatement(sql, Statement.RETURN_GENERATED_KEYS, bindings);
    try {
      stmt.executeUpdate();
      ResultSet rs = stmt.getGeneratedKeys();
      long lastRowId = rs.next() ? rs.getLong(1) : 0;
      rs.close();
      return lastRowId;
    } catch (SQLException e) {
      throw new Database.Error(e);
    }
  }
  
  public PreparedStatement preparedStatement(String sql, int options, Object... bindings) 
      throws Database.Error {
    try {
      PreparedStatement stmt = connection.prepareStatement(sql, options);
      bind(stmt, bindings);
      return stmt;
    } catch (SQLException e) {
      throw new Database.Error(e);
    }
  }
  
  public PreparedStatement preparedStatement(String sql, Object... bindings)
      throws Database.Error {
    return preparedStatement(sql, 0, bindings);
  }
  
  private void bind(PreparedStatement stmt, Object... bindings) throws SQLException {
    int i = 1;
    for (Object b : bindings) {
      stmt.setObject(i, b);
      i += 1;
    }
  }
  
  public Connection getConnection() {
    return connection;
  }

  public String getName() {
    return config.getProperty("db.name");
  }

  public String getUsername() {
    return config.getProperty("db.username");
  }

  public String getPassword() {
    return config.getProperty("db.password");
  }
  
  public String getAdapter() {
    return config.getProperty("db.adapter");
  }
  
  public String getHost() {
    return config.getProperty("db.host");
  }
   
  public static class Error extends Exception {
    public Error(Throwable e) {
      super(e);
    }
  }
}
