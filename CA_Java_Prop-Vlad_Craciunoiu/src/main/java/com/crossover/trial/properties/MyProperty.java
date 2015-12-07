package com.crossover.trial.properties;

public class MyProperty {

	String propertyName;
	String propertyType;
	Object propertyValue;
	
	public String getPropertyName() {
		return propertyName;
	}
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}
	public String getPropertyType() {
		return propertyType;
	}
	public void setPropertyType(String propertyType) {
		this.propertyType = propertyType;
	}
	public Object getPropertyValue() {
		return propertyValue;
	}
	public void setPropertyValue(Object propertyValue) {
		this.propertyValue = propertyValue;
	}
	
	@Override
	public String toString() {
		return propertyName + ", " + propertyType + ", " + propertyValue;
	}
	
}
