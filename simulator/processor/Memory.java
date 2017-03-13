package edu.mcs.processor;

import java.util.LinkedHashMap;
import java.util.Map;
/**
 * @author Saurabh Chaudhari
 *
 */
public class Memory {

	private Map<String,Integer> memory = new LinkedHashMap<String, Integer>();
	private static final Integer NO_MEM_BLOCKS = 10000;
	
	public void initialize(){
		for(int count = 0;count< NO_MEM_BLOCKS;count++){
			memory.put(String.valueOf(count), 0); // put 2nd parameter as zero. 'count' is used just for testing.
		}
	}

	@Override
	public String toString() {
		return "Memory [memory=" + memory + "]";
	}

	public Map<String, Integer> getMemory() {
		return memory;
	}

	public void setMemory(Map<String, Integer> memory) {
		this.memory = memory;
	}
}
