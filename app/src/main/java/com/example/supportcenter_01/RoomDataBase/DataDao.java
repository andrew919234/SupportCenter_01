package com.example.supportcenter_01.RoomDataBase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
@Dao
public interface DataDao {

    String tableName = "users_table";

    /**簡易新增所有資料的方法*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)//預設萬一執行出錯怎麼辦，REPLACE為覆蓋
    void insertData(User myData);
    @Query("INSERT INTO "+tableName+"(email,password) VALUES(:email,:password)")
    void insertData(String email,String password);

    @Query("INSERT INTO "+tableName+"(id,email,password) VALUES(:id,:email,:password)")
    void insertData(int id,String email,String password);
    /**撈取全部資料*/
    @Query("SELECT * FROM " + tableName)
    LiveData<User> displayAll();

    /**撈取某個名字的相關資料*/
    @Query("SELECT * FROM " + tableName +" WHERE email = :email")
    List<User> findDataByName(String email);

    /**簡易更新資料的方法*/
    @Update
    void updateData(User myData);

    /**複雜(?)更新資料的方法*/
    @Query("UPDATE "+tableName+" SET email = :email,password=:password WHERE id = :id" )
    void updateData(int id,String email,String password);

    /**簡單刪除資料的方法*/
    @Delete
    void deleteData(User myData);

    /**複雜(?)刪除資料的方法*/
    @Query("DELETE  FROM " + tableName + " WHERE id = :id")
    void deleteData(int id);
    @Query("DELETE  FROM " + tableName )
    void deleteAllData();

}
