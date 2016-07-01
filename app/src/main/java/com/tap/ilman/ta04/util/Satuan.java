package com.tap.ilman.ta04.util;

enum Satuan {
    jam, menit, detik,

    abad, windu, dasawarsa, tahun, minggu, hari, bulan, semester, caturwulan, lustrum,

    kodi, lusin, buah, rim, gross, lembar,

    mm, cm, dm, m, dam, hm, km, mil, inci, feet,

    mm2, cm2, dm2, m2, dam2, hm2, km2,

    mm3, cm3, dm3, m3, dam3, hm3, km3, ml, cl, dl, l, dal, hl, kl,

    mg, cg, dg, g, dag, hg, kg, ons, pon, ton, kuintal,

    // pretypes
    rp, rupiah,

    // simply unknown
    unknown, akar;

    static Satuan fromString(String s) {
        try {
            return Satuan.valueOf(s.toLowerCase());
        } catch (EnumConstantNotPresentException e) {
            System.out.println("search for in family of km2 and km3");
            switch (s.toLowerCase()) {
                case "km\u00B2":
                    return km2;
                case "km\u00B3":
                    return km3;
                case "hm\u00B2":
                    return hm2;
                case "hm\u00B3":
                    return hm3;
                case "dam\u00B2":
                    return dam2;
                case "dam\u00B3":
                    return dam3;
                case "m\u00B2":
                    return m2;
                case "m\u00B3":
                    return m3;
                case "dm\u00B2":
                    return km2;
                case "dm\u00B3":
                    return km3;
                case "cm\u00B2":
                    return cm2;
                case "cm\u00B3":
                    return cm3;
                case "mm\u00B2":
                    return mm2;
                case "mm\u00B3":
                    return mm3;

                default:
                    return Satuan.unknown;
            }

        }


    }

    public static boolean isPangkatOnly(String s) {
        char c;
        for (int i = 0; i < s.length(); i++) {
            c = s.charAt(i);
            //zero first
            if (s.length() > 1 && i == 0 &&c  == '\u2070'){
                return false;
            }
            if (c == '\u00B2' || c == '\u00B3' || c == '\u2074' || c == '\u2075' || c == '\u2076' || c == '\u2077'
                    || c == '\u2078' || c == '\u2079'
                    || c == '\u00B9' || c == '\u2070') {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }
}


enum Kategori {
    // categorizing it
    pukul, panjang, luas, berat, volume, jmd, buahan, uang, speed, debit, unknown
}
