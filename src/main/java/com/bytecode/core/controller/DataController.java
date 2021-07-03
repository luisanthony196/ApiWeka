package com.bytecode.core.controller;

import com.bytecode.core.service.DataService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import weka.filters.Filter;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.Remove;

@RestController
@RequestMapping("/api")
public class DataController {
    @Autowired DataService dataService;
    
    @GetMapping("/simplekmean")
    public String result() {
        try {
            // Tratamiento de los datos
            Instances datos = dataService.obtenerDatos();
            listarDatos(datos);
            String[] opciones = new String[]{"-R", "2"};
            Remove remover = new Remove();
            remover.setOptions(opciones);
            remover.setInputFormat(datos);
            Instances nDatos = Filter.useFilter(datos, remover);
            // Generacion del modelo
            SimpleKMeans model = new SimpleKMeans();
            model.setNumClusters(5);
            model.buildClusterer(nDatos);
            System.out.println(model);
            return model.toString();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        String datos = dataService.obtenerDatos().toString();
        return datos;
    }

    public void listarDatos(Instances d) {
        System.out.println("Numero de atributos" + d.numAttributes());
        for (int i = 0; i < d.numAttributes(); i++) {
            System.out.println("Atributo Nro" + (i+1) + ": " + d.attribute(i).name());
        }
    }
}
