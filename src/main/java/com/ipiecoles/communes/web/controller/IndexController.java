package com.ipiecoles.communes.web.controller;

import com.ipiecoles.communes.web.model.Commune;
import com.ipiecoles.communes.web.repository.CommuneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
public class IndexController {

    @Autowired
    CommuneRepository communeRepository;

    // serach initialisée avec une valeur par défaut nule : ça sert à laisser le temps à l'appli de récupérer des valeurs. En efet, sans cette variable, au lancement, une recherche va être faite; ce qui va modifier la sélection en cliquant par ex sur le tri ou sur "page suivante"

    /**
     * Lorsqu'on lance l'appli pour la première fois, er qu'on essaye de faire un tri des colonnes, ou de cliquer sur le bouton "suivant",
     * la sélection des communes est modifiées car, l'appli tente de faire une recherche.
     * La solutiion, c'est la variable "init", qui va permettre de différencier le premiers lancement de l'appli,
     * des autres instants d'utilisation. On conserve ainsi la sélection par défaut (ex : 10 lignes par page), lorsqu'on
     * trie les colonnes, ou lorsqu'on clique sur le bouton "page suivante"
     */

    @GetMapping(value = "/")
    public String index(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
//            @RequestParam(defaultValue = "5") Integer size,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "codeInsee") String sortProperty,
            @RequestParam(defaultValue = "ASC") String sortDirection,
//            @RequestParam(required = false) String search, // il faut mettre required=false
            @RequestParam(defaultValue = "") String search,
            RedirectAttributes attributes,
            final ModelMap model) /*throws  IllegalArgumentException*/{

        //        // Erreur si paramètres "page", "size", "sortProperty" et "sortDirection" incorrects
//        if ((page != null && page != (Integer)page) || (size != null && size != (Integer)size) /*|| (sortProperty != null && !sortProperty.equals("codeInsee"))
//                || (sortProperty != null  && !sortProperty.equals("codePostal")) || (sortProperty != null  && !sortProperty.equals("nom"))
//                || (sortProperty != null  && !sortProperty.equals("latitude")) || (sortProperty != null  && !sortProperty.equals("longitude"))
//                || (sortDirection != null  && !sortDirection.equals("ASC")) || (sortDirection != null && !sortDirection.equals("DESC"))*/) {
//            //Gère une exception
//            throw new IllegalArgumentException("Paramètre incorrect");
//        }
//

//        http://localhost:8080/?page=5&search=&size=10&sortDirection=DESC&sortProperty=nom

        try{
//        if ((sortDirection != null && !sortDirection.equals("ASC")) || (sortDirection != null && !sortDirection.equals("DESC"))
//                /*|| (sortProperty != null  && !sortProperty.equals("codePostal")) || (sortProperty != null  && !sortProperty.equals("nom"))
//                || (sortProperty != null  && !sortProperty.equals("latitude")) || (sortProperty != null  && !sortProperty.equals("longitude"))
//                || (sortDirection != null  && !sortDirection.equals("ASC")) || (sortDirection != null && !sortDirection.equals("DESC"))*/) {
//            //Gère une exception
////            model.put("message", "Paramètre incorrect !");
//
//            throw new IllegalArgumentException("Paramètre incorrect");
//        }

        // Consttituer un PageRequest
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.fromString(sortDirection), sortProperty);

        Page<Commune> communes;

        if (search == null || search.isEmpty()) {
            communes = communeRepository.findAll(pageRequest);
        } else {
            communes = communeRepository.findByNomContainingIgnoreCase(search, pageRequest);
        }

        String regexFiveNumbers = "\\d{5}";
        if (search.matches(regexFiveNumbers)) {
            return "redirect:/communes/" + search;
        }

        if (page == 0) {
            model.put("isSelected", "true");
        }

//        Page<Commune> communes;
//        if(search == null){
//            //Appeler findAll si search est null
//            communes = communeRepository.findAll(pageRequest);
//        } else {
//            //Appeler findByNomContainingIgnoreCase si search n'est pas null
//            communes = communeRepository.findByNomContainingIgnoreCase(search, pageRequest);
//        }

//        Page<Commune> communes = communeRepository.findAll(pageRequest);
//        model.put("commune", communes);
        model.put("communes", communes);
//        model.put("nbCommunes", communeRepository.count());
        model.put("nbCommunes", communes.getTotalElements());
//        model.put("pageSizes", Arrays.asList("5", "10", "20", "50", "100"));
//        communeRepository.findAll();
        //Affichage des communes de 1 à 10 => page = 0 et size = 10
        //Affichage des communes de 11 à 20 => page = 1 et size = 10
        //Affichage des communes de 41 à 60 => page = 2 et size = 20
//        Integer start = page;
//        Integer end = page + 1;
//        model.put("start", page);//A remplacer par la valeur dynamique
//        model.put("end", page);//A remplacer par la valeur dynamique


            Integer pageUp = page + 1;
            Integer pageDown = page - 1;
            Integer pageInc = page;
            pageInc = pageInc + 1;

            Integer newPage = 0;

            Integer previousSize = 0;

            model.put("pageSizes", Arrays.asList(5, 10, 20, 50, 100));

            // calcul start et end
//        Integer start = page + size;

//        String title = "Gestion des communes";

            // Pages après modif size
            // de 10 à 5 => page = page * 2 => 10/5=2
            // de 5 à 10 => page = page / 2 => 5/10=0.5
            // de 20 à 10 => page = page * 2
            // de 10 à 20 => page = page / 2
            // de 20 à 5 => 5/20=0.25 => page = page * 0.25
            // de 5 à 20 => 20/5=4 => page = page * 4
            Integer end = 0;
            Integer start = 0;
            Integer sizeSaved = 0;
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
//                newPage = page * 2;
//                size = 5;
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
//                size = 10;
                    break;
                case 20:
                    end = (page + 1) * 20;
                    start = end - 19;
//                size = 20;
                    break;
                case 50:
                    end = (page + 1) * 50;
                    start = end - 49;
//                size = 50;
                    break;
                case 100:
                    end = (page + 1) * 100;
                    start = end - 99;
//                size = 100;
                    break;
            }
            model.put("size", size);
            model.put("start", start);//A remplacer par la valeur dynamique
            model.put("end", end);//A remplacer par la valeur dynamique
            model.put("page", page);
            model.put("pageUp", pageUp);
            model.put("pageDown", pageDown);
            model.put("pageInc", pageInc);
            model.put("sortDirection", sortDirection);
//        if (size != null) {
            model.put("search", search);

        } catch (IllegalArgumentException e){

        }

//        }
//        search = "";
//        model.put("title", title);
//        model.put("page", Arrays.asList("Page 1", "Page 2", "Page 3"));
//        model.put("pageSizes", Arrays.asList("5", "10", "20", "50", "100"));

//        model.put("creationCommune", true);
//        pour modifier l'archi
//        model.put("template", "list");
//        model.put("fragment", "listCommunes");
//        return "main";

//        model.put("pages", Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23));

//        attributes.addFlashAttribute("type", "success");
//        attributes.addFlashAttribute("message", "Suppression de la commune effectuée avec succès !");
        return "list";//Chemin du template (sans .html) à partir du dossier templates
    }


//    @GetMapping(value = "/")
//    public String index(
//            @RequestParam(value = "page", defaultValue = "0") Integer page,
//            @RequestParam(defaultValue = "10") Integer size,
//            @RequestParam(defaultValue = "codeInsee") String sortProperty,
//            @RequestParam(defaultValue = "ASC") String sortDirection,
//            @RequestParam(required = false) String search,
//            final ModelMap model) {
//        //Constituer un PageRequest
//        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.fromString(sortDirection), sortProperty);
//        Page<Commune> communes;
//        if(search == null){
//            //Appeler findAll si search est null
//            communes = communeRepository.findAll(pageRequest);
//        } else {
//            //Appeler findByNomContainingIgnoreCase si search n'est pas null
//            communes = communeRepository.findByNomContainingIgnoreCase(search, pageRequest);
//        }
//        model.put("communes", communes);
//        model.put("nbCommunes", communes.getTotalElements());
//        model.put("pageSizes", Arrays.asList("5", "10", "20", "50", "100"));
//        model.put("pageSizes", Arrays.asList(5, 10, 20, 50, 100));
//        //Affichage des communes de 1 à 10 => page = 0 et size = 10
//        //Affichage des communes de 11 à 20 => page = 1 et size = 10
//        //Affichage des communes de 41 à 60 => page = 2 et size = 20
//        model.put("start", 1);//A remplacer par la valeur dynamique
//        model.put("end", 10);//A remplacer par la valeur dynamique
//        model.put("page", page);
//        model.put("size", size);
//
//        return "list";//Chemin du template (sans .html) à partir du dossier templates
//    }


}
