package ca.ubc.cs.cpsc410;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

/**
 * Created by vincent on 20/10/15.
 * <p>
 * Main class to launch Wesync server-side component.
 */
@SpringBootApplication
public class WesyncApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(WesyncApplication.class, args);
    }

    @Override
    protected final SpringApplicationBuilder configure(final SpringApplicationBuilder application) {
        return application.sources(WesyncApplication.class);
    }
}
