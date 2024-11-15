import java.util.*;

class Process {
    public float arrivalTime;
    public float burstTime;
    public float startTime;
    public float completionTime;
    public float waitingTime;
    public float turnAroundTime;
    public int id;

    public Process(float arrivalTime, float burstTime, int id){
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.id = id;
    }
}

class CPUScheduler {
    public static void FCFS(Process[] processes) {
        Arrays.sort(processes, Comparator.comparingDouble(p -> p.arrivalTime));
        Queue<Process> queue = new LinkedList<>();
        int currentTime = 0;
        int processIndex = 0;

        while (processIndex < processes.length || !queue.isEmpty()) {
            // Add all processes that have arrived by the current time to the queue
            while (processIndex < processes.length && processes[processIndex].arrivalTime <= currentTime) {
                queue.add(processes[processIndex]);
                processIndex++;
            }

            if (!queue.isEmpty()) {
                Process currentProcess = queue.poll();

                // Set start time for the current process
                currentProcess.startTime = currentTime;

                // Update current time with the burst time of the current process
                currentTime += currentProcess.burstTime;

                // Calculate completion, turnaround, and waiting times
                currentProcess.completionTime = currentTime;
                currentProcess.turnAroundTime = currentProcess.completionTime - currentProcess.arrivalTime;
                currentProcess.waitingTime = currentProcess.turnAroundTime - currentProcess.burstTime;

                // Print process details including start time
                System.out.println("Process " + currentProcess.id +
                        " arrived at time " + currentProcess.arrivalTime +
                        " started at time " + currentProcess.startTime +
                        " and completed at time " + currentProcess.completionTime +
                        ", Turnaround Time: " + currentProcess.turnAroundTime +
                        ", Waiting Time: " + currentProcess.waitingTime);
            } else {
                // If no process is ready, increment time
                currentTime++;
            }
        }
        System.out.println("FCFS Algorithm Complete!\n");
    }

    public static void SJF(Process[] processes) {
        // Sort processes by arrival time initially
        Arrays.sort(processes, Comparator.comparingDouble(p -> p.arrivalTime));
        Queue<Process> queue = new LinkedList<>();
        int processIndex = 0;
        int currentTime = 0;

        while (processIndex < processes.length || !queue.isEmpty()) {
            // Add processes that have arrived by currentTime to the queue
            while (processIndex < processes.length && processes[processIndex].arrivalTime <= currentTime) {
                queue.add(processes[processIndex]);
                processIndex++;
            }

            if (!queue.isEmpty()) {
                // Find the process with the shortest burst time in the queue
                Process currentProcess = Collections.min(queue, Comparator.comparingDouble(p -> p.burstTime));
                queue.remove(currentProcess); // Remove the selected process from the queue

                // Set start time for the current process
                currentProcess.startTime = currentTime;

                // Update current time and calculate times for the selected process
                currentTime += currentProcess.burstTime;
                currentProcess.completionTime = currentTime;
                currentProcess.turnAroundTime = currentProcess.completionTime - currentProcess.arrivalTime;
                currentProcess.waitingTime = currentProcess.turnAroundTime - currentProcess.burstTime;

                // Print process details including start time
                System.out.println("Process " + currentProcess.id +
                        " arrived at time " + currentProcess.arrivalTime +
                        " started at time " + currentProcess.startTime +
                        " and completed at time " + currentProcess.completionTime +
                        ", Turnaround Time: " + currentProcess.turnAroundTime +
                        ", Waiting Time: " + currentProcess.waitingTime);
            } else {
                // If no process is ready, increment time to next arrival
                currentTime++;
            }
        }
        System.out.println("SJF Algorithm Complete!\n");
    }

    public static void main(String[] args) {
        Process p1 = new Process(1, 5, 1);
        Process p2 = new Process(4, 3, 2);
        Process p3 = new Process(5, 6, 3);
        Process p4 = new Process(2, 3, 4);
        Process p5 = new Process(3, 2, 5);

        Process[] processes = {p1, p2, p3, p4, p5};

        System.out.println("Here is an example of the FCFS algorithm:\n");
        CPUScheduler.FCFS(processes);

        System.out.println("Here is an example of the SJF algorithm:\n");
        CPUScheduler.SJF(processes);


        System.out.println("Randomly generating processes...\n\n");
        Random rand = new Random();
        Process r1 = new Process(rand.nextInt(9), rand.nextInt(9), 1);
        Process r2 = new Process(rand.nextInt(9), rand.nextInt(9), 2);
        Process r3 = new Process(rand.nextInt(9), rand.nextInt(9), 3);
        Process r4 = new Process(rand.nextInt(9), rand.nextInt(9), 4);
        Process r5 = new Process(rand.nextInt(9), rand.nextInt(9), 5);
        Process[] randProcesses = {r1, r2, r3, r4, r5};

        System.out.println("Here is an example of the FCFS algorithm:\n");
        CPUScheduler.FCFS(randProcesses);

        System.out.println("Here is an example of the SJF algorithm:\n");
        CPUScheduler.SJF(randProcesses);
    }
}


