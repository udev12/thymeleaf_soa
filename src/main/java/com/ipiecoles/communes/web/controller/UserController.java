package com.ipiecoles.communes.web.controller;

import com.ipiecoles.communes.web.model.Role;
import com.ipiecoles.communes.web.model.User;
import com.ipiecoles.communes.web.repository.RoleRepository;
import com.ipiecoles.communes.web.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

//    @GetMapping("/login")
//    public String login(){
//        return "login";
//    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

//    @GetMapping("/register")
//    public String register(final ModelMap model) {
//        model.addAttribute("user", new User());
//        return "register";
//    }

    @GetMapping("/register")
    public String register(final ModelMap model){
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

//    @PostMapping(value = "/register")
//    public String createNewUser(User user, BindingResult bindingResult, final ModelMap model) throws Exception {
//
//        // Vérifier si user existe déjà avec le même nom
//        User userExists = userRepository.findByUserName((user.getUserName()));
//        if (userExists != null){
//            bindingResult.rejectValue("userName", "error")
//            throw new Exception("il y a séjà un utilisateur avec le nom " + user.getUserName());
//        }
//        // Gérer les erreurs de validation : on reste sur la page
//    if (bindingResult.hasErrors()){
//        // Si pas ok, je reste sur la page d'inscription en indiquant les erreurs
//        model.addAttribute("type", "danger");
//        model.addAttribute("message", "danger");
//
//    }
//        // Si OK je sauvegarde le User en hâchant son mot de passe
//
//        // rediriger vers login avec un message
//        return null;
//    }

//    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
//    public String saveNewCommune(User user, RedirectAttributes attributes, final ModelMap model) {
//
//        user = userRepository.save(user);
//
//        // Erreur si on essayer d'enregistrer, alors que les cellules sont vides
//        if (user.getUserName().isEmpty()) {
//            //Gère une exception
//            throw new EntityNotFoundException("Impossible de trouver la commune de code INSEE " + user.getUserName());
//            //model.put("message", "Impossible de trouver la commune de code INSEE " + codeInsee);
////            return "error";//template error qui affiche un message d'erreur
//        }
//
//        model.put("user", user);
//
//        if (user.getUserName() != null) {
//
//            attributes.addFlashAttribute("type", "success");
//            attributes.addFlashAttribute("message", "Création de l'utilisateur effectuée avec succès !");
//
//            return "redirect:/register/" + user.getUserName();
//
//        }
//
//        attributes.addFlashAttribute("type", "success");
//        attributes.addFlashAttribute("message", "Suppression de la commune effectuée avec succès !");
////        return "detail";
//        return "redirect:/";
//    }
//
//    // Création user
//    @GetMapping("/register/new")
//    public String newUser(
//            @Valid Commune commune,
//            final BindingResult result,
//            RedirectAttributes attributes,
//            final ModelMap model
//    ) {
//        model.put("user", new Commune());
////        model.put("newCommune", true);
////        model.put("update", false); // affichage de la carte : false
//        if (!result.hasErrors()) {
////            creationCommune = true;
////            model.put("creationCommune", true);
//            return "redirect:/register/" + commune.getCodeInsee();
////        }
//        }
//        return "detail";
////        return "redirect:/communes/" + commune.getCodeInsee();
//    }


}