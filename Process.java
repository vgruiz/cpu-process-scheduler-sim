
public class Process{
	int pid,  	//process ID
	burstTime, 	//amount of time needed to complete
	priority;	//priority value
	
	/**
	 * constructor
	 * @param pid
	 * @param burstTime
	 * @param priority
	 */
	public Process(int pid, int burstTime, int priority) {
		this.pid = pid;
		this.burstTime = burstTime;
		this.priority = priority;
	}
	
	/**
	 * deducts the burstTime by duration and accounts for burstTime potentially being 0
	 * @param duration
	 * @return
	 */
	public int run(int duration) {
		int timeElapsed;
		
		if(burstTime - duration <= 0) {
			timeElapsed = burstTime;
			burstTime = 0;
			return timeElapsed;
		} else {
			burstTime = burstTime - duration;
			return duration;			
		}
		
	}
	
	public Process clone() {
		Process newProcess = new Process(this.pid, this.burstTime, this.priority);
		return newProcess;
	}
}
