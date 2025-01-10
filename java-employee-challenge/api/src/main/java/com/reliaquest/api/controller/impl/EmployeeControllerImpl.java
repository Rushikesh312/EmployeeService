package com.reliaquest.api.controller.impl;

import com.reliaquest.api.controller.IEmployeeController;
import com.reliaquest.api.dto.EmployeeObj;
import com.reliaquest.api.exception.InvalidArgumentException;
import com.reliaquest.api.service.IEmployeeService;
import com.reliaquest.api.utility.ValidationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Objects;


@RestController
@Slf4j
public class EmployeeControllerImpl<Entity,Input> implements IEmployeeController {


    @Autowired
    IEmployeeService employeeService;

    @Override
    public ResponseEntity<List<EmployeeObj>> getAllEmployees() {
        // get employee list from server
        List<EmployeeObj> employeeObjList =  employeeService.getAllEmployees();
        log.info("Retrieving all employees");
        return ResponseEntity.ok(employeeObjList);
    }

    @Override
    public ResponseEntity<List<EmployeeObj>> getEmployeesByNameSearch(String searchString) {
        if(!StringUtils.hasLength(searchString)){
            throw new InvalidArgumentException(String.format("Please provide valid search string %s",searchString));
        }
        List<EmployeeObj> employeeObjList =  employeeService.getEmployeesByName(searchString);
        return ResponseEntity.ok(employeeObjList);
    }

    @Override
    public ResponseEntity<EmployeeObj> getEmployeeById(String id) {
        if(!ValidationUtils.isValidUUID(id) ){
            throw new InvalidArgumentException(String.format("Please provide valid ID uuid %s",id));
        }
        EmployeeObj employeeObjList =  employeeService.getEmployeesById(id);
        return ResponseEntity.ok(employeeObjList);
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        Integer highestSalary = employeeService.getHighestSalaryOfEmployees();
        return ResponseEntity.ok(highestSalary);
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        List<String> employeeObjList =  employeeService.getTopTenHighestEarningEmployeeNames();
        return ResponseEntity.ok(employeeObjList);
    }

    @Override
    public ResponseEntity<String> createEmployee(Object employeeInput) {
        //validate request for api parameters
        if(Objects.isNull(employeeInput) || !StringUtils.hasLength(String.valueOf(employeeInput))) {
            throw new InvalidArgumentException(String.format("Please provide valid input body %s",employeeInput));
        }
        try{
            return employeeService.createEmployee(employeeInput);
        }catch (Exception ex){

            throw ex;
        }


    }

    @Override
    public ResponseEntity<String> deleteEmployeeById(String id) {
        if(!ValidationUtils.isValidUUID(id) ){
            throw new InvalidArgumentException(String.format("Please provide valid ID uuid %s",id));
        }
        return employeeService.deleteEmployeeById(id);
    }
}
