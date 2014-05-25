package org.shappell.ahocorasick.treenodes;

public abstract class TreeNodeBase implements ITreeNode
{
  public static final int INITIAL_TRANSITION_MAP_CAPACITY = 4;
  public static final int INITIAL_RESULT_LIST_CAPACITY = 2;
  public static int OBJECT_COUNT = 0;
  
  protected char ch;
  protected ITreeNode parent;
  protected ITreeNode failure;
  
  public TreeNodeBase(ITreeNode parent, char ch)
  {
    this.parent = parent;
    this.ch = ch;
  }
 
  @Override
  public ITreeNode getParent()
  {
    return this.parent;
  }
  
  @Override
  public void setParent(ITreeNode parent)
  {
    this.parent = parent;
  }

  @Override
  public ITreeNode getFailure()
  {
    return this.failure;
  }

  @Override
  public void setFailure(ITreeNode failure)
  {
    this.failure = failure;
  }

  @Override
  public char getChar()
  {
    return this.ch;
  }  
}
