package com.vastidev.taskmanager;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TaskManagerApplicationTests {

	@Test
	@Disabled("Temporarily disable the context test for SonarQube integration")
	void contextLoads() {
		// This test is intentionally left empty because it is disabled for SonarQube integration.
		// The context load test will be implemented or re-enabled in the future as needed.
	}

}
