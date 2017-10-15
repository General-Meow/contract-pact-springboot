package com.barath.app;

import au.com.dius.pact.consumer.ConsumerPactTestMk2;
import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import org.junit.Rule;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;

import au.com.dius.pact.consumer.PactProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.model.PactFragment;
import au.com.dius.pact.model.PactSpecVersion;
import au.com.dius.pact.model.RequestResponsePact;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import com.barath.app.Inventory;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.equalTo;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
public class SaveInventoryPactTest{
	
    @Rule
    public PactProviderRuleMk2 mockProvider = new PactProviderRuleMk2("test_provider",PactSpecVersion.V3, this);
    private RestTemplate restTemplate=new RestTemplate();
    private ObjectMapper mapper=new ObjectMapper();

    @Pact(provider = "test_provider", consumer = "test_consumer")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json;charset=UTF-8");


        PactDslJsonBody bodyResponse = new PactDslJsonBody()
                .stringValue("productName", "TV")
                .stringValue("locationName", "CHENNAI")
                .stringType("locationName", "CHENNAI")
                .integerType("inventoryId", 1L)
                .integerType("quantity", 100);

        return builder.uponReceiving("a request to save inventory")
                .path("/api/inventory")
                .body(bodyResponse)
                .method(RequestMethod.POST.name())
                .willRespondWith()
                .headers(headers)
                .status(200).body(bodyResponse).toPact();
    }

   

	
	
	@Test
	@PactVerification
	public void runTest() throws IOException {
		
		Inventory inventory=new Inventory(1L, "TV", "CHENNAI", 100);
    	HttpHeaders headers=new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
    	HttpEntity<Object> request=new HttpEntity<Object>(inventory, headers);
    	ResponseEntity<String> responseEntity=restTemplate.postForEntity(mockProvider.getUrl()+"/api/inventory", request, String.class);
    	Inventory savedInventory=mapper.readValue( responseEntity.getBody(),Inventory.class);
		
	}

}