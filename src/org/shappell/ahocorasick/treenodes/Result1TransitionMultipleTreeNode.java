package org.shappell.ahocorasick.treenodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.shappell.ahocorasick.EntityResult;


public class Result1TransitionMultipleTreeNode extends TreeNodeBase
{
  private EntityResult result;
  private Map<Character, ITreeNode> transitionsMap;
  
  public Result1TransitionMultipleTreeNode(ITreeNode parent, char ch, EntityResult result, ITreeNode transition)
  {
    super(parent,ch);
    this.result = result;
    this.transitionsMap = new HashMap<Character, ITreeNode>(TreeNodeBase.INITIAL_TRANSITION_MAP_CAPACITY);
    this.transitionsMap.put(transition.getChar(), transition);
  }
  
  @Override
  public void addResult(EntityResult result)
  {
    this.result = result;
  }

  @Override
  public void addTransition(ITreeNode node)
  {
    if(transitionsMap == null)
    {
      transitionsMap = new HashMap<Character, ITreeNode>(TreeNodeBase.INITIAL_TRANSITION_MAP_CAPACITY);
    }
    transitionsMap.put(node.getChar(), node);
  }

  @Override
  public ITreeNode getTransition(Character ch)
  {
    if(transitionsMap == null)
    {
      return null;
    }
    else
    {
      return transitionsMap.get(ch);
    }
  }

 @Override
  public boolean containsTransition(Character ch)
  {
    if(transitionsMap == null)
    {
      return false;
    }
    else
    {
      return transitionsMap.containsKey(ch);
    }
  }

 @Override
 public Collection<ITreeNode> getTransitions()
 {
   if(transitionsMap == null)
   {
     return null;
   }
   else
   {
     return transitionsMap.values();
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
    // TODO Auto-generated method stub
    return this;
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
      return new FullTreeNode(this.parent,this.ch,this.result,this.transitionsMap);
    }
  }

}
