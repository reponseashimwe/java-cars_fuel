package com.example.cars.config;

import com.example.cars.servlet.FuelStatsServlet;
import com.example.cars.service.FuelEntryService;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Configuration class to register the manual Java Servlet in Spring Boot
@Configuration
public class ServletConfig {
    
    private final ApplicationContext applicationContext;
    
    public ServletConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
    
    // Registers FuelStatsServlet mapped to /servlet/fuel-stats
    @Bean
    public ServletRegistrationBean<FuelStatsServlet> fuelStatsServletRegistration() {
        // Get the FuelEntryService from Spring context
        FuelEntryService fuelEntryService = applicationContext.getBean(FuelEntryService.class);
        
        // Create the servlet instance with dependency injection
        FuelStatsServlet servlet = new FuelStatsServlet(fuelEntryService);
        
        // Register the servlet and map it to /servlet/fuel-stats
        ServletRegistrationBean<FuelStatsServlet> registration = 
            new ServletRegistrationBean<>(servlet, "/servlet/fuel-stats");
        
        registration.setName("fuelStatsServlet");
        registration.setLoadOnStartup(1);
        
        return registration;
    }
}

