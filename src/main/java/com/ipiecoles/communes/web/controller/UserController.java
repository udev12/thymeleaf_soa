package com.ipiecoles.communes.web.controller;

import com.ipiecoles.communes.web.model.Commune;
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
 *
 */
@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login() {
//        model.addAttribute("user", new User());
//        attributes.addFlashAttribute("type", "success");
//        attributes.addFlashAttribute("message", "Connexion réussie !");
//        return "redirect:/?successfulConnection=true";
        return "login";
    }

    @GetMapping("/login/input")
    public String loginInput(/*@Valid User user, */final ModelMap model, RedirectAttributes attributes) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!auth.isAuthenticated()) {
            attributes.addFlashAttribute("type", "danger");
            attributes.addFlashAttribute("message", "Echec de la connexion !");
            return "redirect:/login?error=true";
        }
        attributes.addFlashAttribute("type", "success");
        attributes.addFlashAttribute("message", "Connexion réussie !");
        return "redirect:/?successfulConnection=true";

    }

    @GetMapping("/register")
    public String register(final ModelMap model) {
        model.addAttribute("user", new User());
        return "register";
    }

    // NB : AddAttribute : return / AddFlashAttribute : redirect
    @PostMapping("/register")
    public String createNewUser(@Valid User user,
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