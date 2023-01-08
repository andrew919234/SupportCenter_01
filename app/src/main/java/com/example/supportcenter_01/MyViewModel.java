package com.example.supportcenter_01;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.supportcenter_01.RoomDataBase.User;
import com.example.supportcenter_01.RoomDataBase.UserRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MyViewModel extends AndroidViewModel {
    private UserRepository repository;
    private LiveData<User> allUser;
    private FirebaseAuth auth;


    public MyViewModel(@NonNull Application application) {
        super(application);
        repository = new UserRepository(application);
        allUser = repository.getAllUsers();
    }

    //login 檢查使用者
    public boolean checkUser(String emailaddress, String password) {
//        if (!emailaddress.isEmpty() && !password.isEmpty()) {
//            auth.signInWithEmailAndPassword(emailaddress, password)
//                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            if (task.isSuccessful()) {
//                                FirebaseUser f_user = auth.getCurrentUser();
//                                insert(emailaddress, password);
//                            } else {
//                            }
//                        }
//                    });
//        } else {
//            return false;
//        }
        return false;
    }

    //login 檢查使用者是否在線上
    public boolean checkUserOnline() {
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            return true;
        }
        return false;
    }

    //資料庫 存取User
    public void insert(String emailaddress, String password) {
        User user = new User(emailaddress, password);
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

