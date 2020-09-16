/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package curso.api.rest.service;

import curso.api.rest.model.Usuario;
import curso.api.rest.repository.UsuarioRepository;
import static org.apache.tomcat.jni.User.username;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 *
 * @author joao
 */

@Service
public class ImplemetacaoUserDetailsService implements UserDetailsService{

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        /*Consulta no banco o usuário*/
        Usuario usuario = usuarioRepository.findUserByLogin(username);
        
        if (usuario == null){
            throw new UsernameNotFoundException("Usuário não foi encontrado");
        }
        
        return new User(usuario.getLogin(), usuario.getPassword(), 
                usuario.getAuthorities());
    }
    
}
