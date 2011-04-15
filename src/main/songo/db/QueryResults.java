package songo.db;

import java.sql.ResultSet;
import java.sql.SQLException;

public class QueryResults {
  ResultSet rs;
  
  public QueryResults(ResultSet rs) { this.rs = rs; }
  
  public boolean next() throws Database.Error { 
    try {
      return rs.next();
    } catch (SQLException e) {
      throw new Database.Error(e);
    } 
  }
  
  public boolean empty() throws Database.Error { return !next(); }
  
  public Object get(String column) throws Database.Error {
    try {
      return rs.getObject(column);
    } catch (SQLException e) {
      throw new Database.Error(e);
    }
  }
  
  public long getLong(String column) throws Database.Error {
    try {
      return rs.getLong(column);
    } catch (SQLException e) {
      throw new Database.Error(e);
    }
  }

  public void close() throws Database.Error { 
    try {
      rs.close();
    } catch (SQLException e) {
      throw new Database.Error(e);
    } 
  }
}
