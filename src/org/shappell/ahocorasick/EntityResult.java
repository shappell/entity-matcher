package org.shappell.ahocorasick;

import org.shappell.ahocorasick.entity.IEntity;


public class EntityResult
{
  private int depth;
  private IEntity entity;
  
  public EntityResult()
  { }
  
  public EntityResult(int depth, IEntity entity)
  { 
    this.depth = depth;
    this.entity = entity;
  }
  
  
  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj)
  {
    return this.entity.equals(obj);
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    return this.entity.hashCode();
  }

  /**
   * @return the depth
   */
  public int getDepth()
  {
    return depth;
  }
  /**
   * @param depth the depth to set
   */
  public void setDepth(int depth)
  {
    this.depth = depth;
  }
  /**
   * @return the entity
   */
  public IEntity getEntity()
  {
    return entity;
  }
  /**
   * @param entity the entity to set
   */
  public void setEntity(IEntity entity)
  {
    this.entity = entity;
  }
  
}
