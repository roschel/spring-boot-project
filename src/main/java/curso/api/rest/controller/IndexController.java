/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package curso.api.rest.controller;

import curso.api.rest.model.Usuario;
import curso.api.rest.repository.UsuarioRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author joao Arquitetura REST
 */
@RestController
@RequestMapping(value = "/usuario")
public class IndexController {
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     *
     * Serviço RESTful
     */
    
       
    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<Usuario> get(@PathVariable(value="id") Long id) {
        
        
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        
        return new ResponseEntity(usuario.get(),HttpStatus.OK);
        
    }
    
    @GetMapping(value = "/", produces = "application/json")
    public ResponseEntity<List<Usuario>> get() {
        

        List<Usuario> list = (List<Usuario>) usuarioRepository.findAll();
        
        return new ResponseEntity<List<Usuario>>(list,HttpStatus.OK);
        
    }
    
    @PostMapping(value="/", produces="application/json")
    public ResponseEntity<Usuario> post(@RequestBody Usuario usuario) {
        
        //Associando telefones ao objeto pai usuario
        for (int pos = 0;pos<usuario.getTelefones().size();pos++){
            usuario.getTelefones().get(pos).setUsuario(usuario);
        }
        
        Usuario usuarioSave = usuarioRepository.save(usuario);
        
        return new ResponseEntity<Usuario> (usuarioSave,HttpStatus.OK);
        
    }
    
    @PutMapping(value="/", produces="application/json")
    public ResponseEntity<Usuario> put(@RequestBody Usuario usuario) {
        
        //Associando telefones ao objeto pai usuario
        for (int pos = 0;pos<usuario.getTelefones().size();pos++){
            usuario.getTelefones().get(pos).setUsuario(usuario);
        }
        
        Usuario usuarioSave = usuarioRepository.save(usuario);
        
        return new ResponseEntity<Usuario> (usuarioSave,HttpStatus.OK);
        
    }
    
    @DeleteMapping(value = "/{id}", produces = "application/text")
    public String delete(@PathVariable(value="id") Long id) {
        
        
        usuarioRepository.deleteById(id);
        
        return "usuario deletado";
        
    }  
   
}
