import com.sun.security.jgss.GSSUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Arrays;

public class DeadlockSimulator {
    private final Map<String, String> resources;        // Maps resource IDs to process IDs holding them
    private final Map<String, Set<String>> waitGraph;   // Tracks circular wait dependencies
    private int completedProcesses = 0;                 // Track number of completed processes (throughput)
    private long startTime;                             // Start time for performance measurement
    private long totalResourceUtilizationTime = 0;      // Total time resources were held by processes
    private int resourceUtilizationCount = 0;           // Counts resource usage instances

    public DeadlockSimulator(int totalResources) {
        this.resources = new HashMap<>();
        this.waitGraph = new HashMap<>();
    }

    // Start tracking execution time
    public void startSimulation() {
        startTime = System.currentTimeMillis();
    }

    // Stop tracking execution time and display performance metrics
    public void endSimulation() {
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        System.out.println("Performance Metrics:");
        System.out.println("Execution Time (ms): " + executionTime);
        System.out.println("System Throughput (Completed Processes): " + completedProcesses);
        System.out.println("Average Resource Utilization Time (ms): " +
                (resourceUtilizationCount == 0 ? 0 : totalResourceUtilizationTime / resourceUtilizationCount));
    }

    // Simulate mutual exclusion with performance metrics tracking
    public boolean mutualExclusion(String resourceId, String processId) {
        long start = System.currentTimeMillis();

        if (resources.containsKey(resourceId) && !resources.get(resourceId).equals(processId)) {
            System.out.println("Process " + processId + " is blocked, as Process "
                    + resources.get(resourceId) + " is using Resource " + resourceId + ".");
            return false;
        } else {
            resources.put(resourceId, processId);
            System.out.println("Process " + processId + " now holds Resource " + resourceId + ".");
            completedProcesses++;  // Increment throughput as the process completes
            recordResourceUtilization(start);
            return true;
        }
    }

    // Method to handle mutual exclusion deadlock by allowing preemption based on priority
    public void resolveMutualExclusion(String resourceId, String requestingProcess) {
        if (resources.containsKey(resourceId)) {
            String currentProcess = resources.get(resourceId);
            System.out.println("Process " + currentProcess + " releases Resource " + resourceId
                    + " due to preemption for Process " + requestingProcess + ".");
            resources.remove(resourceId);
        }
        mutualExclusion(resourceId, requestingProcess);
    }

    // Simulate hold and wait with performance metrics tracking
    public boolean holdAndWait(String processId, Set<String> requiredResources) {
        long start = System.currentTimeMillis();

        for (String resourceId : requiredResources) {
            if (resources.containsKey(resourceId) && !resources.get(resourceId).equals(processId)) {
                System.out.println("Process " + processId + " is waiting for Resource "
                        + resourceId + ", which is held by Process " + resources.get(resourceId) + ".");
                return false;
            }
        }
        for (String resourceId : requiredResources) {
            resources.put(resourceId, processId);
        }
        System.out.println("Process " + processId + " is holding resources " + requiredResources + ".");
        completedProcesses++;
        recordResourceUtilization(start);
        return true;
    }

    // Method to handle hold and wait deadlock by releasing resources if not all are available
    public void resolveHoldAndWait(String processId) {
        System.out.println("Process " + processId + " releases all held resources to avoid hold-and-wait deadlock.");
        resources.values().removeIf(value -> value.equals(processId)); 
    }

    // Simulate no preemption with performance metrics tracking
    public boolean noPreemption(String resourceId, String processId) {
        long start = System.currentTimeMillis();

        if (resources.containsKey(resourceId) && !resources.get(resourceId).equals(processId)) {
            System.out.println("Process " + processId + " cannot preempt Resource " + resourceId
                    + " from Process " + resources.get(resourceId) + ".");
            return false;
        } else {
            resources.put(resourceId, processId);
            System.out.println("Process " + processId + " now holds Resource " + resourceId + ".");
            completedProcesses++;
            recordResourceUtilization(start);
            return true;
        }
    }

    // Method to handle no preemption by forcibly releasing a resource
    public void resolveNoPreemption(String resourceId, String processId) {
        if (resources.containsKey(resourceId)) {
            String currentProcess = resources.get(resourceId);
            System.out.println("Process " + currentProcess + " forcibly releases Resource " + resourceId
                    + " for Process " + processId + " to avoid no-preemption deadlock.");
            resources.remove(resourceId);
        }
        noPreemption(resourceId, processId);
    }

    // Simulate circular wait and detect if there is a circular dependency in the wait graph
    public boolean circularWait() {
        Set<String> visited = new HashSet<>();
        Set<String> stack = new HashSet<>();

        for (String processId : waitGraph.keySet()) {
            if (detectCycle(processId, visited, stack)) {
                System.out.println("Circular wait detected!");
                return true;
            }
        }
        System.out.println("No circular wait detected.");
        return false;
    }

    // Method to handle circular wait by enforcing an ordering on resource acquisition
    public void resolveCircularWait() {
        System.out.println("Breaking circular wait by enforcing resource acquisition order.");
        waitGraph.clear();  // Clear the wait graph to prevent circular dependencies
    }

    // Helper method for circular wait: detect cycles in the wait graph
    private boolean detectCycle(String processId, Set<String> visited, Set<String> stack) {
        if (stack.contains(processId)) {
            return true;
        }
        if (visited.contains(processId)) {
            return false;
        }

        visited.add(processId);
        stack.add(processId);

        if (waitGraph.containsKey(processId)) {
            for (String dependentProcess : waitGraph.get(processId)) {
                if (detectCycle(dependentProcess, visited, stack)) {
                    return true;
                }
            }
        }

        stack.remove(processId);
        return false;
    }

    // Add a dependency to simulate one process waiting for another
    public void addWaitDependency(String processId, String waitingForProcessId) {
        waitGraph.computeIfAbsent(processId, k -> new HashSet<>()).add(waitingForProcessId);
        System.out.println("Process " + processId + " is now waiting for Process " + waitingForProcessId + ".");
    }

    // Record resource utilization time for each operation
    private void recordResourceUtilization(long startTime) {
        long endTime = System.currentTimeMillis();
        totalResourceUtilizationTime += (endTime - startTime);
        resourceUtilizationCount++;
    }

    // Improved acquireResource method usage and added cleanup at the end of the main method
    public boolean acquireResource(String resourceId, String processId) {
        long start = System.currentTimeMillis();
        if (resources.containsKey(resourceId) && !resources.get(resourceId).equals(processId)) {
            System.out.println("Process " + processId + " is blocked by Process " + resources.get(resourceId)
                    + " holding Resource " + resourceId);
            return false;
        }
        resources.put(resourceId, processId);
        completedProcesses++;
        recordResourceUtilization(start);
        System.out.println("Process " + processId + " now holds Resource " + resourceId + ".");
        return true;
    }

    public static void main(String[] args) {
        DeadlockSimulator sim = new DeadlockSimulator(5);
        sim.startSimulation();

        // Simulating Deadlock Scenarios
        sim.mutualExclusion("R1", "P1");
        sim.resolveMutualExclusion("R1", "P2");
        System.out.println();

        Set<String> resources = new HashSet<>(Arrays.asList("R1", "R2"));
        sim.holdAndWait("P2", resources);
        sim.resolveHoldAndWait("P2");
        System.out.println();

        sim.acquireResource("R2", "P2");
        sim.resolveNoPreemption("R2", "P3");
        System.out.println();

        sim.addWaitDependency("P1", "P2");
        sim.addWaitDependency("P2", "P1");
        sim.circularWait();
        sim.resolveCircularWait();
        System.out.println();

        sim.endSimulation();
    }
}








