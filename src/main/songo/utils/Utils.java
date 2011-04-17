package songo.utils;

import java.util.List;

public class Utils {

  public static String join(List<? extends Object> list, String separator, String prefix) {
    StringBuilder s = new StringBuilder();
    for (int i = 0; i < list.size(); i++) {
      s.append(prefix).append(list.get(i));
      if (i < list.size()-1) {
        s.append(separator);
      }
    }
    return s.toString();
  }
  
  public static String join(List<? extends Object> list, String separator) {
    return join(list, separator, "");
  }
  
  public static String join(List<? extends Object> list) {
    return join(list, ", ");
  }
}
