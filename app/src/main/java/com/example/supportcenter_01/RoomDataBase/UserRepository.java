package com.example.supportcenter_01.RoomDataBase;


import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class UserRepository {
    private DataDao dao;
    private LiveData<User> allUsers;

    public UserRepository(Application application) {
        MyDataBase myDataBase = MyDataBase.getInstance(application);
        dao = myDataBase.DataDao();
        allUsers = dao.displayAll();
    }

    public void insert(User user) {
        new InsertUserAsybcTask(dao).execute(user);
    }

    public void update(User user) {
        new UpdateUserAsybcTask(dao).execute(user);
    }

    public void delete(User user) {
        new DeleteUserAsybcTask(dao).execute(user);
    }

    public void deleteAllUsers() {
        new DeleteAllUserAsybcTask(dao).execute();
    }

    public LiveData<User> getAllUsers() {
        return allUsers;
    }

    private static class InsertUserAsybcTask extends AsyncTask<User, Void, Void> {
        private DataDao dao;

        private InsertUserAsybcTask(DataDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(User... users) {
            dao.insertData(users[0]);
            return null;
        }
    }

    private static class UpdateUserAsybcTask extends AsyncTask<User, Void, Void> {
        private DataDao dao;

        private UpdateUserAsybcTask(DataDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(User... users) {
            dao.updateData(users[0]);
            return null;
        }
    }

    private static class DeleteUserAsybcTask extends AsyncTask<User, Void, Void> {
        private DataDao dao;

        private DeleteUserAsybcTask(DataDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(User... users) {
            dao.deleteAllData();
            return null;
        }
    }

    private static class DeleteAllUserAsybcTask extends AsyncTask<User, Void, Void> {
        private DataDao dao;

        private DeleteAllUserAsybcTask(DataDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(User... users) {
            dao.deleteData(users[0]);
            return null;
        }
    }
}
