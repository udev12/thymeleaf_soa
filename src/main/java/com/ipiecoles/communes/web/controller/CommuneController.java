package com.ipiecoles.communes.web.controller;

import com.ipiecoles.communes.web.model.Commune;
import com.ipiecoles.communes.web.repository.CommuneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

//import java.awt.*;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Validated
@Controller
public class CommuneController {

    public static final Double DEGRE_LAT_KM = 111d;
    public static final Double DEGRE_LONG_KM = 77d;
    private Boolean creationCommune = true;

    @Autowired
    private CommuneRepository communeRepository;

    // Deuxième façon de configurer l'authentification
//    //Accessible uniquement aux utilisateurs connectés
//    @PreAuthorize("isAuthenticated()")
//    //Accessible uniquement à un rôle particulier
//    //@Secured("ROLE_ADMIN")
//    @Secured({"ROLE_ADMIN", "ROLE_USER"})
//    @GetMapping("/communes/{codeInsee}")
//
//    // Accessible uniquement aux utilisateurs connectés
//    @PreAuthorize("isAuthenticated()")
//    // Accessible uniquement à un rôle en particulier
////    @Secured("ROLE_ADMIN")
//    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    // Modification commune / gestion périmètre
    @GetMapping("/communes/{codeInsee}")
    public String getCommune(
            @PathVariable String codeInsee,
//            @RequestParam(defaultValue = "10") Integer perimetre,
            @RequestParam(defaultValue = "10") @Max(value=20, message="Le périmètre ne peut être supérieur à 20") Integer perimetre,
            final ModelMap model) {

        // Erreur si on recherche un code INSEE inexistant
        Optional<Commune> commune = communeRepository.findById(codeInsee);
        if (commune.isEmpty()) {
            //Gère une exception
            throw new EntityNotFoundException("Impossible de trouver la commune de code INSEE " + codeInsee);
            //model.put("message", "Impossible de trouver la commune de code INSEE " + codeInsee);
//            return "error";//template error qui affiche un message d'erreur
        }
//        creationCommune = true;
//        model.put("creationCommune", true);

        //Récupérer les communes proches de celle-ci
        model.put("commune", commune.get());
        model.put("communesProches", this.findCommunesProches(commune.get(), perimetre));
        model.put("newCommune", false);
        model.put("update", true); // affichage de la carte : false
        model.put("perimetre", perimetre);
        model.put("messagePerimetre", "Périmètre > 20");
        model.put("codeInsee", codeInsee);

//        if (perimetre > 20) {
//            model.addAttribute("type", "danger");
//            model.addAttribute("message", "Le périmètre ne peut être supérieur à 20");
//            perimetre = 10;
//            model.put("perimetre", perimetre);
//        }

        return "detail";

    }

    @PostMapping(value = "/communes", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String saveNewCommune(Commune commune, final ModelMap model, RedirectAttributes attributes) {
        //Ajouter un certain nombre de contrôles...
        commune = communeRepository.save(commune);

        // Erreur si on essayer d'enregistrer, alors que les cellules sont vides
//        Optional<Commune> commune = communeRepository.findById(codeInsee);
        if (commune.getCodeInsee().isEmpty()) {
            //Gère une exception
            throw new EntityNotFoundException("Impossible de trouver la commune de code INSEE " + commune.getCodeInsee());
            //model.put("message", "Impossible de trouver la commune de code INSEE " + codeInsee);
//            return "error";//template error qui affiche un message d'erreur
        }

        model.put("commune", commune);

        // Suppr
//        if (!creationCommune){
//            String messageDeleteCommune = "Suppression de la commune effectuée avec succès !";
//            model.put("messageDeleteCommune", messageDeleteCommune);
//            creationCommune = true;
//        }
//        creationCommune = false;

        if (commune.getCodeInsee() != null) {
////            creationCommune = true;
////            model.put("creationCommune", true);
            attributes.addFlashAttribute("type", "success");
            attributes.addFlashAttribute("message", "Création de la commune effectuée avec succès !");
////                attributes.addFlashAttribute("type", "success");
////                attributes.addFlashAttribute("message", "Suppression de la commune effectuée avec succès !");
//
            return "redirect:/communes/" + commune.getCodeInsee();
//            return "redirect:/";
//
        }
//        else{
//            return "redirect:/communes/" + commune.getCodeInsee();
//        }
//        attributes.addFlashAttribute("type", "success");
//        attributes.addFlashAttribute("message", "Enregistrement de la commune effectué !");
        attributes.addFlashAttribute("type", "success");
        attributes.addFlashAttribute("message", "Suppression de la commune effectuée avec succès !");
//        return "detail";
        return "redirect:/";
    }

//    @PostMapping(value = "/communes/{codeInsee}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
//    public String saveExistingCommune(
//            Commune commune,
//            @PathVariable String codeInsee,
//            final ModelMap model){
//        //Ajouter un certain nombre de contrôles...
//        commune = communeRepository.save(commune);
//        model.put("commune", commune);
//        return "detail";
//    }

    // ON A RAJOUTE @Valid pour la validation
    @PostMapping(value = "/communes/{codeInsee}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String saveExistingCommune(
            @Valid Commune commune,
            //Juste après le paramètre marqué @Valid
            final BindingResult result,
            @PathVariable String codeInsee,
            final ModelMap model,
            RedirectAttributes attributes) {

        //S'il n'y a pas d'erreurs de validation sur le paramètre commune
        if (!result.hasErrors()) {
//            creationCommune = true;
//            model.put("creationCommune", true);
            commune = communeRepository.save(commune);
            attributes.addFlashAttribute("type", "success");
            attributes.addFlashAttribute("message", "Enregistrement de la commune effectué!");
            return "redirect:/communes/" + commune.getCodeInsee();
        }
        //S'il y a des erreurs...
        //Possibilité 1 : Rediriger l'utilisateur vers la page générique d'erreur
        //Possibilité 2 : Laisse sur la même page en affichant les erreurs pour chaque champ
        model.addAttribute("type", "danger");
        model.addAttribute("message", "Erreur lors de la sauvegarde de la commune");
        return "detail";
    }

    @GetMapping("/communes/old")
    public String toto(RedirectAttributes attributes, final ModelMap model) {
//        attributes.addFlashAttribute("type", "success");
//        attributes.addFlashAttribute("message", "Suppression de la commune effectuée avec succès !");
        model.addAttribute("type", "success");
        model.addAttribute("message", "Suppression de la commune effectuée avec succès !");
        return "list";
    }
//    @GetMapping(value = "/" /*, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE*/)
//    public String saveDeleteCommune(/*Commune commune, final ModelMap model,*/
//            RedirectAttributes attributes) {
//        //Ajouter un certain nombre de contrôles...
////        commune = communeRepository.save(commune);
////
////        model.put("commune", commune);
//
//
//        attributes.addFlashAttribute("type", "success");
//        attributes.addFlashAttribute("message", "Suppression de la commune effectuée avec succès !");
////        attributes.addFlashAttribute("type", "success");
////        attributes.addFlashAttribute("message", "Enregistrement de la commune effectué !");
//        return "list";
////        return "redirect:/";
//    }

    // Création commune
    @GetMapping("/communes/new")
    public String newCommune(
            @Valid Commune commune,
            final BindingResult result,
            RedirectAttributes attributes,
            final ModelMap model
    ) {
        model.put("commune", new Commune());
        model.put("newCommune", true);
        model.put("update", false); // affichage de la carte : false
        if (!result.hasErrors()) {
//            creationCommune = true;
//            model.put("creationCommune", true);
            return "redirect:/communes/" + commune.getCodeInsee();
//        }
        }
        return "detail";
//        return "redirect:/communes/" + commune.getCodeInsee();
    }

//    @GetMapping("/communes/{codeInsee}/delete")
//    public String deleteCommune(
//            @PathVariable String codeInsee) {
//        communeRepository.deleteById(codeInsee);
//        return "REDIRECTION A GERER";
//    }

//    @GetMapping("/communes/{codeInsee}/delete")
//    public String deleteCommune(
//            @Valid Commune commune,
//            final BindingResult result,
//            @PathVariable String codeInsee,
//            RedirectAttributes attributes,
//            final ModelMap model) {
//        if (!result.hasErrors()) {
//            communeRepository.deleteById(codeInsee);
////            creationCommune = false;
////            model.put("creationCommune", false);
//
//            attributes.addFlashAttribute("flashAttribute", "redirectWithRedirectAttributes");
//            attributes.addAttribute("attribute", "redirectWithRedirectAttributes");
//
//            return "redirect:" + "/";
////            return "redirect:/list.html";
//
////        return "list";
////        String url = "/communes/" + codeInsee + "/delete";
////        return new RedirectView(url).toString();
////        return "list";
//        }
//
//        return "detail";
//    }

    @GetMapping("/communes/{codeInsee}/delete")
//    @RequestMapping(value = "/communes/{codeInsee}/delete", method = RequestMethod.DELETE)
    public String deleteCommune(
            @Valid Commune commune,
            final BindingResult result,
            @PathVariable String codeInsee,
            RedirectAttributes attributes,
            final ModelMap model) {

        // Erreur si on recherche un code INSEE inexistant
        Optional<Commune> commune1 = communeRepository.findById(codeInsee);
        if (commune1.isEmpty()) {
            //Gère une exception
            throw new EntityNotFoundException("Impossible de trouver la commune de code INSEE " + codeInsee);
        }

        if (!result.hasErrors()) {
            communeRepository.deleteById(codeInsee);
            attributes.addFlashAttribute("type", "success");
            attributes.addFlashAttribute("message", "Suppression de la commune effectuée avec succès !");
//            model.put("messageDeleteCommune", "Suppression de la commune effectuée avec succès !");
            return "redirect:" + "/";
        }
        model.addAttribute("type", "danger");
        model.addAttribute("message", "Erreur lors de la suppression de la commune");
        return "detail";
    }


    /**
     * Récupère une liste des communes dans un périmètre autour d'une commune
     *
     * @param commune       La commune sur laquelle porte la recherche
     * @param perimetreEnKm Le périmètre de recherche en kilomètre
     * @return La liste des communes triées de la plus proche à la plus lointaine
     */
    private List<Commune> findCommunesProches(Commune commune, Integer perimetreEnKm) {
        Double latMin, latMax, longMin, longMax, degreLat, degreLong;
        //1 degré latitude = 111km, 1 degré longitude = 77km
        degreLat = perimetreEnKm / DEGRE_LAT_KM;
        degreLong = perimetreEnKm / DEGRE_LONG_KM;
        latMin = commune.getLatitude() - degreLat;
        latMax = commune.getLatitude() + degreLat;
        longMin = commune.getLongitude() - degreLong;
        longMax = commune.getLongitude() + degreLong;
        List<Commune> communesProches = communeRepository.findByLatitudeBetweenAndLongitudeBetween(latMin, latMax, longMin, longMax);
        ;
        return communesProches.stream().
                filter(commune1 -> !commune1.getNom().equals(commune.getNom()) && commune1.getDistance(
                        commune.getLatitude(), commune.getLongitude()) <= perimetreEnKm).
                sorted(Comparator.comparing(o -> o.getDistance(commune.getLatitude(), commune.getLongitude()))).
                collect(Collectors.toList());
    }

}

