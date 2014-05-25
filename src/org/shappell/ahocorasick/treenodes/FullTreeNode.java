package org.shappell.ahocorasick.treenodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.shappell.ahocorasick.EntityResult;



public class FullTreeNode extends TreeNodeBase
{
  private List<EntityResult> results;
  private Map<Character, ITreeNode> transitionsMap;
  
  public FullTreeNode(ITreeNode parent, char ch)
  {
    super(parent,ch);
  }
  
  public FullTreeNode(ITreeNode parent, char ch,EntityResult result, Map<Character, ITreeNode> transitionsMap)
  {
    super(parent,ch);
    this.transitionsMap = transitionsMap;
    this.results = new ArrayList<EntityResult>(TreeNodeBase.INITIAL_RESULT_LIST_CAPACITY);
    this.results.add(result);  
  }
  
  public FullTreeNode(ITreeNode parent, char ch, List<EntityResult> results, ITreeNode transition)
  {
    super(parent,ch);
    this.transitionsMap = new HashMap<Character, ITreeNode>(TreeNodeBase.INITIAL_TRANSITION_MAP_CAPACITY);
    this.transitionsMap.put(transition.getChar(), transition);
    this.results = results; 
  }
  
  @Override
  public void addResult(EntityResult result)
  {
    if(results == null)
    {
      results = new ArrayList<EntityResult>(TreeNodeBase.INITIAL_RESULT_LIST_CAPACITY);
    }
    
    // TODO: special handling for duplicate ResultPair's???
    if(!results.contains(result))
    {
      results.add(result);
    }
    
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
   return this.results;
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
    // TODO Auto-generated method stub
    return this;
  }

}
