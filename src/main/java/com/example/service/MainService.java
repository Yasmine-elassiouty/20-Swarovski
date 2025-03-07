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

    public ArrayList<T> findAll() {
        return mainRepository.findAll();
    }


    public void save(T data) {
        mainRepository.save(data);
    }


    public void saveAll(ArrayList<T> data) {
        mainRepository.saveAll(data);
    }


    public void overrideData(ArrayList<T> data) {
        mainRepository.overrideData(data);
    }
}