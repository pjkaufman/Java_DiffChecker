package dbdiffchecker.nosql;

import dbdiffchecker.sql.Index;
import java.util.HashMap;
import java.util.ArrayList;
public Collection {

  private String name;
  private HashMap<String, Index> indices;
  private boolean isCapped;

  public Collection(String name, HashMap<String, Index> indices, boolean isCapped) {
    this.name = name;
    this.indices = indices;
    this.isCapped = isCapped;
  }

  private boolean isCapped() {
    return isCapped;
  }

  public ArrayList<String> compare() {
    
  }
}