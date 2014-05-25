package org.shappell.ahocorasick.treenodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.shappell.ahocorasick.EntityResult;

/**
 * node with single result/match and a single transition past this current character
 * @author shappell
 *
 */
public class Result1Transition1TreeNode extends TreeNodeBase
{
  private EntityResult result;
  private ITreeNode transition;
  
  public Result1Transition1TreeNode(ITreeNode parent, char ch)
  {
    super(parent,ch);
  }

  @Override
  public void addResult(EntityResult result)
  {
    this.result = result;
  }

  @Override
  public void addTransition(ITreeNode node)
  {
    this.transition = node;
  }

  @Override
  public ITreeNode getTransition(Character ch)
  {    
    if(this.transition != null && this.transition.getChar() == ch)
    {
      return this.transition;
    }
    else
    {
      return null;
    }
  }

  @Override
  public boolean containsTransition(Character ch)
  {
    if(this.transition == null)
    {
      return false;
    }
    else
    {
      return ch.equals(this.transition.getChar());
    }
  }

  @Override
  public Collection<ITreeNode> getTransitions()
  {
    if(this.transition == null)
    {
      return null;
    }
    else
    {
      List<ITreeNode> transitions = new ArrayList<ITreeNode>(2);
      transitions.add(this.transition);
      return transitions;
    }
  }

  @Override
  public List<EntityResult> getResults()
  {
    if(this.result == null)
    {
      return null;
    }
    else
    {
      List<EntityResult> results = new ArrayList<EntityResult>(2);
      results.add(this.result);
      return results;
    }
  }

  @Override
  public ITreeNode getTreeNodeForAddingTransition()
  {
    if(this.transition == null)
    {
      return this;
    }
    else
    {
      return new Result1TransitionMultipleTreeNode(parent,ch, result, transition);
    }
  }

  @Override
  public ITreeNode getTreeNodeForAddingResult()
  {
    if(this.result == null)
    {
      return this;
    }
    else
    {
      return new ResultMultipleTransition1TreeNode(parent,ch, result, transition);
    }
  }
}
