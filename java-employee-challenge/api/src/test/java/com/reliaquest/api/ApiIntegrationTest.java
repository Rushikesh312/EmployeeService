package com.reliaquest.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.dto.EmployeeObj;
import com.reliaquest.api.service.impl.EmployeeServiceImpl;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureMockMvc
@SpringBootTest
public class ApiIntegrationTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    EmployeeServiceImpl employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    public ApiIntegrationTest() {
        this.objectMapper = new ObjectMapper();
    }

    private static final Logger log = LoggerFactory.getLogger(ApiApplicationTest.class);


    @DisplayName("CreateEmployeeAndDeleteEmployee")
    @Test
    void createAndDeleteEmployee() throws Exception {
        log.info("Execution testing case of create employee with valid input");
        // Given
        JSONObject requestPayload = getValidRequestPayload();
        // create employee
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("")
                .contentType(MediaType.APPLICATION_JSON).content(requestPayload.toString())).andReturn();

        log.info("Result received as result:{}, status:{}",result,result.getResponse().getStatus());
        assertEquals(result.getResponse().getStatus(), HttpStatus.OK.value());
        assertNotNull(result.getResponse().getContentAsString());

        JSONObject responseData = new JSONObject(result.getResponse().getContentAsString());
        JSONObject employee = responseData.getJSONObject("data");

        validateEmployeeInformation(employee , requestPayload);
        // delete the dummy data created by test case in server
        deleteEmployee(employee.getString("id"));

    }

    private JSONObject getValidRequestPayload() {
        JSONObject requestPayload = new JSONObject();
        requestPayload.put("name","Rushikesh");
        requestPayload.put("age",26);
        requestPayload.put("salary",1000000);
        requestPayload.put("title","Senior Software Engineer");
        return requestPayload;
    }


    @DisplayName("CreateEmployeeWithInvalidAge")
    @Test
    void CreateEmployeeWithInvalidAge() throws Exception {
        log.info("Execution testing case of create employee with invalid age");
        // Given
        JSONObject data = new JSONObject();
        data.put("name","Rushikesh");
        data.put("age",14);
        data.put("salary",1000000);
        data.put("title","Senior Software Engineer");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("")
                .contentType(MediaType.APPLICATION_JSON).content(data.toString())).andReturn();

        log.info("Result received as :{} ,{}",result,result.getResponse().getStatus());
        assertEquals(result.getResponse().getStatus(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertNotNull(result.getResponse().getContentAsString());

    }

    @DisplayName("CreateEmployeeWithInvalidSalary")
    @Test
    void CreateEmployeeWithInvalidSalary() throws Exception {
        log.info("Execution testing case of create employee with invalid salary");
        // Given
        JSONObject data = new JSONObject();
        data.put("name","Rushikesh");
        data.put("age",26);
        data.put("salary",-1000000);
        data.put("title","Senior Software Engineer");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("")
                .contentType(MediaType.APPLICATION_JSON).content(data.toString())).andReturn();

        log.info("Result received as :{} ,{}",result,result.getResponse().getStatus());
        assertEquals(result.getResponse().getStatus(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertNotNull(result.getResponse().getContentAsString());

    }

    @DisplayName("CreateEmployeeWithInvalidName")
    @Test
    void CreateEmployeeWithInvalidName() throws Exception {
        log.info("Execution testing case of create employee with invalid salary");
        // Given
        JSONObject data = new JSONObject();
        data.put("name","");
        data.put("age",26);
        data.put("salary",1000000);
        data.put("title","Senior Software Engineer");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("")
                .contentType(MediaType.APPLICATION_JSON).content(data.toString())).andReturn();

        log.info("Result received as :{} ,{}",result,result.getResponse().getStatus());
        assertEquals(result.getResponse().getStatus(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertNotNull(result.getResponse().getContentAsString());
    }

    @DisplayName("SearchEmployeeByName")
    @Test
    void searchEmployeeByName() throws Exception {
        log.info("Execution testing case of delete employee with valid input");

        JSONObject employeeJson = createEmployeeForTest();
        String searchString = employeeJson.getString("employee_name");

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(String.format("/search/%s",searchString));
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(result.getResponse().getContentAsString());
        assertEquals(result.getResponse().getStatus(), HttpStatus.OK.value());

        log.info("Result received as :{} status:{}",result,result.getResponse().getStatus());
        List<EmployeeObj> employees = objectMapper.readValue(result.getResponse().getContentAsString() ,
                new TypeReference<>() {
                });

        assertFalse(employees.isEmpty(), "Employee List should not be empty");
        deleteEmployee(employeeJson.getString("id"));
    }

    @DisplayName("getEmployeeById")
    @Test
    void getEmployeeById() throws Exception {

        JSONObject employeeJson = createEmployeeForTest();
        log.info("Execution testing case of delete employee with valid input");
        //createEmployee for testing
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/"+ employeeJson.getString("id"));
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        log.info("Result received as :{} status:{}",result,result.getResponse().getStatus());

        EmployeeObj employeeObj = objectMapper.readValue(result.getResponse().getContentAsString(), EmployeeObj.class);
        assertNotNull(employeeObj);
        assertEquals(employeeObj.getId() , employeeJson.getString("id"));
        deleteEmployee(employeeJson.getString("id"));
    }

    @DisplayName("GetAllEmployeesTest")
    @Test
    void getAllEmployeeTest() throws Exception {
        // do the thing here\\\
        log.info("Executing testing case of getting all employees");
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        log.info("Result received as result:{} status:{}",result,result.getResponse().getStatus());
        List<EmployeeObj> employees = objectMapper.readValue(result.getResponse().getContentAsString() ,
                new TypeReference<>() {
                });
        assertEquals(result.getResponse().getStatus(), HttpStatus.OK.value());
        assertNotNull(result.getResponse().getContentAsString());
        assertFalse(employees.isEmpty(), "Employee List should not be empty");
    }


    @DisplayName("getHighestEmployeeSalary")
    @Test
    void getHighestEmployeeSalary() throws Exception {
        log.info("Executing testing case of getting highest salary");
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/highestSalary");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        log.info("Result received as result:{} status:{}",result,result.getResponse().getStatus());
        assertEquals(result.getResponse().getStatus(), HttpStatus.OK.value());
        assertNotNull(result.getResponse().getContentAsString());
        List<EmployeeObj> employees = getEmployeeList();

        Optional<Integer> maxSalary = employees.stream()
                .max(Comparator.comparing(EmployeeObj::getEmployeeSalary)).map(e->e.getEmployeeSalary());

        assertFalse(maxSalary.isEmpty() , "Max salary not found");
        assertEquals(maxSalary.get() , Integer.parseInt(result.getResponse().getContentAsString()));

    }



    @DisplayName("getTopTenHighestEarningEmployeeNames")
    @Test
    void getTopTenHighestEarningEmployeeNames() throws Exception {
        log.info("Executing testing case of top ten highest earning employees");
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/topTenHighestEarningEmployeeNames");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        log.info("Result received as result:{} status:{}",result,result.getResponse().getStatus());
        assertEquals(result.getResponse().getStatus(), HttpStatus.OK.value());
        assertNotNull(result.getResponse().getContentAsString());

        List<String> employeeNames = objectMapper.readValue(result.getResponse().getContentAsString() ,
                new TypeReference<>() {
                });

        assertTrue(employeeNames.size() <= 10);
    }

    private List<EmployeeObj> getEmployeeList() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        log.info("Result received as result:{} status:{}",result,result.getResponse().getStatus());
        List<EmployeeObj> employees = objectMapper.readValue(result.getResponse().getContentAsString() ,
                new TypeReference<>() {
                });
        return employees;
    }

    private void deleteEmployee(String employeeId) throws Exception {

        MvcResult deleteResult = mockMvc.perform(MockMvcRequestBuilders.delete("/"+employeeId)
                .contentType(MediaType.APPLICATION_JSON)).andReturn();
        assertEquals(deleteResult.getResponse().getStatus(), HttpStatus.OK.value());
        assertNotNull(deleteResult.getResponse().getContentAsString());
    }

    private void validateEmployeeInformation(JSONObject employee, JSONObject requestPayload) {

        assertEquals(employee.getString("employee_name"),requestPayload.getString("name"));
        assertEquals(employee.getInt("employee_salary"),requestPayload.getInt("salary"));
        assertEquals(employee.getInt("employee_age"),requestPayload.getInt("age"));
        assertEquals(employee.getString("employee_title"),requestPayload.getString("title"));

    }

    private JSONObject createEmployeeForTest() throws Exception {
        JSONObject requestPayload = getValidRequestPayload();
        // create employee
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("")
                .contentType(MediaType.APPLICATION_JSON).content(requestPayload.toString())).andReturn();

        log.info("Result received as result:{}, status:{}",result,result.getResponse().getStatus());
        JSONObject responseData = new JSONObject(result.getResponse().getContentAsString());
        JSONObject employee = responseData.getJSONObject("data");
        return employee;
    }

}
