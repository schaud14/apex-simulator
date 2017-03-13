package edu.mcs.processor;
/**
 * @author Saurabh Chaudhari
 *
 */
public class Value {

	Integer value;
	Integer cycleUpdated;
	
	public Value() {
		super();
		value = 0;
		cycleUpdated = 0;
	}
	public Value(Integer value, Integer cycleUpdated) {
		super();
		this.value = value;
		this.cycleUpdated = cycleUpdated;
	}
	public Integer getValue() {
		return value;
	}
	public void setValue(Integer value) {
		this.value = value;
	}
	public Integer getCycleUpdated() {
		return cycleUpdated;
	}
	public void setCycleUpdated(Integer cycleUpdated) {
		this.cycleUpdated = cycleUpdated;
	}
	
}
