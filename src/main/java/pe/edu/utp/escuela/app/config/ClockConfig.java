package pe.edu.utp.escuela.app.config;

import java.time.Clock;
import java.time.ZoneId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClockConfig {

    @Bean
    Clock applicationClock(@Value("${application.time-zone}") String timeZone) {
        return Clock.system(ZoneId.of(timeZone));
    }
}
