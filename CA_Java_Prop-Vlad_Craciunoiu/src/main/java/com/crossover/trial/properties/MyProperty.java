package com.crossover.trial.properties;

import com.amazonaws.regions.Regions;

/**
 * This is a POJO which holds the properties to be displayed.
 * 
 * @author vlad
 *
 */
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
	
	/*
	 * type safe accessors
	 */
	
	public String getStringValue() throws ClassCastException {
		return (String) propertyValue;
	}
	
	public void setStringValue(String propertyValue) {
		this.propertyValue = propertyValue;
	}
	
	public Integer getIntegerValue() throws ClassCastException {
		return (Integer) propertyValue;
	}
	
	public void setIntegerValue(Integer propertyValue) {
		this.propertyValue = propertyValue;
	}
	
	public Double getDoubleValue() throws ClassCastException {
		return (Double) propertyValue;
	}
	
	public void setDoubleValue(Double propertyValue) {
		this.propertyValue = propertyValue;
	}
	
	public Regions getRegionValue() throws ClassCastException {
		return (Regions) propertyValue;
	}
	
	public void setRegionsValue(Regions propertyValue) {
		this.propertyValue = propertyValue;
	}
	
	@Override
	public String toString() {
		return propertyName + ", " + propertyType + ", " + propertyValue;
	}
	
}
