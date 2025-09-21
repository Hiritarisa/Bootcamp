package com.hiri.crediya;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Basic integration test to verify Spring context loads successfully.
 */
@SpringBootTest
@ActiveProfiles("test")
class MainApplicationTest {

    @Test
    void contextLoads() {
        // This test verifies that the Spring application context loads successfully
        // without any configuration issues
    }
}
