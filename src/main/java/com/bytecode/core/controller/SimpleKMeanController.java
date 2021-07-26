package com.bytecode.core.controller;

import com.bytecode.core.model.SimpleKMean;
import com.bytecode.core.service.InstancesService;
import com.bytecode.core.service.SimpleKMeanService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import weka.clusterers.SimpleKMeans;
import weka.core.Instances;

@RestController
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST})
@RequestMapping("/api")
public class SimpleKMeanController {
  @Autowired
  InstancesService dataService;
  @Autowired
  SimpleKMeanService resultService;

  @GetMapping("/simplekmean/{num}")
  public SimpleKMean simplekmean(@PathVariable(name = "num") int num) {
    SimpleKMean r = null;
    try {
      // Tratamiento de los datos
      Instances datos = dataService.obtenerDatos();
      dataService.listarDatos(datos);
      // Generacion del modelo
      SimpleKMeans modelo = new SimpleKMeans();
      modelo.setNumClusters(num);
      modelo.buildClusterer(datos);
      r = resultService.obtenerResultado(modelo);
      return r;
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
    return r;
  }
    
}
