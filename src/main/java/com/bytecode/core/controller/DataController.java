package com.bytecode.core.controller;

import com.bytecode.core.service.DataService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import weka.clusterers.SimpleKMeans;

@RestController
@RequestMapping("/api")
public class DataController {
    @Autowired DataService dataService;
    
    @GetMapping("/percent")
    public String result() {
        try {
            SimpleKMeans model = new SimpleKMeans();
            model.setNumClusters(5);
            model.buildClusterer(dataService.obtenerDatos());
            System.out.println(model);
            return model.toString();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        String datos = dataService.obtenerDatos().toString();
        return datos;
    }
}
