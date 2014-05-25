package org.shappell.ahocorasick.entity;

import java.util.List;

import org.shappell.ahocorasick.entity.ads.AdEntityType;


public class Entity implements IEntity
{
  private int id;
  private String name;
  private List<String> synonyms;
  private IEntityType entityType;
  
  protected Entity()
  { }
  
  public Entity(int id, String name)
  {
    this(id,name,AdEntityType.OTHER);
  }
  
  public Entity(String name, AdEntityType entityType)
  {
    this(0,name,AdEntityType.OTHER);
  }
  
  public Entity(int id, String name, AdEntityType entityType)
  {
    this.id = id;
    this.name = name;
    this.entityType = entityType;
  }
  
  @Override
  public String toString()
  {
    return "Entity[id = " + id + ", name = " + name + ", entitytype = " + entityType +  "]";
  }

  @Override
  public int hashCode()
  {
    int result = 17;
    result = 37 * result + id;
    //result = 37 * result + ((name == null) ? 0 : name.hashCode());
    result = 37 * result + entityType.hashCode();
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
      return true;
    if (!(obj instanceof Entity))
      return false;

    Entity entity = (Entity) obj;

    if (this.id != entity.id)
      return false;

    return this.entityType == entity.entityType;
  }
  
  public int getId()
  {
    return id;
  }
  public void setId(int id)
  {
    this.id = id;
  }
  public String getName()
  {
    return name;
  }
  public void setName(String name)
  {
    this.name = name;
  }
  public void setEntityType(IEntityType entityType)
  {
    this.entityType = entityType;
  }
  @Override
  public IEntityType getEntityType()
  {
    return entityType;
  }

  public void setSynonyms(List<String> synonyms)
  {
    this.synonyms = synonyms;
  }

  public List<String> getSynonyms()
  {
    return synonyms;
  }
}
