/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package curso.api.rest.security;

import curso.api.rest.cursospringrestapi.ApplicationContextLoad;
import curso.api.rest.model.Usuario;
import curso.api.rest.repository.UsuarioRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.io.IOException;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @author joao
 */
@Service
@Component
public class JWTTokenAutenticacaoService {

    /**
     * Tempo de validade do token (ms)
     */
    private static final long EXPIRATION_TIME = 86400000;

    /**
     * Uma senha unica para compor a autenticação e ajudar na segurança
     */
    private static final String SECRET = "coracaopeludo";

    /**
     * Prefixo padrão de token
     */
    private static final String TOKEN_PREFIX = "Bearer";

    private static final String HEADER_STRING = "Authorization";

    /**
     * Gerando token de autenticação e adicionando ao cabeçalho e resposta http
     */
    public void addAuthentication(HttpServletResponse response,
                                  String username) throws IOException {

        /**
         * Montagem do token
         */
        String JWT = Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();

        String token = TOKEN_PREFIX + " " + JWT;

        /**
         * Adiciona o cabeçalho HTTP
         */
        response.addHeader(HEADER_STRING, token);

        ApplicationContextLoad.getApplicationContext().getBean(UsuarioRepository.class).atualizaTokenUser(JWT,username);

        /**
         * Liberando resposta para ports diferentes que usam api ou clientes web
         */
        liberacaoCors(response);

        /**
         * Escreve token como resposta no corpo do http
         */
        response.getWriter().write("{\"Authorization\": \"" + token + "\"}");
    }

    /**
     * Validando JWT
     * <p>
     * Retorna o usuário validado com token ou caso não seja válido retorna null
     */
    public Authentication getAuthentication(HttpServletRequest request, HttpServletResponse response) {

        /**
         * recebe o token eniado pelo cabeçalho
         */
        String token = request.getHeader(HEADER_STRING);

        try {
            if (token != null) {

                String tokenLimpo = token.replace(TOKEN_PREFIX, "").trim();

                /**
                 * Faz a validação do token do usuário na requisição retorna o
                 * usuário. Exemplo: João
                 */
                String user = Jwts.parser().setSigningKey(SECRET)
                        .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                        .getBody().getSubject();

                if (user != null) {

                    Usuario usuario = ApplicationContextLoad.getApplicationContext()
                            .getBean(UsuarioRepository.class).findUserByLogin(user);

                    if (usuario != null) {
                        if (tokenLimpo.equalsIgnoreCase(usuario.getToken())){
                            return new UsernamePasswordAuthenticationToken(
                                    usuario.getLogin(),
                                    usuario.getPassword(),
                                    usuario.getAuthorities()
                            );
                        }
                    }
                }
            }
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            try {
                response.getOutputStream().println("Seu token está expirado, faça um novo login.");
            } catch (IOException e1) {}
        }

        liberacaoCors(response);
        return null;

    }

    private void liberacaoCors(HttpServletResponse response) {
        if (response.getHeader("Access-Control-Allow-Origin") == null) {
            response.addHeader("Access-Control-Allow-Origin", "*");
        }

        if (response.getHeader("Access-Control-Allow-Headers") == null) {
            response.addHeader("Access-Control-Allow-Headers", "*");
        }

        if (response.getHeader("Access-Control-Request-Headers") == null) {
            response.addHeader("Access-Control-Request-Headers", "*");
        }

        if (response.getHeader("Access-Control-Allow-Methods") == null) {
            response.addHeader("Access-Control-Allow-Methods", "*");
        }
    }
}
