package com.ipiecoles.communes.web.controller;

import com.ipiecoles.communes.web.model.Commune;
import com.ipiecoles.communes.web.repository.CommuneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.awt.*;
import java.util.Optional;

@Controller
public class CommuneController {

    @Autowired
    private CommuneRepository communeRepository;

    @GetMapping("/communes/{codeInsee}")
    public String getCommune(
            @PathVariable String codeInsee,
            final ModelMap model)
    {
        Optional<Commune> commune = communeRepository.findById(codeInsee);
        model.put("commune", commune.get());
        return "detail";
    }

    @PostMapping(value = "/communes", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String saveNewCommune(Commune commune, final ModelMap model){
        //Ajouter un certain nombre de contrôles...
        commune = communeRepository.save(commune);
        model.put("commune", commune);
        return "detail";
    }

    @PostMapping(value = "/communes/{codeInsee}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String saveExistingCommune(
            Commune commune,
            @PathVariable String codeInsee,
            final ModelMap model){
        //Ajouter un certain nombre de contrôles...
        commune = communeRepository.save(commune);
        model.put("commune", commune);
        return "detail";
    }

    @GetMapping("/communes/new")
    public String newCommune(final ModelMap model){
        model.put("commune", new Commune());
        return "detail";
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