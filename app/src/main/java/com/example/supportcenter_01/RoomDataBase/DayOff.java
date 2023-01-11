package com.example.supportcenter_01.RoomDataBase;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "dayOff_table")
public class DayOff {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "staffid")
    private int staffid;

//    @ColumnInfo(name = "leave")
    private Leave leave;
    @ColumnInfo(name = "applicationdate")
    private int applicationDate;
    @ColumnInfo(name = "approval")
    private boolean approval;

    public Leave getLeave() {
        return leave;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLeave(Leave leave) {
        this.leave = leave;
    }


}
