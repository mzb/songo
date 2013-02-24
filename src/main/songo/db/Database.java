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
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import songo.utils.Utils;

/**
 * Klasa odpowiedzialna za połączenie z bazą danych, oraz wykonywanie zapytań - 
 * prosty wrapper na interfejsy JDBC.
 */
public class Database {
  Properties config;
  Connection connection;

  /**
   * Konstruktor.
   * @param configFileName scieżka do pliku *.properties zawierającego konfigurację połączenia.
   * @throws Database.Error
   */
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
  
  /**
   * Otwiera połączenie z bazą danych.
   * @throws Database.Error
   */
  public void connect() throws Database.Error {
    try {
      Class.forName("com.mysql.jdbc.Driver").newInstance();
      final String url = String.format("%s://%s/%s", getAdapter(), getHost(), getName());
      connection = DriverManager.getConnection(url, getUsername(), getPassword());
    } catch (Exception e) {
      throw new Database.Error(e);
    }
  }
  
  /**
   * Zamyka połączenie z bazą danych.
   * @throws Database.Error
   */
  public void disconnect() throws Database.Error {
    try {
      if (connection != null) {
        connection.close();
      }
    } catch (SQLException e) {
      throw new Database.Error(e);
    }
  }
  
  /**
   * Wykonuje zapytanie do bazy.
   * @param sql zapytanie.
   * @param bindings parametry zapytania
   * @return wynik zapytania
   * @see {@link QueryResults}
   * @throws Database.Error
   */
  public QueryResults query(String sql, Object... bindings) throws Database.Error {
    PreparedStatement stmt = preparedStatement(sql, bindings);
    try {
      return new QueryResults(stmt.executeQuery());
    } catch (SQLException e) {
      throw new Database.Error(e);
    }
  }
  
  /**
   * Wykonuje polecenie modyfikujące bazy (UPDATE, DELETE).
   * @param sql polecenie
   * @param bindings paramtery zapytania
   * @throws Database.Error
   */
  public void update(String sql, Object... bindings) throws Database.Error {
    PreparedStatement stmt = preparedStatement(sql, bindings);
    try {
      stmt.executeUpdate();
    } catch (SQLException e) {
      throw new Database.Error(e);
    }
  }
  
  /**
   * Wykonuje polecenie INSERT.
   * @param sql polecenie
   * @param bindings parametry
   * @return ID wstawionego rekordu
   * @throws Database.Error
   */
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
  
  /**
   * Tworzy polecenie do bazy danych.
   * @param sql polecenie
   * @param options opcje
   * @param bindings parametry
   * @return utworzone polecenie
   * @throws Database.Error
   */
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
  
  /**
   * {@inheritDoc}
   */
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
  
  /**
   * @return obiekt reprezentujący połączenie od bazy
   */
  public Connection getConnection() {
    return connection;
  }

  /**
   * @return nazwa bazy
   */
  public String getName() {
    return config.getProperty("db.name");
  }

  /**
   * @return nazwa użytkownika
   */
  public String getUsername() {
    return config.getProperty("db.username");
  }

  /**
   * @return hasło
   */
  public String getPassword() {
    return config.getProperty("db.password");
  }
  
  /**
   * @return typ bazy (MySQL itp)
   */
  public String getAdapter() {
    return config.getProperty("db.adapter");
  }
  
  /**
   * @return adres serwera
   */
  public String getHost() {
    return config.getProperty("db.host");
  }
  
  /**
   * Reprezentuje błąd bazy
   */
  public static class Error extends Exception {
    public Error(Throwable e) {
      super(e);
    }
  }
  
  /**
   * Łączy podane warunki podanym operatorem logicznym.
   * @param operator operator SQL
   * @param conditions warunki
   * @return połączone warunki
   * @see Utils#join(List, String)
   */
  public static String sqlize(String operator, List<?> conditions) {
    return Utils.join(conditions, " " + operator + " ");
  }
  
  /**
   * Łączy podane ID w listę, której można użyć w zapytaniach SQL.
   * @param ids lista ID
   * @return lista ID rozdzielonych przecinkami
   */
  public static String sqlize(List<Long> ids) {
    return sqlize(",", ids);
  }
}
