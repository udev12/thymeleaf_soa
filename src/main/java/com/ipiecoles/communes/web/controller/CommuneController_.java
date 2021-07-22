//package com.ipiecoles.communes.web.controller;
//
//import com.ipiecoles.communes.web.model.Commune_;
//import com.ipiecoles.communes.web.repository.CommuneRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.ModelMap;
//import org.springframework.validation.BindingResult;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import javax.persistence.EntityNotFoundException;
//import javax.validation.Valid;
//import javax.validation.constraints.Max;
//import java.util.Comparator;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
///**
// * On trouve principalement dans cette classe, les méthodes qui participent au dynamisme du template "detail"
// */
//@Validated
//@Controller
//public class CommuneController_ {
//
//    public static final Double DEGRE_LAT_KM = 111d;
//    public static final Double DEGRE_LONG_KM = 77d;
////    private Boolean newCommune = false;
//
//    @Autowired
//    private CommuneRepository communeRepository;
//
//    /**
//     * Permet la modification d'une commune et la gestion du périmètre
//     *
//     * @param codeInsee : paramètre code INSEE
//     * @param perimetre : paramètre périmètre
//     * @param model     : paramètre de type "ModelMap"
//     * @return le template "detail"
//     */
//    @GetMapping("/communes/{codeInsee}")
//    public String getCommune(
//            @PathVariable String codeInsee,
//            @RequestParam(defaultValue = "10") @Max(value = 20, message = "Le périmètre ne peut être supérieur à 20") Integer perimetre,
//            final ModelMap model) {
//
//        // Erreur si on recherche un code INSEE inexistant
//        Optional<Commune_> commune = communeRepository.findById(codeInsee);
//        if (commune.isEmpty() /*&& newCommune*/) { // si on n'a pas de code INSEE et qu"on n'est pas en train de créer une commune
//            //Gère une exception
//            return "redirect/communes";
////            throw new EntityNotFoundException("Impossible de trouver la commune de code INSEE " + codeInsee);
//
//        }
//
//        //Récupérer les communes proches de celle-ci
//        model.put("commune", commune.get()); // on passe l'objet "Commune" à la vue
//        model.put("communesProches", this.findCommunesProches(commune.get(), perimetre)); // on passe les communes proches trouvées à la vue
////        newCommune = false;
//        model.put("newCommune", false); // newCommune = false
//        model.put("update", true); // affichage de la carte : true
//        model.put("perimetre", perimetre); // on passe le paramètre "perimetre" à la vue
//        model.put("messagePerimetre", "Périmètre > 20"); // on passe le message à la vue
//        model.put("codeInsee", codeInsee); // on passe le code INSEE à la vue
//
//        return "detail";
//    }
//
//    /**
//     * Permet d'enregistrer une nouvelle commune
//     *
//     * @param commune    : objet de type "Commune"
//     * @param model      : paramètre de type "ModelMap"
//     * @param attributes : paramètre pour la redirection "list.html" et le template "detail.html"
//     * @return le template "list.html" ou le template "detail.html"
//     */
//    @PostMapping(value = "/communes", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
//    public String saveNewCommune(Commune_ commune, final ModelMap model, RedirectAttributes attributes) {
//        //Ajouter un certain nombre de contrôles...
//        if (commune == null){
//            return "detail";
//        }
//        commune = communeRepository.save(commune);
//
//        // Erreur si on essayer d'enregistrer, alors que les cellules sont vides
//        if (commune.getCodeInsee().isEmpty() /*&& !newCommune*/) {
//            //Gère une exception
//            return "redirect/communes";
////            throw new EntityNotFoundException("Impossible de trouver la commune de code INSEE " + commune.getCodeInsee());
//        }
//
//        model.put("commune", commune); // on passe l'objet "Commune" au template
//
//        // Redirection vers le template "detail.html" en cas de création réussie
//        if (commune.getCodeInsee() != null) {
//            attributes.addFlashAttribute("type", "success");
//            attributes.addFlashAttribute("message", "Création de la commune effectuée avec succès !");
//            return "redirect:/communes/" + commune.getCodeInsee();
//        }
//
//        // Redirection vers le template "list.html" en cas de suppression réussie
//        attributes.addFlashAttribute("type", "success");
//        attributes.addFlashAttribute("message", "Suppression de la commune effectuée avec succès !");
//        return "redirect:/";
//    }
//
//    /**
//     * Méthode  qui permet d'enregistrer une commune existante
//     *
//     * @param commune    : objet de type "Commune"
//     * @param result     : pour la validation
//     * @param codeInsee  : code INSEE
//     * @param model      : paramètre de type "ModelMap"
//     * @param attributes : permet le return ou la redirection
//     * @return la page générique d'erreur ou la page de modification d'une commune
//     */
//    @PostMapping(value = "/communes/{codeInsee}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
//    public String saveExistingCommune(
//            @Valid Commune_ commune, // on rajoute @Valid pour la validation
//            //Juste après le paramètre marqué @Valid
//            final BindingResult result,
//            @PathVariable String codeInsee,
//            final ModelMap model,
//            RedirectAttributes attributes) {
//
//        //S'il n'y a pas d'erreurs de validation sur le paramètre commune
//        if (!result.hasErrors()) {
//            commune = communeRepository.save(commune);
//            attributes.addFlashAttribute("type", "success");
//            attributes.addFlashAttribute("message", "Enregistrement de la commune effectué!");
//            return "redirect:/communes/" + commune.getCodeInsee();
//        }
//        //S'il y a des erreurs...
//        //Possibilité 1 : Rediriger l'utilisateur vers la page générique d'erreur
//        //Possibilité 2 : Laisse sur la même page en affichant les erreurs pour chaque champ
//        model.addAttribute("type", "danger");
//        model.addAttribute("message", "Erreur lors de la sauvegarde de la commune");
//        return "detail";
//    }
//
////    // MODIFIER NOM METHODE
////    @GetMapping("/communes/old")
////    public String toto(RedirectAttributes attributes, final ModelMap model) {
//////        attributes.addFlashAttribute("type", "success");
//////        attributes.addFlashAttribute("message", "Suppression de la commune effectuée avec succès !");
////        model.addAttribute("type", "success");
////        model.addAttribute("message", "Suppression de la commune effectuée avec succès !");
////        return "list";
////    }
//
//    /**
//     * Méthode pour créer une nouvelle commune
//     *
//     * @param commune : objet de type "Commune"
//     * @param model   : paramètre de type "ModelMap"
//     * @return le template "detail.html"
//     */
//    @GetMapping("/communes/new")
//    public String newCommune(
//            @Valid Commune_ commune,
////            Commune commune,
//            final BindingResult result,
//            RedirectAttributes attributes,
//            final ModelMap model
//    ) {
//
//        model.put("commune", new Commune_());
////        newCommune = true;
//        model.put("newCommune", true); // newCommune = true
//        model.put("update", false); // affichage de la carte : false
//
//            return "detail";
//
////        }
//
////        attributes.addFlashAttribute("type", "success");
////        attributes.addFlashAttribute("message", "Commune créée avec succès!");
////        return "redirect:/communes/" + commune.getCodeInsee();
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
//
//    @GetMapping("/communes/{codeInsee}/delete")
//    public String deleteCommune(
//            @Valid Commune_ commune,
//            final BindingResult result,
//            @PathVariable String codeInsee,
//            RedirectAttributes attributes,
//            final ModelMap model) {
//
//        // Erreur si on recherche un code INSEE inexistant
//        Optional<Commune_> commune1 = communeRepository.findById(codeInsee);
//        if (commune1.isEmpty()) {
//            //Gère une exception
//            throw new EntityNotFoundException("Impossible de trouver la commune de code INSEE " + codeInsee);
//        }
//
//        if (!result.hasErrors()) {
//            communeRepository.deleteById(codeInsee);
//            attributes.addFlashAttribute("type", "success");
//            attributes.addFlashAttribute("message", "Suppression de la commune effectuée avec succès !");
//            return "redirect:" + "/";
//        }
//        model.addAttribute("type", "danger");
//        model.addAttribute("message", "Erreur lors de la suppression de la commune");
//        return "detail";
//    }
//
//
//    /**
//     * Récupère une liste des communes dans un périmètre autour d'une commune
//     *
//     * @param commune       La commune sur laquelle porte la recherche
//     * @param perimetreEnKm Le périmètre de recherche en kilomètre
//     * @return La liste des communes triées de la plus proche à la plus lointaine
//     */
//    private List<Commune_> findCommunesProches(Commune_ commune, Integer perimetreEnKm) {
//        Double latMin, latMax, longMin, longMax, degreLat, degreLong;
//        //1 degré latitude = 111km, 1 degré longitude = 77km
//        degreLat = perimetreEnKm / DEGRE_LAT_KM;
//        degreLong = perimetreEnKm / DEGRE_LONG_KM;
//        latMin = commune.getLatitude() - degreLat;
//        latMax = commune.getLatitude() + degreLat;
//        longMin = commune.getLongitude() - degreLong;
//        longMax = commune.getLongitude() + degreLong;
//        List<Commune_> communesProches = communeRepository.findByLatitudeBetweenAndLongitudeBetween(latMin, latMax, longMin, longMax);
//        ;
//        return communesProches.stream().
//                filter(commune1 -> !commune1.getNom().equals(commune.getNom()) && commune1.getDistance(
//                        commune.getLatitude(), commune.getLongitude()) <= perimetreEnKm).
//                sorted(Comparator.comparing(o -> o.getDistance(commune.getLatitude(), commune.getLongitude()))).
//                collect(Collectors.toList());
//    }
//
//}
//
