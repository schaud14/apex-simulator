package edu.mcs.beans;
/**
 * @author Saurabh Chaudhari
 */
public class Operand {
	private String operand;
	
	public Operand() {
		super();
	}

	public Operand(String operand) {
		super();
		this.operand = operand;
	}

	public String getOperand() {
		return operand;
	}

	public boolean isLiteral(){
		return !this.operand.startsWith("R");
	}
	
	public Integer getLiteralValue(){
		return Integer.parseInt(this.operand);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((operand == null) ? 0 : operand.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		Operand otherOp = (Operand) obj;
		return (this.getOperand().equals(otherOp.getOperand()) && (this.isLiteral() == otherOp.isLiteral()));
	}

	@Override
	public String toString() {
		return operand;
	}
}
