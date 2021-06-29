package com.ipiecoles.communes.web.controller;

import com.ipiecoles.communes.web.model.Commune;
import com.ipiecoles.communes.web.repository.CommuneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

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
            final ModelMap model) {
        model.put("nbCommunes", communeRepository.count());
        //Affichage des communes de 1 à 10 => page = 0 et size = 10
        //Affichage des communes de 11 à 20 => page = 1 et size = 10
        //Affichage des communes de 41 à 60 => page = 2 et size = 20
//        Integer start = page;
//        Integer end = page + 1;
//        model.put("start", page);//A remplacer par la valeur dynamique
//        model.put("end", page);//A remplacer par la valeur dynamique
        model.put("start", 1);//A remplacer par la valeur dynamique
        model.put("end", 10);//A remplacer par la valeur dynamique

        return "list";//Chemin du template (sans .html) à partir du dossier templates
    }

//    @GetMapping("/communes/{codeInsee}")
//    public String getCommune(
//            @PathVariable String codeInsee,
//            final ModelMap model)
//    {
//        Optional<Commune> commune = communeRepository.findById(codeInsee);
//        model.put("commune", commune.get());
//        return "detail";
//    }

//    @GetMapping(value = "/")
////    public String index(final ModelMap model) {
//    public String index(
//            @RequestParam(value = "page", defaultValue = "0") Integer page,
//            @RequestParam(defaultValue = "10") Integer size,
//            @RequestParam(defaultValue = "codeInsee") String sortProperty,
//            @RequestParam(defaultValue = "ASC") String sortDirection,
//            final ModelMap model) {
//        model.put("nbCommunes", communeRepository.count());
////        model.put("nom", "IPI");
////        model.put("htmlText", "How are <strong>you</strong> ?");
////        model.put("otherText", "Have a nice day !");
////        return "index";
//
//        // Affichage des communes de 1 à 10 => page = 0 et size = 10
//        // Affichage des communes de 1 à 10 => page = 0 et size = 10
//        // Affichage des communes de 1 à 10 => page = 0 et size = 10
//        model.put("start", 1); // à remplacer par la valeur dynamique
//        model.put("end", 10); // à remplacer par la valeur dynamique
//        return "list"; // chemin du template sans le ".html" à partir du dossier "src/main
//    }

}
