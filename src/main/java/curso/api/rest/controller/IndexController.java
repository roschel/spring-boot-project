/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package curso.api.rest.controller;

import com.google.gson.Gson;
import curso.api.rest.model.Usuario;
import curso.api.rest.model.UsuarioDTO;
import curso.api.rest.repository.UsuarioRepository;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    public ResponseEntity<UsuarioDTO> get(@PathVariable(value="id") Long id) {
        
        
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        
        return new ResponseEntity<UsuarioDTO>(new UsuarioDTO(usuario.get()),HttpStatus.OK);
        
    }
    
    @GetMapping(value = "/", produces = "application/json")
    @CacheEvict(value="cacheusuariosget", allEntries = true)
    @CachePut("cacheusuariosget")
    public ResponseEntity<List<Usuario>> get() throws InterruptedException {
        

        List<Usuario> list = (List<Usuario>) usuarioRepository.findAll();
        
        return new ResponseEntity<List<Usuario>>(list,HttpStatus.OK);
        
    }
    
    @PostMapping(value="/", produces="application/json")
    public ResponseEntity<Usuario> post(@RequestBody Usuario usuario) throws Exception {
        
        //Associando telefones ao objeto pai usuario
        for (int pos = 0;pos<usuario.getTelefones().size();pos++){
            usuario.getTelefones().get(pos).setUsuario(usuario);
        }

        /**
         * Consumindo API externa de CEP
         */
        URL url = new URL(String.format("https://viacep.com.br/ws/%s/json/", usuario.getCep()));
        URLConnection connection = url.openConnection();
        InputStream is = connection.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

        String cep="";
        StringBuilder jsonCep = new StringBuilder();

        while ((cep = br.readLine()) != null){
            jsonCep.append(cep);
        }

        // Convertendo string em json
        Usuario userAux = new Gson().fromJson(jsonCep.toString(),Usuario.class);
        usuario.setCep(userAux.getCep());
        usuario.setLogradouro(userAux.getLogradouro());
        usuario.setBairro(userAux.getBairro());
        usuario.setComplemento(userAux.getComplemento());
        usuario.setLocalidade(userAux.getLocalidade());
        usuario.setUf(userAux.getUf());

        /**
         * Consumindo API externa de CEP
         */


        String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
        usuario.setSenha(senhaCriptografada);

        Usuario usuarioSave = usuarioRepository.save(usuario);
        
        return new ResponseEntity<Usuario> (usuarioSave,HttpStatus.OK);
        
    }
    
    @PutMapping(value="/", produces="application/json")
    public ResponseEntity<Usuario> put(@RequestBody Usuario usuario) throws Exception{
        
        //Associando telefones ao objeto pai usuario
        for (int pos = 0;pos<usuario.getTelefones().size();pos++){
            usuario.getTelefones().get(pos).setUsuario(usuario);
        }

        Usuario userTemporario = usuarioRepository.findUserByLogin(usuario.getLogin());

        // verificando se a senha passada é diferente da cadastrada
        if (!userTemporario.getSenha().equals(usuario.getSenha())){
            if(usuario.getSenha() != null){
                String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
                usuario.setSenha(senhaCriptografada);
            }else{
                //Caso senha seja um campo vazio, pegar a ultima senha cadastrada
                usuario.setSenha(userTemporario.getSenha());
            }
        }

        if (usuario.getCep() != null) {
            /**
             * Consumindo API externa de CEP
             */
            URL url = new URL(String.format("https://viacep.com.br/ws/%s/json/", usuario.getCep()));
            URLConnection connection = url.openConnection();
            InputStream is = connection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            String cep="";
            StringBuilder jsonCep = new StringBuilder();

            while ((cep = br.readLine()) != null){
                jsonCep.append(cep);
            }

            // Convertendo string em json
            Usuario userAux = new Gson().fromJson(jsonCep.toString(),Usuario.class);
            usuario.setCep(userAux.getCep());
            usuario.setLogradouro(userAux.getLogradouro());
            usuario.setBairro(userAux.getBairro());
            usuario.setComplemento(userAux.getComplemento());
            usuario.setLocalidade(userAux.getLocalidade());
            usuario.setUf(userAux.getUf());

            /**
             * Consumindo API externa de CEP
             */

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
