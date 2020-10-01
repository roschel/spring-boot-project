/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package curso.api.rest.security;

import curso.api.rest.service.ImplemetacaoUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Class to map url, address, authority or block url
 *
 * @author joao
 */
@Configuration
@EnableWebSecurity
public class WebConfigSecurity extends WebSecurityConfigurerAdapter {

    @Autowired
    private ImplemetacaoUserDetailsService implemetacaoUserDetailsService;

    /*Configura as solicitações de acesso por http*/
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        /**
         * Ativando proteção contra usuário que não estão validados por token
         */
        http.csrf().csrfTokenRepository(CookieCsrfTokenRepository
                .withHttpOnlyFalse())
                /**
                 * Ativando a restrição a URL. Ativando a permissão para acesso
                 * a página inicial do sistema. Qualquer um poderá acessar essa
                 * página Ex: sistema.com.br/index
                 */
                .disable().authorizeRequests().antMatchers("/").permitAll()
                .antMatchers("/index").permitAll()

                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                /**
                 * URL logout - Redireciona após o user deslogar do sistema
                 */
                .anyRequest().authenticated().and().logout().logoutSuccessUrl("/index")
                /**
                 * Mapeia url de logout e invalida o usuario
                 */
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))

                /**
                 * Filtra requisições de login para autenticação
                 */
                .and().addFilterBefore(new JWTLoginFilter("/login", authenticationManager()), UsernamePasswordAuthenticationFilter.class)
                /**
                 * Filtra demais requisições para verificar a presença do token jwt no header http
                 */
                .addFilterBefore(new JWTApiAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Override

    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        /*Service que irá consultar o usuário no banco de dados*/
        auth.userDetailsService(implemetacaoUserDetailsService)
                /*Padrão de codificação de senha do usuário*/
                .passwordEncoder(new BCryptPasswordEncoder());
    }

}
