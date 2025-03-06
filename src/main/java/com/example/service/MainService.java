package com.example.service;

import com.example.repository.MainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Primary
@Service
public abstract class MainService<T> {

    protected MainRepository<T> mainRepository;

    @Autowired
    public MainService(MainRepository<T> repository) {
        this.mainRepository = repository;
    }

    /**
     * Retrieves all entities from the system.
     *
     * @return A list of all entities.
     */
    public ArrayList<T> findAll() {
        return mainRepository.findAll();
    }

    /**
     * Saves a single entity to the system.
     *
     * @param data The entity to save.
     */
    public void save(T data) {
        mainRepository.save(data);
    }

    /**
     * Saves a list of entities to the system.
     *
     * @param data The list of entities to save.
     */
    public void saveAll(ArrayList<T> data) {
        mainRepository.saveAll(data);
    }

    /**
     * Overwrites the entire data with the provided list of entities.
     *
     * @param data The list of entities to overwrite the existing data.
     */
    public void overrideData(ArrayList<T> data) {
        mainRepository.overrideData(data);
    }
}