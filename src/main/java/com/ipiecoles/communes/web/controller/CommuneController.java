
// CODE DU 21/07

package com.ipiecoles.communes.web.controller;

import com.ipiecoles.communes.web.model.Commune;
import com.ipiecoles.communes.web.repository.CommuneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * On trouve principalement dans cette classe, les méthodes qui participent au dynamisme du template "detail"
 */
//@Validated // à enlever, si on veut que les erreurs soient affichées sur la page de saisie, et ce, pour chaque champ
@Controller
public class CommuneController {

    public static final Double DEGRE_LAT_KM = 111d;
    public static final Double DEGRE_LONG_KM = 77d;
    boolean update = true;
//    private Boolean newCommune = false;

    @Autowired
    private CommuneRepository communeRepository;

    @GetMapping("/communes/{codeInsee}")
    public String getCommune(
            @PathVariable String codeInsee,
            @RequestParam(defaultValue = "10") /*@Max(value = 20, message = "Le périmètre ne peut être supérieur à 20")*/ Integer perimetre,
            final ModelMap model,
            RedirectAttributes attributes) throws EntityNotFoundException {

        // Erreur si on recherche un code INSEE inexistant
        Optional<Commune> commune = communeRepository.findById(codeInsee);
        if (commune.isEmpty()) {
            //Gère une exception
            throw new EntityNotFoundException("Impossible de trouver la commune de code INSEE " + codeInsee);
        }

        //Récupérer les communes proches de celle-ci
        model.put("commune", commune.get());
        model.put("communesProches", this.findCommunesProches(commune.get(), perimetre));
        model.put("newCommune", false);
        model.put("update", true); // affichage de la carte : false
        if (perimetre > 20) {
            perimetre = 10;
            model.put("perimetre", perimetre);
            attributes.addFlashAttribute("type", "danger");
            attributes.addFlashAttribute("message", "Le périmètre ne peut être supérieur à 20!");
            return "redirect:/communes/" + codeInsee;
//            model.addAttribute("message", "Le périmètre ne peut être supérieur à 20!");
//            model.put("horsPerimetre", true);
//            model.put("messagePerimetre", "Périmètre > 20");
        } else{
            model.put("perimetre", perimetre);
//            model.put("horsPerimetre", false);
        }
        model.put("codeInsee", codeInsee);

        return "detail";

    }
//    @GetMapping("/communes/{codeInsee}")
//    public String getCommune(
//            @PathVariable String codeInsee,
//            @RequestParam(defaultValue = "10") Integer perimetre,
//            final ModelMap model) throws EntityNotFoundException {
//        Optional<Commune> commune = communeRepository.findById(codeInsee);
//        update = false;
//        if (!commune.isEmpty()) {
//            //Récupérer les communes proches de celle-ci
//            model.put("commune", commune.get());
//            model.put("communesProches", this.findCommunesProches(commune.get(), perimetre));
//            model.put("newCommune", false);
//            return "detail";
//        }
//        //Gère une exception
//        throw new EntityNotFoundException("Impossible de trouver la commune de code INSEE " + codeInsee);
//
//    }

//    @GetMapping("/communes/{codeInsee}")
//    public String getCommune(
//            @PathVariable String codeInsee,
//            @RequestParam(defaultValue = "10") Integer perimetre,
//            final ModelMap model) throws EntityNotFoundException{
//        Optional<Commune> commune = communeRepository.findById(codeInsee);
//        update = false;
//        if (commune.isEmpty() && update) {
//            //Gère une exception
//            throw new EntityNotFoundException("Impossible de trouver la commune de code INSEE " + codeInsee);
//        }
//        //Récupérer les communes proches de celle-ci
//        model.put("commune", commune.get());
//        model.put("communesProches", this.findCommunesProches(commune.get(), perimetre));
//        model.put("newCommune", false);
//        return "detail";
//    }

    //    @PostMapping(value = "/communes", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
//    public String saveNewCommune(Commune commune, final ModelMap model) {
//        //Ajouter un certain nombre de contrôles...
//        commune = communeRepository.save(commune);
//        model.put("commune", commune);
//        return "detail";
//    }
    @PostMapping(value = "/communes", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String saveNewCommune(
            @Valid Commune commune,
            //Juste après le paramètre marqué @Valid
            final BindingResult result,
//            @PathVariable String codeInsee,
            final ModelMap model,
            RedirectAttributes attributes) {
        //S'il n'y a pas d'erreurs de validation sur le paramètre commune
        if (!result.hasErrors()) {
            commune = communeRepository.save(commune);
            attributes.addFlashAttribute("type", "success");
            attributes.addFlashAttribute("message", "Enregistrement de la commune effectué !");
            return "redirect:/communes/" + commune.getCodeInsee();
        }
        //S'il y a des erreurs...
        //Possibilité 1 : Rediriger l'utilisateur vers la page générique d'erreur
        //Possibilité 2 : Laisse sur la même page en affichant les erreurs pour chaque champ
        model.addAttribute("type", "danger");
        model.addAttribute("message", "Erreur lors de la sauvegarde de la commune");
        return "detail";
    }

//    @PostMapping(value = "/communes", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
//    public String saveNewCommune(@Valid Commune commune,
//                                 final BindingResult bindingResult,
//                                 final ModelMap model,
//                                 RedirectAttributes attributes) /*throws EntityNotFoundException*/ {
//        //Ajouter un certain nombre de contrôles...
//
//        //Vérifier si une commune existe déjà avec le même nom
//        Commune communeExists = communeRepository.findCommuneByNom(commune.getNom());
//        if (communeExists != null) {
//            bindingResult.rejectValue("codeInsee", "error.codeInsee",
//                    "Cette commune existe déjà");
//        }
//
//        commune = communeRepository.save(commune);
//
//        // Erreur si on essayer d'enregistrer, alors que les cellules sont vides
////        if (commune.getCodeInsee().isEmpty()) {
////            //Gère une exception
////            throw new EntityNotFoundException("Impossible de trouver la commune de code INSEE " + commune.getCodeInsee());
////        }
//
//        model.put("commune", commune);
//
//        if (commune.getCodeInsee() != null) {
//            attributes.addFlashAttribute("type", "success");
//            attributes.addFlashAttribute("message", "Création de la commune effectuée avec succès !");
//            return "redirect:/communes/" + commune.getCodeInsee();
//        }
//
//        attributes.addFlashAttribute("type", "success");
//        attributes.addFlashAttribute("message", "Suppression de la commune effectuée avec succès !");
//        return "redirect:/";
//    }



//    @PostMapping(value = "/communes", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
//    public String saveNewCommune(Commune commune, final ModelMap model, RedirectAttributes attributes) throws EntityNotFoundException {
//        //Ajouter un certain nombre de contrôles...
//        commune = communeRepository.save(commune);
//
//        // Erreur si on essayer d'enregistrer, alors que les cellules sont vides
//        if (commune.getCodeInsee().isEmpty()) {
//            //Gère une exception
//            throw new EntityNotFoundException("Impossible de trouver la commune de code INSEE " + commune.getCodeInsee());
//        }
//
//        model.put("commune", commune);
//
//        if (commune.getCodeInsee() != null) {
//            attributes.addFlashAttribute("type", "success");
//            attributes.addFlashAttribute("message", "Création de la commune effectuée avec succès !");
//            return "redirect:/communes/" + commune.getCodeInsee();
//        }
//
//        attributes.addFlashAttribute("type", "success");
//        attributes.addFlashAttribute("message", "Suppression de la commune effectuée avec succès !");
//        return "redirect:/";
//    }

    /**
     * Méthode pour l'affichage du template d'ajout d'une commune
     *
     * @param model : variable de type "ModelMap"
     * @return le template "detail.html"
     */

    @GetMapping("communes/new")
    public String newCommune(final ModelMap model) {
        model.addAttribute("commune", new Commune());
        model.put("newCommune", true);
        return "detail";
    }

//    /**
//     * Permet de créer une nouvelle commune
//     *
//     * @param commune       : l'objet "Commune"
//     * @param bindingResult : pour la validation
//     * @param model         : variable de type "ModelMap"
//     * @param attributes    : paramètre pour le return sur le template
//     * @return : le template "detail.html"
//     */
//    // NB : AddAttribute : return / AddFlashAttribute : redirect
//    @PostMapping("communes/new") // endpoint inscription
//    public String createNewCommmune(@Valid Commune commune, // @Valid est utile pour la validation
//                                    BindingResult bindingResult,
//                                    final ModelMap model,
//                                    RedirectAttributes attributes) {
//        //Vérifier si une commune existe déjà avec le même nom
//        Commune communeExists = communeRepository.findCommuneByNom(commune.getNom());
//        if (communeExists != null) {
//            bindingResult.rejectValue("codeInsee", "error.codeInsee",
//                    "Cette commune existe déjà");
//        }
//
//        //Gérer les erreurs de validation
//        if (bindingResult.hasErrors() || commune.getCodeInsee() == null) {
//            //Si pas OK je reste sur la page d'ajout d'une commune
//            // en indiquant les erreurs pour chaque champ
//            model.addAttribute("type", "danger");
//            model.addAttribute("message", "Erreur lors de la création de la commune");
//            return "detail";
//        }
//
//        model.put("commune", commune);
//
//        //Sauvegarde en BDD
//        communeRepository.save(commune);
//
//        //Redirige vers "/communes/CODEINSEE" avec un message de succès
////        if (commune.getCodeInsee() != null) {
//        attributes.addFlashAttribute("type", "success");
//        attributes.addFlashAttribute("message", "La commune a été créée avec succès!");
//        return "redirect:/communes";
////        }
//    }


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

//    @GetMapping("/communes/new")
//    public String newCommune(final ModelMap model) {
//        model.put("commune", new Commune());
//        model.put("newCommune", true);
//        return "detail";
//    }

//    @PostMapping("/communes/new")
//    public String createNewCommune(
//            @Valid Commune commune,
//            final BindingResult result,
//            final ModelMap model,
//            RedirectAttributes attributes) {
//        //Vérifier si une commune existe déjà avec le même code insee
//        Commune communeExists = communeRepository.findCommuneByCodeinsee(commune.getCodeInsee());
//        if (communeExists != null) {
//            result.rejectValue("codeInsee", "error.codeInsee",
//                    "Cette commune existe déjà");
//        }
//        model.put("commune", new Commune());
//        model.put("newCommune", true);
//        if (result.hasErrors()) {
//            model.addAttribute("type", "danger");
//            model.addAttribute("message", "Erreur lors de l'enregistrement!");
//            return "detail";
//        }
//
//        attributes.addFlashAttribute("type", "success");
//        attributes.addFlashAttribute("message", "Commune créée avec succès!");
//        return "redirect:/communes/" + commune.getCodeInsee();
//
////        attributes.addFlashAttribute("type", "danger");
////        attributes.addFlashAttribute("message", "Erreur lors de l'enregistrement!");
////        return "redirect:/communes/new";
//
//    }

    ////    @GetMapping("/communes/new")
////    public String newCommune(
////            @Valid Commune commune,
//////            Commune commune,
////            final BindingResult result,
////            RedirectAttributes attributes,
////            final ModelMap model
////    ) {
////        model.put("commune", new Commune());
////        newCommune = true;
////        model.put("newCommune", newCommune); // newCommune = true
////        model.put("update", false); // affichage de la carte : false
////        if (!result.hasErrors() && commune.getCodeInsee() != null) {
////            attributes.addFlashAttribute("type", "success");
////            attributes.addFlashAttribute("message", "Commune créée avec succès!");
////            return "redirect:/communes/" + commune.getCodeInsee();
////        }
////        model.addAttribute("type", "danger");
////        model.addAttribute("message", "Erreur lors de l'enregistrement!");
////        return "detail";
////    }

//    @GetMapping("/communes/new")
//    public String newCommune(final ModelMap model){
//        model.put("commune", new Commune());
//        model.put("newCommune", true);
//        return "detail";
//    }

    //     @Valid juste avant Commune commune dans la méthode saveExistingCommune
//     @Valid Commune commune,
//    Juste après le paramètre marqué @Valid
//     final BindingResult result,
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
            commune = communeRepository.save(commune);
            attributes.addFlashAttribute("type", "success");
            attributes.addFlashAttribute("message", "Enregistrement de la commune effectué !");
            return "redirect:/communes/" + commune.getCodeInsee();
        }
        //S'il y a des erreurs...
        //Possibilité 1 : Rediriger l'utilisateur vers la page générique d'erreur
        //Possibilité 2 : Laisse sur la même page en affichant les erreurs pour chaque champ
        model.addAttribute("type", "danger");
        model.addAttribute("message", "Erreur lors de la sauvegarde de la commune");
        return "detail";
    }

//    Suppression d'un employé qui n'existe pas
//
//    Avant d'essayer de supprimer un employé on vérifie s'il existe
//
//    Paramètres incorrects au niveau de la liste des communes (page, size, sortProperty, sortDirection)
//
//    D'autres cas d'erreurs ?
//
//    Exceptions cohérentes :
//
//    Avant d'essayer de supprimer un employé on vérifie s'il existe
//    => EntityNotFoundException 404
//            [
//    Paramètres incorrects au niveau de la liste des communes (page, size, sortProperty, sortDirection)
//    => IllegalArgumentException 400

    @GetMapping("/communes/{codeInsee}/delete")
    public String deleteCommune(
            Commune commune,
//            final BindingResult result,
            @PathVariable String codeInsee,
            RedirectAttributes attributes,
            final ModelMap model) throws EntityNotFoundException {

        Optional<Commune> communeExists = communeRepository.findById(codeInsee);
        if (communeExists.isEmpty()) {
            //Gère une exception
            throw new EntityNotFoundException("Impossible de trouver la commune de code INSEE " + codeInsee);
        }
        // Erreur si on recherche un code INSEE inexistant
//        Optional<Commune> commune1 = communeRepository.findById(codeInsee);
//        Optional<Commune> commune = communeRepository.findById(codeInsee);
//        if (commune.isEmpty()) {
//            //Gère une exception
//            throw new EntityNotFoundException("Impossible de trouver la commune de code INSEE " + codeInsee);
//            //model.put("message", "Impossible de trouver la commune de code INSEE " + codeInsee);
//            //return "error";//template error qui affiche un message d'erreur
//        }

//        model.put("commune", commune);

        if (commune.getCodeInsee() != null) {
            communeRepository.deleteById(codeInsee);
            attributes.addFlashAttribute("type", "success");
            attributes.addFlashAttribute("message", "Suppression de la commune effectuée avec succès!");
            return "redirect:" + "/";
        }
        model.addAttribute("type", "danger");
        model.addAttribute("message", "Erreur lors de la suppression de la commune!");
        return "detail";
//        attributes.addFlashAttribute("type", "danger");
//        attributes.addFlashAttribute("message", "Erreur lors de la suppression de la commune!");
//        return "redirect:/communes/{codeInsee}/delete";
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

