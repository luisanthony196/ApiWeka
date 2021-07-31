package com.bytecode.core.controller;

import java.util.HashMap;

import com.bytecode.core.model.SimpleKMeanCluster;
import com.bytecode.core.service.SimpleKMeanService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST})
@RequestMapping("/api/simplekmean")
public class SimpleKMeanController {
  @Autowired
  SimpleKMeanService simpleKMeanService;

  @PostMapping("")
  public SimpleKMeanCluster hierarchical(@RequestBody HashMap<String, Object> params) {
    simpleKMeanService.initService(params);
    return simpleKMeanService.toSimpleKMeansCluster();
  }

  // @PostMapping("/list")
  // public ClusteredInstances hierarchicalList() {
  //   return hierarchicalService.toClusteredInstances();
  // }
}
