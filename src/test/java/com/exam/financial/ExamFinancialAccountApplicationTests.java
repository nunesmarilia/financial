package com.exam.financial;

import com.exam.financial.model.Event;
import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
class ExamFinancialAccountApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@LocalServerPort
	int randomServerPort;

	@Test
	@DisplayName("Reset state before starting tests")
	void postResetTest() {
		String baseUrl = "http://localhost:"+randomServerPort+"/reset";

		HttpHeaders headers = new HttpHeaders();

		HttpEntity<Object> entity = new HttpEntity<>(headers);

		ResponseEntity<String> result   = restTemplate.exchange(baseUrl, HttpMethod.POST, entity, String.class);

		Assert.assertEquals(200, result.getStatusCodeValue());
		Assert.assertTrue(result.getBody() != null && result.getBody().contains("OK"));
	}

	@Test
	@DisplayName("Get balance for non-existing account")
	void getBalanceNonExistingAccountTest() {
		String baseUrl = "http://localhost:"+randomServerPort+"/balance?account_id=1234";

		HttpHeaders headers = new HttpHeaders();

		HttpEntity<Object> entity = new HttpEntity<>(headers);

		ResponseEntity<String> result   = restTemplate.exchange(baseUrl, HttpMethod.GET, entity, String.class);

		Assert.assertEquals(404, result.getStatusCodeValue());
		Assert.assertTrue(result.getBody() != null && result.getBody().contains("0"));
	}

	@Test
	@DisplayName("Invalid transaction option")
	void postEventInvalidTransactionOption() {
		String baseUrl  = "http://localhost:"+randomServerPort+"/event";
		Event event     = new Event("X", "100", "", 10);

		HttpHeaders headers = new HttpHeaders();

		HttpEntity<Event> request = new HttpEntity<>(event, headers);

		ResponseEntity<String> result = restTemplate.postForEntity(baseUrl, request, String.class);

		Assert.assertEquals(400, result.getStatusCodeValue());
		Assert.assertTrue(result.getBody() != null && result.getBody().equals("Invalid transaction option"));
	}

	@Test
	@DisplayName("Create account with initial balance")
	void postEventDepositCreateAccountTest() {
		String baseUrl  = "http://localhost:"+randomServerPort+"/event";
		Event event     = new Event("deposit", "100", "", 10);

		HttpHeaders headers = new HttpHeaders();

		HttpEntity<Event> request = new HttpEntity<>(event, headers);

		ResponseEntity<String> result = restTemplate.postForEntity(baseUrl, request, String.class);

		Assert.assertEquals(201, result.getStatusCodeValue());
		Assert.assertTrue(result.getBody() != null && result.getBody().equals("{\"destination\": {\"id\":\"100\", \"balance\":10}}"));
	}

	@Test
	@DisplayName("Deposit into existing account")
	void postEventDepositExistingAccountTest() {
		String baseUrl = "http://localhost:"+randomServerPort;

		HttpHeaders headers = new HttpHeaders();

		HttpEntity<Object> entity = new HttpEntity<>(headers);

		restTemplate.exchange(baseUrl+"/reset", HttpMethod.POST, entity, String.class);

		Event eventInitial                 = new Event("deposit", "100", "",10);
		HttpEntity<Event> requestInitial   = new HttpEntity<>(eventInitial, headers);
		restTemplate.postForEntity(baseUrl+"/event", requestInitial, String.class);

		Event event     = new Event("deposit", "100","", 10);

		HttpEntity<Event> request = new HttpEntity<>(event, headers);

		ResponseEntity<String> result = restTemplate.postForEntity(baseUrl+"/event", request, String.class);

		Assert.assertEquals(201, result.getStatusCodeValue());
		Assert.assertTrue(result.getBody() != null && result.getBody().equals("{\"destination\": {\"id\":\"100\", \"balance\":20}}"));
	}

	@Test
	@DisplayName("Deposit - Destination Account is empty")
	void postEventDepositDestinationEmptyTest() {
		String baseUrl = "http://localhost:"+randomServerPort;

		HttpHeaders headers = new HttpHeaders();

		HttpEntity<Object> entity = new HttpEntity<>(headers);

		restTemplate.exchange(baseUrl+"/reset", HttpMethod.POST, entity, String.class);

		Event eventInitial                 = new Event("deposit", "100", "",10);
		HttpEntity<Event> requestInitial   = new HttpEntity<>(eventInitial, headers);
		restTemplate.postForEntity(baseUrl+"/event", requestInitial, String.class);

		Event event     = new Event("deposit", "","", 10);

		HttpEntity<Event> request = new HttpEntity<>(event, headers);

		ResponseEntity<String> result = restTemplate.postForEntity(baseUrl+"/event", request, String.class);

		Assert.assertEquals(400, result.getStatusCodeValue());
		Assert.assertTrue(result.getBody() != null && result.getBody().equals("Destination Account is empty"));
	}

	@Test
	@DisplayName("Deposit - Destination Account is null")
	void postEventDepositDestinationNullTest() {
		String baseUrl = "http://localhost:"+randomServerPort;

		HttpHeaders headers = new HttpHeaders();

		HttpEntity<Object> entity = new HttpEntity<>(headers);

		restTemplate.exchange(baseUrl+"/reset", HttpMethod.POST, entity, String.class);

		Event eventInitial                 = new Event("deposit", "100", "",10);
		HttpEntity<Event> requestInitial   = new HttpEntity<>(eventInitial, headers);
		restTemplate.postForEntity(baseUrl+"/event", requestInitial, String.class);

		Event event     = new Event("deposit", null,"", 10);

		HttpEntity<Event> request = new HttpEntity<>(event, headers);

		ResponseEntity<String> result = restTemplate.postForEntity(baseUrl+"/event", request, String.class);

		Assert.assertEquals(400, result.getStatusCodeValue());
		Assert.assertTrue(result.getBody() != null && result.getBody().equals("Destination Account is empty"));
	}



	@Test
	@DisplayName("Get balance for existing account")
	void getBalanceAccountExistingTest() {
		String baseUrl = "http://localhost:"+randomServerPort;

		HttpHeaders headers = new HttpHeaders();

		HttpEntity<Object> entity = new HttpEntity<>(headers);

		restTemplate.exchange(baseUrl+"/reset", HttpMethod.POST, entity, String.class);

		Event event                 = new Event("deposit", "100", "", 20);
		HttpEntity<Event> request   = new HttpEntity<>(event, headers);
		restTemplate.postForEntity(baseUrl+"/event", request, String.class);

		ResponseEntity<String> result   = restTemplate.exchange(baseUrl+"/balance?account_id=100", HttpMethod.GET, entity, String.class);

		Assert.assertEquals(200, result.getStatusCodeValue());
		Assert.assertTrue(result.getBody() != null && result.getBody().contains("20"));
	}

	@Test
	@DisplayName("Withdraw from non-existing account")
	void postWithdrawAccountNonExistingTest() {
		String baseUrl = "http://localhost:"+randomServerPort;

		HttpHeaders headers = new HttpHeaders();

		HttpEntity<Object> entity = new HttpEntity<>(headers);

		restTemplate.exchange(baseUrl+"/reset", HttpMethod.POST, entity, String.class);

		Event event                     = new Event("withdraw", "", "200", 10);
		HttpEntity<Event> request       = new HttpEntity<>(event, headers);
		ResponseEntity<String> result   = restTemplate.postForEntity(baseUrl+"/event", request, String.class);

		Assert.assertEquals(404, result.getStatusCodeValue());
		Assert.assertTrue(result.getBody() != null && result.getBody().contains("0"));
	}

	@Test
	@DisplayName("Withdraw - Origin Account is Empty")
	void postWithdrawOriginEmptyTest() {
		String baseUrl = "http://localhost:"+randomServerPort;

		HttpHeaders headers = new HttpHeaders();

		HttpEntity<Object> entity = new HttpEntity<>(headers);

		restTemplate.exchange(baseUrl+"/reset", HttpMethod.POST, entity, String.class);

		Event event                     = new Event("withdraw", "", "", 10);
		HttpEntity<Event> request       = new HttpEntity<>(event, headers);
		ResponseEntity<String> result   = restTemplate.postForEntity(baseUrl+"/event", request, String.class);

		Assert.assertEquals(400, result.getStatusCodeValue());
		Assert.assertTrue(result.getBody() != null && result.getBody().contains("Origin Account is empty"));
	}

	@Test
	@DisplayName("Withdraw - Origin Account is null")
	void postWithdrawOriginNullTest() {
		String baseUrl = "http://localhost:"+randomServerPort;

		HttpHeaders headers = new HttpHeaders();

		HttpEntity<Object> entity = new HttpEntity<>(headers);

		restTemplate.exchange(baseUrl+"/reset", HttpMethod.POST, entity, String.class);

		Event event                     = new Event("withdraw", "", null, 10);
		HttpEntity<Event> request       = new HttpEntity<>(event, headers);
		ResponseEntity<String> result   = restTemplate.postForEntity(baseUrl+"/event", request, String.class);

		Assert.assertEquals(400, result.getStatusCodeValue());
		Assert.assertTrue(result.getBody() != null && result.getBody().contains("Origin Account is empty"));
	}

	@Test
	@DisplayName("Withdraw from existing account")
	void postEventWithdrawExistingAccountTest() {
		String baseUrl = "http://localhost:"+randomServerPort;

		HttpHeaders headers = new HttpHeaders();

		HttpEntity<Object> entity = new HttpEntity<>(headers);

		restTemplate.exchange(baseUrl+"/reset", HttpMethod.POST, entity, String.class);

		Event eventInitial                 = new Event("deposit", "100", "",20);
		HttpEntity<Event> requestInitial   = new HttpEntity<>(eventInitial, headers);
		restTemplate.postForEntity(baseUrl+"/event", requestInitial, String.class);

		Event event     = new Event("withdraw", "","100", 5);

		HttpEntity<Event> request = new HttpEntity<>(event, headers);

		ResponseEntity<String> result = restTemplate.postForEntity(baseUrl+"/event", request, String.class);

		Assert.assertEquals(201, result.getStatusCodeValue());
		Assert.assertTrue(result.getBody() != null && result.getBody().equals("{\"origin\": {\"id\":\"100\", \"balance\":15}}"));
	}

	@Test
	@DisplayName("Withdraw - Insufficient funds")
	void postEventWithdrawInsufficientFundsTest() {
		String baseUrl = "http://localhost:"+randomServerPort;

		HttpHeaders headers = new HttpHeaders();

		HttpEntity<Object> entity = new HttpEntity<>(headers);

		restTemplate.exchange(baseUrl+"/reset", HttpMethod.POST, entity, String.class);

		Event eventInitial                 = new Event("deposit", "100", "",20);
		HttpEntity<Event> requestInitial   = new HttpEntity<>(eventInitial, headers);
		restTemplate.postForEntity(baseUrl+"/event", requestInitial, String.class);

		Event event     = new Event("withdraw", "","100", 25);

		HttpEntity<Event> request = new HttpEntity<>(event, headers);

		ResponseEntity<String> result = restTemplate.postForEntity(baseUrl+"/event", request, String.class);

		Assert.assertEquals(400, result.getStatusCodeValue());
		Assert.assertTrue(result.getBody() != null && result.getBody().equals("{\"message\":\"Insufficient funds\"}"));
	}

	@Test
	@DisplayName("Transfer from non-existing account")
	void postTransferAccountNonExistingTest() {
		String baseUrl = "http://localhost:"+randomServerPort;

		HttpHeaders headers = new HttpHeaders();

		HttpEntity<Object> entity = new HttpEntity<>(headers);

		restTemplate.exchange(baseUrl+"/reset", HttpMethod.POST, entity, String.class);

		Event event                     = new Event("transfer", "300", "100", 15);
		HttpEntity<Event> request       = new HttpEntity<>(event, headers);
		ResponseEntity<String> result   = restTemplate.postForEntity(baseUrl+"/event", request, String.class);

		Assert.assertEquals(404, result.getStatusCodeValue());
		Assert.assertTrue(result.getBody() != null && result.getBody().contains("0"));
	}

	@Test
	@DisplayName("Transfer - Destination Account is empty")
	void postTransferDestinationEmptyTest() {
		String baseUrl = "http://localhost:"+randomServerPort;

		HttpHeaders headers = new HttpHeaders();

		HttpEntity<Object> entity = new HttpEntity<>(headers);

		restTemplate.exchange(baseUrl+"/reset", HttpMethod.POST, entity, String.class);

		Event event                     = new Event("transfer", "", "100", 15);
		HttpEntity<Event> request       = new HttpEntity<>(event, headers);
		ResponseEntity<String> result   = restTemplate.postForEntity(baseUrl+"/event", request, String.class);

		Assert.assertEquals(400, result.getStatusCodeValue());
		Assert.assertTrue(result.getBody() != null && result.getBody().contains("Destination Account is empty or Origin Account is empty"));
	}

	@Test
	@DisplayName("Transfer - Destination Account is null")
	void postTransferDestinationNullTest() {
		String baseUrl = "http://localhost:"+randomServerPort;

		HttpHeaders headers = new HttpHeaders();

		HttpEntity<Object> entity = new HttpEntity<>(headers);

		restTemplate.exchange(baseUrl+"/reset", HttpMethod.POST, entity, String.class);

		Event event                     = new Event("transfer", null, "100", 15);
		HttpEntity<Event> request       = new HttpEntity<>(event, headers);
		ResponseEntity<String> result   = restTemplate.postForEntity(baseUrl+"/event", request, String.class);

		Assert.assertEquals(400, result.getStatusCodeValue());
		Assert.assertTrue(result.getBody() != null && result.getBody().contains("Destination Account is empty or Origin Account is empty"));
	}

	@Test
	@DisplayName("Transfer - Origin Account is empty")
	void postTransferOriginEmptyTest() {
		String baseUrl = "http://localhost:"+randomServerPort;

		HttpHeaders headers = new HttpHeaders();

		HttpEntity<Object> entity = new HttpEntity<>(headers);

		restTemplate.exchange(baseUrl+"/reset", HttpMethod.POST, entity, String.class);

		Event event                     = new Event("transfer", "300", "", 15);
		HttpEntity<Event> request       = new HttpEntity<>(event, headers);
		ResponseEntity<String> result   = restTemplate.postForEntity(baseUrl+"/event", request, String.class);

		Assert.assertEquals(400, result.getStatusCodeValue());
		Assert.assertTrue(result.getBody() != null && result.getBody().contains("Destination Account is empty or Origin Account is empty"));
	}

	@Test
	@DisplayName("Transfer - Origin Account is null")
	void postTransferOriginNullTest() {
		String baseUrl = "http://localhost:"+randomServerPort;

		HttpHeaders headers = new HttpHeaders();

		HttpEntity<Object> entity = new HttpEntity<>(headers);

		restTemplate.exchange(baseUrl+"/reset", HttpMethod.POST, entity, String.class);

		Event event                     = new Event("transfer", "300", null, 15);
		HttpEntity<Event> request       = new HttpEntity<>(event, headers);
		ResponseEntity<String> result   = restTemplate.postForEntity(baseUrl+"/event", request, String.class);

		Assert.assertEquals(400, result.getStatusCodeValue());
		Assert.assertTrue(result.getBody() != null && result.getBody().contains("Destination Account is empty or Origin Account is empty"));
	}

	@Test
	@DisplayName("Transfer from existing account")
	void postEventTransferExistingAccountTest() {
		String baseUrl = "http://localhost:"+randomServerPort;

		HttpHeaders headers = new HttpHeaders();

		HttpEntity<Object> entity = new HttpEntity<>(headers);

		restTemplate.exchange(baseUrl+"/reset", HttpMethod.POST, entity, String.class);

		Event eventOrigin                   = new Event("deposit", "100", "",15);
		HttpEntity<Event> requestOrigin     = new HttpEntity<>(eventOrigin, headers);
		restTemplate.postForEntity(baseUrl+"/event", requestOrigin, String.class);

		Event eventDestination                  = new Event("deposit", "300", "",0);
		HttpEntity<Event> requestDestination    = new HttpEntity<>(eventDestination, headers);
		restTemplate.postForEntity(baseUrl+"/event", requestDestination, String.class);

		Event event     = new Event("transfer", "300","100", 15);

		HttpEntity<Event> request = new HttpEntity<>(event, headers);

		ResponseEntity<String> result = restTemplate.postForEntity(baseUrl+"/event", request, String.class);

		Assert.assertEquals(201, result.getStatusCodeValue());
		Assert.assertTrue(result.getBody() != null && result.getBody().equals("{\"origin\": {\"id\":\"100\", \"balance\":0},\"destination\": {\"id\":\"300\", \"balance\":15}}"));
	}

	@Test
	@DisplayName("Transfer - Insufficient funds")
	void postEventTransfernsufficientFundsTest() {
		String baseUrl = "http://localhost:"+randomServerPort;

		HttpHeaders headers = new HttpHeaders();

		HttpEntity<Object> entity = new HttpEntity<>(headers);

		restTemplate.exchange(baseUrl+"/reset", HttpMethod.POST, entity, String.class);

		Event eventInitial                 = new Event("deposit", "100", "",20);
		HttpEntity<Event> requestInitial   = new HttpEntity<>(eventInitial, headers);
		restTemplate.postForEntity(baseUrl+"/event", requestInitial, String.class);

		Event event     = new Event("transfer", "200","100", 25);

		HttpEntity<Event> request = new HttpEntity<>(event, headers);

		ResponseEntity<String> result = restTemplate.postForEntity(baseUrl+"/event", request, String.class);

		Assert.assertEquals(400, result.getStatusCodeValue());
		Assert.assertTrue(result.getBody() != null && result.getBody().equals("{\"message\":\"Insufficient funds\"}"));
	}

}
