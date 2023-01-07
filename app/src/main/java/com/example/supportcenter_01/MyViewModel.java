package com.example.supportcenter_01;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.supportcenter_01.RoomDataBase.User;
import com.example.supportcenter_01.RoomDataBase.UserRepository;


public class MyViewModel extends AndroidViewModel {
    private UserRepository repository;
    private LiveData<User> allUser;


    public MyViewModel(@NonNull Application application) {
        super(application);
        repository = new UserRepository(application);
        allUser = repository.getAllUsers();
    }

    public void insert(User user) {
        repository.insert(user);
    }

    public void update(User user) {
        repository.update(user);
    }

    public void delete(User user) {
        repository.delete(user);
    }

    public void deleteAllUsers() {
        repository.deleteAllUsers();
    }

    public LiveData<User> getAllUsers() {
        return allUser;
    }

}

