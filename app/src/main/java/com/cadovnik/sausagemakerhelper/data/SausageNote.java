package com.cadovnik.sausagemakerhelper.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayInputStream;

import androidx.annotation.Nullable;

public class SausageNote implements IDBHelper{
    private String name;
    private SaltingUnit salting;
    private HeatingProcess heating;
    private String description;
    private Bitmap bitmap;

    public SausageNote(String name, SaltingUnit salting, @Nullable HeatingProcess heating){
        this.name = name;
        this.salting = salting;
        this.heating = heating;
    }
    public SausageNote(ContentValues values){
        name = values.getAsString(DataContract.SausageNoteDB.COLUMN_SAUSAGE_NAME);
        description = values.getAsString(DataContract.SausageNoteDB.COLUMN_SAUSAGE_DESCRIPTION);
//        setBitmap(values.getAsByteArray(DataContract.SausageNoteDB.COLUMN_SAUSAGE_IMAGE));
        salting = new SaltingUnit(values);
        heating = new HeatingProcess(values);
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

    public HeatingProcess getHeating() {
        return heating;
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
//        values.put(DataContract.SausageNoteDB._ID, getId());
        values.put(DataContract.SausageNoteDB.COLUMN_SALTING_UNIT, salting.getId());
        values.put(DataContract.SausageNoteDB.COLUMN_HEATING_PROCESS, -1);
        values.put(DataContract.SausageNoteDB.COLUMN_SAUSAGE_NAME, getName());
        values.put(DataContract.SausageNoteDB.COLUMN_SAUSAGE_DESCRIPTION, getDescription());
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, stream);
//        byte imageInByte[] = stream.toByteArray();
//        values.put(DataContract.SausageNoteDB.COLUMN_SAUSAGE_IMAGE, imageInByte);
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
    public int getId() {
        return this.hashCode();
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
