import utils.SLF4J;
import utils.XMLParser;

import javax.xml.stream.XMLStreamConstants;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
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

        // TODO calculate and display statistics
        System.out.printf("1. The average hourly wage of all employees is %.2f%n", calculateAverageHourlyWage());
        System.out.printf("2. The longest project is '%s' with %d available working days%n", calculateLongestProject(), calculateLongestProject().getNumWorkingDays());
        System.out.printf("3. The follow employees have the broadest assignment in no less than %d different projects:%n%s%n", 12, calculateMostInvolvedEmployees().toString());
        System.out.printf("4. The total budget of committed project manpower is %d%n", calculateTotalManpowerBudget());
        System.out.printf("5. Below is an overview of total managed budget by junior employees {hourly wage <= 26}:%n%s%n", calculateManagedBudgetOverview(e -> e.getHourlyWage() <= 26));
        System.out.printf("6. Below is an overview of employees working at least 8 hours per day: %d%n", 1);
        System.out.printf("7. Below is an overview of cumulative monthly project spends: %n%s%n", calculateCumulativeMonthlySpends());
    }

    /**
     * calculates the average hourly wage of all known employees in this system
     * @return
     */
    public double calculateAverageHourlyWage() {
        double totalWage = employees.stream().mapToDouble(Employee::getHourlyWage).sum();
        return totalWage / employees.size();
    }

    /**
     * finds the project with the highest number of available working days.
     * (if more than one project with the highest number is found, any one is returned)
     * @return
     */
    public Project calculateLongestProject() {
        Comparator<Project> comparator = Comparator.comparing(Project::getEndDate);
        return projects.stream().filter(e -> e.getEndDate() != null).max(comparator).orElse(null);
    }

    /**
     * calculates the total budget for assigned employees across all projects and employees in the system
     * based on the registration of committed hours per day per employee,
     * the number of working days in each project
     * and the hourly rate of each employee
     * @return
     */
    public int calculateTotalManpowerBudget() {
        return employees.stream()
                .mapToInt(e -> e.getManagedProjects().stream()
                    .mapToInt(Project::calculateManpowerBudget).sum()
                ).sum();
    }


    /**
     * finds the employees that are assigned to the highest number of different projects
     * (if multiple employees are assigned to the same highest number of projects,
     * all these employees are returned in the set)
     * @return
     */
    public Set<Employee> calculateMostInvolvedEmployees() {
        return employees.stream()
                .filter(e -> e.getAssignedProjects().size() == employees.stream()
                    .mapToInt(i -> i.getAssignedProjects().size()).max().orElse(0))
                .collect(Collectors.toSet());
    }

    /**
     * Calculates an overview of total managed budget per employee that complies with the filter predicate
     * The total managed budget of an employee is the sum of all man power budgets of all projects
     * that are being managed by this employee
     * @param filter
     * @return
     */
    public Map<Employee,Integer> calculateManagedBudgetOverview(Predicate<Employee> filter) {
        return employees.stream().filter(filter).collect(Collectors.toMap(e -> e, Employee::calculateManagedBudget));
    }

    /**
     * Calculates and overview of total monthly spends across all projects in the system
     * The monthly spend of a single project is the accumulated manpower cost of all employees assigned to the
     * project across all working days in the month.
     * @return
     */
    public Map<Month,Integer> calculateCumulativeMonthlySpends() {
//        Map<Month, Integer> overviewMonthlySpends = new TreeMap<>();
//        for (Month m : Month.values()) {
//            this.projects.forEach(p -> {
//                long numDays = p.getWorkingDays().stream()
//                        .filter(d -> d.getMonth().equals(m))
//                        .count();
//                int totalBudget = (int) (numDays * p.getCommittedHoursPerDay().entrySet().stream()
//                    .mapToInt(e -> e.getValue() * e.getKey().getHourlyWage()).sum());
//                if (totalBudget > 0) {
//                    overviewMonthlySpends.merge(m, totalBudget, (current, adding) -> current + adding);
//                }
//            });
//        }
//        return overviewMonthlySpends;

        return Stream.of(Month.values())
                .map(m -> {
                    int i = projects.stream().mapToInt(p -> (int) ((p.getWorkingDays().stream()
                            .filter(d -> d.getMonth().equals(m))
                            .count()) * (p.getCommittedHoursPerDay().entrySet().stream()
                            .mapToInt(e -> e.getValue() * e.getKey().getHourlyWage())
                            .sum()))
                    ).sum();
                    return Collections.singletonMap(m, i);
                }).filter(e -> e.get(e.keySet().iterator().next()) > 0)
                .collect(Collectors.toMap(k -> k.keySet().iterator().next(), v -> v.get(v.keySet().iterator().next()), (o1, o2) -> o1, TreeMap::new));
    }

    /**
     * Returns a set containing all the employees that work at least fulltime for at least one day per week on a project.
     * @return
     */
    public Set<Employee> getFulltimeEmployees() {
        // TODO
        return Set.of();
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
