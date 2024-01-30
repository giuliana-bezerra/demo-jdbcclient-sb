package br.com.giulianabezerra.demojdbcclient;

import java.util.List;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class DemoJdbcClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoJdbcClientApplication.class, args);
	}

	@Bean
	ApplicationRunner runner(UserRepository userRepository) {
		return args -> {
			var leia = new User(1L, "leia", "leia@email.com", "123");
			userRepository.save(leia);
			leia = leia.withPassword("novasenha123");
			userRepository.update(leia);
			// userRepository.deleteById(leia.id());
			userRepository.findAll().forEach(System.out::println);
		};
	}
}

@Component
class UserRepository {
	private final JdbcClient jdbcClient;

	public UserRepository(JdbcClient jdbcClient) {
		this.jdbcClient = jdbcClient;
	}

	public List<User> findAll() {
		return jdbcClient.sql("SELECT * FROM users")
				.query(User.class)
				.list();
	}

	public User findById(Long id) {
		return jdbcClient.sql("SELECT * FROM users WHERE id = :id")
				.param("id", id)
				.query(User.class)
				.single();
	}

	public User save(User user) {
		jdbcClient.sql("""
				INSERT INTO users (id, username, email, password)
				VALUES (:id, :username, :email, :password)
					""")
				.param("id", user.id())
				.param("username", user.username())
				.param("email", user.email())
				.param("password", user.password())
				.update();
		return user;
	}

	public User update(User user) {
		jdbcClient.sql("""
				UPDATE users
				SET username = :username, email = :email, password = :password
				WHERE id = :id
					""")
				.param("id", user.id())
				.param("username", user.username())
				.param("email", user.email())
				.param("password", user.password())
				.update();
		return user;
	}

	public void deleteById(Long id) {
		jdbcClient.sql("DELETE FROM users WHERE id = :id")
				.param("id", id)
				.update();
	}
}

record User(Long id, String username, String email, String password) {
	public User withPassword(String newPassword) {
		return new User(id, username, email, newPassword);
	}
}
