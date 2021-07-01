package com.bytecode.core.service;

import com.bytecode.core.repository.DataRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import weka.core.Instances;

@Service
public class DataService {
    @Autowired
    DataRepository dataRepository;

    public Instances obtenerDatos() {
        if (dataRepository.isEmpty())
            dataRepository.llenarDatos();
        return dataRepository.obtenerDatos();
    }
}
