package api.sales.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.test.context.ActiveProfiles;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
@ActiveProfiles("test")
public class ApplicationTest
{
    public static void main( String[] args )
    {
        SpringApplication.run(ApplicationTest.class, args);
    }
}
