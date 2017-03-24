package com.marc.microservice.ordercart

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.context.WebApplicationContext

import spock.lang.Specification

@ContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApplicationSpecIT extends Specification {

    @Autowired
    WebApplicationContext context

    def "Application should start without error"() {
        expect: "a context that exists"
        context != null
    }
}
