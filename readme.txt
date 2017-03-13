Steps for execution -

1. Traverse into directory kulkarni_anand_project1 
3. Execute make command from within the directory to compile the source code -
	make
4. Run following command to run the program :
	java -cp ./src simulator.driver.Driver "input filename/filePath"
5. Program execution will begin by displaying following menu -
	1. Initialize.
	2. Simulate
	3. Display
	4. Exit
	Enter your choice -
---------------------------------------

Menu - 
1. Initialize
On selecting this option program will initialize register and memory contents and will also assign the PC value to 20000.

2. Simulate
Program will ask for the number of cycles to simulate and will simulate the number of cycles provided by the user.After simulation is complete it will show following message -
"Simulation completed successfully. Select option 3 to view current state of the processor."

3. Display
Program will display the current state of processor. It includes contents of register file, memory and contents of each stage of a pipeline at the simulated cycle.
It will also write pipeline state after each cycle to the simulate.txt file.

---------------------------------------
