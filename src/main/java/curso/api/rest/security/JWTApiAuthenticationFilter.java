/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package curso.api.rest.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author joao
 */
public class JWTApiAuthenticationFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        /**
         * Estabelece a autenticação para a requisição
         */
        Authentication authentication = new JWTTokenAutenticacaoService().getAuthentication((HttpServletRequest) request,
                (HttpServletResponse) response);

        /**
         * Coloca o processo de autenticação no spring security
         */
        SecurityContextHolder.getContext().setAuthentication(authentication);

        /**
         * Continua o processo
         */
        chain.doFilter(request, response);
    }
}
