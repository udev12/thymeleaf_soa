package com.ipiecoles.communes.web.service;

import com.ipiecoles.communes.web.model.Role;
import com.ipiecoles.communes.web.model.User;
import com.ipiecoles.communes.web.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 *
 */
@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    //@Transactional // pour éviter l'erreur au moment du login
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // Récupérer en base l'utilisateur correpsodndant a unom passé en paramètre
        User user = userRepository.findByUserName(username);

        // Le ver une exception si l'utilisateur n'est pas trouvé
        if (user == null) {
            throw new UsernameNotFoundException("Aucun utilisateur nommé " + username + " n'a pas été trouvé");
        }

// Construire un UserDetails à partir de l'utilisateur récupéré
        return buildSpringUserFromMyUser(user);
    }

    //    private UserDetails buildSpringUserFromMyUser(User user) {
//
//    }
//    private UserDetails buildSpringUserFromMyUser(User user) {
//        // Initialise la liste des droits de l'utilisateur à partir d la liste
//        Set<GrantedAuthority> authorities = new HashSet<>();
////        org.springframework.security.core.userdetails.User()
//        org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword())
//    }
    private UserDetails buildSpringUserFromMyUser(User user) {
        //Initialise la liste des droits de l'utilisateur à partir de la liste
        //des rôles présents en BDD pour cet utilisateur
        Set<GrantedAuthority> authorities = new HashSet<>();
        for (Role role : user.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(role.getRole()));
        }
        //Instanciation du User Spring à partir des infos du user de la BDD
        org.springframework.security.core.userdetails.User springUser =
                new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(), authorities);
        return springUser;
    }

}
