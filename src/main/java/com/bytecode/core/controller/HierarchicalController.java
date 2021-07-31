package com.bytecode.core.controller;

import java.util.HashMap;

import com.bytecode.core.model.ClusteredInstances;
import com.bytecode.core.model.HierarchicalCluster;
import com.bytecode.core.service.HierarchicalService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST })
@RequestMapping("/api/hierarchical")
public class HierarchicalController {
  @Autowired
  HierarchicalService hierarchicalService;

  @PostMapping("")
  public HierarchicalCluster hierarchical(@RequestBody HashMap<String, Object> params) {
    hierarchicalService.initService(params);
    return hierarchicalService.toHierarchicalCluster();
  }

  @PostMapping("/list")
  public ClusteredInstances hierarchicalList() {
    return hierarchicalService.toClusteredInstances();
  }
}
