package com.posapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource; // Nueva importación

@SpringBootTest
@TestPropertySource(properties = { // Deshabilita la auto-configuración de la base de datos para esta prueba
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration",
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration"
})
class PosApiApplicationTests {

	@Test
	void contextLoads() {
	}

}
