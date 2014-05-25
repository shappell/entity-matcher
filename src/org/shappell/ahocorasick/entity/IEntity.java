package org.shappell.ahocorasick.entity;

import java.util.List;

public interface IEntity
{
  public int getId();
 
  public String getName();
  
  public IEntityType getEntityType();
  
  public List<String> getSynonyms();
  
  public boolean equals(Object obj);
}
