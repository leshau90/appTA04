package com.tap.ilman.ta04.util;


import java.util.Queue;

/**
 * Created by Lenovo on 3/4/2015.
 */
public class Analyze {

    public static PExpr[] getKeys(Param ListKunci[]) {
        System.out.println("getKeys: getting key from array of key in DetaiSoal");
        int i = 0;
        PExpr compiledKeys[] = new PExpr[ListKunci.length];
        while (i < ListKunci.length) {
            System.out.println("Apputil: getKeys: tobeparsed "+ListKunci[i]);
            SemanticParam asj = SintakProcessor.startAnalysisPT2(LexUtility.getLex3(ListKunci[i]), ListKunci[i]);
            if (asj.isHasilSintakSudahBenar()) {
                compiledKeys[i] = MantikProcessor.createExpr(asj);
            } else {
                System.out.println(" key number " + i + " is wrong please check database");
            }
            i++;
        }
        SemanticParam AnalisaSintaktisJawaban = SintakProcessor.startAnalysisPT2(LexUtility.getLex3(ListKunci[1]), ListKunci[1]);
        return compiledKeys;
    }

    public static boolean isItTrue(PExpr[] kunci, PExpr jawaban) {
        boolean b = false;
        System.out.println("isItTrue mengecek sejumlah kunci dengan jawaban ....");
        int i = 0;
        while (i < kunci.length) {
            if (kunci[i] != null) {
                if (kunci[i].equals(jawaban)) {
                    b = true;
                    System.out.println("Nilai Semantic Kunci ke " + (i + 1) + " sama dengan jawaban:" + jawaban + " BREAKING");
                    break;
                }
            }
            i++;
        }
        return b;
    }



    public static SemanticParam checkSemantic(Param p) {
        return SintakProcessor.startAnalysisPT2(LexUtility.getLex3(p), p);
    }

    public static PExpr toPExpr(SemanticParam sp) {
        return MantikProcessor.createExpr(sp);
    }

    public static void justOne(String s) {
        System.out.println("input is " + s);

        Param in = new Param().setInput(s);

        Queue<Lexeme> m = LexUtility.getLex3(in);
        SintakProcessor.startAnalysisPT2(m, in);

//      StringBuilder sb = new StringBuilder();
//		PExpr Jawaban = MantikProcessor.createExpr((Queue) SintakProcessor
//				.startAnalysisP(SintakProcessor.OperandMerge(LexUtility
//						.getLex(s)))[1]);

        // System.out.println(Jawaban);

//		System.out.println(Jawaban.printTree(0));
//
//		System.out.println(Jawaban);

    }


//    public static String isItTrue(Param tulisanKunci, Param tulisanJawaban) {
//        StringBuilder sb = new StringBuilder();
//        System.out.println("~~~~~~Kunci Jawaban~~~~~~~~");
//
//        SemanticParam AnalisaSintaktisJawaban = SintakProcessor.startAnalysisPT2(LexUtility.getLex3(tulisanKunci), tulisanKunci);
//        System.out.println("~~~~Kunci Jawaban~~~Selesai~~~~~");
//
//
//        SemanticParam paramater = SintakProcessor.startAnalysisPT2(LexUtility.getLex3(tulisanJawaban));
//
//        if (!(boolean) HasilAnalisaSintaktis[0]) {
//            // return false;
//
//        } else {
//
//            PExpr MJawaban = MantikProcessor
//                    .createExpr((Queue) HasilAnalisaSintaktis[1]);
//            sb.append("\nini akan di cek jawaban : " + MJawaban).append(
//                    "\n dan kuncinya adalah : " + Kunci);
//            MJawaban.cekKurungBerlebih();
//            Kunci.cekKurungBerlebih();
//            if (MJawaban.equals(Kunci)) {
//                // return true;
//                sb.append("\nkunci dan jawaban cocok...");
//            } else {
//                // return false;
//                sb.append("\njawaban salah coba baca soal lagi...");
//            }
//        }
//        return sb.toString();
//    }
}
