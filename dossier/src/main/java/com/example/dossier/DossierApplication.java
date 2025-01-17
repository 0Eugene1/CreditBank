package com.example.dossier;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DossierApplication {

	public static void main(String[] args) {

		Dotenv dotenv = Dotenv.configure().load();
		System.setProperty("MAIL_USERNAME", dotenv.get("MAIL_USERNAME"));
		System.setProperty("MAIL_PASSWORD", dotenv.get("MAIL_PASSWORD"));
		SpringApplication.run(DossierApplication.class, args);
	}

}
