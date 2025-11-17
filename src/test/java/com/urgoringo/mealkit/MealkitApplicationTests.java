package com.urgoringo.mealkit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import(TestContainersConfiguration.class)
@SpringBootTest
@ActiveProfiles("test")
class MealkitApplicationTests {

	@Test
	void contextLoads() {
	}

}
