package org.github.aleksey_kn.aniland;

import org.springframework.boot.SpringApplication;

public class TestAnilandApplication {

	public static void main(String[] args) {
		SpringApplication.from(AniLandApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
