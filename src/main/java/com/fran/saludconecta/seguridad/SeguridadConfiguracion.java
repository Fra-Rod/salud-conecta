package com.fran.saludconecta.seguridad;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SeguridadConfiguracion {

    @Autowired
    private UsuarioDetallesService usuarioDetallesService;

    @Bean
    public PasswordEncoder codificar() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public AuthenticationProvider autenticar() {
        DaoAuthenticationProvider buscador = new DaoAuthenticationProvider();
        buscador.setUserDetailsService(usuarioDetallesService);
        buscador.setPasswordEncoder(codificar());
        return buscador;
    }

    @Bean
    public SecurityFilterChain filtrar(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authenticationProvider(autenticar())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/css/**", "/js/**").permitAll()
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/inicio", true)
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout"))
                .httpBasic(basic -> {
                });

        return http.build();
    }
}
