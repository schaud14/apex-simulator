package edu.mcs.driver;

import java.util.Scanner;

import edu.mcs.processor.Processor;
import edu.mcs.reader.InstructionLoader;
/**
 * @author Saurabh Chaudhari
 *
 */
public class Driver {

	public static final String OUTPUT = "simulate.txt";
	
	public static void main(String[] args) {
		InstructionLoader instructionLoader = null;
		Processor processor = new Processor();
		Scanner userInput = new Scanner(System.in);
		Integer choice;
		Integer noCyclesToSim = 1;
		if(args == null || args.length == 0){
			userInput.close();
			throw new IllegalArgumentException("Please enter input filename/filepath.");
		}
		else if(args.length > 1){
			userInput.close();
			throw new IllegalArgumentException("Please enter a valid input.");
		}
		instructionLoader = new InstructionLoader(args[0]);
		processor.setInstructionSet(instructionLoader.loadInstructions());
		do{
			System.out.println("\n1. Initialize");
			System.out.println("2. Simulate");
			System.out.println("3. Display");
			System.out.println("4. Exit");
			System.out.println("Enter your choice - ");
			choice = userInput.nextInt();
			switch(choice){
			case 1:
				processor.initialize();
				processor.dInitialize();
				break;
			case 2:
				System.out.println("Enter number of cycles to simulate : ");
				noCyclesToSim = userInput.nextInt();
				processor.initialize();
				processor.simulate(noCyclesToSim);
				System.out.println("Simulation completed successfully. Select option 3 to view current state of the processor.");
				break;
			case 3:
				processor.dSimulate(noCyclesToSim);
				break;
			case 4:
				break;
				default:
					System.out.println("Please enter a valid choice.");
			}
		}while(choice != 4);
		userInput.close();
	}
}