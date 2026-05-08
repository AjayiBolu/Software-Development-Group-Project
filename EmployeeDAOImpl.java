package com.companyz.ems.dao;

import com.companyz.ems.db.DatabaseConnection;
import com.companyz.ems.entity.Employee;
import com.companyz.ems.entity.Payroll;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EmployeeDAOImpl implements IEmployeeDAO {

    private final Connection conn;

    public EmployeeDAOImpl() {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    private static final String EMP_SELECT =
        "SELECT e.empid, e.Fname, e.Lname, e.email, e.HireDate, e.Salary, e.SSN, e.addressID, " +
        "       a.DOB, a.mobile_phone, a.emergency_contact_name, a.emergency_contact_phone, " +
        "       d.Name AS division_name, j.job_title AS title " +
        "FROM employees e " +
        "LEFT JOIN addresses a ON e.addressID = a.addrID " +
        "LEFT JOIN employee_division ed ON e.empid = ed.empid " +
        "LEFT JOIN division d ON ed.div_ID = d.ID " +
        "LEFT JOIN employee_job_titles ej ON e.empid = ej.empid " +
        "LEFT JOIN job_titles j ON ej.job_title_id = j.job_title_id ";

    private Employee mapRow(ResultSet rs) throws SQLException {
        Employee e = new Employee();
        e.setEmpID(rs.getInt("empid"));
        e.setFirstName(rs.getString("Fname"));
        e.setLastName(rs.getString("Lname"));
        e.setEmail(rs.getString("email"));
        Date hd = rs.getDate("HireDate");
        e.setHireDate(hd == null ? null : hd.toLocalDate());
        e.setSalary(rs.getDouble("Salary"));
        e.setSsn(rs.getString("SSN"));
        int addr = rs.getInt("addressID");
        e.setAddressID(rs.wasNull() ? null : addr);
        Date dob = rs.getDate("DOB");
        e.setDob(dob == null ? null : dob.toLocalDate());
        e.setMobilePhone(rs.getString("mobile_phone"));
        e.setEmergencyContactName(rs.getString("emergency_contact_name"));
        e.setEmergencyContactPhone(rs.getString("emergency_contact_phone"));
        e.setDivisionName(rs.getString("division_name"));
        e.setJobTitle(rs.getString("title"));
        return e;
    }

    @Override
    public Employee findById(int empID) {
        String sql = EMP_SELECT + "WHERE e.empid = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, empID);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("findById failed: " + ex.getMessage(), ex);
        }
    }

    @Override
    public List<Employee> findByLastName(String last) {
        String sql = EMP_SELECT + "WHERE e.Lname LIKE ? ORDER BY e.Lname, e.Fname";
        List<Employee> out = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + last + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapRow(rs));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("findByLastName failed: " + ex.getMessage(), ex);
        }
        return out;
    }

    @Override
    public Employee findBySSN(String ssn) {
        String sql = EMP_SELECT + "WHERE e.SSN = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ssn);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("findBySSN failed: " + ex.getMessage(), ex);
        }
    }

    @Override
    public List<Employee> findAll() {
        String sql = EMP_SELECT + "ORDER BY e.empid";
        List<Employee> out = new ArrayList<>();
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) out.add(mapRow(rs));
        } catch (SQLException ex) {
            throw new RuntimeException("findAll failed: " + ex.getMessage(), ex);
        }
        return out;
    }

    @Override
    public int insert(Employee e, int divisionID, int jobTitleID,
                      String street, int cityID, int stateID, String zip,
                      LocalDate dob, String mobile,
                      String emergencyName, String emergencyPhone) {

        try {
            conn.setAutoCommit(false);
            int addrID;
            String sqlA = "INSERT INTO addresses(street, cityID, stateID, zip, DOB, mobile_phone, " +
                          "emergency_contact_name, emergency_contact_phone) VALUES (?,?,?,?,?,?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlA, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, street);
                ps.setInt(2, cityID);
                ps.setInt(3, stateID);
                ps.setString(4, zip);
                ps.setDate(5, dob == null ? null : Date.valueOf(dob));
                ps.setString(6, mobile);
                ps.setString(7, emergencyName);
                ps.setString(8, emergencyPhone);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    addrID = keys.getInt(1);
                }
            }

            int empID;
            String sqlE = "INSERT INTO employees(Fname, Lname, email, HireDate, Salary, SSN, addressID) " +
                          "VALUES (?,?,?,?,?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlE, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, e.getFirstName());
                ps.setString(2, e.getLastName());
                ps.setString(3, e.getEmail());
                ps.setDate(4, e.getHireDate() == null ? null : Date.valueOf(e.getHireDate()));
                ps.setDouble(5, e.getSalary());
                ps.setString(6, e.getSsn());
                ps.setInt(7, addrID);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    empID = keys.getInt(1);
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO employee_division(empid, div_ID) VALUES (?,?)")) {
                ps.setInt(1, empID);
                ps.setInt(2, divisionID);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO employee_job_titles(empid, job_title_id) VALUES (?,?)")) {
                ps.setInt(1, empID);
                ps.setInt(2, jobTitleID);
                ps.executeUpdate();
            }

            conn.commit();
            return empID;
        } catch (SQLException ex) {
            try { conn.rollback(); } catch (SQLException ignore) {}
            throw new RuntimeException("insert failed: " + ex.getMessage(), ex);
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException ignore) {}
        }
    }

    @Override
    public boolean update(Employee e) {
        String sql = "UPDATE employees SET Fname=?, Lname=?, email=?, HireDate=?, Salary=?, SSN=? WHERE empid=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, e.getFirstName());
            ps.setString(2, e.getLastName());
            ps.setString(3, e.getEmail());
            ps.setDate(4, e.getHireDate() == null ? null : Date.valueOf(e.getHireDate()));
            ps.setDouble(5, e.getSalary());
            ps.setString(6, e.getSsn());
            ps.setInt(7, e.getEmpID());
            return ps.executeUpdate() == 1;
        } catch (SQLException ex) {
            throw new RuntimeException("update failed: " + ex.getMessage(), ex);
        }
    }

    @Override
    public boolean delete(int empID) {

        String sql = "DELETE FROM employees WHERE empid = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, empID);
            return ps.executeUpdate() == 1;
        } catch (SQLException ex) {
            throw new RuntimeException("delete failed: " + ex.getMessage(), ex);
        }
    }

    @Override
    public int updateSalaryBelowThreshold(double threshold, double pctIncrease) {

        String sql = "UPDATE employees SET Salary = Salary * (1 + (?/100)) WHERE Salary < ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, pctIncrease);
            ps.setDouble(2, threshold);
            return ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("updateSalaryBelowThreshold failed: " + ex.getMessage(), ex);
        }
    }

    @Override
    public int updateSalaryInRange(double minSalary, double maxSalary, double pctIncrease) {
        String sql = "UPDATE employees SET Salary = Salary * (1 + (?/100)) WHERE Salary BETWEEN ? AND ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, pctIncrease);
            ps.setDouble(2, minSalary);
            ps.setDouble(3, maxSalary);
            return ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("updateSalaryInRange failed: " + ex.getMessage(), ex);
        }
    }

    @Override
    public List<Payroll> payHistoryFor(int empID) {
        System.out.println("[DEBUG v4] payHistoryFor called for empID=" + empID);
        String sql =
            "SELECT CAST(payID AS CHAR) AS c1, " +
            "DATE_FORMAT(pay_date, '%Y-%m-%d') AS c2, " +
            "CAST(ROUND(earnings, 2) AS CHAR) AS c3, " +
            "CAST(ROUND(fed_tax, 2) AS CHAR) AS c4, " +
            "CAST(ROUND(fed_med, 2) AS CHAR) AS c5, " +
            "CAST(ROUND(fed_SS, 2) AS CHAR) AS c6, " +
            "CAST(ROUND(state_tax, 2) AS CHAR) AS c7, " +
            "CAST(ROUND(retire_401k, 2) AS CHAR) AS c8, " +
            "CAST(ROUND(health_care, 2) AS CHAR) AS c9, " +
            "CAST(empid AS CHAR) AS c10 " +
            "FROM payroll WHERE empid = ? ORDER BY pay_date DESC";
        List<Payroll> out = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, empID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Payroll p = new Payroll();
                    p.setPayID(Integer.parseInt(rs.getString("c1")));
                    String dateStr = rs.getString("c2");
                    p.setPayDate(dateStr == null ? null : LocalDate.parse(dateStr));
                    p.setEarnings(parseDbl(rs.getString("c3")));
                    p.setFedTax(parseDbl(rs.getString("c4")));
                    p.setFedMed(parseDbl(rs.getString("c5")));
                    p.setFedSS(parseDbl(rs.getString("c6")));
                    p.setStateTax(parseDbl(rs.getString("c7")));
                    p.setRetire401k(parseDbl(rs.getString("c8")));
                    p.setHealthCare(parseDbl(rs.getString("c9")));
                    p.setEmpID(Integer.parseInt(rs.getString("c10")));
                    out.add(p);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("payHistoryFor failed: " + ex.getMessage(), ex);
        }
        System.out.println("[DEBUG v4] returning " + out.size() + " rows");
        return out;
    }

    private static double parseDbl(String s) {
        if (s == null || s.isEmpty()) return 0.0;
        return Double.parseDouble(s.replace(",", ""));
    }

    @Override
    public Map<String, Double> totalPayByJobTitle(int month, int year) {
        String sql =
            "SELECT j.job_title, SUM(p.earnings) AS total " +
            "FROM payroll p " +
            "JOIN employee_job_titles ej ON p.empid = ej.empid " +
            "JOIN job_titles j ON ej.job_title_id = j.job_title_id " +
            "WHERE MONTH(p.pay_date) = ? AND YEAR(p.pay_date) = ? " +
            "GROUP BY j.job_title ORDER BY total DESC";
        Map<String, Double> out = new LinkedHashMap<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, month);
            ps.setInt(2, year);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.put(rs.getString("job_title"), rs.getDouble("total"));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("totalPayByJobTitle failed: " + ex.getMessage(), ex);
        }
        return out;
    }

    @Override
    public Map<String, Double> totalPayByDivision(int month, int year) {
        String sql =
            "SELECT d.Name, SUM(p.earnings) AS total " +
            "FROM payroll p " +
            "JOIN employee_division ed ON p.empid = ed.empid " +
            "JOIN division d ON ed.div_ID = d.ID " +
            "WHERE MONTH(p.pay_date) = ? AND YEAR(p.pay_date) = ? " +
            "GROUP BY d.Name ORDER BY total DESC";
        Map<String, Double> out = new LinkedHashMap<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, month);
            ps.setInt(2, year);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.put(rs.getString("Name"), rs.getDouble("total"));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("totalPayByDivision failed: " + ex.getMessage(), ex);
        }
        return out;
    }

    @Override
    public List<Employee> newHiresBetween(LocalDate start, LocalDate end) {
        String sql = EMP_SELECT + "WHERE e.HireDate BETWEEN ? AND ? ORDER BY e.HireDate ASC";
        List<Employee> out = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapRow(rs));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("newHiresBetween failed: " + ex.getMessage(), ex);
        }
        return out;
    }
}
