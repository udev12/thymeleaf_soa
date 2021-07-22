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

    private Long lastPage = 0L;
    private Long nbCommunes = 0L;

    @Autowired
    CommuneRepository communeRepository;

    /**
     * Lorsqu'on lance l'appli pour la première fois, et qu'on essaye de faire un tri des colonnes, ou de cliquer sur le bouton "suivant",
     * la sélection des communes est modifiées car, l'appli tente de faire une recherche.
     * La solution, c'est la variable "init", qui va permettre de différencier le premier lancement de l'appli,
     * des autres instants d'utilisation. On conserve ainsi la sélection par défaut (ex : 10 lignes par page), lorsqu'on
     * trie les colonnes, ou lorsqu'on clique sur le bouton "page suivante"
     */
    @GetMapping(value = "/") // endpoint par défaut
    public String index(
            @RequestParam(value = "page", defaultValue = "0") String page, // le paramètre "page" est initialisé à 0 : on a un type "String" au lieu d'un "Integer" pour faciliter les contrôles (valeur incoreccte)
            @RequestParam(defaultValue = "10") String size, // le paramètre "size" est initialisé à 10 : on a un type "String" au lieu d'un "Integer" pour faciliter les contrôles (valeur incoreccte)
            @RequestParam(defaultValue = "codeInsee") String sortProperty, // le paramètre "sortProperty" prend par défaut, la valeur "codeInsee"
            @RequestParam(defaultValue = "ASC") String sortDirection, // le paramètre "sortDirection" prend par défaut, la valeur "ASC"
            @RequestParam(defaultValue = "") String search, // search est initialisé avec une valeur par défaut nule : ça sert à laisser le temps à l'appli de récupérer des valeurs.
            // En effet, sans cette variable au lancement, une recherche va être faite; ce qui va modifier la sélection en cliquant par ex sur le tri ou sur "page suivante"
//            RedirectAttributes attributes,
            final ModelMap model) throws IllegalArgumentException {

//            if (String.{
//                throw new IllegalArgumentException("Le paramètre est 'page' est incorrect!");
//            }

//        else if (strPage.contains("[a-zA-Z]+")){
//            throw new IllegalArgumentException("Le paramètre est 'page' est incorrect!");
//        }

        // On contrôle la valeur des paramètres


//        try {

        int intPage = 0;
        int intSize = 0;

        // Pas très académique de mettre un "throw new exception" dans un "try .. catch", mais c'est la seule piste fonctionnelle que j'ai pour le moment!
        try{
            intPage = Integer.parseInt(page);
        } catch (Exception e) {
            throw new IllegalArgumentException("Le paramètre est 'page' est incorrect!");
        }
        try{
            intSize = Integer.parseInt(size);
        } catch (Exception e) {
            throw new IllegalArgumentException("Le paramètre est 'size' est incorrect!");
        }

        // Constituer un PageRequest
        PageRequest pageRequest = PageRequest.of(intPage, intSize, Sort.Direction.fromString(sortDirection), sortProperty);

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

        nbCommunes = communes.getTotalElements();

        int pageUp = intPage + 1; // on incrémente le numéro des pages
        int pageDown = intPage - 1; // on décrémente le numéro des pages
        int pageInc = intPage;
        pageInc = pageInc + 1;

        model.put("communes", communes); // on passe l'objet "communes" à la vue "list.html"
        model.put("nbCommunes", nbCommunes); // on passe l'objet "nbCommunes" à la vue "list.html"
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
        switch (intSize) {
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
                end = (intPage + 1) * 5;
                start = end - 4;
                lastPage = nbCommunes / 5; // size=5 => lastPage=5991 => lastPage=nbCommunes/size => 29959/5=5991
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
                end = (intPage + 1) * 10;
                start = end - 9;
                lastPage = nbCommunes / 10; // size=10 => lastPage=2995 => lastPage=nbCommunes/size => 29959/10=2995
                break;
            case 20:
                end = (intPage + 1) * 20;
                start = end - 19;
                lastPage = nbCommunes / 20; // size=20 => lastPage=1497 => lastPage=nbCommunes/size => 29959/20=1497
                break;
            case 50:
                end = (intPage + 1) * 50;
                start = end - 49;
                lastPage = nbCommunes / 50; // size=50 => lastPage=599 => lastPage=nbCommunes/size => 29959/20=599
                break;
            case 100:
                end = (intPage + 1) * 100;
                start = end - 99;
                lastPage = nbCommunes / 100; // size=100 => lastPage=299 => lastPage=nbCommunes/size => 29959/20=299
                break;
        }


        // On contrôle la valeur du paramètre "page"
        // if (x == (int)x { le nb est un entier }
//        if (page != (int)page){


//        String strPage = page.toString();
//        Integer isNumber = Integer.parseInt(strPage);
//        if (!isNumber.equals(page)){
//            throw new IllegalArgumentException("Le paramètre est 'page' est incorrect!");
//        }


//        if (!page.getClass().equals(String.class)){
//        if (Integer.parseInt(page.toString()) != page){


//        if (strPage.contains("[a-zA-Z]+")){
//
//        }

//        String strPage1 = Integer.toString(page);

        // Pas très académique, mais je n'ai pas d'autres solutions dans l'immédiat
//        Integer calculPage = 0;
//        try{
//            page = page + 1;
//            model.put("page", page); // on passe le paramètre "page" à la vue "list.html"
//        } catch (Exception e){
//            throw new IllegalArgumentException("Le paramètre est 'page' est incorrect!");
//        }



        if (intPage < 0 || intPage > lastPage) {
            throw new IllegalArgumentException("Le paramètre est 'page' est incorrect!");
        }
//        if ((intSize != 0) || (intSize != 100)) {
        if (intSize < 0 || intSize > 100) {
            throw new IllegalArgumentException("Le paramètre est 'size' est incorrect!");
        }

//        if (page != (int)page){
//            throw new IllegalArgumentException("Le paramètre est 'page' est incorrect!");
//        }
        // On contrôle la valeur du paramètre "sortProperty"
        if (!sortProperty.equals("codeInsee") && !sortProperty.equals("codePostal") && !sortProperty.equals("nom") && !sortProperty.equals("latitude") && !sortProperty.equals("longitude")) {
            throw new IllegalArgumentException("Le paramètre est 'sortProperty' est incorrect!");
        }
        // On contrôle la valeur du paramètre "sortDirection"
        if (!sortDirection.equals("ASC") && !sortDirection.equals("DESC")) {
            throw new IllegalArgumentException("Le paramètre est 'sortDirection' est incorrect!");
        }


//            lastPage = 2995L;
        model.put("size", intSize); // on passe le paramètre "size" à la vue "list.html"
        model.put("start", start); // on passe la variable "start" à la vue "list.html"
        model.put("end", end); // on passe la variable "end" à la vue "list.html"
        model.put("page", intPage); // on passe le paramètre "page" à la vue "list.html"
        model.put("pageUp", pageUp); // on passe la variable "pageUp" à la vue "list.html"
        model.put("pageDown", pageDown); // on passe la variable "pageDown" à la vue "list.html"
        model.put("pageInc", pageInc); // on passe la variable "pageInc" à la vue "list.html"
        model.put("sortDirection", sortDirection); // on passe le paramètre "sortDirection" à la vue "list.html"
        model.put("search", search); // on passe le paramètre "search" à la vue "list.html"
        model.put("lastPage", lastPage); // on passe la variable "lastPage" à la vue "list.html"

//        if (!sortDirection.equals("ASC") && !sortDirection.equals("DESC")){
//            throw new IllegalArgumentException("Le paramètre 'sortDirection' est incorrect");
//        }
//    }
//        catch (IllegalArgumentException e) {
//            e.getMessage(); // si l'exception "IllegalArgumentException" est levée
//        }

        return "list"; // chemin du template (sans .html) à partir du dossier templates
    }

}


//package com.ipiecoles.communes.web.controller;
//
//import com.ipiecoles.communes.web.model.Commune;
//import com.ipiecoles.communes.web.repository.CommuneRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Sort;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.ModelMap;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import java.util.Arrays;
//
///**
// * On trouve principalement dans cette classe, une méthode qui participe au dynamisme du template "list" (notre page d'accueil)
// */
//@Controller
//public class IndexController {
//
//    private Long lastPage = 0L;
//    private Long nbCommunes = 0L;
//
//    @Autowired
//    CommuneRepository communeRepository;
//
//    /**
//     * Lorsqu'on lance l'appli pour la première fois, er qu'on essaye de faire un tri des colonnes, ou de cliquer sur le bouton "suivant",
//     * la sélection des communes est modifiées car, l'appli tente de faire une recherche.
//     * La solutiion, c'est la variable "init", qui va permettre de différencier le premiers lancement de l'appli,
//     * des autres instants d'utilisation. On conserve ainsi la sélection par défaut (ex : 10 lignes par page), lorsqu'on
//     * trie les colonnes, ou lorsqu'on clique sur le bouton "page suivante"
//     */
//    @GetMapping(value = "/") // endpoint par défaut
//    public String index(
//            @RequestParam(value = "page", defaultValue = "0") Integer page, // le paramètre "page" est initialisé à 0
//            @RequestParam(defaultValue = "10") Integer size, // le paramètre "size" est initialisé à 10
//            @RequestParam(defaultValue = "codeInsee") String sortProperty, // le paramètre "sortProperty" prend par défaut, la valeur "codeInsee"
//            @RequestParam(defaultValue = "ASC") String sortDirection, // le paramètre "sortDirection" prend par défaut, la valeur "ASC"
//            @RequestParam(defaultValue = "") String search, // search est initialisé avec une valeur par défaut nule : ça sert à laisser le temps à l'appli de récupérer des valeurs.
//            // En effet, sans cette variable, au lancement, une recherche va être faite; ce qui va modifier la sélection en cliquant par ex sur le tri ou sur "page suivante"
////            RedirectAttributes attributes,
//            final ModelMap model) throws IllegalArgumentException {
//
////            if (String.{
////                throw new IllegalArgumentException("Le paramètre est 'page' est incorrect!");
////            }
//
////        else if (strPage.contains("[a-zA-Z]+")){
////            throw new IllegalArgumentException("Le paramètre est 'page' est incorrect!");
////        }
//
//        // On contrôle la valeur des paramètres
//
//
////        try {
//
//
//        // Constituer un PageRequest
//        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.fromString(sortDirection), sortProperty);
//
//        Page<Commune> communes;
//
//        if (search == null || search.isEmpty()) {
//            communes = communeRepository.findAll(pageRequest); // on affiche toutes les communes, s'il n'y a rien à chercher
//        } else {
//            communes = communeRepository.findByNomContainingIgnoreCase(search, pageRequest); // sinon, on affiche le résultat de la recherche
//        }
//
//        String regexFiveNumbers = "\\d{5}"; // avec ce regex, on s'assure que le code postal contient bien 5 chiffres
//        if (search.matches(regexFiveNumbers)) {
//            return "redirect:/communes/" + search;
//        }
//
//        nbCommunes = communes.getTotalElements();
//
//        int pageUp = page + 1; // on incrémente le numéro des pages
//        int pageDown = page - 1; // on décrémente le numéro des pages
//        int pageInc = page;
//        pageInc = pageInc + 1;
//
//        model.put("communes", communes); // on passe l'objet "communes" à la vue "list.html"
//        model.put("nbCommunes", nbCommunes); // on passe l'objet "nbCommunes" à la vue "list.html"
//        model.put("pageSizes", Arrays.asList(5, 10, 20, 50, 100)); // on passe l'objet "pageSizes" à la vue "list.html" (fragment "pagination.html")
//
//        // Pages après modif size
//        // de 10 à 5 => page = page * 2 => 10/5=2
//        // de 5 à 10 => page = page / 2 => 5/10=0.5
//        // de 20 à 10 => page = page * 2
//        // de 10 à 20 => page = page / 2
//        // de 20 à 5 => 5/20=0.25 => page = page * 0.25
//        // de 5 à 20 => 20/5=4 => page = page * 4
//        int end = 0;
//        int start = 0;
//        switch (size) {
//            case 5:
//                // page 1 => plage de 1 à 5
//                // page 2 => plage de 6 à 10
//                // page 3 => plage de 11 à 15
//                // page 4 => plage de 16 à 20
//                // page 5 => plage de 21 à 25
//                // page 6 => plage de 26 à 30
//                // page 7 => plage de 31 à 35
//                // page 8 => plage de 36 à 40
//                // d'où formule : end = n° page * 5 / start = end - 4
//                end = (page + 1) * 5;
//                start = end - 4;
//                lastPage = nbCommunes / 5; // size=5 => lastPage=5991 => lastPage=nbCommunes/size => 29959/5=5991
//                break;
//            case 10:
//                // page 1 => plage de 1 à 10
//                // page 2 => plage de 11 à 20
//                // page 3 => plage de 21 à 30
//                // page 4 => plage de 31 à 40
//                // page 5 => plage de 41 à 50
//                // page 6 => plage de 51 à 60
//                // page 7 => plage de 61 à 70
//                // d'où formule : end = n° page * 10 / start = end - 9 => end = n° page * size / start = end - (size - 1)
//                end = (page + 1) * 10;
//                start = end - 9;
//                lastPage = nbCommunes / 10; // size=10 => lastPage=2995 => lastPage=nbCommunes/size => 29959/10=2995
//                break;
//            case 20:
//                end = (page + 1) * 20;
//                start = end - 19;
//                lastPage = nbCommunes / 20; // size=20 => lastPage=1497 => lastPage=nbCommunes/size => 29959/20=1497
//                break;
//            case 50:
//                end = (page + 1) * 50;
//                start = end - 49;
//                lastPage = nbCommunes / 50; // size=50 => lastPage=599 => lastPage=nbCommunes/size => 29959/20=599
//                break;
//            case 100:
//                end = (page + 1) * 100;
//                start = end - 99;
//                lastPage = nbCommunes / 100; // size=100 => lastPage=299 => lastPage=nbCommunes/size => 29959/20=299
//                break;
//        }
//
//
//        // On contrôle la valeur du paramètre "page"
//        // if (x == (int)x { le nb est un entier }
////        if (page != (int)page){
//
//
////        String strPage = page.toString();
////        Integer isNumber = Integer.parseInt(strPage);
////        if (!isNumber.equals(page)){
////            throw new IllegalArgumentException("Le paramètre est 'page' est incorrect!");
////        }
//
//
////        if (!page.getClass().equals(String.class)){
////        if (Integer.parseInt(page.toString()) != page){
//
//
////        if (strPage.contains("[a-zA-Z]+")){
////
////        }
//
////        String strPage1 = Integer.toString(page);
//
//        // Pas très académique, mais je n'ai pas d'autres solutions dans l'immédiat
////        Integer calculPage = 0;
////        try{
////            page = page + 1;
////            model.put("page", page); // on passe le paramètre "page" à la vue "list.html"
////        } catch (Exception e){
////            throw new IllegalArgumentException("Le paramètre est 'page' est incorrect!");
////        }
//
//
//        if (page < 0 || page > 5991) {
//            throw new IllegalArgumentException("Le paramètre est 'page' est incorrect!");
//        }
//        if (size < 0 || size > 100) {
//            throw new IllegalArgumentException("Le paramètre est 'size' est incorrect!");
//        }
//
////        if (page != (int)page){
////            throw new IllegalArgumentException("Le paramètre est 'page' est incorrect!");
////        }
//        // On contrôle la valeur du paramètre "sortProperty"
//        if (!sortProperty.equals("codeInsee") && !sortProperty.equals("codePostal") && !sortProperty.equals("nom") && !sortProperty.equals("latitude") && !sortProperty.equals("longitude")) {
//            throw new IllegalArgumentException("Le paramètre est 'sortProperty' est incorrect!");
//        }
//        // On contrôle la valeur du paramètre "sortDirection"
//        if (!sortDirection.equals("ASC") && !sortDirection.equals("DESC")) {
//            throw new IllegalArgumentException("Le paramètre est 'sortDirection' est incorrect!");
//        }
//
//
////            lastPage = 2995L;
//        model.put("size", size); // on passe le paramètre "size" à la vue "list.html"
//        model.put("start", start); // on passe la variable "start" à la vue "list.html"
//        model.put("end", end); // on passe la variable "end" à la vue "list.html"
//        model.put("page", page); // on passe le paramètre "page" à la vue "list.html"
//        model.put("pageUp", pageUp); // on passe la variable "pageUp" à la vue "list.html"
//        model.put("pageDown", pageDown); // on passe la variable "pageDown" à la vue "list.html"
//        model.put("pageInc", pageInc); // on passe la variable "pageInc" à la vue "list.html"
//        model.put("sortDirection", sortDirection); // on passe le paramètre "sortDirection" à la vue "list.html"
//        model.put("search", search); // on passe le paramètre "search" à la vue "list.html"
//        model.put("lastPage", lastPage); // on passe la variable "lastPage" à la vue "list.html"
//
////        if (!sortDirection.equals("ASC") && !sortDirection.equals("DESC")){
////            throw new IllegalArgumentException("Le paramètre 'sortDirection' est incorrect");
////        }
////    }
////        catch (IllegalArgumentException e) {
////            e.getMessage(); // si l'exception "IllegalArgumentException" est levée
////        }
//
//        return "list"; // chemin du template (sans .html) à partir du dossier templates
//    }
//
//}
