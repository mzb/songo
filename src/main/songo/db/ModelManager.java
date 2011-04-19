package songo.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Podstawowa klasa do zarządzająnia modelami. 
 * Służy jako pomost między aplikacją a bazą danych.
 * @param <M> Typ (klasa) modelu
 */
public abstract class ModelManager<M extends Model> {

  protected Database db;
  
  /**
   * Konstruktor.
   * @param db baza danych
   */
  public ModelManager(Database db) {
    this.db = db;
  }
  
  /**
   * @return nazwa głowej tabeli, w której składowane są zarządzane modele.
   */
  public abstract String getTableName();
  
  /**
   * Buduje instancję modelu na podstawie wyników zapytania do bazy.
   * @param row wynik zapytania
   * @return instanja modelu
   * @throws Database.Error
   */
  public abstract M build(QueryResults row) throws Database.Error;
  
  /**
   * Buduje instancję modelu na podstawie podanego atrybutu.
   * @param attr atrybut niezbędny do otworzenia instancji modelu (arg. konstruktora).
   * @return instancja modelu
   */
  public abstract M build(String attr);
  
  /**
   * Dodaje do bazy wiersz reprezentujący podany model.
   * @param model model do zapisania
   * @throws Database.Error
   */
  public abstract void create(M model) throws Database.Error;
  
  /**
   * Uaktualnia w bazie zapisany model.
   * @param model model do aktualizacji
   * @throws Database.Error
   */
  public abstract void update(M model) throws Database.Error;
  
  /**
   * Wyszukuje w bazie listę modeli spełniających podane warunki.
   * @param conditions fragm SQL (klauzula WHERE)
   * @param order sortowanie wyników (klauzula ORDER)
   * @param limit ilość wyników (klauzula LIMIT)
   * @return lista modeli spełniających podane warunki
   * @throws Database.Error
   */
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
  
  /**
   * Wyszukuje pierwszy model spełniający podane warunki.
   * @param conditions klauzula WHERE
   * @param order klauzula ORDER
   * @return instancja modelu o podanym id lub null jesli nie znaleziono.
   * @throws Database.Error
   */
  public M findFirst(String conditions, String order) throws Database.Error {
    List<M> results = find(conditions, order, "1");
    return results.get(0);
  }
  
  /**
   * Wyszukuje w bazie model o podanym ID.
   * @param id ID modelu
   * @return instancja modelu o podanym ID lub null
   * @throws Database.Error
   */
  public M findById(Long id) throws Database.Error {
    return findFirst(String.format(getTableName() + ".id = %d", id), null);
  }
  
  /**
   * Zwraca ilosc obiektow w bazie spelniajacych podane warunki.
   * @param conditions klauzula WHERE
   * @return ilość obiektów
   * @throws Database.Error
   */
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
  
  /**
   * Zwraca obiekt w bazie posiadający kolumnę z podaną wartością lub tworzy taki obiekt, 
   * jeśli go nie znaleziono.
   * @param column nazwa kolumny
   * @param value wartość kolumny
   * @return znaleziona lub utworzona instancja modelu
   * @throws Database.Error
   */
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
  
  /**
   * Zapisuje podany model do bazy - albo przez dodanie albo aktualizację - 
   * o ile powiodła się walidacja danych modelu.
   * @param model model do zapisania
   * @throws Database.Error
   * @throws ValidationErrors
   */
  public void save(M  model) throws Database.Error, ValidationErrors {
    validate(model);
    if (model.id != null) {
      update(model);
    } else {
      create(model);
    }
  }

  /**
   * Usuwa z bazy modele spełniające podane warunki.
   * @param conditions klauzula WHERE
   * @throws Database.Error
   */
  public void delete(String conditions) throws Database.Error {
    db.update(String.format("delete from %s where (%s)", getTableName(), conditions));
  }
  
  /**
   * Usuwa podany model z bazy.
   * @param model model do usunięcia
   * @throws Database.Error
   */
  public void delete(M model) throws Database.Error {
    delete(String.format("id = %d", model.id));
  }
  
  /**
   * Waliduje podany model. 
   * Klasy dziedziczące powinny przeciążyć tą metodę 
   * w celu wykonania walidacji przed zapisem do bazy.
   * @param model model do zwalidowania.
   * @throws ValidationErrors
   */
  protected void validate(M model) throws ValidationErrors {
    
  }
  
  /**
   * Klasa reprezentująca błędy walidacji danego modelu.
   */
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
