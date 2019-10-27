package dbdiffchecker.nosql;

import dbdiffchecker.sql.Index;
import java.util.HashMap;
import java.util.ArrayList;
public class Collection {

  private String name;
  private HashMap<String, Index> indices;
  private boolean isCapped;
  private int size;

  public Collection(String name, HashMap<String, Index> indices, boolean isCapped, int size) {
    this.name = name;
    this.indices = indices;
    this.isCapped = isCapped;
    this.size = size;
  }

  protected boolean isCapped() {
    return isCapped;
  }

  protected String getName() {
    return name;
  }

  protected HashMap<String, Index> getIndices() {
    return indices;
  }

  protected int getSize() {
    return size;
  }

  public boolean equals(Collection coll2) {
    return name == coll2.getName() && isCapped == coll2.isCapped() 
            && indices == coll2.getIndices() && size == coll2.getSize();
  }
}