import java.io.File;
import java.util.Random;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.net.ssl.ManagerFactoryParameters;

public class CentralProcessingUnit {
	private static final int SWAP_TIME = 3;
	
	/**
	 * reads the input from the test data files
	 */
	Scanner reader;
	/**
	 * stores the data from the input files
	 */
	Process[] processManager;
	/**
	 * stores a clone of the processManager array
	 */
	Process[] manager;
	/**
	 * stores total elapsed time, starting from when the processes enter the queue
	 */
	int cpuTime;
	/**
	 * stores the output data, later to be output to a .CSV
	 */
	int[][] outputData;
	/**
	 * records the time elapsed from the Process.run() method
	 */
	int timeElapsed;
	/**
	 * for navigating through the outputData matrix
	 */
	int row;
	/**
	 * for navigating through the manager array
	 */
	int index;
	/**
	 * counts the number of completed processes
	 */
	int completionCount;
	
	/**
	 * Reads all processes from the passed file and stores them in the processManager array
	 * @param processFile
	 * @throws FileNotFoundException
	 */
	public void readProcesses(File processFile) throws FileNotFoundException {
		reader = new Scanner(processFile);
		
		int count = 0;
		while(reader.hasNext()) {
			reader.nextInt();
			count++;
		}
		count = count / 3;
		
		//closing and reopening the file so the pointer is at the beginning
		reader.close();				
		reader = new Scanner(processFile);
		
		processManager = new Process[count];
		
		int pid = 0, burstTime = 0, priority = 0;
		
		for(int i = 0; i < count; i++) {
			pid = reader.nextInt();
			burstTime = reader.nextInt();
			priority = reader.nextInt();
			
			processManager[i] = new Process(pid, burstTime, priority);
		}
		
		//System.out.println(processManager[8].pid);
	}
	
	/**
	 * First-Come-First-Serve
	 */
	public int[][] FCFS() {
		//System.out.println("***FCFS");
		
		//create a clone of the processManager array
		manager = new Process[processManager.length];
		for(int i = 0; i < manager.length; i++) {
			manager[i] = new Process(processManager[i].pid, processManager[i].burstTime, processManager[i].priority);
		}
		
		outputData = new int[manager.length][5];
		/*
		 * col 0 = cpuTime
		 * col 1 = PID
		 * col 2 = StartingBurstTime
		 * col 3 = EndingBurstTime
		 * col 4 = CompletionTime
		 */

		cpuTime = 0; 				//processes are in the queue at CPU time = 0
				
		for(int i = 0; i < manager.length; i++) {
			outputData[i][0] = cpuTime;					//record cpuTime
			outputData[i][1] = manager[i].pid; 			//record pid
			outputData[i][2] = manager[i].burstTime;	//record StartingBurstTime
			cpuTime = cpuTime + manager[i].burstTime;	//adding the full burstTime to the cpuTime
			manager[i].run(manager[i].burstTime); 		//running for the full burstTime
			outputData[i][3] = manager[i].burstTime;	//record EndingBurstTime, which will be 0
			outputData[i][4] = cpuTime;					//record CompletionTime
			cpuTime = cpuTime + SWAP_TIME;				//add the swap time to the CPU
		}
		
		//printMatrix(outputData);
		return outputData;
	}

	/**
	 * Shortest-Job-First
	 */
	public int[][] SJF() {
		//System.out.println("\n\n***SJF");
		
		//create a clone of the processManager array
		manager = new Process[processManager.length];
		for(int i = 0; i < manager.length; i++) {
			manager[i] = new Process(processManager[i].pid, processManager[i].burstTime, processManager[i].priority);
		}
		
		outputData = new int[manager.length][5];
		/*
		 * col 0 = cpuTime
		 * col 1 = PID
		 * col 2 = StartingBurstTime
		 * col 3 = EndingBurstTime
		 * col 4 = CompletionTime
		 */
		
		int min = 0;				//for storing the minimum burstTime value
		cpuTime = 0; 				//processes are in the queue at CPU time = 0		
		row = 0;
		index = 0;					
		
		//seek the min burstTime and run that process
		for(int i = 0; i < manager.length; i++) {
			
			//finding the next non-zero burstTime
			for(int c = 0; c < manager.length; c++) {
				if(manager[c].burstTime != 0) {
					min = manager[c].burstTime;
					break;
				}
			}
			
			//find the min burstTime
			for(int x = 0; x < manager.length; x++) {
				if(manager[x].burstTime <= min && manager[x].burstTime != 0) {
					min = manager[x].burstTime;
					index = x;
				}
			}
			
			outputData[row][0] = cpuTime;						//record cpuTime
			outputData[row][1] = manager[index].pid;			//record pid
			outputData[row][2] = manager[index].burstTime;		//record StartingBurstTime
			cpuTime = cpuTime + manager[index].burstTime;		//add the full burstTime to cpuTime
			manager[index].run(manager[index].burstTime);		//run for the full burstTime
			outputData[row][3] = manager[index].burstTime;		//record EndingBurstTime, which will be 0
			
			if(manager[index].burstTime == 0) {					//the process has completed
				outputData[row][4] = cpuTime;					//record CompletionTime
			}
			
			cpuTime = cpuTime + SWAP_TIME;						//add the swap time to the CPU
			row++;
		}
		
		//printMatrix(outputData);
		return outputData;
	}
	
	/**
	 * Round Robin
	 * @param timeQuantum
	 */
	public int[][] RoundRobin(int timeQuantum) {
		//System.out.println("\n\n***Round Robin " + timeQuantum);
		
		manager = new Process[processManager.length];
		for(int i = 0; i < manager.length; i++) {
			manager[i] = new Process(processManager[i].pid, processManager[i].burstTime, processManager[i].priority);
		}
		
		outputData = new int[650][5];
		/*
		 * col 0 = cpuTime
		 * col 1 = PID
		 * col 2 = StartingBurstTime
		 * col 3 = EndingBurstTime
		 * col 4 = CompletionTime
		 */
		
		cpuTime = 0; 				//processes are in the queue at CPU time = 0
		row = 0;
		index = 0;
		timeElapsed = 0;
		completionCount = 0;
		
		while(completionCount != manager.length)	{
			outputData[row][0] = cpuTime;						//record cpuTime
			outputData[row][1] = manager[index].pid;			//record pid
			outputData[row][2] = manager[index].burstTime;		//record StartingBurstTime
			timeElapsed = manager[index].run(timeQuantum); 		//run process and save timeElapsed
			
			if(manager[index].burstTime == 0) {					//process completed
				outputData[row][3] = 0;							//record EndingBurstTime, if process completed
				cpuTime = cpuTime + timeElapsed;				//add timeElapsed to cpuTime
				outputData[row][4] = cpuTime;					//record CompletionTime
				completionCount++;
			} else {
				cpuTime = cpuTime + timeElapsed;				//add timeElapsed to cpuTime
				outputData[row][3] = manager[index].burstTime;	//record EndingBurstTime, if process did not complete
			}
			
			//choose next index w/ burstTime > 0
			int tmp = index;
			for(int i = 0; i < manager.length; i++) {
				index++;

				if(index == manager.length)	{			//reached max index, wrap around
					index = 0;
				}
				
				//if there is only one process left, do not add SWAP_TIME to cpuTime
				if(manager[index].burstTime > 0) {
					if(index == tmp) {
						break;
					} else {
						cpuTime = cpuTime + SWAP_TIME;
						break;						
					}
				}
			}
			
			row++;
		}
		
		//printMatrix(outputData);
		return outputData;
	}
	
	/**
	 * Lottery
	 * @param timeQuantum
	 */
	public int[][] Lottery(int timeQuantum) {
		//System.out.println("\n\n***Lottery " + timeQuantum);
		
		Random random = new Random();
		
		
		//manually clone the processManager array because .clone() doesn't work for an array of objects
		Process[] manager = new Process[processManager.length];
		for(int i = 0; i < manager.length; i++) {
			manager[i] = new Process(processManager[i].pid, processManager[i].burstTime, processManager[i].priority);
		}
		
		
		outputData = new int[400][5];
		/*
		 * col 0 = cpuTime
		 * col 1 = PID
		 * col 2 = StartingBurstTime
		 * col 3 = EndingBurstTime
		 * col 4 = CompletionTime
		 */
		
		Process tmpProcess;			//for swapping processes in manager
		int prioritySum = 0;		//stores the sum of the priorities of the incomplete processes
		int comparisonSum = 0;		//used to find the index corresponding to the lottery value
		int lotteryValue;			//to store the randomly selected lottery value
		int tmppid = -1;			//used to check if a process swap occurred
		int tmppid1 = -1;			// " 		" 
		cpuTime = 0; 				//processes are in the queue at CPU time = 0
		row = 0;				
		index = 0;
		timeElapsed = 0;
		completionCount = 0;
		timeElapsed = 0;
		
		while(completionCount != manager.length) {
			
			//sum the remaining priorities and generate a random number between them
			prioritySum = 0;
			for(int i = 0; i < manager.length; i++)	{
				prioritySum = prioritySum + manager[i].priority;
			}			
			
			lotteryValue = random.nextInt(prioritySum);			//choose the next random lottery value
			
			tmppid = manager[index].pid;						//getting last pid
			
			//add the priorities one at a time, and then compare that to the random number. Select the index
			index = 0;
			comparisonSum = manager[index].priority;
			while (lotteryValue - comparisonSum > 0) {
				index++;
				comparisonSum = comparisonSum + manager[index].priority;
			}

			tmppid1 = manager[index].pid;						//getting new pid
			
			if(tmppid != tmppid1) {			//add SWAP_TIME only if there it switched processes
				cpuTime = cpuTime + SWAP_TIME;
			}
			
			//run the process for the time quantum and output data
			//if a process completes, swap out with another incomplete process. 
			outputData[row][0] = cpuTime;						//record cpuTime
			outputData[row][1] = manager[index].pid;			//record pid
			outputData[row][2] = manager[index].burstTime;		//record StartingBurstTime
			timeElapsed = manager[index].run(timeQuantum); 		//run process and save timeElapsed
			
			if(manager[index].burstTime == 0) {					//process completed
				outputData[row][3] = 0;							//record EndingBurstTime
				cpuTime = cpuTime + timeElapsed;				//add timeElapsed to cpuTime
				outputData[row][4] = cpuTime;					//record CompletionTime
				manager[index].priority = 0;					//setting the process' priority to 0 so it does not interfere with the lottery selection
				
				//swap an incomplete process in for the completed one
				//keep completed processes at the end of the array
				tmpProcess = manager[index];
				for(int i = manager.length - 1; i > -1; i--) {
					if(manager[i].burstTime != 0) {
						if(i < index) {
							break;
						}
						manager[index] = manager[i];
						manager[i] = tmpProcess;
						break;
					}
				}
				
				completionCount++;
			} else {
				cpuTime = cpuTime + timeElapsed;				//add timeElapsed to cpuTime
				outputData[row][3] = manager[index].burstTime;	//record EndingBurstTime
			}
			
			row++;	
		}
		
		//printMatrix(outputData);
		return outputData;
	}
	
	public void printMatrix(int[][] matrix) {
		for(int i = 0; i < matrix.length; i++) {
			for(int j = 0; j < matrix[i].length; j++) {
				System.out.print(matrix[i][j] + "          ");
			}
			System.out.println("");
		}
	}
}
