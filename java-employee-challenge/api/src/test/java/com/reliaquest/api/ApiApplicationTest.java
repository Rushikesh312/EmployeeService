package com.reliaquest.api;

import com.reliaquest.api.dto.EmployeeObj;
import com.reliaquest.api.service.impl.EmployeeServiceImpl;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureMockMvc
@SpringBootTest
class ApiApplicationTest {


    private static final Logger log = LoggerFactory.getLogger(ApiApplicationTest.class);

    @Spy
    @InjectMocks
    private EmployeeServiceImpl employeeService; // Mock the EmployeeService
    // Allows partial mocking of the class under test


    @Mock
    private EmployeeServiceImpl employeeServiceMock;


    @DisplayName("CreateEmployee")
    @Test
    void CreateEmployee() throws Exception {

        JSONObject requestPayload = new JSONObject();
        requestPayload.put("name","Rushikesh");
        requestPayload.put("age",26);
        requestPayload.put("salary",1000000);
        requestPayload.put("title","Senior Software Engineer");

        Mockito.when(employeeServiceMock.createEmployee(requestPayload)).thenReturn(ResponseEntity.ok("{\"data\":{\"id\":\"7a7d34f8-f47b-475e-8106-fe8367cb625b\",\"employee_name\":\"Rushikesh\",\"employee_salary\":451212,\"employee_age\":52,\"employee_title\":\"VP\",\"employee_email\":\"y-find@company.com\"},\"status\":\"Successfully processed request.\"}"));
        ResponseEntity<String> response = employeeServiceMock.createEmployee(requestPayload);
        JSONObject result = new JSONObject(response.getBody());
        JSONObject employee  = result.getJSONObject("data");

        assertEquals(response.getStatusCode().value(),200);
        assertEquals(requestPayload.getString("name") ,  employee.getString("employee_name"));

    }


    @DisplayName("DeleteEmployee")
    @Test
    void DeleteEmployee() throws Exception {

        String deleteApiResponse = "{\"data\":true,\"status\":\"Successfully processed request.\"}";
        String employeeId = "7a7d34f8-f47b-475e-8106-fe8367cb625b";
        Mockito.when(employeeServiceMock.deleteEmployeeById(employeeId)).thenReturn(ResponseEntity.ok(deleteApiResponse));

        ResponseEntity<String> response = employeeServiceMock.deleteEmployeeById(employeeId);
        JSONObject result = new JSONObject(response.getBody());
        assertTrue(result.getBoolean("data"));;
        assertEquals(200,response.getStatusCode().value());
        Mockito.verify(employeeServiceMock, Mockito.times(1)).deleteEmployeeById(employeeId);

    }

    @DisplayName("GetEmployees")
    @Test
    void getEmployees() throws Exception {

        List<EmployeeObj> employeeList = getDummyEmployeeList();
        Mockito.when(employeeServiceMock.getAllEmployees()).thenReturn(employeeList);
        //call method
        List<EmployeeObj> result = employeeServiceMock.getAllEmployees();
        log.debug("result received as :{}",result);
        assertNotNull(result);
        assertEquals(employeeList.size(), result.size());
        assertEquals(employeeList.size(), result.size());

        Mockito.verify(employeeServiceMock, Mockito.times(1)).getAllEmployees();

    }

    @DisplayName("SearchEmployees")
    @Test
    void searchEmployees() throws Exception {

        String searchString = "Kulkarni";
        Mockito.doReturn(getDummyEmployeeList()).when(employeeService).getAllEmployees();
        //call method
        List<EmployeeObj> result = employeeServiceMock.getEmployeesByName(searchString);
        log.debug("result received as :{}",result);
        assertNotNull(result);
        result.stream().forEach(x->{
            assertTrue(x.getEmployeeName().contains(searchString));
        });
        Mockito.verify(employeeServiceMock, Mockito.times(1)).getEmployeesByName(searchString);
    }

    @DisplayName("HighestSalaryEmployee")
    @Test
    void getHighestSalary() throws Exception {

        log.info("Execute test case of getting highest salary of Employee ");
        Mockito.doReturn(getDummyEmployeeList()).when(employeeService).getAllEmployees();
        Integer highestSalary = employeeService.getHighestSalaryOfEmployees();
        assertNotNull(highestSalary);
        assertEquals(74000 ,highestSalary);
        Mockito.verify(employeeService, Mockito.times(1)).getHighestSalaryOfEmployees();

    }

    @DisplayName("getTopTenEarningEmployeeNames")
    @Test
    void getTopTenEarningEmployeeNames() throws Exception {

        log.info("Execute test case of getTopTenEarningEmployeeNames ");
        Mockito.doReturn(getDummyEmployeeList()).when(employeeService).getAllEmployees();
        List<String> result = employeeService.getTopTenHighestEarningEmployeeNames();
        assertNotNull(result);
        assertFalse(result.size() > 10 ,"List should be less than 10 " );
        Mockito.verify(employeeService, Mockito.times(1)).getTopTenHighestEarningEmployeeNames();

    }

    private List<EmployeeObj> getDummyEmployeeList() {
        return  List.of(
                new EmployeeObj(UUID.randomUUID().toString(),"Rushikesh Kulkarni",
                        45000,29,"Engineer","rk@gmail.com" ),
                new EmployeeObj(UUID.randomUUID().toString(),"Suresh Kulkarni",
                        43000,34,"Manager","sureshk@gmail.com" ),
                new EmployeeObj(UUID.randomUUID().toString(),"A Kulkarni",
                        74000,40,"VP","ak@gmail.com" ),
                new EmployeeObj(UUID.randomUUID().toString(),"S Deshmukh",
                        58000,28,"Senior Engineer","sk@gmail.com" ));
    }
}
