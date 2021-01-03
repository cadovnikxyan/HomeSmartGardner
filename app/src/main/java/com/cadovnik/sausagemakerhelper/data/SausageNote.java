package com.cadovnik.sausagemakerhelper.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Random;

public class SausageNote implements IDBHelper{
    private String name;
    private SaltingUnit salting;
    private String description= "";
    private Bitmap bitmap;
    private long id = -1;

    public SausageNote(String name, SaltingUnit salting){
        this.name = name;
        this.salting = salting;
    }
    public SausageNote(ContentValues values){
        name = values.getAsString(DataContract.SausageNoteDB.COLUMN_SAUSAGE_NAME);
        description = values.getAsString(DataContract.SausageNoteDB.COLUMN_SAUSAGE_DESCRIPTION);
        setBitmap(values.getAsByteArray(DataContract.SausageNoteDB.COLUMN_SAUSAGE_IMAGE));
        salting = new SaltingUnit(values);
        id = values.getAsLong(DataContract.SausageNoteDB._ID);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public SaltingUnit getSalting() {
        return salting;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public ContentValues convert() {
        ContentValues values = new ContentValues();
        values.put(DataContract.SausageNoteDB.COLUMN_SALTING_UNIT, salting.getId());
        values.put(DataContract.SausageNoteDB.COLUMN_SAUSAGE_NAME, getName());
        values.put(DataContract.SausageNoteDB.COLUMN_SAUSAGE_DESCRIPTION, getDescription());
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        getBitmap().compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte imageInByte[] = stream.toByteArray();
        values.put(DataContract.SausageNoteDB.COLUMN_SAUSAGE_IMAGE, imageInByte);
        return values;
    }

    @Override
    public long insert(SQLiteDatabase db, ContentValues values) {
        salting.setSausage_name(name);
        ContentValues saltingValues = salting.convert();
        salting.insert(db, saltingValues);
        db.insert(DataContract.SausageNoteDB.TABLE_NAME,null, values);
        return 0;
    }

    @Override
    public long getId() {
        if (id == -1 )
            id = new Random().nextInt(Integer.MAX_VALUE) + (1L << 31);
        return id;
    }

    @Override
    public void removeRow(SQLiteDatabase db) {
        db.delete(DataContract.SausageNoteDB.TABLE_NAME,DataContract.SausageNoteDB._ID +"=?", new String[]{String.valueOf(getId())});
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void setBitmap(byte bitmap[]) {
        ByteArrayInputStream imageStream = new ByteArrayInputStream(bitmap);
        this.bitmap = BitmapFactory.decodeStream(imageStream);
    }
}
