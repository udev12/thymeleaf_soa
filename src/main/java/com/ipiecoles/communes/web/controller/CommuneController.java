package com.ipiecoles.communes.web.controller;

import com.ipiecoles.communes.web.model.Commune;
import com.ipiecoles.communes.web.repository.CommuneRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
//@Validated // à enlever, si on veut que les erreurs soient affichées sur chaque champ de la page de saisie
@Controller
public class CommuneController {

    static final Logger logger = LoggerFactory.getLogger(CommuneController.class);

    public static final Double DEGRE_LAT_KM = 111d;
    public static final Double DEGRE_LONG_KM = 77d;

    @Autowired
    private CommuneRepository communeRepository;

    /**
     * Cette méthode permet d'afficher le détail de la commune
     *
     * @param codeInsee  : le code INSEE
     * @param perimetre  : le périmètre autour de la commune
     * @param model      : variable de type "ModelMap"
     * @param attributes : paramètre qui permet la redirection
     * @return le template "detail.html" ou redirection vers "/communes/CODEINSEE"
     * @throws EntityNotFoundException si le code INSEE n'est pas trouvé
     */
    @GetMapping("/communes/{codeInsee}")
    public String getCommune(
            @PathVariable String codeInsee,
            @RequestParam(defaultValue = "10") Integer perimetre,
            final ModelMap model,
            RedirectAttributes attributes) throws EntityNotFoundException {

        // Erreur si on recherche un code INSEE inexistant
        Optional<Commune> commune = communeRepository.findById(codeInsee);
        if (commune.isEmpty()) {
            //Gère une exception
            logger.error("Impossible de trouver la commune de code INSEE {}", codeInsee);
            throw new EntityNotFoundException("Impossible de trouver la commune de code INSEE " + codeInsee);
        }

        //Récupérer les communes proches de celle-ci
        model.put("commune", commune.get()); // on passe l'objet "commune" à la vue "detail"
        model.put("communesProches", this.findCommunesProches(commune.get(), perimetre)); // on passe "communesProches" à la vue "detail"
        model.put("newCommune", false); // on passe "newCommune" à la vue "detail"
        model.put("update", true); // affichage de la carte : false
        if (perimetre > 20) {
            perimetre = 10;
            model.put("perimetre", perimetre);
            attributes.addFlashAttribute("type", "danger");
            attributes.addFlashAttribute("message", "Le périmètre ne peut être supérieur à 20!");
            logger.info("Redirection vers '/communes/{}", codeInsee);
            return "redirect:/communes/" + codeInsee; // redirection vers "/communes/CODEINSEE"
        } else {
            model.put("perimetre", perimetre); // on passe le périmètre à la vue "detail"
        }
        model.put("codeInsee", codeInsee); // on passe le code INSEE à la vue "detail"

        return "detail";

    }

    /**
     * Permet d'enregistrer une nouvelle commune
     *
     * @param commune    : objet de type "Commune"
     * @param result     : pour la validation
     * @param model      : variable de type "ModelMap"
     * @param attributes : paramètre pour la redirection vers "/communes/CODEINSEE"
     * @return le template "detail.html" ou redirection vers "/communes/CODEINSEE"
     */
    @PostMapping(value = "/communes", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String saveNewCommune(
            @Valid Commune commune,
            //Juste après le paramètre marqué @Valid
            final BindingResult result,
            final ModelMap model,
            RedirectAttributes attributes) {
        //S'il n'y a pas d'erreurs de validation sur le paramètre commune
        if (!result.hasErrors()) {
            commune = communeRepository.save(commune);
            attributes.addFlashAttribute("type", "success");
            attributes.addFlashAttribute("message", "Enregistrement de la commune effectué !");
            logger.info("Redirection vers '/communes/{}", commune.getCodeInsee());
            return "redirect:/communes/" + commune.getCodeInsee();
        }
        model.put("newCommune", true); // on passe "newCommune" à la vue "detail"
        //S'il y a des erreurs...
        //Possibilité 1 : Rediriger l'utilisateur vers la page générique d'erreur
        //Possibilité 2 : Laisse sur la même page en affichant les erreurs pour chaque champ
        model.addAttribute("type", "danger");
        model.addAttribute("message", "Erreur lors de la sauvegarde de la commune");
        logger.error("Erreur lors de la sauvegarde de la commune");
        return "detail";
    }

    /**
     * Méthode pour l'affichage du template d'ajout d'une commune
     *
     * @param model : variable de type "ModelMap"
     * @return le template "detail.html"
     */
    @GetMapping("communes/new")
    public String newCommune(final ModelMap model) {
        model.addAttribute("commune", new Commune());
        model.put("newCommune", true); // on passe "newCommune" à la vue "detail"
        return "detail";
    }

    /**
     * Méthode  qui permet d'enregistrer une commune existante
     *
     * @param commune    : objet de type "Commune"
     * @param result     : pour la validation
     * @param codeInsee  : le code INSEE
     * @param model      : paramètre de type "ModelMap"
     * @param attributes : permet le return ou la redirection
     * @return la page générique d'erreur ou la page de modification d'une commune
     */
    @PostMapping(value = "/communes/{codeInsee}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String saveExistingCommune(
            @Valid Commune commune, // @Valid juste avant Commune commune
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
            logger.info("Redirection vers '/communes/{}", commune.getCodeInsee());
            return "redirect:/communes/" + commune.getCodeInsee();
        }
        //S'il y a des erreurs...
        //Possibilité 1 : Rediriger l'utilisateur vers la page générique d'erreur
        //Possibilité 2 : Laisse sur la même page en affichant les erreurs pour chaque champ
        model.addAttribute("type", "danger");
        model.addAttribute("message", "Erreur lors de la sauvegarde de la commune");
        logger.error("Erreur lors de la sauvegarde de la commune");
        return "detail";
    }

    /**
     * Méthode qui permet de supprimer une commune
     *
     * @param commune    : objet de type "Commune"
     * @param codeInsee  : le code INSEE
     * @param attributes : permet le return ou la redirection
     * @param model      : paramètre de type "ModelMap"
     * @return le template "detail.html" ou redirection vers le template "list.html"
     * @throws EntityNotFoundException si le code INSEE n'est pas trouvé
     */
    @GetMapping("/communes/{codeInsee}/delete")
    public String deleteCommune(
            Commune commune,
            @PathVariable String codeInsee,
            RedirectAttributes attributes,
            final ModelMap model) throws EntityNotFoundException {

        Optional<Commune> communeExists = communeRepository.findById(codeInsee);
        if (communeExists.isEmpty()) {
            //Gère une exception
            throw new EntityNotFoundException("Impossible de trouver la commune de code INSEE " + codeInsee);
        }
        // Erreur si on recherche un code INSEE inexistant
        if (commune.getCodeInsee() != null) {
            communeRepository.deleteById(codeInsee);
            attributes.addFlashAttribute("type", "success");
            attributes.addFlashAttribute("message", "Suppression de la commune effectuée avec succès!");
            logger.info("Redirection vers le template 'list.html'");
            return "redirect:" + "/";
        }
        model.addAttribute("type", "danger");
        model.addAttribute("message", "Erreur lors de la suppression de la commune!");
        logger.error("Erreur lors de la suppression de la commune");
        return "detail";
    }


    /**
     * Récupère une liste des communes dans un périmètre autour d'une commune
     *
     * @param commune       : la commune sur laquelle porte la recherche
     * @param perimetreEnKm : le périmètre de recherche en kilomètre
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

        logger.info("Recherche des communes proches ...");
        return communesProches.stream().
                filter(commune1 -> !commune1.getNom().equals(commune.getNom()) && commune1.getDistance(
                        commune.getLatitude(), commune.getLongitude()) <= perimetreEnKm).
                sorted(Comparator.comparing(o -> o.getDistance(commune.getLatitude(), commune.getLongitude()))).
                collect(Collectors.toList());
    }

}

