/*
 * Author: Victor Ruiz 010698870
 * Class: CS 431 - 02
 * Professor: Dominick Atanasio
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Main {
	static FileWriter fw;
	static PrintWriter pw;

	public static void main(String[] args) throws IOException {
		int[][] results;
		
		File file0 = new File("testdata1.txt");
		File file1 = new File("testdata2.txt");
		File file2 = new File("testdata3.txt");
		File file3 = new File("testdata4.txt");
		
		CentralProcessingUnit cpu = new CentralProcessingUnit();
		
		System.out.println("Test start...");
		
		//testdata1
		cpu.readProcesses(file0);
		results = cpu.FCFS();
		outputResults("FCFS-testdata1.csv", results);
		results = cpu.SJF();
		outputResults("SJF-testdata1.csv", results);
		results = cpu.RoundRobin(25);
		outputResults("Round_Robin_25-testdata1.csv", results);
		results = cpu.RoundRobin(50);
		outputResults("Round_Robin_50-testdata1.csv", results);
		results = cpu.Lottery(50);
		outputResults("Lottery_50-testdata1.csv", results);
		
		//testdata2
		cpu.readProcesses(file1);
		results = cpu.FCFS();
		outputResults("FCFS-testdata2.csv", results);
		results = cpu.SJF();
		outputResults("SJF-testdata2.csv", results);
		results = cpu.RoundRobin(25);
		outputResults("Round_Robin_25-testdata2.csv", results);
		results = cpu.RoundRobin(50);
		outputResults("Round_Robin_50-testdata2.csv", results);
		results = cpu.Lottery(50);
		outputResults("Lottery_50-testdata2.csv", results);
		
		//testdata3
		cpu.readProcesses(file2);
		results = cpu.FCFS();
		outputResults("FCFS-testdata3.csv", results);
		results = cpu.SJF();
		outputResults("SJF-testdata3.csv", results);
		results = cpu.RoundRobin(25);
		outputResults("Round_Robin_25-testdata3.csv", results);
		results = cpu.RoundRobin(50);
		outputResults("Round_Robin_50-testdata3.csv", results);
		results = cpu.Lottery(50);
		outputResults("Lottery_50-testdata3.csv", results);
		
		
		//testdata4
		cpu.readProcesses(file3);
		results = cpu.FCFS();
		outputResults("FCFS-testdata4.csv", results);
		results = cpu.SJF();
		outputResults("SJF-testdata4.csv", results);
		results = cpu.RoundRobin(25);
		outputResults("Round_Robin_25-testdata4.csv", results);
		results = cpu.RoundRobin(50);
		outputResults("Round_Robin_50-testdata4.csv", results);
		results = cpu.Lottery(50);
		outputResults("Lottery_50-testdata4.csv", results);
		
		System.out.println("Test complete.");
	}
	
	/**
	 * Taking the output filename and results matrix, and using the output() method to put the results in a .CSV file
	 * @param fileName
	 * @param results
	 * @throws IOException
	 */
	public static void outputResults(String fileName, int[][] results) throws IOException {
		fw = new FileWriter(fileName);
		pw = new PrintWriter(fw);
		output(results);
		pw.close();
	}
	
	/**
	 *	takes a matrix and outputs the contents to a .CSV
	 * @param data
	 */
	public static void output(int[][] data) {
		boolean eof;
		int totalCompletionTime = 0;
		int numProcesses = 0;
		
		pw.println("CpuTime,PID,StartingBurstTime,EndingBurstTime,CompletionTime");
		
		for(int i = 0; i < data.length; i++) {
			eof = false;
			for(int j = 0; j < data[i].length; j++) {
				pw.print(data[i][j] + ",");
				if(data[i][j] != 0) {
					eof = true;
				}
				
				if(j == 4 && data[i][j] != 0) {
					totalCompletionTime = totalCompletionTime + data[i][j];
					numProcesses++;
				}
			}
			if(!eof) {
				break;
			}
			pw.println("");
		}
		
		//System.out.println(totalCompletionTime + " " + numProcesses + " " + totalCompletionTime/numProcesses);
		pw.println("\n\"Average Turnaround Time\"," + totalCompletionTime/numProcesses);
	}
}