import utils.SLF4J;
import utils.XMLParser;

import javax.xml.stream.XMLStreamConstants;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.function.Predicate;

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
        return projects.stream().filter(e -> e.getEndDate() != null).max(comparator).get();
    }

    /**
     * calculates the total budget for assigned employees across all projects and employees in the system
     * based on the registration of committed hours per day per employee,
     * the number of working days in each project
     * and the hourly rate of each employee
     * @return
     */
    public int calculateTotalManpowerBudget() {
        // TODO
        return 0;
    }

    /**
     * finds the employees that are assigned to the highest number of different projects
     * (if multiple employees are assigned to the same highest number of projects,
     * all these employees are returned in the set)
     * @return
     */
    public Set<Employee> calculateMostInvolvedEmployees() {
        // TODO
        return null;
    }

    /**
     * Calculates an overview of total managed budget per employee that complies with the filter predicate
     * The total managed budget of an employee is the sum of all man power budgets of all projects
     * that are being managed by this employee
     * @param filter
     * @return
     */
    public Map<Employee,Integer> calculateManagedBudgetOverview(Predicate<Employee> filter) {
        // TODO
        return null;
    }

    /**
     * Calculates and overview of total monthly spends across all projects in the system
     * The monthly spend of a single project is the accumulated manpower cost of all employees assigned to the
     * project across all working days in the month.
     * @return
     */
    public Map<Month,Integer> calculateCumulativeMonthlySpends() {
        // TODO
        return null;

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
            manager.getManagedProjects().add(project);
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
