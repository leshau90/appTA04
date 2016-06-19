package com.tap.ilman.ta04;

enum MenuCategory {
    SATU, DUA, TIGA,
    EMPAT, LIMA, ENAM, SATUAN, GEOMETRI,
    LUAS, BUAH, CAMPURAN, HARD, SIMPLE, VOLUME, WAKTU,
    NEGATIVE, UANG, BERAT, PUKUL,
    PANJANG, PANGKAT, KELILING, PERSEN, JMD, TEMPERATURE, SPEED, MAINUNKNOWN, UNKNOWN;
}

public class MenuItem implements Comparable<MenuItem> {

    MenuCategory cat;
    public int amount;


    public MenuItem() {
    }

    public MenuItem(MenuCategory cat, int amount) {
        this.cat = cat;
        this.amount = amount;

    }

    public MenuCategory getType() {
        return cat;
    }

    public void setType(MenuCategory it) {
        this.cat = it;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return new StringBuilder("[").append(this.getType().name()).append(" ").append(this.getAmount()).append(" soal").append("]").toString();
    }

    @Override
    public int compareTo(MenuItem another) {
        return this.getType().compareTo(another.getType());
    }
}

class ItemDaftarSoal {
    String _id;

    public void setContent(String content) {
        this.content = content;
    }

    String content="";

    public ItemDaftarSoal() {
    }

    public ItemDaftarSoal(String _id, String content) {
        this._id = _id;
        this.content = content;    }

    @Override
    public String toString() {
        return new StringBuilder('[').append(_id).append(" , ").append(content).append(']').toString();
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getContent() {
        return this.content;
    }
}

class ItemSoalQuick {
    String s;

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }
}
