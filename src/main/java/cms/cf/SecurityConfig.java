package cms.cf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain1(HttpSecurity http) throws Exception 
	{
		http
			.authorizeHttpRequests((requests) -> 
				requests
					.requestMatchers("/","/legendas","/bootstrap/**","/css/**","/fonts/**","/footable/**","/image/**","/jquery/**","/js/**")
					.permitAll()
					.requestMatchers("/legendas/login/*").hasAnyRole("admin","worker","login")
					.requestMatchers("/legendas/worker/*").hasAnyRole("admin","worker")
					.requestMatchers("/legendas/admin/*").hasRole("admin")
					.anyRequest().authenticated()
			)
			.formLogin((form) -> 
			    form
					.loginPage("/login")
					.permitAll()
			)
			.logout((logout) -> 
				logout
				    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
				    .logoutSuccessUrl("/legendas")
					.permitAll()
			);
		
// Versao deprectada
//		http
//		.authorizeHttpRequests()
//		.requestMatchers("/","/legendas","/bootstrap/**","/css/**","/fonts/**","/footable/**","/image/**","/jquery/**","/js/**")
//		.permitAll()
//		.requestMatchers("/legendas/login/*").hasAnyRole("admin","worker","login")
//		.requestMatchers("/legendas/worker/*").hasAnyRole("admin","worker")
//		.requestMatchers("/legendas/admin/*").hasRole("admin")
//		.anyRequest().authenticated()
//		.and()
//		.formLogin().loginPage("/login").permitAll()
//		.and()
//		.logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/legendas").permitAll();

		return http.build();
	}

	//@Bean
	public UserDetailsService userDetailsService(PasswordEncoder enc) {

		//precisa adicionar o prefixo "ROLE_" nos nomes das roles.
		//varios metodos da api http e spring adicionam isso ao comparar nomes de roles.
		//vc so precisa usar o prefixo cadastrar as roles com o usuario.
		
		UserDetails user3 =
				 User.withUsername("worker@gmail.com")
					.password(enc.encode("123"))
					.roles("worker","login") //roles() : adiciona o prefixo "ROLE_" automaticamente
					.build();
		
		UserDetails user4 =
				 User.withUsername("cl.silveira@gmail.com")
					.password(enc.encode("123"))
					.authorities("ROLE_worker","ROLE_login") //authorities() : NAO adiciona o prefixo "ROLE_"
					.build();
		

		return new InMemoryUserDetailsManager(user3,user4);
	}
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new sha512HexPasswordEncoder();
	}

}