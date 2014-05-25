package org.shappell.ahocorasick.treenodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.shappell.ahocorasick.EntityResult;

public class ResultMultipleTransition1TreeNode extends TreeNodeBase
{
  private List<EntityResult> results;
  private ITreeNode transition;
  
  public ResultMultipleTransition1TreeNode(ITreeNode parent, char ch,EntityResult result, ITreeNode transition)
  {
    super(parent,ch);
    this.results = new ArrayList<EntityResult>(TreeNodeBase.INITIAL_RESULT_LIST_CAPACITY);
    this.results.add(result);  
    this.transition = transition;
  }
  
  @Override
  public void addResult(EntityResult result)
  {
    if(results == null)
    {
      results = new ArrayList<EntityResult>(TreeNodeBase.INITIAL_RESULT_LIST_CAPACITY);
    }
    else if (results.contains(result))
    {
      return;
    }
    results.add(result);
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
    return this.results;
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
      return new FullTreeNode(parent,ch,results,transition);
    }
  }

  @Override
  public ITreeNode getTreeNodeForAddingResult()
  {
    return this;
  }

}
