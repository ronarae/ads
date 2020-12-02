import utils.SLF4J;
import utils.XMLParser;

import javax.xml.stream.XMLStreamConstants;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PPS {

    private static Random randomizer = new Random(06112020);

    private String name;                // the name of the planning system refers to its xml source file
    private int planningYear;                   // the year indicates the period of start and end dates of the projects
    private Set<Employee> employees;
    private Set<Project> projects;

    @Override
    public String toString() {
        return String.format("PPS_e%d_p%d", this.employees.size(), this.projects.size());
    }

    private PPS() {
        name = "none";
        planningYear = 2000;
        projects = new TreeSet<>();
        employees = new TreeSet<>();
    }
    private PPS(String resourceName, int year) {
        this();
        name = resourceName;
        planningYear = year;
    }

    /**
     * Reports the statistics of the project planning year
     */
    public void printPlanningStatistics() {
        System.out.printf("\nProject Statistics of '%s' in the year %d\n", name, planningYear);
        if (employees == null || projects == null || employees.size() == 0 || projects.size() == 0) {
            System.out.println("No employees or projects have been set up...");
            return;
        }

        System.out.printf("%d employees have been assigned to %d projects:\n\n",
                employees.size(), projects.size());

        System.out.printf("1. The average hourly wage of all employees is %.2f%n", calculateAverageHourlyWage());
        System.out.printf("2. The longest project is '%s' with %d available working days%n", calculateLongestProject(), calculateLongestProject().getNumWorkingDays());
        System.out.printf("3. The follow employees have the broadest assignment in no less than %d different projects:%n%s%n", calculateHighestAmountOfAssignedProjects(), calculateMostInvolvedEmployees().toString());
        System.out.printf("4. The total budget of committed project manpower is %d%n", calculateTotalManpowerBudget());
        System.out.printf("5. Below is an overview of total managed budget by junior employees (hourly wage <= 26):%n%s%n", calculateManagedBudgetOverview(e -> e.getHourlyWage() <= 26));
        System.out.printf("6. Below is an overview of employees working at least 8 hours per day: %n%s%n", getFulltimeEmployees());
        System.out.printf("7. Below is an overview of cumulative monthly project spends: %n%s%n", calculateCumulativeMonthlySpends());
    }

    /**
     * calculates the average hourly wage of all known employees in this system
     * @return
     */
    public double calculateAverageHourlyWage() {
        //open stream op de employees set
        return employees.stream()
                //use the default collector averingdouble to calculate the average hourly wage
                .collect(Collectors.averagingDouble(Employee::getHourlyWage));
    }

    /**
     * finds the project with the highest number of available working days.
     * (if more than one project with the highest number is found, any one is returned)
     * @return
     */
    public Project calculateLongestProject() {
        //open een stream on the projects set
        return projects.stream()
                //get the highest end date (project with the highest number of working days)
                .max(
                        //use the default comparator on this field (in this case the integer comparator on the number of working days)
                        Comparator.comparing(Project::getNumWorkingDays)
                )
                //if the stream returns null return null, otherwise return the integer value
                .orElse(null);
    }

    /**
     * calculates the total budget for assigned employees across all projects and employees in the system
     * based on the registration of committed hours per day per employee,
     * the number of working days in each project
     * and the hourly rate of each employee
     * @return
     */
    public int calculateTotalManpowerBudget() {
        //open a stream on the employees set
        return employees.stream()
                //map all the values and make sure that the return type of the result is an integer
                .mapToInt(e ->
                        //open a stream of the set which contains all the projects that this employee is managing
                        e.getManagedProjects().stream()
                                //get the manpower budget of each managed project
                                .mapToInt(Project::calculateManpowerBudget)
                                //add the manpower budget together of the different projects
                                    .sum()
                )
                //add the total manpower budget of each manager together
                .sum();
    }


    /**
     * finds the employees that are assigned to the highest number of different projects
     * (if multiple employees are assigned to the same highest number of projects,
     * all these employees are returned in the set)
     * @return
     */
    public Set<Employee> calculateMostInvolvedEmployees() {
        //open a stream on the employees set
        return employees.stream()
                //remove alll the items from the stream where the amount of assigned projects is less than the highest amount of assigned projects
                .filter(e ->
                        e.getAssignedProjects().size() == calculateHighestAmountOfAssignedProjects()
                )
                //convert the result list to a set
                .collect(Collectors.toSet());
    }

    /**
     * finds the highest amount of projects that are assigned to a single employee
     * @return
     */
    private int calculateHighestAmountOfAssignedProjects() {
        //open a stream on the employees set
        return employees.stream()
                //get from each employee the amount of assigned projects
                .mapToInt(
                        i -> i.getAssignedProjects().size()
                )
                //get the maximum amount of assigned projects from the employees
                .max()
                //return the maximum amount of assigned projects or return null
                .orElse(0);
    }

    /**
     * Calculates an overview of total managed budget per employee that complies with the filter predicate
     * The total managed budget of an employee is the sum of all man power budgets of all projects
     * that are being managed by this employee
     * @param filter
     * @return
     */
    public Map<Employee,Integer> calculateManagedBudgetOverview(Predicate<Employee> filter) {
        //open a stream on the employees set
        return employees.stream()
                //filter all the employees with the given predicate of the type employee
                .filter(filter)
                //collect the result into a map
                .collect(Collectors.toMap(
                        //add the employee to the key part of the map
                        e -> e,
                        //add the amount of managed budget to the value part of the map
                        Employee::calculateManagedBudget
                ));
    }

    /**
     * Calculates and overview of total monthly spends across all projects in the system
     * The monthly spend of a single project is the accumulated manpower cost of all employees assigned to the
     * project across all working days in the month.
     * @return
     */
    public Map<Month,Integer> calculateCumulativeMonthlySpends() {
        //create a stream of the months enum
        return Stream.of(Month.values())
                //get a singletonmap with each month with the total amount of monthly spends of that month
                .map(
                        m -> Collections.singletonMap(
                                m,
                                projects.stream()
                                        .mapToInt(p -> (int) ((p.getWorkingDays().stream()
                                                                //get every working day of that month
                                                                .filter(d -> d.getMonth().equals(m))
                                                                //count the amount of working days in the month
                                                                .count()) *
                                                                //get the key value pairs of who committed how many hours on a project
                                                                (p.getCommittedHoursPerDay().entrySet().stream()
                                                                        //get the hourly wage of the employee
                                                                        .mapToInt(e -> e.getValue() * e.getKey().getHourlyWage())
                                                                        //add the hourly wages together
                                                                        .sum()
                                                                ))
                                        )
                                        //add the amount per day together to get the total amount for each working day
                                        .sum())
                )
                //remove all the months that have zero monthly spends
                .filter(e -> e.get(e.keySet().iterator().next()) > 0)
                //add the results to a treemap
                .collect(Collectors.toMap(
                        //add the month as a key
                        k -> k.keySet().iterator().next(),
                        //add the amount of monthly spends as value
                        v -> v.get(v.keySet().iterator().next()),
                        //if there is a duplicated key, just keep the first item
                        (o1, o2) -> o1,
                        //set the type of map to a treemap
                        TreeMap::new)
                );
    }

    /**
     * Returns a set containing all the employees that work at least fulltime for at least one day per week on a project.
     * @return
     */
    public Set<Employee> getFulltimeEmployees() {
        //open a stream of the employees set
        return employees.stream()
                //remove all the employees that don't have a day that they spend more than 8 hours on one project on one day.
                .filter(
                        e ->
                                //open a stream of the assigned projects set
                            e.getAssignedProjects().stream()
                                    //flatmap to filter to remove the projects that are not worked on by this employee
                                .flatMap(
                                        i -> i.getCommittedHoursPerDay().entrySet().stream()
                                            .filter(em -> em.getKey().getNumber() == e.getNumber())
                                )
                                    //the next action of the stream will be called on the value of the map
                                .mapToInt(
                                        Map.Entry::getValue
                                )
                                    //get the highest value of the amount of hours of the map
                                .max()
                                    //if exists get the value that was calculated by the max or get 0  check if that value is >= 8
                                    .orElse(0) >= 8
                        //collect the results inside a set
                        ).collect(Collectors.toSet());
    }

    public String getName() {
        return name;
    }

    /**
     * A builder helper class to compose a small PPS using method-chaining of builder methods
     */
    public static class Builder {
        PPS pps;

        public Builder() {
            pps = new PPS();
        }

        /**
         * Add another employee to the PPS being build
         * @param employee
         * @return
         */
        public Builder addEmployee(Employee employee) {
            pps.employees.add(employee);
            return this;
        }

        /**
         * Add another project to the PPS
         * register the specified manager as the manager of the new
         * @param project
         * @param manager
         * @return
         */
        public Builder addProject(Project project, Employee manager) {
            pps.projects.add(project);
            addEmployee(manager);
            Employee m = pps.getEmployees()
                    .stream().filter(e -> e.getNumber() == manager.getNumber())
                    .findFirst()
                    .get();
            m.getManagedProjects()
                    .add(pps.getProjects().stream()
                    .filter(e -> e.getCode() == project.getCode())
                    .findFirst()
                    .get());
            return this;
        }

        /**
         * Add a commitment to work hoursPerDay on the project that is identified by projectCode
         * for the employee who is identified by employeeNr
         * This commitment is added to any other commitment that the same employee already
         * has got registered on the same project,
         * @param projectCode
         * @param employeeNr
         * @param hoursPerDay
         * @return
         */
        public Builder addCommitment(String projectCode, int employeeNr, int hoursPerDay) {
            Project p = pps.projects.stream().filter(e -> e.getCode().equals(projectCode)).findFirst().get();
            Employee e = pps.employees.stream().filter(n -> n.getNumber() == employeeNr).findFirst().get();
            p.addCommitment(e, hoursPerDay);
            return this;
        }

        /**
         * Complete the PPS being build
         *
         * @return
         */
        public PPS build() {
            return pps;
        }
    }

    public Set<Project> getProjects() {
        return projects;
    }

    public Set<Employee> getEmployees() {
        return employees;
    }

    /**
     * Loads a complete configuration from an XML file
     *
     * @param resourceName the XML file name to be found in the resources folder
     * @return
     */
    public static PPS importFromXML(String resourceName) {
        XMLParser xmlParser = new XMLParser(resourceName);

        try {
            xmlParser.nextTag();
            xmlParser.require(XMLStreamConstants.START_ELEMENT, null, "projectPlanning");
            int year = xmlParser.getIntegerAttributeValue(null, "year", 2000);
            xmlParser.nextTag();

            PPS pps = new PPS(resourceName, year);

            Project.importProjectsFromXML(xmlParser, pps.projects);
            Employee.importEmployeesFromXML(xmlParser, pps.employees, pps.projects);

            return pps;

        } catch (Exception ex) {
            SLF4J.logException("XML error in '" + resourceName + "'", ex);
        }

        return null;
    }
}
