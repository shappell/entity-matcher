package org.shappell.ahocorasick.treenodes;

import java.util.Collection;
import java.util.List;

import org.shappell.ahocorasick.EntityResult;

public interface ITreeNode
{
  public void addResult(EntityResult result);

  // / <summary>
  // / Adds trabsition node
  // / </summary>
  // / <param name="node">Node</param>
  public void addTransition(ITreeNode node);

  // / <summary>
  // / Returns transition to specified character (if exists)
  // / </summary>
  // / <param name="c">Character</param>
  // / <returns>Returns TreeNode or null</returns>
  public ITreeNode getTransition(Character ch);

  // / <summary>
  // / Returns true if node contains transition to specified character
  // / </summary>
  // / <param name="c">Character</param>
  // / <returns>True if transition exists</returns>
  public boolean containsTransition(Character ch);

  /**
   * @return the _parent
   */
  public ITreeNode getParent();

  /**
   * @return the _failure
   */
  public ITreeNode getFailure();

  /**
   * @param _failure
   *          the _failure to set
   */
  public void setFailure(ITreeNode failure);

  /**
   * @return the _transitions
   */
  public Collection<ITreeNode> getTransitions();

  public List<EntityResult> getResults();

  public char getChar();
  
  public ITreeNode getTreeNodeForAddingTransition();
  
  public ITreeNode getTreeNodeForAddingResult();
  
  public void setParent(ITreeNode parent);
}
