package edu.mcs.writer;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import edu.mcs.driver.Driver;
/**
 * @author Saurabh Chaudhari
 *
 */
public class OutputWriter {

	private String[][] pipeline;
	private Integer noOfCycles;

	public void write() {
		try {
			PrintWriter out = new PrintWriter(new FileWriter(Driver.OUTPUT));
			StringBuffer sbuffer = new StringBuffer("");
			for (int cycle = 1; cycle <= noOfCycles; cycle++) {
				sbuffer.append("Pipeline state after cycle " + cycle + " - \n");
				sbuffer.append("Stage       -          Content\n");
				sbuffer.append("[FETCH]     - [ " + getElement(0, cycle) + " ]\n");
				sbuffer.append("[DECODE]    - [ " + getElement(1, cycle) + " ]\n");
				sbuffer.append("[EXECUTE]   - [ " + getElement(2, cycle) + " ]\n");
				sbuffer.append("[MEMORY]    - [ " + getElement(3, cycle) + " ]\n");
				sbuffer.append("[WRITEBACK] - [ " + getElement(4, cycle) + " ]\n");
				sbuffer.append("\n");
			}
			out.println(sbuffer);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String getElement(int stage, int noOfCyclesToDisplay) {
		if(null != pipeline){
		return pipeline[stage][noOfCyclesToDisplay - 1] == null ? "No Instruction"
				: pipeline[stage][noOfCyclesToDisplay - 1];
		}
		return "";
	}

	public String[][] getPipeline() {
		return pipeline;
	}

	public void setPipeline(String[][] pipeline) {
		this.pipeline = pipeline;
	}

	public Integer getNoOfCycles() {
		return noOfCycles;
	}

	public void setNoOfCycles(Integer noOfCycles) {
		this.noOfCycles = noOfCycles;
	}

}
