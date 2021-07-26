package com.bytecode.core.service;

import com.bytecode.core.repository.InstancesRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import weka.core.Instances;

@Service
public class InstancesService {
    @Autowired
    InstancesRepository dataRepository;

    public Instances obtenerDatos() {
        if (dataRepository.isEmpty())
            dataRepository.llenarDatos();
        return dataRepository.obtenerDatos();
    }

    public void listarDatos(Instances d) {
        System.out.println("\nNumero de atributos" + d.numAttributes());
        for (int i = 0; i < d.numAttributes(); i++) {
            System.out.println("Atributo Nro" + (i+1) + ": " + d.attribute(i).name());
        }
    }
}
