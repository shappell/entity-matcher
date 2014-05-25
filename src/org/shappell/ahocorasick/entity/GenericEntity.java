package org.shappell.ahocorasick.entity;

import java.util.List;

public class GenericEntity<T> implements IEntity
{
	private IEntityType entityType;
	private int id = 0;
	private T value;
	private String name;

	public GenericEntity(String name, IEntityType type) {
		this.entityType = type;
		this.name = name;
	}

	public GenericEntity(String name, IEntityType type, int id) {
		this.entityType = type;
		this.id = id;
		this.name = name;
	}
	
	public GenericEntity(String name, IEntityType type, T value, int id) {
		this.entityType = type;
		this.id = id;
		this.name = name;
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "[GenericEntity<T>: id = " + id + ", name = " + name + ",entityType=" + entityType + ", T = " + value + "]";
	}

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public IEntityType getEntityType() {
		return this.entityType;
	}

	@Override
	public List<String> getSynonyms() {
		return null;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}
}
