package com.ipiecoles.communes.web.controller;

import com.ipiecoles.communes.web.model.Commune;
import com.ipiecoles.communes.web.repository.CommuneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;

/**
 * On trouve principalement dans cette classe, une méthode qui participe au dynamisme du template "list" (notre page d'accueil)
 */
@Controller
public class IndexController {

    @Autowired
    CommuneRepository communeRepository;

    /**
     * Lorsqu'on lance l'appli pour la première fois, er qu'on essaye de faire un tri des colonnes, ou de cliquer sur le bouton "suivant",
     * la sélection des communes est modifiées car, l'appli tente de faire une recherche.
     * La solutiion, c'est la variable "init", qui va permettre de différencier le premiers lancement de l'appli,
     * des autres instants d'utilisation. On conserve ainsi la sélection par défaut (ex : 10 lignes par page), lorsqu'on
     * trie les colonnes, ou lorsqu'on clique sur le bouton "page suivante"
     */

    @GetMapping(value = "/") // endpoint par défaut
    public String index(
            @RequestParam(value = "page", defaultValue = "0") Integer page, // le paramètre "page" est initialisé à 0
            @RequestParam(defaultValue = "10") Integer size, // le paramètre "size" est initialisé à 10
            @RequestParam(defaultValue = "codeInsee") String sortProperty, // le paramètre "sortProperty" prend par défaut, la valeur "codeInsee"
            @RequestParam(defaultValue = "ASC") String sortDirection, // le paramètre "sortDirection" prend par défaut, la valeur "ASC"
            @RequestParam(defaultValue = "") String search, // search est initialisé avec une valeur par défaut nule : ça sert à laisser le temps à l'appli de récupérer des valeurs.
            // En efet, sans cette variable, au lancement, une recherche va être faite; ce qui va modifier la sélection en cliquant par ex sur le tri ou sur "page suivante"
//            RedirectAttributes attributes,
            final ModelMap model) /*throws IllegalArgumentException*/ {

        try {

            // Constituer un PageRequest
            PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.fromString(sortDirection), sortProperty);

            Page<Commune> communes;

            if (search == null || search.isEmpty()) {
                communes = communeRepository.findAll(pageRequest); // on affiche toutes les communes, s'il n'y a rien à chercher
            } else {
                communes = communeRepository.findByNomContainingIgnoreCase(search, pageRequest); // sinon, on affiche le résultat de la recherche
            }

            String regexFiveNumbers = "\\d{5}"; // avec ce regex, on s'assure que le code postal contient bien 5 chiffres
            if (search.matches(regexFiveNumbers)) {
                return "redirect:/communes/" + search;
            }

            model.put("communes", communes); // on passe l'objet "communes" à la vue "list.html"

            model.put("nbCommunes", communes.getTotalElements()); // on passe l'objet "nbCommunes" à la vue "list.html"

            int pageUp = page + 1; // on incrémente le numéro des pages
            int pageDown = page - 1; // on décrémente le numéro des pages
            int pageInc = page;
            pageInc = pageInc + 1;

            model.put("pageSizes", Arrays.asList(5, 10, 20, 50, 100)); // on passe l'objet "pageSizes" à la vue "list.html" (fragment "pagination.html")

            // Pages après modif size
            // de 10 à 5 => page = page * 2 => 10/5=2
            // de 5 à 10 => page = page / 2 => 5/10=0.5
            // de 20 à 10 => page = page * 2
            // de 10 à 20 => page = page / 2
            // de 20 à 5 => 5/20=0.25 => page = page * 0.25
            // de 5 à 20 => 20/5=4 => page = page * 4
            int end = 0;
            int start = 0;
            switch (size) {
                case 5:
                    // page 1 => plage de 1 à 5
                    // page 2 => plage de 6 à 10
                    // page 3 => plage de 11 à 15
                    // page 4 => plage de 16 à 20
                    // page 5 => plage de 21 à 25
                    // page 6 => plage de 26 à 30
                    // page 7 => plage de 31 à 35
                    // page 8 => plage de 36 à 40
                    // d'où formule : end = n° page * 5 / start = end - 4
                    end = (page + 1) * 5;
                    start = end - 4;
                    break;
                case 10:
                    // page 1 => plage de 1 à 10
                    // page 2 => plage de 11 à 20
                    // page 3 => plage de 21 à 30
                    // page 4 => plage de 31 à 40
                    // page 5 => plage de 41 à 50
                    // page 6 => plage de 51 à 60
                    // page 7 => plage de 61 à 70
                    // d'où formule : end = n° page * 10 / start = end - 9 => end = n° page * size / start = end - (size - 1)
                    end = (page + 1) * 10;
                    start = end - 9;
                    break;
                case 20:
                    end = (page + 1) * 20;
                    start = end - 19;
                    break;
                case 50:
                    end = (page + 1) * 50;
                    start = end - 49;
                    break;
                case 100:
                    end = (page + 1) * 100;
                    start = end - 99;
                    break;
            }
            model.put("size", size); // on passe le paramètre "size" à la vue "list.html"
            model.put("start", start); // on passe la variable "start" à la vue "list.html"
            model.put("end", end); // on passe la variable "end" à la vue "list.html"
            model.put("page", page); // on passe le paramètre "page" à la vue "list.html"
            model.put("pageUp", pageUp); // on passe la variable "pageUp" à la vue "list.html"
            model.put("pageDown", pageDown); // on passe la variable "pageDown" à la vue "list.html"
            model.put("pageInc", pageInc); // on passe la variable "pageInc" à la vue "list.html"
            model.put("sortDirection", sortDirection); // on passe le paramètre "sortDirection" à la vue "list.html"
            model.put("search", search); // on passe le paramètre "search" à la vue "list.html"

        } catch (IllegalArgumentException e) {
            e.getMessage(); // si l'exception "IllegalArgumentException" est levée
        }

        return "list"; // chemin du template (sans .html) à partir du dossier templates
    }

}
