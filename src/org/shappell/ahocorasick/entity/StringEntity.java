package org.shappell.ahocorasick.entity;

import java.util.List;

public class StringEntity implements IEntity {

	 private String name;
	 
	 public StringEntity() {		 
	 }
	 
	 public StringEntity(String name) {		 
		 this.name = name;
	 }
	
	@Override
	public int getId() {
		return 0;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public IEntityType getEntityType() {
		return GenericEntityType.STRING;
	}

	@Override
	public List<String> getSynonyms() {
		return null;
	}

	public void setName(String name) {
		this.name = name;
	}

}
