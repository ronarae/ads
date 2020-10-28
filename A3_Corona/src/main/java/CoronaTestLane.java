import com.sun.source.tree.Tree;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class CoronaTestLane {

    private List<Patient> patients;     // all patients visiting the test lane today
    private List<Nurse> nurses;         // all nurses working at the test lane today
    private LocalTime openingTime;      // start time of sampling at the test lane today
    private LocalTime closingTime;      // latest time of possible arrivals of patients
                                        // hereafter, nurses will continue work until the queue is empty
    // simulation statistics for reporting
    private int maxQueueLength;             // the maximum queue length of waiting patients at any time today
    private int maxRegularWaitTime;         // the maximum wait time of regular patients today
    private int maxPriorityWaitTime;        // the maximum wait time of priority patients today
    private double averageRegularWaitTime;  // the average wait time of regular patients today
    private double averagePriorityWaitTime; // the average wait time of priority patients today
    private LocalTime workFinished;         // the time when all nurses have finished work with no more waiting patients

    private Random randomizer;              // used for generation of test data and to produce reproducible simulation results

    /**
     * Instantiates a corona test line for a given day of work
     * @param openingTime       start time of sampling at the test lane today
     * @param closingTime       latest time of passible arrivals of patientss
     */
    public CoronaTestLane(LocalTime openingTime, LocalTime closingTime) {
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.workFinished = openingTime;
        this.randomizer = new Random(0);
        System.out.printf("\nCorona test lane simulation between %s and %s\n\n", openingTime, closingTime);
    }

    /**
     * Simulate a day at the Test Lane
     * @param numNurses         the number of nurses that shall be scheduled to work in parallel
     * @param numPatients       the number of patient profiles that shall be generated to visit the Test Lane today
     * @param priorityFraction  the fraction of patients that shall be given priority
     *                          and will be allowed to skip non-priority patients on the waiting queue
     * @param seed              used to initialize a randomizer to generate reproducible semi-random data
     */
    public void configure(int numNurses, int numPatients, double priorityFraction, long seed) {
        randomizer = new Random(seed);
        System.out.printf("Configuring test lane with %d nurse(s) and %d patients (%.0f%% priority); seed=%d.\n",
                numNurses, numPatients, 100*priorityFraction, seed);

        // Configure the nurses
        nurses = new ArrayList<>();
        for (int n = 0; n < numNurses; n++) {
            nurses.add( new Nurse("Nurse-" + (n+1), openingTime, randomizer));
        }

        // Generate the full list of patients that will be arriving at the test lane (and show a few)
        patients = new ArrayList<>();
        for (int p = 0; p < numPatients; p++) {
            patients.add( new Patient(openingTime, closingTime, priorityFraction, randomizer) );
        }

        // echo some patients for runtime confirmation
        if (patients.size() > 2) {
            System.out.printf("   a few patients: %s - %s - %s - ...\n", patients.get(0), patients.get(1), patients.get(2));
        }
    }

    /**
     * Simulate a day at the Test Lane and calculate the relevant statistics from this simulation
     */
    public void simulate() {

        System.out.printf("Simulating the sampling of %d patients by %d nurse(s).\n",
                patients.size(), nurses.size());

        // interleaved by nurses inviting patients from the waiting queue to have their sample taken from their nose...

        // maintain the patients queue by priority and arrival time
        Queue<Patient> waitingPatients = new PriorityQueue<>();


        // reset availability of the nurses
        for (Nurse nurse : nurses) {
            nurse.setAvailableAt(openingTime);
            nurse.setNumPatientsSampled(0);
            nurse.setTotalSamplingTime(0);
        }

        // maintain a queue of nurses ordered by earliest time of availability
        Queue<Nurse> availableNurses = new PriorityQueue<>();
        availableNurses.addAll(nurses);

        // ensure patients are processed in order of arrival
        patients.sort(new Patient.CompareOnArrivalTime());

        // track the max queuelength as part of the simulation
        maxQueueLength = 0;

        // determine the first available nurse
        Nurse nextAvailableNurse = availableNurses.poll();

        // process all patients in order of arrival at the Test Lane
        for (Patient patient: patients) {
            // let nurses handle patients on the queue, if any
            // until the time of the next available nurse is later than the patient who just arrived
            while (waitingPatients.size() > 0 && nextAvailableNurse.getAvailableAt().compareTo(patient.getArrivedAt()) <= 0) {
                // handle the next patient from the queue
                Patient nextPatient = waitingPatients.poll();

                LocalTime startTime = nextAvailableNurse.getAvailableAt().isAfter(nextPatient.getArrivedAt()) ?
                        nextAvailableNurse.getAvailableAt() :
                        nextPatient.getArrivedAt();
                nextAvailableNurse.samplePatient(nextPatient, startTime);

                // reorder the current nurse into the queue of nurses as per her next availability
                // (after completing the current patient)
                availableNurses.add(nextAvailableNurse);

                // get the next available nurse for handling of the next patient
                nextAvailableNurse = availableNurses.poll();
            }

            // add the patient that just arrived to the queue before letting the nurses proceed
            waitingPatients.add(patient);

            // keep track of the maximum queue length
            maxQueueLength = Integer.max(maxQueueLength, waitingPatients.size());
        }

        // process the remaining patients on the queue, same as above
        while (waitingPatients.size() > 0) {
            Patient nextPatient = waitingPatients.poll();
            LocalTime startTime = nextAvailableNurse.getAvailableAt().isAfter(nextPatient.getArrivedAt()) ?
                    nextAvailableNurse.getAvailableAt() :
                    nextPatient.getArrivedAt();
            nextAvailableNurse.samplePatient(nextPatient, startTime);
            availableNurses.add(nextAvailableNurse);
            nextAvailableNurse = availableNurses.poll();
        }

        // all patients are underway

        //calculate the time that the workday was over (including overtime)
        LocalTime timeWorkFinished = LocalTime.MIN;
        for (Nurse n : nurses) {
            if (n.getAvailableAt().compareTo(timeWorkFinished) > 0) timeWorkFinished = n.getAvailableAt();
        }
        workFinished = timeWorkFinished;

        //variables to keep track of the regular patients times
        long regularAverageWaitingTimeTotal = 0;
        long regularMaxWaitingTime = 0;
        int regularPatientCounter = 0;

        //variables to keep track of the priority patients times
        long priorityAverageWaitingTimeTotal = 0;
        long priorityMaxWaitingTime  = 0;
        int priorityPatientCounter = 0;

        //loop through all the patients
        for (Patient p : patients) {
            //check if the patient is a priority patient
            if (!p.isHasPriority()) {
                //add one to the regular patient counter
                regularPatientCounter++;
                //get the total time that the patient was waiting for
                long totalTime = p.getArrivedAt().until(p.getSampledAt(), ChronoUnit.SECONDS);
                //add the total time to the total total waiting time
                regularAverageWaitingTimeTotal += totalTime;
                //check if the total time is bigger than the last biggest total time if this is the case replace the max total time with this total time
                if (totalTime > regularMaxWaitingTime) regularMaxWaitingTime = totalTime;
            } else {
                //add one to the priority patient counter
                priorityPatientCounter++;
                //get the total time that the patient was waiting for
                long totalTime = p.getArrivedAt().until(p.getSampledAt(), ChronoUnit.SECONDS);
                //add the total time to the total total waiting time
                priorityAverageWaitingTimeTotal += totalTime;
                //check if the total time is bigger than the last biggest total time if this is the case replace the max total time with this total time
                if (totalTime > priorityMaxWaitingTime) priorityMaxWaitingTime = totalTime;
            }
        }

        //add the calculated values to the correct variables.
        averageRegularWaitTime = (double) regularAverageWaitingTimeTotal / regularPatientCounter;
        maxRegularWaitTime = (int) regularMaxWaitingTime;

        averagePriorityWaitTime = (double) priorityAverageWaitingTimeTotal / priorityPatientCounter;
        maxPriorityWaitTime = (int) priorityMaxWaitingTime;
    }

    /**
     * Report the statistics of the simulation
     */
    public void printSimulationResults() {
        System.out.println("Simulation results per nurse:");
        System.out.println("    Name: #Patients:    Avg. sample time: Workload:");

        for (Nurse n : nurses) {
            System.out.printf("%-9s%10d%20.2f%8d%%%n",
                    n.getName(),
                    n.getNumPatientsSampled(),
                    (double)n.getTotalSamplingTime() / (double)n.getNumPatientsSampled(),
                    (int)(((double)n.getTotalSamplingTime() / (double)(getOpeningTime().until(getClosingTime(), ChronoUnit.SECONDS))) * 100)
            );
        }


        System.out.println("Work finished at " + workFinished);

        System.out.println("Maximum patient queue length " + maxQueueLength);

        System.out.print("Wait times:        Average:  Maximum:\n");
        System.out.printf("Regular patients:%9.2f%10d%n", averageRegularWaitTime, maxRegularWaitTime);
        if (patients.stream().anyMatch(Patient::isHasPriority)) System.out.printf("Priority patients:%8.2f%10d%n", averagePriorityWaitTime, maxPriorityWaitTime);

        System.out.println("\n\n");
    }

    /**
     * Report the statistics of the patients
     */
    public void printPatientStatistics() {

        System.out.println("\nPatient counts by zip area:");
        Map<String, Integer> patientCounts = patientsByZipArea();
        System.out.println(patientsByZipArea());

        System.out.println("\nZip area with highest patient percentage per complaint:");
        Map<Patient.Symptom, String> zipAreasPerSymptom =
                zipAreasWithHighestPatientPercentageBySymptom(patientCounts);
        System.out.println(zipAreasPerSymptom);
    }

    /**
     * Calculate the number of patients per zip-area code (i.e. the digits of a zipcode)
     * @return  a map of patient counts per zip-area code
     */
    public Map<String, Integer> patientsByZipArea() {

        TreeMap<String, Integer> map = new TreeMap<>();
        for (Patient p : patients) {
            map.put(p.getZipCode().substring(0, 4), map.getOrDefault(p.getZipCode().substring(0, 4), 0) + 1);
        }
        return map;
    }

    public Map<Patient.Symptom, String> zipAreasWithHighestPatientPercentageBySymptom(Map<String, Integer> patientsByZipArea) {
        //Create a new treemap to store the final result
        TreeMap<Patient.Symptom, String> map = new TreeMap<>();

        //loop through all the different symptoms with a normal for loop so the index is available
        for (int i = 0; i < Patient.Symptom.values().length; i++) {
            //create a shortcut for the current symptom in the loop
            Patient.Symptom s = Patient.Symptom.values()[i];
            //create the variables that keep track of the highest percentage and the zip code
            String zip = "";
            double highestPercentage = 0.;
            //loop through the key value pairs that contain as key the zipcode and as value the count of patients
            for (Map.Entry<String, Integer> m : patientsByZipArea.entrySet()) {
                //initialize a counter to keep track of the number of patients with a specific symptom in a specific zipcode
                int symptomCounter = 0;
                //loop through all the patients
                for (Patient p : patients) {
                    //when the zipcode is equals to the currently counted zipcode and the patient has the currently looked for symptom add one to the symptom counter
                    if (p.getZipCode().substring(0, 4).equals(m.getKey()) && p.getSymptoms()[i]) symptomCounter++;
                }
                //calculate the percentage of the patients that has a specific symptom
                double percentage = (double)symptomCounter /  (double)m.getValue() * 100;
                //check if the percentage is higher than the highest percentage, when that is the case change the zip to the new zipcode and the highest percentage to the new percentage
                if (percentage > highestPercentage) {
                    zip = m.getKey();
                    highestPercentage = percentage;
                }
            }
            //put the key value pair of this symptom together with the zip code with the highest percentage
            map.put(s, zip);
        }
        //return the tree map
        return map;
    }

    public List<Patient> getPatients() {
        return patients;
    }

    public List<Nurse> getNurses() {
        return nurses;
    }

    public LocalTime getOpeningTime() {
        return openingTime;
    }

    public LocalTime getClosingTime() {
        return closingTime;
    }

    public int getMaxQueueLength() {
        return maxQueueLength;
    }

    public int getMaxRegularWaitTime() {
        return maxRegularWaitTime;
    }

    public int getMaxPriorityWaitTime() {
        return maxPriorityWaitTime;
    }

    public double getAverageRegularWaitTime() {
        return averageRegularWaitTime;
    }

    public double getAveragePriorityWaitTime() {
        return averagePriorityWaitTime;
    }

    public LocalTime getWorkFinished() {
        return workFinished;
    }
}
