package com.bytecode.core.controller;

import java.util.HashMap;

import com.bytecode.core.model.Result;
import com.bytecode.core.service.DataService;
import com.bytecode.core.service.HierarchicalService;
import com.bytecode.core.service.ResultService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import weka.filters.Filter;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.Remove;

@RestController
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST})
@RequestMapping("/api")
public class DataController {
  @Autowired
  DataService dataService;
  @Autowired
  ResultService resultService;
  @Autowired
  HierarchicalService hierarchicalService;

  @GetMapping("/hierarchical")
  public Result hierarchical(@RequestParam(defaultValue = "single") String link, @RequestParam int clusters) {
    try {
      // Se obtienen los datos
      Instances data = dataService.obtenerDatos();
      hierarchicalService.setLinkType(link);
      hierarchicalService.setNumClusters(clusters);
      // Se procesan las instancias
      hierarchicalService.init(data);
    } catch (Exception e) {
      System.err.print(e.getMessage());
    }
    return hierarchicalService.obtenerResultados();
  }

  @GetMapping("/hierarchical/list")
  public HashMap<Integer, String[]> hierarchicalList() {
    if(hierarchicalService.m_clusters == null) {
      hierarchical("single", 2);
    }
    return hierarchicalService.obtenerLista();
  }

  @GetMapping("/simplekmean/{num}")
  public Result simplekmean(@PathVariable(name = "num") int num) {
    Result r = null;
    try {
      // Tratamiento de los datos
      Instances datos = dataService.obtenerDatos();
      dataService.listarDatos(datos);
      // Ingnorar atributos
      String[] opciones = new String[] { "-R", "2" };
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
