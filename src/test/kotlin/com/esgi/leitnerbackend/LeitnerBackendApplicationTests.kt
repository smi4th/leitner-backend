package com.esgi.leitnerbackend

import org.junit.jupiter.api.Test
import org.junit.platform.suite.api.SelectPackages
import org.junit.platform.suite.api.Suite
import org.springframework.boot.test.context.SpringBootTest

@Suite
@SelectPackages("com.esgi.leitnerbackend.cards")
class LeitnerBackendApplicationTests {
}
