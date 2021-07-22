package com.ipiecoles.communes.web.controller;

import com.ipiecoles.communes.web.model.Role;
import com.ipiecoles.communes.web.model.User;
import com.ipiecoles.communes.web.repository.RoleRepository;
import com.ipiecoles.communes.web.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Collections;
import java.util.HashSet;

/**
 * Cette classe gère les utilisateurs : inscription et connexion
 */
@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Cet endpoint permet d'afficher la page "login"
     * @return la vue login
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * Méthode qui gère la redirection après la saisie du nom d'utilisateur et du mot de passe
     * @param model : variable de type "ModelMap"
     * @param attributes : paramètre qui permet la redirection
     * @return le template "login.html" ou le template "list.html"
     */
    @GetMapping("/login/input") // endpoint saisie login
    public String loginInput(final ModelMap model, RedirectAttributes attributes) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Si l'authentification échoue, on reste sur la page d'authentification
        if (!auth.isAuthenticated()) {
            attributes.addFlashAttribute("type", "danger");
            attributes.addFlashAttribute("message", "Echec de la connexion !");
            return "redirect:/login?error=true";
        }
        // En cas de succès, on est redirigé vers la liste des communes
        attributes.addFlashAttribute("type", "success");
        attributes.addFlashAttribute("message", "Connexion réussie !");
        return "redirect:/?successfulConnection=true";
    }

    /**
     * Méthode pour l'affichage du template d'inscription
     * @param model : variable de type "ModelMap"
     * @return le template "register.html"
     */
    @GetMapping("/register")
    public String register(final ModelMap model) {
        model.addAttribute("user", new User());
        return "register";
    }

    /**
     * Permet de créer un nouvel utilisateur
     * @param user : l'utilisateur
     * @param bindingResult : pour la validation
     * @param model : variable de type "ModelMap"
     * @param attributes : paramètre pour le return sur le template
     * @return : le template "register.html"
     */
    // NB : AddAttribute : return / AddFlashAttribute : redirect
    @PostMapping("/register") // endpoint inscription
    public String createNewUser(@Valid User user, // @Valid est utile pour la validation
                                BindingResult bindingResult,
                                final ModelMap model,
                                RedirectAttributes attributes) {
        //Vérifier si un User existe déjà avec le même nom
        User userExists = userRepository.findByUserName(user.getUserName());
        if (userExists != null) {
            bindingResult.rejectValue("userName", "error.username",
                    "Nom d'utilisateur déjà pris");
        }

        //Gérer les erreurs de validation
        if (bindingResult.hasErrors()) {
            //Si pas OK je reste sur la page d'inscription
            // en indiquant les erreurs pour chaque champ
            model.addAttribute("type", "danger");
            model.addAttribute("message", "Erreur lors de l'inscription de l'utilisateur");
            return "register";
        }

        //Si OK je sauvegarde le User en hâchant son mot de passe
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        //Affecter le rôle USER...
        Role userRole = roleRepository.findByRole("ROLE_USER");
        user.setRoles(new HashSet<>(Collections.singletonList(userRole)));
        //Gérer une validation par email... Ici valide par défaut
        user.setActive(true);
        //Sauvegarde en BDD
        userRepository.save(user);

        //Redirige vers Login avec un message de succès
        attributes.addFlashAttribute("type", "success");
        attributes.addFlashAttribute("message", "Inscription réussie, vous pouvez vous connecter");
        return "redirect:/login";
    }

}