package com.ipiecoles.communes.web.controller;

import com.ipiecoles.communes.web.model.Commune;
import com.ipiecoles.communes.web.repository.CommuneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
public class IndexController {

    @Autowired
    CommuneRepository communeRepository;

    @GetMapping(value = "/")
    public String index(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "codeInsee") String sortProperty,
            @RequestParam(defaultValue = "ASC") String sortDirection,
            @RequestParam(required = false) String search, // il faut mettre required=false
            final ModelMap model) {


        // Consttituer un PageRequest
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.fromString(sortDirection), sortProperty);

        Page<Commune>communes;

        if(search == null || search.isEmpty()){
            communes = communeRepository.findAll(pageRequest);
        }else{
            communes = communeRepository.findByNomContainingIgnoreCase(search, pageRequest);
        }

    if (page == 0){
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


//        String title = "Gestion des communes";


        model.put("start", 1);//A remplacer par la valeur dynamique
        model.put("end", 10);//A remplacer par la valeur dynamique
        model.put("page", page);
        model.put("pageUp", pageUp);
        model.put("pageDown", pageDown);
        model.put("pageInc", pageInc);
        model.put("size", size);
//        model.put("title", title);
//        model.put("page", Arrays.asList("Page 1", "Page 2", "Page 3"));
//        model.put("pageSizes", Arrays.asList("5", "10", "20", "50", "100"));
        model.put("pageSizes", Arrays.asList(5, 10, 20, 50, 100));


//        pour modifier l'archi
//        model.put("template", "list");
//        model.put("fragment", "listCommunes");
//        return "main";

//        model.put("pages", Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23));
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
