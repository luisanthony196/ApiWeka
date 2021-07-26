package com.bytecode.core.controller;

import com.bytecode.core.model.ClusteredInstances;
import com.bytecode.core.model.HierarchicalCluster;
import com.bytecode.core.service.InstancesService;
import com.bytecode.core.service.HierarchicalService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import weka.core.Instances;

@RestController
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST})
@RequestMapping("/api")
public class HierarchicalController {
  @Autowired
  InstancesService dataService;
  @Autowired
  HierarchicalService hierarchicalService;

  @GetMapping("/hierarchical")
  public HierarchicalCluster hierarchical(@RequestParam(defaultValue = "single") String link, @RequestParam int clusters) {
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
  public ClusteredInstances hierarchicalList() {
    if(hierarchicalService.m_clusters == null) {
      hierarchical("single", 2);
    }
    return hierarchicalService.obtenerLista();
  }
}
