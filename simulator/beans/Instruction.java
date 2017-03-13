package edu.mcs.beans;
import edu.mcs.util.InstructionType;
/**
 * @author Saurabh Chaudhari
 *
 */
public class Instruction {
	Integer programCounter;
	InstructionType type;
	String opCode;
	Integer opCodeInt;
	Operand destination;
	Operand source1;
	Operand source2;
	Integer src1,src2,dest;
	Integer literalValue;
	Integer memContents;

	public Instruction() {
		super();
	}

	public Instruction(String opCode) {
		super();
		this.opCode = opCode;
		this.type = getInstructionType(opCode);
	}

	private InstructionType getInstructionType(String opCode) {
		InstructionType type;
		switch(opCode){
		case "MOV":
		case "ADD":
		case "SUB":
		case "MOVC":
		case "MUL":
		case "AND":
		case "OR":
		case "EX-OR":
			type = InstructionType.RTR;
			break;
		case "LOAD":
		case "STORE":
			type = InstructionType.MEM;
			break;
		case "BZ":
		case "BNZ":
		case "JUMP":
		case "BAL":
		case "HALT":
			type = InstructionType.BR;
			break;
		default:
			throw new IllegalArgumentException("Instruction type is invalid..."+opCode);
		}
		return type;
	}

	public Instruction(String opCode, Operand source1, Operand source2) {
		super();
		this.opCode = opCode;
		this.source1 = source1;
		this.source2 = source2;
	}

	public String getOpCode() {
		return opCode;
	}

	public void setOpCode(String opCode) {
		this.opCode = opCode;
	}
	
	public Operand getSource1() {
		return source1;
	}

	public void setSource1(Operand source1) {
		this.source1 = source1;
	}

	public Operand getSource2() {
		return source2;
	}

	public void setSource2(Operand source2) {
		this.source2 = source2;
	}

	public Operand getDestination() {
		return destination;
	}

	public void setDestination(Operand destination) {
		this.destination = destination;
	}
	
	public Integer getProgramCounter() {
		return programCounter;
	}

	public void setProgramCounter(Integer programCounter) {
		this.programCounter = programCounter;
	}
	
	public InstructionType getType() {
		return type;
	}
	
	public Integer getSrc1() {
		return src1;
	}

	public void setSrc1(Integer src1) {
		this.src1 = src1;
	}

	public Integer getSrc2() {
		return src2;
	}

	public void setSrc2(Integer src2) {
		this.src2 = src2;
	}

	public Integer getDest() {
		return dest;
	}

	public void setDest(Integer dest) {
		this.dest = dest;
	}
	
	public Integer getLiteralValue() {
		return literalValue;
	}

	public void setLiteralValue(Integer literalValue) {
		this.literalValue = literalValue;
	}

	public Integer getMemContents() {
		return memContents;
	}

	public void setMemContents(Integer memContents) {
		this.memContents = memContents;
	}

	public String getStageInformation(){
		String information = programCounter + " : " + opCode + " ";
		if (null != this.getDestination()) {
			if (null != this.getDest()) {
				information += this.getDest();
			} else {
				information += this.getDestination().getOperand();
			}
		}
		if(null != this.getSource1()){
			if(null != this.getSrc1()){
				information += ","+this.getSrc1();
			}
			else {
				information += ","+this.getSource1().getOperand();
			}
		}
		if(null != this.getSource2()){
			if(null != this.getSrc2()){
				information += ","+this.getSrc2();
			}
			else {
				information += ","+this.getSource2().getOperand();
			}
		}
		return information;  
	}
	
	@Override
	public String toString() {
		return programCounter + " : " + opCode + " " + destination + "," + source1 + "," + source2;
	}

}
