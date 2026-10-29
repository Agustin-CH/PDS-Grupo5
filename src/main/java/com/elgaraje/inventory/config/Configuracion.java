package com.elgaraje.inventory.config;

import java.time.Clock;
import org.springframework.context.annotation.*;

@Configuration
public class Configuracion {
  @Bean
  public Clock reloj() {
    return Clock.systemUTC();
  }
}
