/************************************************************************************
 *  $Name:$
 * 
 *  $Id$                                                            
 *  Copyright (C) 2000-2010 MusicNet, Inc. 
 * 
 *  All rights reserved. 
 *  This source code is property of MusicNet, Inc. 
 *  No modifications or additions to the source can be made without written consent from MusicNet, Inc.
 * 
 *************************************************************************************/

package org.shappell.ahocorasick;

import java.util.Comparator;

import org.shappell.ahocorasick.entity.IEntity;

public class StringSearchResult
{
  public final static StringSearchResult EMPTY = new StringSearchResult(-1,-1, "",null);
  private final static SortByStartIndex sorter = new SortByStartIndex();
  
  public static class SortByStartIndex implements Comparator<StringSearchResult>
  {
    public int compare(StringSearchResult o1, StringSearchResult o2) 
    {
        if(o1.start < o2.start)
          return -1;
        else if(o1.start > o2.start)
          return 1;
        else
          return 0;
    }
  }
  
  public static SortByStartIndex getIndexSorter()
  {
    return sorter;
  }

  
  private int start;
  private int end;
  private IEntity entity;
  private String keyword;
  private int termposition;
  private boolean quoted;
  private boolean capitalized;
  private boolean exactMatch;
  private boolean possessive;
  private double rawScore;
  private String[] terms;
  private boolean partOfEntityChain;

  
  public StringSearchResult(int start, int end, String keyword, IEntity entity)
  {
    this.setStart(start);
    this.setEnd(end);
    this.setKeyword(keyword);
    this.termposition = 0;
    this.rawScore = -1.0;
    this.setEntity(entity);
  }

  public StringSearchResult(int start, int end, String keyword, IEntity entity, int termposition)
  {
    this.setStart(start);
    this.setEnd(end);
    this.setKeyword(keyword);
    this.setEntity(entity);
    this.termposition = termposition;
    this.rawScore = -1.0;
  }

  @Override
  public String toString()
  {
    return "StringSearchResult[start = " + start + ",end = " + end + ", keyword = " + keyword + ", entity = " + getEntity() + ", termposition = " + termposition + "]";
  }

  @Override
  public int hashCode()
  {
    int result = 17;
    result = 37 * result + start;
    result = 37 * result + end;
    result = 37 * result + ((keyword == null) ? 0 : keyword.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
      return true;
    if (!(obj instanceof StringSearchResult))
      return false;

    StringSearchResult strResult = (StringSearchResult) obj;

    if (this.start != strResult.start || this.end != strResult.end)
      return false;

    if (this.keyword == null)
    {
      return strResult.keyword == null;
    }
    else
    {
      return this.keyword.equals(strResult.keyword);
    }
  }

  public void setKeyword(String keyword)
  {
    this.keyword = keyword;
  }

  public String getKeyword()
  {
    return keyword;
  }

  public void setQuoted(boolean quoted)
  {
    this.quoted = quoted;
  }

  public boolean isQuoted()
  {
    return quoted;
  }

  public void setCapitalized(boolean capitalized)
  {
    this.capitalized = capitalized;
  }

  public boolean isCapitalized()
  {
    return capitalized;
  }

  public void setRawScore(double rawScore)
  {
    this.rawScore = rawScore;
  }

  public double getRawScore()
  {
    return rawScore;
  }

  public void setTerms(String[] terms)
  {
    this.terms = terms;
  }

  public String[] getTerms()
  {
    return terms;
  }

  public void setStart(int start)
  {
    this.start = start;
  }

  public int getStart()
  {
    return start;
  }

  public void setEnd(int end)
  {
    this.end = end;
  }

  public int getEnd()
  {
    return end;
  }

  public void setExactMatch(boolean exactMatch)
  {
    this.exactMatch = exactMatch;
  }

  public boolean isExactMatch()
  {
    return exactMatch;
  }

  public void setPossessive(boolean possessive)
  {
    this.possessive = possessive;
  }

  public boolean isPossessive()
  {
    return possessive;
  }

  public void setPartOfEntityChain(boolean partOfEntityChain)
  {
    this.partOfEntityChain = partOfEntityChain;
  }

  public boolean isPartOfEntityChain()
  {
    return partOfEntityChain;
  }

public IEntity getEntity() {
	return entity;
}

public void setEntity(IEntity entity) {
	this.entity = entity;
}
}
