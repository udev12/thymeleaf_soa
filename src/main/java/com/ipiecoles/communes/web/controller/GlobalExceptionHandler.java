package com.ipiecoles.communes.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.persistence.EntityNotFoundException;

/**
 *
 */
@ControllerAdvice// Permet à cette classe de "catcher" les exceptions
// levées par les contrôleurs
public class GlobalExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleEntityNotFoundException(EntityNotFoundException e, IllegalArgumentException e1,
                                                final ModelMap model){
        model.put("message",e.getMessage());
        model.put("message",e1.getMessage());
        return "error";
    }
}