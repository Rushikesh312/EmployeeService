package com.reliaquest.api.service.impl;

import com.reliaquest.api.dto.EmployeeObj;
import com.reliaquest.api.service.IEmployeeService;

import com.reliaquest.api.utility.HttpUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.IntStream;


@Service
public class EmployeeServiceImpl implements IEmployeeService {

    private static final Logger log = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Value(value = "${server.url}")
    private String serverUrl;

    private List<EmployeeObj> EMPLOYEE_LIST_CACHE = new ArrayList<>();
    /**
     * @return
     */
    @Override
    public List<EmployeeObj> getAllEmployees() {
        //cache employees result to save server calls
        if(!EMPLOYEE_LIST_CACHE.isEmpty()){
            log.info("Found employee list in cache, returning from cache ");
            return EMPLOYEE_LIST_CACHE;
        }else{
            String employeeListResponse = getEmployeeListFromServer();
            EMPLOYEE_LIST_CACHE = new ArrayList<>(parseResponseAndGetEmployeeList(employeeListResponse));
            return EMPLOYEE_LIST_CACHE;
        }
    }



    /**
     * @param searchString
     * @return
     */
    @Override
    public List<EmployeeObj> getEmployeesByName(String searchString) {

        List<EmployeeObj> employeeListResponse = getAllEmployees();
        return  employeeListResponse.stream()
                .filter(employeeObj -> employeeObj.getEmployeeName().contains(searchString))
                .toList();
    }

    /**
     * @param id
     * @return
     */
    @Override
    public EmployeeObj getEmployeesById(String id) {
        List<EmployeeObj> employeeListResponse = getAllEmployees();
        //filter employees by names
        return  employeeListResponse.stream()
                .filter(employeeObj -> employeeObj.getId().equals(id))
                .findFirst()
                .orElseThrow(()-> new RuntimeException("Invalid id provided, employee not found with id"));

    }

    /**
     * @return
     */
    @Override
    public Integer getHighestSalaryOfEmployees() {
        List<EmployeeObj> employeeListResponse = getAllEmployees();
        log.info("Getting list of Employees :{} " ,employeeListResponse);
        //comparing employees based on salary and get highest salary employee
        Optional<EmployeeObj>  employee =  employeeListResponse.stream().max(Comparator.comparing(EmployeeObj::getEmployeeSalary));
        EmployeeObj highestEarningEmployee = employee.orElseThrow(()-> new RuntimeException("Failed to get employee, Please contact support."));
        return highestEarningEmployee.getEmployeeSalary();
    }

    /**
     * @return
     */
    @Override
    public List<String> getTopTenHighestEarningEmployeeNames() {
        List<EmployeeObj> employeeListResponse = getAllEmployees();
        //get top 10 employee names based on salary
        return employeeListResponse
                .stream()
                .sorted(Comparator.comparing(EmployeeObj::getEmployeeSalary))
                .limit(10)
                .map(EmployeeObj::getEmployeeName)
                .toList();
    }

    /**
     * @param employeeInput
     * @return ResponseEntity<String>
     */
    @Override
    public ResponseEntity<String> createEmployee(Object employeeInput) {
        RestTemplate restTemplate = HttpUtil.getAllAcceptRestTemplate();
        log.info("EmployeeInput :{}",employeeInput);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(serverUrl,employeeInput,String.class);
        JSONObject response = new JSONObject(responseEntity.getBody());
        EmployeeObj employeeObj = getEmployeeFromJson(response.getJSONObject("data"));
        //adding employee to cache
        EMPLOYEE_LIST_CACHE.add(employeeObj);
        log.info("Employee is created successfully");
        return responseEntity;

    }

    /**
     * @param id
     */
    @Override
    public ResponseEntity<String> deleteEmployeeById(String id) {
        EmployeeObj employeeObj = getEmployeesById(id);
        RestTemplate restTemplate = HttpUtil.getAllAcceptRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        // Request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", employeeObj.getEmployeeName());
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(serverUrl, HttpMethod.DELETE,requestEntity,String.class);
        if(responseEntity.getStatusCode().is2xxSuccessful()){
            Boolean removed = EMPLOYEE_LIST_CACHE.removeIf(employee -> employee.getId().equals(id));
            log.info("Employee removed from cache :{}, id:{}",removed , id);
        }
        log.info("Employee deleted successfully");
        return responseEntity;

    }

    private List<EmployeeObj> parseResponseAndGetEmployeeList(String response) {
        JSONObject data = new JSONObject(response);
        JSONArray jsonArray = data.getJSONArray("data");
        // Stream over JSONArray
        return IntStream.range(0, jsonArray.length())
                .mapToObj(jsonArray::getJSONObject)// Convert index to JSONObject
                .map(this::getEmployeeFromJson)
                .toList();
    }

    private EmployeeObj getEmployeeFromJson(JSONObject jsonObject) {
        return new EmployeeObj(jsonObject.getString("id"),
                jsonObject.getString("employee_name"),
                jsonObject.getInt("employee_salary"),
                jsonObject.getInt("employee_age"),
                jsonObject.getString("employee_title"),
                jsonObject.getString("employee_email"));
    }

    private String getEmployeeListFromServer() {
        RestTemplate restTemplate = HttpUtil.getAllAcceptRestTemplate();
        log.info("Making API call to server, to get EmployeeList ");
        return restTemplate.getForObject(serverUrl,String.class);
    }
}
