package com.bytecode.core.controller;

import com.bytecode.core.service.DataService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DataController {
    @Autowired DataService dataService;
    
    @GetMapping("/percent")
    public String result() {
        String datos = dataService.obtenerDatos().toString();
        return datos;
    }
}
