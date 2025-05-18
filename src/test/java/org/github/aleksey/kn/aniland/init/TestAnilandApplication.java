package org.github.aleksey.kn.aniland.init;

import org.github.aleksey.kn.aniland.AniLandApplication;
import org.springframework.boot.SpringApplication;

public class TestAnilandApplication {

    public static void main(String[] args) {
        SpringApplication.from(AniLandApplication::main).with(TestcontainersConfiguration.class).run(args);
    }
}
