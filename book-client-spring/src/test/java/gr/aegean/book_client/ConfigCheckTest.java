package gr.aegean.book_client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import gr.aegean.book_client.configuration.ImmutableApiConfiguration;

@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = ImmutableApiConfiguration.class)
@TestPropertySource("classpath:other.properties")
public class ConfigCheckTest implements TestInterface{

    @Autowired
    private ImmutableApiConfiguration config;

    @Test
    void givenUserDefinedPOJO_whenBindingPropertiesFile_thenAllFieldsAreSet() {
    	assertNotEquals(config,null);
        assertEquals("checkhost", config.getHost());
        assertEquals("8082", config.getPort());
        assertEquals("api/books2", config.getApi());
    }
}
