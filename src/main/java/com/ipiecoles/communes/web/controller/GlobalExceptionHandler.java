package com.ipiecoles.communes.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityNotFoundException;

/**
 * Ici, on va gérer de façon globale, toutes les exceptions "EntityNotFoundException" et "IllegalArgumentException" levées dans notre contrôleur
 */
@ControllerAdvice// Permet à cette classe de "catcher" les exceptions
public class GlobalExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleEntityNotFoundException(EntityNotFoundException e){
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("message", e.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleIllegalArgumentException(IllegalArgumentException e){
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("message", e.getMessage());
        return modelAndView;
    }

}











//package com.ipiecoles.communes.web.controller;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.ui.ModelMap;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.ResponseStatus;
//
//import javax.persistence.EntityNotFoundException;
//
///**
// * Classe qui gère les exceptions de l'application de façon globale
// */
//@ControllerAdvice // permet à cette classe de "catcher" les exceptions levées par les contrôleurs
//public class GlobalExceptionHandler {
//    @ExceptionHandler(EntityNotFoundException.class)
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    public String handleEntityNotFoundException(EntityNotFoundException e/*, IllegalArgumentException e1*/,
//                                                final ModelMap model){
//        model.put("message",e.getMessage());
////        model.put("message",e1.getMessage());
//        return "error";
//    }
//}