package com.ipiecoles.communes.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    /**
     * Permet d'afficher le message inhérent à l'exception "EntityNotFoundException"
     *
     * @param e : exception de type "EntityNotFoundException"
     * @return modelAndView
     */
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleEntityNotFoundException(EntityNotFoundException e) {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("message", e.getMessage());
        logger.info("Une exception de type 'EntityNotFoundException' a été levée ...");
        return modelAndView;
    }

    /**
     * Permet d'afficher le message inhérent à l'exception "IllegalArgumentException"
     *
     * @param e : exception de type "IllegalArgumentException"
     * @return modelAndView
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleIllegalArgumentException(IllegalArgumentException e) {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("message", e.getMessage());
        logger.info("Une exception de type 'IllegalArgumentException' a été levée ...");
        return modelAndView;
    }

    /**
     * Permet d'afficher le message inhérent à l'exception "UsernameNotFoundException"
     *
     * @param e : exception de type "UsernameNotFoundException"
     * @return modelAndView
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ModelAndView handleUsernameNotFoundException(UsernameNotFoundException e) {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("message", e.getMessage());
        logger.info("Une exception de type 'UsernameNotFoundException' a été levée ...");
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