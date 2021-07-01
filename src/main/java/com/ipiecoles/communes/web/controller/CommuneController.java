package com.ipiecoles.communes.web.controller;

import com.ipiecoles.communes.web.model.Commune;
import com.ipiecoles.communes.web.repository.CommuneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

//import java.awt.*;
import javax.persistence.EntityNotFoundException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class CommuneController {

    public static final Double DEGRE_LAT_KM = 111d;
    public static final Double DEGRE_LONG_KM = 77d;

    @Autowired
    private CommuneRepository communeRepository;

    // Modification commune / gestion périmètre
    @GetMapping("/communes/{codeInsee}")
    public String getCommune(
            @PathVariable String codeInsee,
            @RequestParam(defaultValue = "10") Integer perimetre,
            final ModelMap model) {
//        Optional<Commune> commune = communeRepository.findById(codeInsee);
//        if (commune.isEmpty()) {
//            //Gère une exception
//            throw  new EntityNotFoundException("Impossible de trouver la commune de code INSEE " + codeInsee);
//        }

//        Optional<Commune> commune = communeRepository.findById(codeInsee);
//        if(commune.isEmpty()){
//            //Gère une exception
//            //throw new EntityNotFoundException("Impossible de trouver la commune de code INSEE " + codeInsee);
//            model.put("message", "Impossible de trouver la commune de code INSEE " + codeInsee);
//            return "error";//template error qui affiche un message d'erreur
//        }

        // Erreur si on recherche un code INSEE inexistant
        Optional<Commune> commune = communeRepository.findById(codeInsee);
        if(commune.isEmpty()){
            //Gère une exception
            throw new EntityNotFoundException("Impossible de trouver la commune de code INSEE " + codeInsee);
            //model.put("message", "Impossible de trouver la commune de code INSEE " + codeInsee);
//            return "error";//template error qui affiche un message d'erreur
        }

        //Récupérer les communes proches de celle-ci
        model.put("commune", commune.get());
        model.put("communesProches", this.findCommunesProches(commune.get(), perimetre));
        model.put("newCommune", false);
        model.put("update", true); // affichage de la carte : false
//        model.put("delete_creation", false); // affichage de la carte : false
        model.put("perimetre", perimetre);

        return "detail";

    }
//    @GetMapping("/communes/{codeInsee}")
//    public String getCommune(
//            @PathVariable String codeInsee,
//            final ModelMap model)
//    {
//        Optional<Commune> commune = communeRepository.findById(codeInsee);
//        model.put("commune", commune.get());
//        model.put("newCommune", false);
//        model.put("update", true); // affichage de la carte : false
//        return "detail";
//    }

    @PostMapping(value = "/communes", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String saveNewCommune(Commune commune, final ModelMap model) {
        //Ajouter un certain nombre de contrôles...
        commune = communeRepository.save(commune);

        // Erreur si on essayer d'enregistrer, alors que les cellules sont vides
//        Optional<Commune> commune = communeRepository.findById(codeInsee);
        if(commune.getCodeInsee().isEmpty()){
            //Gère une exception
            throw new EntityNotFoundException("Impossible de trouver la commune de code INSEE " + commune.getCodeInsee());
            //model.put("message", "Impossible de trouver la commune de code INSEE " + codeInsee);
//            return "error";//template error qui affiche un message d'erreur
        }

        model.put("commune", commune);
        return "detail";
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

    @PostMapping(value = "/communes/{codeInsee}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String saveExistingCommune(
            Commune commune,
            @PathVariable String codeInsee,
            final ModelMap model) {
        //Ajouter un certain nombre de contrôles...
        commune = communeRepository.save(commune);
        return "redirect:/communes/" + commune.getCodeInsee();
    }

    // Création commune
//    @GetMapping("/communes/new")
//    public String newCommune(final ModelMap model) {
//        model.put("commune", new Commune());
//        model.put("newCommune", true);
//        model.put("update", false); // affichage de la carte : false
//        return "detail";
//    }
//    Optional<Commune> commune
    @GetMapping("/communes/new")
    public String newCommune(Commune commune, final ModelMap model) {
        model.put("commune", new Commune());
        model.put("newCommune", true);
        model.put("update", false); // affichage de la carte : false
//        model.put("delete_creation", true); // affichage de la carte : true
//        Optional<Commune> commune1 = communeRepository.findById(commune.getCodeInsee());
//        Optional <Commune> toto = communeRepository.findById(commune.getCodeInsee());
//        commune = communeRepository.save(commune);
//        return "redirect:/communes/new/" + commune.getCodeInsee();
        if (commune.getCodeInsee() != null) {
            return "redirect:/communes/" + commune.getCodeInsee();
        }
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

//    @GetMapping("/communes/{codeInsee}/delete")
//    public String deleteCommune(
//            @PathVariable String codeInsee) {
//        communeRepository.deleteById(codeInsee);
//        return "REDIRECTION A GERER";
//    }

    @GetMapping("/communes/{codeInsee}/delete")
    public String deleteCommune(
            Commune commune,
            @PathVariable String codeInsee) {
        communeRepository.deleteById(codeInsee);
//        return "REDIRECTION A GERER";
//        return "redirect:/communes/" + commune.getCodeInsee();
//        return "list";
//        String url = "/communes/" + commune.getCodeInsee() + "/delete";
//        return new RedirectView(url).toString();
        return "list";
    }


//    @GetMapping("/communes/new")
//    public String getCommune(
//            @PathVariable String commune,
//            final ModelMap model)
//    {
//        Optional<Commune> commune = communeRepository.(codeInsee);
//        model.put("commune", commune.get());
//        return "detail";
//    }

//    @PostMapping(value = "/communes", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
//    public  String saveNewCommune(Commune commune, final ModelMap model){
//
//        // Ajouter un certain nombre de contrôles
//
//        commune = communeRepository.save(commune);
//        model
//    }
//
//    @RequestMapping("/communes/new")
//    public String addCommmune(final ModelMap m){
//        m.addAttribute("commune", new Commune());
//        return "detail";
//    }


}

// controller Commune : affichage, suppression, modif
//@Controller
//public class CommuneController {
//
//    @Autowired
//    CommuneRepository communeRepository;
//
//    // http://localhost:8080/communes/01009
////    @RequestMapping(value = "/communes/{id}", method = RequestMethod.GET)
////    public String findById(
////            @PathVariable(value = "id") Integer id, final ModelMap m)
////    {
////        // 01009, Andert Et Condon
////        m.put("codePostal", 7600);
////        m.put("nomCommune", "Andert Et Condon");
////        m.put("latitude", 45.7873565333);
////        m.put("longitude", 5.65788307924);
////        return "The id=" + id;
////    }
//
//    @GetMapping("/communes/{codeInsee}")
//    public String getCommune(
//            @PathVariable String codeInsee,
//            final ModelMap model
//    ){
//        Optional<Commune> commune = communeRepository.findById(codeInsee);
//        model.put("commune", commune.get());
//        return "detail";
//    }
//
//
//}

//@Controller
//public class IndexController {
//    @GetMapping(value = "/say")
//    public String index(final ModelMap m) {
//        m.put("now", LocalDate.now());
//        m.put("codePostal", 7600);
//        m.put("prix", 1500);
//        m.put("reduction", 0.1012);
//        m.put("liste", Arrays.asList(5,3,9,6));
//        return "index";
//    }
//}