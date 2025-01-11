# EmployeeService

I have implemented all controller endpoints.
This containes Junit tests & integration test also.
For server which is having rate limiter, we can use API caching now. So I have implemented for GetEmployees call. We can add retry mechanism in case of 429 error code from server. 
We can limit the number of requests from client to server using semaphore, Flux or Executor service. 



