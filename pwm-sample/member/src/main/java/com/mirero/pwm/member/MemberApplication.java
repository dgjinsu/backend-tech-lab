package com.mirero.pwm.member;

import com.mirero.pwm.common.exception.CommonExceptionConfig;
import com.mirero.pwm.common.swagger.SwaggerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({CommonExceptionConfig.class, SwaggerConfig.class})
public class MemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(MemberApplication.class, args);
    }

}
