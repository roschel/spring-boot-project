/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package curso.api.rest.repository;

import curso.api.rest.model.Usuario;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author joao
 */
@Repository
public interface UsuarioRepository extends CrudRepository<Usuario, Long>{
    
    @Query("select user from Usuario user where user.login = ?1")
    Usuario findUserByLogin(String login);
    
}
