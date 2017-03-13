package edu.mcs.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.mcs.beans.Instruction;
import edu.mcs.beans.Operand;
/**
 * @author Saurabh Chaudhari
 *
 */
public class InstructionLoader {

	private String fileName;

	public InstructionLoader(String fileName) {
		super();
		this.fileName = fileName;
	}

	public List<Instruction> loadInstructions(){
		List<Instruction> instructionList = new ArrayList<Instruction>();
		try {
			File file = new File(this.fileName);
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = null;
			String[] instrusplit = null;
			while ((line = br.readLine()) != null) {
				instrusplit = line.split(" ");
				Instruction instruction = new Instruction(instrusplit[0]);
				if (instrusplit.length > 1) {
					switch (instrusplit.length) {
					case 4:				
						instruction.setDestination(new Operand(instrusplit[1].trim()));
						instruction.setSource1(new Operand(instrusplit[2].trim()));
						instruction.setSource2(new Operand(instrusplit[3].trim()));
						break;
					case 3:
						instruction.setDestination(new Operand(instrusplit[1]));
						instruction.setSource1(new Operand(instrusplit[2]));
						break;
					case 2:
						instruction.setDestination(new Operand(instrusplit[1]));
						break;
					default:
						throw new IllegalArgumentException("Invalid Instruction..."+line);
					}
				}
				instructionList.add(instruction);
			}
			br.close();
		} catch (FileNotFoundException e) {
			System.err.println("Please enter a valid filename/filepath");
			e.printStackTrace();
			System.exit(0);
		} catch (IOException e) {
			System.err.println("Encountered error while reading an input file");
			e.printStackTrace();
			System.exit(0);
		}
		return instructionList;
	}
}
