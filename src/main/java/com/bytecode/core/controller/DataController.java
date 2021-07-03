package com.bytecode.core.controller;

import com.bytecode.core.model.Result;
import com.bytecode.core.service.DataService;
import com.bytecode.core.service.ResultService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    @Autowired ResultService resultService;
    
    @GetMapping("/simplekmean/{num}")
    public Result simplekmean(@PathVariable(name = "num") int num) {
        Result r = null;
        try {
            // Tratamiento de los datos
            Instances datos = dataService.obtenerDatos();
            dataService.listarDatos(datos);
            // Ingnorar atributos
            String[] opciones = new String[]{"-R", "2"};
            Remove remover = new Remove();
            remover.setOptions(opciones);
            remover.setInputFormat(datos);
            Instances nDatos = Filter.useFilter(datos, remover);
            // Generacion del modelo
            SimpleKMeans modelo = new SimpleKMeans();
            modelo.setNumClusters(num);
            modelo.buildClusterer(nDatos);
            // Enviamos los resultados
            System.out.println(modelo);
            r = resultService.obtenerResultado(modelo);
            return r;
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return r;
    }
}
