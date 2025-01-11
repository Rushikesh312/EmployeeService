package com.reliaquest.api.controller.impl;

import com.reliaquest.api.ApiApplication;
import com.reliaquest.api.controller.IEmployeeController;
import com.reliaquest.api.dto.EmployeeObj;
import com.reliaquest.api.exception.InvalidArgumentException;
import com.reliaquest.api.service.IEmployeeService;
import com.reliaquest.api.service.impl.EmployeeServiceImpl;
import com.reliaquest.api.utility.StringConstants;
import com.reliaquest.api.utility.ValidationUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Objects;


@RestController

public class EmployeeControllerImpl<Entity,Input> implements IEmployeeController {


    private static final Logger log = LoggerFactory.getLogger(EmployeeControllerImpl.class);

    @Autowired
    IEmployeeService employeeService;

    @Override
    public ResponseEntity<List<EmployeeObj>> getAllEmployees() {
        // get employee list from server
        log.info("Request received to get employees ");
        List<EmployeeObj> employeeObjList =  employeeService.getAllEmployees();
        log.info("fetched all employees :{}",employeeObjList);
        return ResponseEntity.ok(employeeObjList);
    }

    @Override
    public ResponseEntity<List<EmployeeObj>> getEmployeesByNameSearch(String searchString) {
        log.info("Request received to get getEmployeesByNameSearch :{}" ,searchString);
        if(!StringUtils.hasLength(searchString)){
            throw new InvalidArgumentException(String.format(StringConstants.INVALID_PARAMETER,searchString));
        }
        List<EmployeeObj> employeeObjList =  employeeService.getEmployeesByName(searchString);
        log.info("fetched all employees with searchString :{}, result:{}",searchString,employeeObjList);
        return ResponseEntity.ok(employeeObjList);
    }

    @Override
    public ResponseEntity<EmployeeObj> getEmployeeById(String id) {
        log.info("Request received to get getEmployeeById :{}" ,id);
        if(!ValidationUtils.isValidUUID(id) ){
            throw new InvalidArgumentException(String.format(StringConstants.INVALID_UUID,id));
        }
        EmployeeObj employeeObjList =  employeeService.getEmployeesById(id);
        log.info("Request completed to get getEmployeeById :{} employee:{}" ,id , employeeObjList);
        return ResponseEntity.ok(employeeObjList);
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        log.info("Request received to get highest salary of employees ");
        Integer highestSalary = employeeService.getHighestSalaryOfEmployees();
        log.info("Highest Salary received :{} " ,highestSalary);
        return ResponseEntity.ok(highestSalary);
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {

        log.info("Request received to get top ten highest earning employees ");
        List<String> employeeObjList =  employeeService.getTopTenHighestEarningEmployeeNames();
        log.info("Received Highest Salary of employees names received :{} " ,employeeObjList);
        return ResponseEntity.ok(employeeObjList);
    }

    @Override
    public ResponseEntity<String> createEmployee(Object employeeInput) {
        //validate request for api parameters
        log.info("Request received to create employee :{}" ,employeeInput);
        if(Objects.isNull(employeeInput) || !StringUtils.hasLength(String.valueOf(employeeInput))) {
            throw new InvalidArgumentException(String.format(StringConstants.INVALID_PARAMETER,employeeInput));
        }
        return employeeService.createEmployee(employeeInput);
    }

    @Override
    public ResponseEntity<String> deleteEmployeeById(String id) {
        log.info("Request received to delete employee :{}",id);
        if(!ValidationUtils.isValidUUID(id) ){
            throw new InvalidArgumentException(String.format(StringConstants.INVALID_UUID,id));
        }
        return employeeService.deleteEmployeeById(id);
    }
}
