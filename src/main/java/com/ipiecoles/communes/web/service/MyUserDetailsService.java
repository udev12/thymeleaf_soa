package com.ipiecoles.communes.web.service;

import com.ipiecoles.communes.web.controller.UserController;
import com.ipiecoles.communes.web.model.Role;
import com.ipiecoles.communes.web.model.User;
import com.ipiecoles.communes.web.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * Service qui implémente l'interface Spring "UserDetailsService"
 */
@Service
public class MyUserDetailsService implements UserDetailsService {

    static final Logger logger = LoggerFactory.getLogger(MyUserDetailsService.class);

    @Autowired
    UserRepository userRepository;

    /**
     * Permet de construire un utilisateur Spring à partir de l'utilisateur de type "User"
     *
     * @param username : nom d'utilisateur
     * @return l'utilisateur Spring construit
     * @throws UsernameNotFoundException s'il n'y a pas d'utilisateur
     */
    @Override
    //@Transactional // pour éviter l'erreur au moment du login
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Récupérer en base l'utilisateur correspondant a unom passé en paramètre
        User user = userRepository.findByUserName(username);

        // Vers une exception si l'utilisateur n'est pas trouvé
        if (user == null) {
            logger.error("Aucun utilisateur nommé {} n'a pas été trouvé", username);
            throw new UsernameNotFoundException("Aucun utilisateur nommé " + username + " n'a pas été trouvé");
        }

        // Construire un UserDetails à partir de l'utilisateur récupéré
        logger.info("Construction de l'utilisateur Spring réussie!");
        return buildSpringUserFromMyUser(user);
    }

    /**
     * Méthode qui permet d'instancier un utilisateur Spring
     *
     * @param user : l'utilisateur
     * @return l'utilisateur Spring instancié
     */
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
