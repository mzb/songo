package songo.db;

import java.util.ArrayList;
import java.util.List;

public abstract class ModelManager<M extends Model> {

  protected Database db;
  
  public ModelManager(Database db) {
    this.db = db;
  }
  
  public abstract String getTableName();
  
  public abstract M build(QueryResults row) throws Database.Error;
  
  public abstract M build(String attr);
  
  public abstract void create(M model) throws Database.Error;
  
  public abstract void update(M model) throws Database.Error;
  
  public List<M> find(String conditions, String order, String limit) throws Database.Error {
    String query = "select * from " + getTableName();
    if (conditions != null && !conditions.isEmpty()) {
      query += " where (" + conditions + ")";
    }
    if (order != null && !order.isEmpty()) {
      query += " order by " + order;
    }
    if (limit != null && !limit.isEmpty()) {
      query += " limit " + limit;
    }
    
    QueryResults rows = db.query(query);
    
    List<M> models = new ArrayList<M>();
    while (rows.next()) {
      models.add(build(rows));
    }
    rows.close();
    
    return models;
  }
  
  public long count(String conditions) throws Database.Error {
    String query = "select count(*) as models_count from " + getTableName();
    if (conditions != null && !conditions.isEmpty()) {
      query += " where (" + conditions + ")";
    }
    
    QueryResults rows = db.query(query);
    
    if (!rows.empty()) {
      return rows.getLong("models_count");
    }
    return 0;
  }
  
  public M findOrCreateBy(String column, String value) throws Database.Error {
    List<M> found = find(String.format("%s = '%s'", column, value), null, "1");
    M model;
    if (found.isEmpty()) {
      model = build(value);
      create(model);
    } else {
      model = found.get(0);
    }
    return model;
  }
  
  public void save(M  model) throws Database.Error {
    if (model.id > 0) {
      update(model);
    } else {
      create(model);
    }
  }
}
