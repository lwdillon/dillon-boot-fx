package org.dillon.fx;


import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class DillonBootFxApplication {

    public DillonBootFxApplication() {
        AppUI.main(new String[]{});
    }

    public static void main(String[] args) {
        // 初始化Logback
        new SpringApplicationBuilder(DillonBootFxApplication.class).headless(false).run(args);
    }
}
