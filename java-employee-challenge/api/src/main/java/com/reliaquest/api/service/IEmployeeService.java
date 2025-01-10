package com.reliaquest.api.service;

import com.reliaquest.api.dto.EmployeeObj;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IEmployeeService {


    List<EmployeeObj> getAllEmployees();

    List<EmployeeObj> getEmployeesByName(String searchString);

    EmployeeObj getEmployeesById(String id);

    Integer getHighestSalaryOfEmployees();

    List<String> getTopTenHighestEarningEmployeeNames();

    ResponseEntity<String> createEmployee(Object employeeInput);

    ResponseEntity<String> deleteEmployeeById(String id);
}
