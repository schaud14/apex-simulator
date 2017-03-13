package edu.mcs.processor;

import java.util.LinkedHashMap;
import java.util.Map;
/**
 * @author Saurabh Chaudhari
 *
 */
public class RegisterFile {
	Map<String, Value> registerSet;

	public void initialize() {
		registerSet = new LinkedHashMap<String, Value>();
		for (int count = 0; count < 8; count++) {
			registerSet.put("R" + count, new Value(0, 0)); // count to be
																// reverted to
																// 0. replace be
																// default
																// constructor.
		}
		registerSet.put("X", new Value());
	}

	public Value getRegisterValue(String reg) {
		return registerSet.get(reg);
	}

	public Map<String, Value> getRegisterSet() {
		return registerSet;
	}

}
