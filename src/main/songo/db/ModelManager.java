package songo.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
  
  public M findFirst(String conditions, String order) throws Database.Error {
    List<M> results = find(conditions, order, "1");
    return results.get(0);
  }
  
  public M findById(Long id) throws Database.Error {
    return findFirst(String.format(getTableName() + ".id = %d", id), null);
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
  
  public void save(M  model) throws Database.Error, ValidationErrors {
    validate(model);
    if (model.id != null) {
      update(model);
    } else {
      create(model);
    }
  }
  
  public void delete(String conditions) throws Database.Error {
    db.update(String.format("delete from %s where (%s)", getTableName(), conditions));
  }
  
  public void delete(M model) throws Database.Error {
    delete(String.format("id = %d", model.id));
  }
  
  protected void validate(M model) throws ValidationErrors {
    
  }
  
  public static class ValidationErrors extends Throwable {
    protected List<String> errors;
    
    public ValidationErrors(List<String> errors) {
      this.errors = errors;
    }
    
    public List<String> getErrors() {
      return errors;
    }
  }

}
