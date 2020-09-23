/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package curso.api.rest.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import curso.api.rest.model.Usuario;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 *
 * @author joao
 * Estabelece nosso gerenciador de token
 */
public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter{
    
    /**
     * 
     * Configurando o gerenciador de autenticação
     */
    protected JWTLoginFilter(String url, AuthenticationManager authenticationManager){
        super(new AntPathRequestMatcher(url));
        
        /**
         * Gerenciador de autenticação
         */
        setAuthenticationManager(authenticationManager);
    }
    
    /**
     * Retorna usuário ao processar autenticação
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, 
            HttpServletResponse hsr1) 
            throws AuthenticationException, IOException, ServletException {
        
        /*Pegando o token para validar*/
        Usuario user = new ObjectMapper().readValue(request.getInputStream(), Usuario.class);
        
        /* Retorna o usuário (login, senha e acessos) */
        
        return getAuthenticationManager().authenticate(new 
        UsernamePasswordAuthenticationToken(user.getLogin(), user.getSenha()));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, 
            HttpServletResponse response, FilterChain chain, 
            Authentication authResult) throws IOException, ServletException {
        
        
        
        
        new JWTTokenAutenticacaoService().addAuthentication(response, authResult.getName());
    }
    
    
    
}
