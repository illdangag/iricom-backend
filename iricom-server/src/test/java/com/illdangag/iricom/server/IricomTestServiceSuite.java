package com.illdangag.iricom.server;

import com.illdangag.iricom.core.test.IricomTestCoreSuite;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest(classes = ServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IricomTestServiceSuite extends IricomTestCoreSuite {
    public IricomTestServiceSuite(ApplicationContext context) {
        super(context);
    }
}
