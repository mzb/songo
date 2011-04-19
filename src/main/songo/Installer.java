package songo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import songo.db.Database;

public class Installer {
  
  List<String> readSchemaFile(String filename) {
    List<String> stmts = new ArrayList<String>();
    try {
      BufferedReader in = new BufferedReader(new FileReader(new File(filename)));
      try {
        String s;
        while ((s = in.readLine()) != null) {
          stmts.add(s);
        }
      } finally {
        in.close();
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return stmts;
  }
  
  void writeConfigFile(String filename, String config) {
    try {
      PrintWriter out = new PrintWriter(new File(filename).getAbsoluteFile());
      try {
        out.print(config);
      } finally {
        out.close();
      } 
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  
  void run(String[] args) {
    String host = "localhost:3306";
    String user = "root";
    String password = "";
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    String input = "";
    
    System.out.println("Instalacja aplikacji WBD - Projekt (songo):");
    System.out.println("[ENTER powoduje wybranie domyslnej wartosci]");
    System.out.println("");
    try {
      try {
        System.out.print(String.format("Serwer MySQL [%s]> ", host));
        input = in.readLine();
        host = input.isEmpty() ? host : input;
        System.out.print(String.format("Uzytkownik MySQL [%s]> ", user));
        input = in.readLine();
        user = input.isEmpty() ? user : input;
        System.out.print(String.format("Haslo dla %s [%s]> ", user, password));
        input = in.readLine();
        password = input.isEmpty() ? password : input;
        
        System.out.println();
        System.out.println("Zapisywanie konfiguracji...");
        String config = 
          "db.adapter: jdbc:mysql\n" + 
          "db.host: " + host + "\n" +
          "db.name: songo\n" + 
          "db.username: " + user + "\n" +
          "db.password: " + password + "\n";   
        writeConfigFile("config/test.properties", config);
        System.out.println("OK.");
        
        System.out.println();
        System.out.println("Tworzenie bazy...");
        List<String> schemaStmts = readSchemaFile("db/schema.sql");
        Database db = new Database("config/test.properties");
        db.connect();
        for (String stmt : schemaStmts) {
          db.update(stmt);
        }
        db.disconnect();
        System.out.println("OK");
        
      } finally {
        in.close();
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static void main(String[] args) {
    new Installer().run(args);
  }
}
