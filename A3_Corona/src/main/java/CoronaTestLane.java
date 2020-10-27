import java.time.LocalTime;
import java.util.*;

public class CoronaTestLane {

    private List<Patient> patients;     // all patients visiting the test lane today
    private List<Nurse> nurses;         // all nurses working at the test lane today
    private LocalTime openingTime;      // start time of sampling at the test lane today
    private LocalTime closingTime;      // latest time of passible arrivals of patients
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
        // TODO This priority queue needs a proper way of determining the priority for the patients
        Queue<Patient> waitingPatients = new PriorityQueue<>();

        // reset availability of the nurses
        for (Nurse nurse : nurses) {
            nurse.setAvailableAt(openingTime);
            nurse.setNumPatientsSampled(0);
            nurse.setTotalSamplingTime(0);
        }

        // maintain a queue of nurses ordered by earliest time of availability
        // TODO This priority queue needs a proper way of determining the next available nurse
        Queue<Nurse> availableNurses = new PriorityQueue<>();
        availableNurses.addAll(nurses);

        // ensure patients are processed in order of arrival
        // TODO Ensure that the patients are ordered by arrival time
//        patients.sort(...);

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

        // TODO calculate the aggregated statistics from the simulation
        //  i.e. time the work was finished
        //       average and maximum waiting times

    }

    /**
     * Report the statistics of the simulation
     */
    public void printSimulationResults() {
        System.out.println("Simulation results per nurse:");
        System.out.println("    Name: #Patients:    Avg. sample time: Workload:");

        // TODO report per nurse:
        //  numPatients,
        //  average sample time for taking the nose sample,
        //  and percentage of opening hours of the Test Lane actually spent on taking samples



        // TODO report the time all nurses had finished all sampling work

        // TODO report the maximum length of the queue at any time

        // TODO report average and maximum wait times for regular and priority patients (if any)
        System.out.printf("Wait times:        Average:  Maximum:\n");

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

        // TODO create, populate and return the result map


        return null;
    }

    public Map<Patient.Symptom, String> zipAreasWithHighestPatientPercentageBySymptom(Map<String, Integer> patientsByZipArea) {

        // TODO create, populate and return the result map



        return null;
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
