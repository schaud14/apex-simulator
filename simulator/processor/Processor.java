package edu.mcs.processor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.mcs.beans.Instruction;
import edu.mcs.util.InstructionType;
import edu.mcs.writer.OutputWriter;

import java.util.Set;

/**
 * @author Saurabh Chaudhari
 *
 */
public class Processor {

	/*
	 * Program Counter.
	 */
	private static Integer PC = 20000;

	/*
	 * Set of instructions.
	 */
	private List<Instruction> instructionSet = new ArrayList<Instruction>();

	private Map<Integer, Instruction> instructionMap = new LinkedHashMap<Integer, Instruction>();

	/*
	 * Register File.
	 */
	private RegisterFile registerFile = new RegisterFile();

	private boolean haltFlag = false;

	private Memory memory = new Memory();

	/*
	 * Holds pipeline status.
	 */
	private String[][] pipeline = null;

	/*
	 * Holds row and column values.
	 */
	private int row = 0;

	private int column = 0;

	private Integer currentPC = PC;

	private Integer lastPC = 0;
	/*
	 * Holds the value of number of cycles to simulate.
	 */
	private int simCycle = 0;
	/*
	 * Number of stages - 1. Fetch. 2. Decode. 3. Execute. 4. Memory. 5.
	 * WriteBack.
	 */
	private static final Integer NO_STAGES = 5;

	public Processor() {
		super();
	}

	public void initialize() {
		// Assign program counter to each instruction in the given input.
		initializeInstructionSet();
		currentPC = PC;
		column = 0;
		haltFlag = false;
		// populate lastPC value which will be used to stop the pipeline
		// execution when the last instruction gets executed.
		lastPC = instructionSet.get(instructionSet.size() - 1).getProgramCounter();
		// Initialize registers R0 to R7 and Register X.
		registerFile.initialize();
		// Initialize memory.
		memory.initialize();
		// display initialized values.
	}

	private void initializeInstructionSet() {
		Integer programCounter = PC;
		for (Instruction instruction : instructionSet) {
			instruction.setProgramCounter(programCounter);
			programCounter += 1;
		}
		for (Instruction instruction : instructionSet) {
			instructionMap.put(instruction.getProgramCounter(), instruction);
		}
	}

	public void dInitialize() {
		System.out.println("\nInstruction Sequence -");
		for (Instruction instruction : instructionSet) {
			String instr = instruction.getProgramCounter() + " : " + instruction.getOpCode() + " "
					+ instruction.getDestination();
			if (null != instruction.getSource1()) {
				instr = instr + " " + instruction.getSource1();
			}
			if (null != instruction.getSource2()) {
				instr = instr + " " + instruction.getSource2();
			}
			System.out.println(instr);
		}
		dRegisterFile();
		dMemory();
	}

	private void dRegisterFile() {
		System.out.println("\nRegister File -\n");
		Set<Entry<String, Value>> regItr = registerFile.getRegisterSet().entrySet();
		String contents = "";
		for (Map.Entry<String, Value> reg : regItr) {
			contents = contents + "[ " + reg.getKey() + " = " + reg.getValue().getValue() + " ]\t";
		}
		System.out.println(contents);
	}

	private void dMemory() {
		System.out.println("\nMemory -\n");
		int low, high;
		for (int outerCount = 0; outerCount < 10; outerCount++) {
			low = outerCount * 10;
			high = ((outerCount + 1) * 10) - 1;
			String tenMemLoc = "";
			for (int count = low; count <= high; count++) {
				tenMemLoc = tenMemLoc + "[ " + count + " : " + memory.getMemory().get(String.valueOf(count)) + " ]";
			}
			System.out.println(tenMemLoc);
		}
	}

	public void simulate(Integer noOfCycles) {
		// Initialize pipeline data structure with number of stages as rows and
		// number of cycles as columns.
		pipeline = new String[NO_STAGES][noOfCycles];
		simCycle = noOfCycles;
		// initialize flag
		boolean hasInstructionstoExecute = true;
		while (hasInstructionstoExecute && column < simCycle) {
			// fetch the next instruction to simulate.
			if(instructionMap.get(currentPC) == null){
				break;
			}
			Instruction instruction = instructionMap.get(currentPC);
			iInitialize(instruction);
			// start simulation by calling fetch() method.
			fetchStage(instruction, column);
			// Increment current Program counter value to point to the next
			// value.
			currentPC++;
			column++;
			// Initialize stage to the fetch stage i.e 0.
			row = 0;
			if (currentPC > lastPC) {
				hasInstructionstoExecute = false;
			}
		}
		if (null != pipeline[4][simCycle - 1]) {
			if (pipeline[4][simCycle - 1].trim().endsWith("HALT") && (haltFlag == true)) {
				haltFlag = false;
				dSimulate(simCycle);
				System.exit(0);
			}
		}
	}

	private void iInitialize(Instruction instruction) {
		instruction.setDest(null);
		instruction.setSrc1(null);
		instruction.setSrc2(null);
		instruction.setMemContents(null);
		instruction.setLiteralValue(null);

	}

	public void dSimulate(Integer noCycleToDisplay) {
		System.out.println("\nPipeline state after cycle " + noCycleToDisplay + " - \n");
		System.out.println("Stage       -          Content\n");
		System.out.println("[FETCH]     - [ " + getElement(0, noCycleToDisplay) + " ]");
		System.out.println("[DECODE]    - [ " + getElement(1, noCycleToDisplay) + " ]");
		System.out.println("[EXECUTE]   - [ " + getElement(2, noCycleToDisplay) + " ]");
		System.out.println("[MEMORY]    - [ " + getElement(3, noCycleToDisplay) + " ]");
		System.out.println("[WRITEBACK] - [ " + getElement(4, noCycleToDisplay) + " ]");
		dRegisterFile();
		dMemory();
		OutputWriter outputWriter = new OutputWriter();
		outputWriter.setNoOfCycles(noCycleToDisplay);
		outputWriter.setPipeline(pipeline);
		outputWriter.write();
	}

	private String getElement(int stage, int noOfCyclesToDisplay) {
		if(null != pipeline){
		return pipeline[stage][noOfCyclesToDisplay - 1] == null ? "No Instruction"
				: pipeline[stage][noOfCyclesToDisplay - 1];
		}
		return "";
	}

	public void fetchStage(Instruction instruction, Integer cycle) {
		if (pipeline[row][cycle] == null) {
			pipeline[row][cycle] = instruction.getStageInformation();
		}
		decodeStage(instruction, cycle);
	}

	public void decodeStage(Instruction instruction, Integer cycle) {
		if (((cycle + 1) < simCycle) && (pipeline[row + 1][cycle + 1] == null)) {
			decodeOperation(instruction, cycle + 1);
			pipeline[++row][++cycle] = instruction.getStageInformation();
		} else {
			while (((cycle + 1) < simCycle) && (pipeline[row + 1][cycle + 1] != null)) {
				if (pipeline[row][cycle + 1] == null) {
					pipeline[row][++cycle] = instruction.getStageInformation();
					column++;
				}
			}
			if (((cycle + 1) < simCycle)) {
				decodeOperation(instruction, cycle + 1);
				pipeline[++row][++cycle] = instruction.getStageInformation();
			}
		}
		int result = dependencyCheck(instruction, cycle);
		switch (result) {
		case 2:
			if ((cycle + 1) < simCycle) {
				decodeOperation(instruction, cycle + 1);
				pipeline[row][++cycle] = instruction.getStageInformation();
			}
			if ((cycle + 1) < simCycle) {
				decodeOperation(instruction, cycle + 1);
				pipeline[row][++cycle] = instruction.getStageInformation();
				executeStage(instruction, cycle);
			}
			break;
		case 1:
			if ((cycle + 1) < simCycle) {
				decodeOperation(instruction, cycle + 1);
				pipeline[row][++cycle] = instruction.getStageInformation();
				executeStage(instruction, cycle);
			}
			break;
		default:
			executeStage(instruction, cycle);
		}
	}

	private void decodeOperation(Instruction instruction, Integer cycle) {
		switch (instruction.getType()) {
		case RTR:
			switch (instruction.getOpCode()) {
			/*
			 * ADD,SUB,MUL,AND,OR,EX-OR are three operand instructions and can
			 * not have literal operands.
			 */
			case "ADD":
			case "SUB":
			case "MUL":
			case "AND":
			case "OR":
			case "EX-OR":
				if (null != instruction.getSource1()) {
					if (registerFile.getRegisterSet().get(instruction.getSource1().getOperand())
							.getCycleUpdated() <= cycle) {
						instruction.setSrc1(
								registerFile.getRegisterValue(instruction.getSource1().getOperand()).getValue());
					}
				}
				if (null != instruction.getSource2()) {
					if (registerFile.getRegisterSet().get(instruction.getSource2().getOperand())
							.getCycleUpdated() <= cycle) {
						instruction.setSrc2(
								registerFile.getRegisterValue(instruction.getSource2().getOperand()).getValue());
					}
				}
				break;
			case "MOV":
				/*
				 * MOV is a two operand instruction and can not have literal
				 * operands.
				 */
				if (null != instruction.getSource1()) {
					if (registerFile.getRegisterSet().get(instruction.getSource1().getOperand())
							.getCycleUpdated() <= cycle) {
						instruction.setSrc1(
								registerFile.getRegisterValue(instruction.getSource1().getOperand()).getValue());
					}
				}
				break;
			case "MOVC":
				/*
				 * MOVC is a two operand instruction where second operand can be
				 * a literal.
				 */
				if (null != instruction.getSource1()) {
					if (instruction.getSource1().isLiteral()) {
						instruction.setSrc1(instruction.getSource1().getLiteralValue());
					} else if (registerFile.getRegisterSet().get(instruction.getSource1().getOperand())
							.getCycleUpdated() <= cycle) {
						instruction.setSrc1(
								registerFile.getRegisterValue(instruction.getSource1().getOperand()).getValue());
					}
				}
				break;
			}
			break;
		case MEM:
			/*
			 * Second source operand of LOAD instruction can be a literal.
			 */
			switch (instruction.getOpCode()) {
			case "LOAD":
				if (null != instruction.getSource1()) {
					if (registerFile.getRegisterSet().get(instruction.getSource1().getOperand())
							.getCycleUpdated() <= cycle) {
						instruction.setSrc1(
								registerFile.getRegisterValue(instruction.getSource1().getOperand()).getValue());
					}
				}
				if (null != instruction.getSource2()) {
					if (instruction.getSource2().isLiteral()) {
						instruction.setSrc2(instruction.getSource2().getLiteralValue());
					} else if (registerFile.getRegisterSet().get(instruction.getSource2().getOperand())
							.getCycleUpdated() <= cycle) {
						instruction.setSrc1(
								registerFile.getRegisterValue(instruction.getSource2().getOperand()).getValue());
					}
				}
				break;
			case "STORE":
				if (null != instruction.getDestination()) {
					if (registerFile.getRegisterSet().get(instruction.getDestination().getOperand())
							.getCycleUpdated() <= cycle) {
						instruction.setDest(
								registerFile.getRegisterValue(instruction.getDestination().getOperand()).getValue());
					}
				}
				if (null != instruction.getSource1()) {
					if (registerFile.getRegisterSet().get(instruction.getSource1().getOperand())
							.getCycleUpdated() <= cycle) {
						instruction.setSrc1(
								registerFile.getRegisterValue(instruction.getSource1().getOperand()).getValue());
					}
				}
				if (null != instruction.getSource2()) {
					if (instruction.getSource2().isLiteral()) {
						instruction.setSrc2(instruction.getSource2().getLiteralValue());
					} else if (registerFile.getRegisterSet().get(instruction.getSource2().getOperand())
							.getCycleUpdated() <= cycle) {
						instruction.setSrc2(
								registerFile.getRegisterValue(instruction.getSource2().getOperand()).getValue());
					}
				}
				break;
			default:
				throw new IllegalArgumentException("Invalid Instruction Type...");
			}
			break;
		case BR:
			switch (instruction.getOpCode()) {
			case "HALT":
				/*
				 * Halt instruction will stop instruction execution when it is
				 * in the write-back stage.
				 */
				if (simCycle > (cycle + 4)) {
					simCycle = cycle + 4;
				}
				break;
			case "JUMP":
				if (null != instruction.getDestination()) {
					if (registerFile.getRegisterSet().get(instruction.getDestination().getOperand())
							.getCycleUpdated() <= cycle) {
						instruction.setDest(
								registerFile.getRegisterValue(instruction.getDestination().getOperand()).getValue());
					}
				}
				instruction.setSrc1(instruction.getSource1().getLiteralValue());
				break;
			case "BAL":
				if (null != instruction.getDestination()) {
					if (registerFile.getRegisterSet().get(instruction.getDestination().getOperand())
							.getCycleUpdated() <= cycle) {
						instruction.setDest(
								registerFile.getRegisterValue(instruction.getDestination().getOperand()).getValue());
					}
				}
				instruction.setSrc1(instruction.getSource1().getLiteralValue());
				break;
			case "BNZ":
				if (null != instruction.getDestination()) {
					instruction.setDest(instruction.getDestination().getLiteralValue());
				}
				break;
			case "BZ":
				if (null != instruction.getDestination()) {
					instruction.setDest(instruction.getDestination().getLiteralValue());
				}
				break;
			}
			break;
		default:
			throw new IllegalArgumentException("Invalid Instruction Type...");
		}
	}

	public void executeStage(Instruction instruction, Integer cycle) {
		if ((cycle + 1) >= simCycle) {
			return;
		}
		if (pipeline[row + 1][cycle + 1] == null) {
			pipeline[++row][++cycle] = instruction.getStageInformation();
		}
		executeOperation(instruction, cycle);
		memoryStage(instruction, cycle);
	}

	private void executeOperation(Instruction instruction, Integer cycle) {
		switch (instruction.getOpCode()) {
		case "ADD":
			instruction.setDest(instruction.getSrc1() + instruction.getSrc2());
			registerFile.getRegisterSet().get(instruction.getDestination().getOperand()).setCycleUpdated(cycle + 2);
			break;
		case "SUB":
			instruction.setDest(instruction.getSrc1() - instruction.getSrc2());
			registerFile.getRegisterSet().get(instruction.getDestination().getOperand()).setCycleUpdated(cycle + 2);
			break;
		case "MUL":
			instruction.setDest(instruction.getSrc1() * instruction.getSrc2());
			registerFile.getRegisterSet().get(instruction.getDestination().getOperand()).setCycleUpdated(cycle + 2);
			break;
		case "AND":
			instruction.setDest(instruction.getSrc1() & instruction.getSrc2());
			registerFile.getRegisterSet().get(instruction.getDestination().getOperand()).setCycleUpdated(cycle + 2);
			break;
		case "OR":
			instruction.setDest(instruction.getSrc1() | instruction.getSrc2());
			registerFile.getRegisterSet().get(instruction.getDestination().getOperand()).setCycleUpdated(cycle + 2);
			break;
		case "EX-OR":
			instruction.setDest(instruction.getSrc1() ^ instruction.getSrc2());
			registerFile.getRegisterSet().get(instruction.getDestination().getOperand()).setCycleUpdated(cycle + 2);
			break;
		case "MOV":
			// No Operation.;
			registerFile.getRegisterSet().get(instruction.getDestination().getOperand()).setCycleUpdated(cycle + 2);
		case "MOVC":
			// No Operation.;
			registerFile.getRegisterSet().get(instruction.getDestination().getOperand()).setCycleUpdated(cycle + 2);
			break;
		case "LOAD":
			instruction.setLiteralValue(instruction.getSrc1() + instruction.getSrc2());
			registerFile.getRegisterSet().get(instruction.getDestination().getOperand()).setCycleUpdated(cycle + 2);
			break;
		case "STORE":
			instruction.setLiteralValue(instruction.getSrc1() + instruction.getSrc2());
			break;
		case "JUMP":
			/*
			 * Add a check if instructions are available.
			 */
			instruction.setLiteralValue(instruction.getDest() + instruction.getSrc1());
			if (null != instructionMap.get(currentPC + 1)) {
				Instruction fwd11 = instructionMap.get(currentPC + 1);
				int tempcolumn1 = column + 1;
				while ((tempcolumn1 <= (cycle - 1)) && (tempcolumn1 < simCycle)) {
					pipeline[row - 2][tempcolumn1] = fwd11.getStageInformation();
					tempcolumn1++;
				}
				decodeOperation(fwd11, cycle);
				pipeline[row - 1][cycle] = fwd11.getStageInformation();
				if (null != instructionMap.get(currentPC + 2)) {
					Instruction fwd21 = instructionMap.get(currentPC + 2);
					pipeline[row - 2][cycle] = fwd21.getStageInformation();
				}
			}
			currentPC = instruction.getLiteralValue() - 1;
			column = cycle;
			break;
		case "HALT":
			// no operation;
			haltFlag = true;
			break;
		case "BAL":
			instruction.setLiteralValue(instruction.getDest() + instruction.getSrc1());
			registerFile.getRegisterSet().put("X", new Value(currentPC + 1, cycle));
			if (null != instructionMap.get(currentPC + 1)) {
				Instruction fwd11 = instructionMap.get(currentPC + 1);
				int tempcolumn1 = column + 1;
				while ((tempcolumn1 <= (cycle - 1)) && (tempcolumn1 < simCycle)) {
					pipeline[row - 2][tempcolumn1] = fwd11.getStageInformation();
					tempcolumn1++;
				}
				decodeOperation(fwd11, cycle);
				pipeline[row - 1][cycle] = fwd11.getStageInformation();
				if (null != instructionMap.get(currentPC + 2)) {
					Instruction fwd21 = instructionMap.get(currentPC + 2);
					pipeline[row - 2][cycle] = fwd21.getStageInformation();
				}
			}
			currentPC = instruction.getLiteralValue() - 1;
			column = cycle;
			break;
		case "BNZ":
			Instruction prev = instructionMap.get(currentPC - 1);
			if (registerFile.getRegisterSet().get(prev.getDestination().getOperand()).getValue() != 0) {
				if (null != instructionMap.get(currentPC + 1)) {
					Instruction fwd11 = instructionMap.get(currentPC + 1);
					int tempcolumn1 = column + 1;
					while ((tempcolumn1 <= (cycle - 1)) && (tempcolumn1 < simCycle)) {
						pipeline[row - 2][tempcolumn1] = fwd11.getStageInformation();
						tempcolumn1++;
					}
					decodeOperation(fwd11, cycle);
					pipeline[row - 1][cycle] = fwd11.getStageInformation();
					if (null != instructionMap.get(currentPC + 2)) {
						Instruction fwd21 = instructionMap.get(currentPC + 2);
						pipeline[row - 2][cycle] = fwd21.getStageInformation();
					}
				}
				// currentPC is set to dest target -1 because the currentpc is
				// being updated in the while loop.
				currentPC = (currentPC + instruction.getDest()) - 1;
				column = cycle;
			}
			break;
		case "BZ":
			Instruction prev1 = instructionMap.get(currentPC - 1);
			if (registerFile.getRegisterSet().get(prev1.getDestination().getOperand()).getValue() == 0) {
				if (null != instructionMap.get(currentPC + 1)) {
					Instruction fwd11 = instructionMap.get(currentPC + 1);
					int tempcolumn1 = column + 1;
					while ((tempcolumn1 <= (cycle - 1)) && (tempcolumn1 < simCycle)) {
						pipeline[row - 2][tempcolumn1] = fwd11.getStageInformation();
						tempcolumn1++;
					}
					decodeOperation(fwd11, cycle);
					pipeline[row - 1][cycle] = fwd11.getStageInformation();
					if (null != instructionMap.get(currentPC + 2)) {
						Instruction fwd21 = instructionMap.get(currentPC + 2);
						pipeline[row - 2][cycle] = fwd21.getStageInformation();
					}
				}
				// currentPC is set to dest target -1 because the currentpc is
				// being updated in the while loop.
				currentPC = (currentPC + instruction.getDest()) - 1;
				column = cycle;
			}
			break;
		default:
			throw new IllegalArgumentException("Invalid Instruction...");
		}
	}

	public void memoryStage(Instruction instruction, Integer cycle) {
		if ((cycle + 1) >= simCycle) {
			return;
		}
		if (pipeline[row + 1][cycle + 1] == null) {
			pipeline[++row][++cycle] = instruction.getStageInformation();
		}
		memoryOperation(instruction);
		writeBackStage(instruction, cycle);
	}

	private void memoryOperation(Instruction instruction) {
		if (instruction.getType() == InstructionType.MEM) {
			/*
			 * Memory operations will only be performed for Load and Store
			 * instructions.
			 */
			switch (instruction.getOpCode()) {
			case "LOAD":
				instruction.setMemContents(memory.getMemory().get(instruction.getLiteralValue().toString()));
				break;
			case "STORE":
				break;
			}
		}
	}

	public void writeBackStage(Instruction instruction, Integer cycle) {
		if ((cycle + 1) >= simCycle) {
			return;
		}
		if (pipeline[row + 1][cycle + 1] == null) {
			pipeline[++row][++cycle] = instruction.getStageInformation();
		}
		writeBackOperation(instruction, cycle);
	}

	private void writeBackOperation(Instruction instruction, Integer cycle) {
		switch (instruction.getOpCode()) {
		case "ADD":
		case "SUB":
		case "MUL":
		case "AND":
		case "OR":
		case "EX-OR":
			registerFile.getRegisterSet().put(instruction.getDestination().getOperand(),
					new Value(instruction.getDest(), cycle));
			break;
		case "MOV":
		case "MOVC":
			registerFile.getRegisterSet().put(instruction.getDestination().getOperand(),
					new Value(instruction.getSrc1(), cycle));
			break;
		case "LOAD":
			registerFile.getRegisterSet().put(instruction.getDestination().getOperand(),
					new Value(instruction.getMemContents(), cycle));
			break;
		case "STORE":
			memory.getMemory().put(instruction.getLiteralValue().toString(), instruction.getDest());
			break;
		case "JUMP":
			// no Operation;
			break;
		case "HALT":
			break;
		case "BAL":
			// no Operation;
			break;
		case "BNZ":
			// no Operation;
			break;
		case "BZ":
			// no Operation.
			break;
		default:
			throw new IllegalArgumentException("Invalid Instruction..." + instruction.getOpCode());
		}
	}

	public Integer dependencyCheck(Instruction instruction, Integer cycle) {
		int op1Diff = 0;
		int op2Diff = 0;
		int destDiff = 0;
		switch (instruction.getType()) {
		case RTR:
			switch (instruction.getOpCode()) {
			case "ADD":
			case "SUB":
			case "MUL":
			case "AND":
			case "OR":
			case "EX-OR":
				/*
				 * If source operand is register, check if it is dependent on
				 * the previous instructions.
				 */
				if (null != instruction.getSource1()) {
					if (!instruction.getSource1().isLiteral()) {
						if ((cycle <= registerFile.getRegisterSet().get(instruction.getSource1().getOperand())
								.getCycleUpdated())) {
							op1Diff = (registerFile.getRegisterSet().get(instruction.getSource1().getOperand())
									.getCycleUpdated() - cycle);
						}
					}
				}
				if (null != instruction.getSource2()) {
					if (!instruction.getSource2().isLiteral()) {
						if ((cycle <= registerFile.getRegisterSet().get(instruction.getSource2().getOperand())
								.getCycleUpdated())) {
							op2Diff = (registerFile.getRegisterSet().get(instruction.getSource2().getOperand())
									.getCycleUpdated() - cycle);
						}
					}
				}
				if (op1Diff > op2Diff) {
					return op1Diff;
				} else if (op2Diff > op1Diff) {
					return op2Diff;
				}
				return op1Diff;
			case "MOV":
				/*
				 * If source operand is register, check if it is dependent on
				 * the previous instructions.
				 */
				if (null != instruction.getSource1()) {
					if ((cycle <= registerFile.getRegisterSet().get(instruction.getSource1().getOperand())
							.getCycleUpdated())) {
						op1Diff = (registerFile.getRegisterSet().get(instruction.getSource1().getOperand())
								.getCycleUpdated() - cycle);
					}
				}
				return op1Diff;
			case "MOVC":
				/*
				 * If source operand is register, check if it is dependent on
				 * the previous instructions.
				 */
				if (null != instruction.getSource1()) {
					if (!instruction.getSource1().isLiteral()) {
						if ((cycle <= registerFile.getRegisterSet().get(instruction.getSource1().getOperand())
								.getCycleUpdated())) {
							op1Diff = (registerFile.getRegisterSet().get(instruction.getSource1().getOperand())
									.getCycleUpdated() - cycle);
						}
					}
				}
				return op1Diff;
			}
		case MEM:
			switch (instruction.getOpCode()) {
			case "LOAD":
				/*
				 * Load can have either literal or a register as a operand.
				 */
				if (null != instruction.getSource1()) {
					if ((cycle <= registerFile.getRegisterSet().get(instruction.getSource1().getOperand())
							.getCycleUpdated())) {
						op1Diff = (registerFile.getRegisterSet().get(instruction.getSource1().getOperand())
								.getCycleUpdated() - cycle);
					}
				}
				if (null != instruction.getSource2()) {
					if (!instruction.getSource2().isLiteral()) {
						if ((cycle <= registerFile.getRegisterSet().get(instruction.getSource2().getOperand())
								.getCycleUpdated())) {
							op2Diff = (registerFile.getRegisterSet().get(instruction.getSource2().getOperand())
									.getCycleUpdated() - cycle);
						}
					}
				}
				if (op1Diff > op2Diff) {
					return op1Diff;
				} else if (op2Diff > op1Diff) {
					return op2Diff;
				}
				return op1Diff;
			case "STORE":
				if (null != instruction.getDestination()) {
					if ((cycle <= registerFile.getRegisterSet().get(instruction.getDestination().getOperand())
							.getCycleUpdated())) {
						destDiff = registerFile.getRegisterSet().get(instruction.getDestination().getOperand())
								.getCycleUpdated() - cycle;
					}
				}
				if (null != instruction.getSource1()) {
					if ((cycle <= registerFile.getRegisterSet().get(instruction.getSource1().getOperand())
							.getCycleUpdated())) {
						op1Diff = registerFile.getRegisterSet().get(instruction.getSource1().getOperand())
								.getCycleUpdated() - cycle;
					}
				}
				if (null != instruction.getSource2()) {
					if (!instruction.getSource2().isLiteral()) {
						if ((cycle <= registerFile.getRegisterSet().get(instruction.getSource2().getOperand())
								.getCycleUpdated())) {
							op2Diff = registerFile.getRegisterSet().get(instruction.getSource2().getOperand())
									.getCycleUpdated() - cycle;
						}
					}
				}
				// TODO : Check if this works on bingsuns server.
				return Collections.max(Arrays.asList(destDiff, op1Diff, op2Diff));
			}
			break;
		case BR:
			switch (instruction.getOpCode()) {
			case "BAL":
			case "JUMP":
				int brDestDiff = 0;
				if ((cycle <= registerFile.getRegisterSet().get(instruction.getDestination().getOperand())
						.getCycleUpdated())) {
					brDestDiff = registerFile.getRegisterSet().get(instruction.getDestination().getOperand())
							.getCycleUpdated() - cycle;
				}
				return brDestDiff;
			case "BNZ":
				return 2;
			case "BZ":
				return 2;
			default:
			}
		default:
		}
		return 0;
	}

	/*
	 * Displays the pipeline at the simulated cycle. TODO: Use this while
	 * submitting the code with nicer display.
	 */
	private void printRow(String[] pipelineRow) {
		System.out.println(pipelineRow[pipelineRow.length - 1]);
	}

	public List<Instruction> getInstructionSet() {
		return instructionSet;
	}

	public void setInstructionSet(List<Instruction> instructionSet) {
		this.instructionSet = instructionSet;
	}
}
