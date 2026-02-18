package com.example.Vidal_Health_Test;


 
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;


import org.springframework.boot.CommandLineRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class StartUpRunner implements CommandLineRunner {

	@Override
	public void run(String... args) throws Exception { 
		System.out.println("App Started");
		
		
		
		RestTemplate restTemp = new RestTemplate();
		String url= "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
		
		Map<String, String> req= new HashMap<>();
		req.put("name", "Pralayakaveri Raj Kumar");
		req.put( "regNo", "8008084051");
		req.put("email", "rajkumarraju60433@gmail.com");
		
		ResponseEntity< Map> response = restTemp.postForEntity(url, req, Map.class);
		Map body = response.getBody();
		
		String webhookUrl = body.get( "webhook").toString();
		String accessToken= body.get( "accessToken").toString();
		
		System.out.println("webhook :"+ webhookUrl );
		System.out.println("accessToken :"+accessToken);
		
		
		
		String Query = """
				SELECT d.DEPARTMENT_NAME,
				       t.SALARY,
				       CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS EMPLOYEE_NAME,
				       TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE
				FROM (
				    SELECT e.EMP_ID,
				           e.DEPARTMENT,
				           SUM(p.AMOUNT) AS SALARY
				    FROM EMPLOYEE e
				    JOIN PAYMENTS p ON e.EMP_ID = p.EMP_ID
				    WHERE DAY(p.PAYMENT_TIME) <> 1
				    GROUP BY e.EMP_ID, e.DEPARTMENT
				) t
				JOIN EMPLOYEE e ON t.EMP_ID = e.EMP_ID
				JOIN DEPARTMENT d ON t.DEPARTMENT = d.DEPARTMENT_ID
				WHERE (t.DEPARTMENT, t.SALARY) IN (
				    SELECT e3.DEPARTMENT,
				           MAX(total_salary)
				    FROM (
				        SELECT e3.DEPARTMENT,
				               SUM(p3.AMOUNT) AS total_salary
				        FROM EMPLOYEE e3
				        JOIN PAYMENTS p3 ON e3.EMP_ID = p3.EMP_ID
				        WHERE DAY(p3.PAYMENT_TIME) <> 1
				        GROUP BY e3.EMP_ID, e3.DEPARTMENT
				    ) sub
				    GROUP BY e3.DEPARTMENT
				);
				""";


		
		
		 HttpHeaders header = new HttpHeaders();
		 
		 header.set("Authorization",accessToken);
		 header.setContentType( MediaType.APPLICATION_JSON);
		
		 Map<String , String> res = new HashMap<String, String>();
		 res.put("finalQuery",Query);
		 
		 HttpEntity< Map<String, String>> entity = new HttpEntity<Map<String,String>>(res,header);
		 
		 restTemp.postForEntity(webhookUrl, entity, String.class);
		 System.out.println("Submitted");
		
	}

}
