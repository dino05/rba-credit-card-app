package com.rba.creditcardapp;

import com.rba.creditcardapp.api.NewCardRequestApi;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@TestPropertySource(properties = {
        "external.api.enabled=false"
})
class CreditCardApplicationTests {

    @MockitoBean
    private NewCardRequestApi newCardRequestApi;

	@Test
	void contextLoads() {
	}

}
