package com.fran.saludconecta.seguridad;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.fran.saludconecta.usuario.dto.UsuarioDTO;
import com.fran.saludconecta.usuario.service.IUsuarioService;

@Service
public class UsuarioDetallesService implements UserDetailsService {

    @Autowired
    private IUsuarioService usuarioService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<UsuarioDTO> todosLosUsuarios = usuarioService.mostrarTodos();

        for (UsuarioDTO usuarioDTO : todosLosUsuarios) {
            if (usuarioDTO.getNombre().equals(username)) {
                return new UsuarioDetalles(usuarioDTO);
            }
        }

        throw new UsernameNotFoundException("Usuario no encontrado: " + username);
    }
}
