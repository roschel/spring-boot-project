/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package curso.api.rest.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 *
 * @author joao
 */
@Service
@Component
public class JWTTokenAutenticacaoService {

    /**
     * Tempo de validade do token (ms)
     */
    private static final long EXPIRATION_TIME = 120000;

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
            String username) throws Exception {

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
    }
}
