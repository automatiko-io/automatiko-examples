package io.automatiko.examples.operator.services;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PasswordService {

	
	public String generate() {
		return "my password string";
	}
}
