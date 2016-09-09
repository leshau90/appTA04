package com.tap.ilman.ta04.util;

import java.util.Queue;

enum SSM {
    // beside checking for syntax error...
    // this FSM will also merge operand as necessary
    SYNTAX_START("kamu harus memulai jawabanmu dengan angka") {
        @Override
        SSM tokenSelanjutnya(Queue<Lexeme> m, Lexeme lexeme, Lexeme lastLexeme, TypedLexeme merged,
                             Param p, int lvl, ErrMes err, Lexeme nextLexeme) {
            switch (lexeme.getToken()) {
                case WHITESPACE:
                    return this;
                case KURUNGBUKA:
                    m.add(lexeme);
                    return SYNTAX_OPEN_PARENTHESIS;
                case OPERATOR:
                    if (p.input.charAt(lexeme.getStart()) == '-') {
                        // don't add it yet to queue, as it will be added after
                        // merged as an operand
                        merged.setNegative(true);
                        merged.setNegLocation(lexeme);

                        return SYNTAX_NEGATIVE;
                    } else {
                        SintakProcessor.simpleErrSet(err, lexeme);
                        err.setMess("hanya tanda buka kurung atau tanda negatif diizinkan di awal");

                        return SYNTAX_ERROR;
                    }

                case OPERAND:
                    // add to temp instead
                    merged.setStart(lexeme.getStart());
                    merged.setEnd(lexeme.getEnd());
                    merged.setKategori(Kategori.unknown);
                    merged.setToken(Token.OPERAND);

                    // sb.append((String) lexeme[SintakProcessor.iIsi]);
                    //SintakProcessor.simpleErrSet(err, lexeme);
                    return SYNTAX_UNTYPED_OPERAND;
                case SATUAN:
                    // hanya rp saja yang boleh
                    if (Satuan.rp.toString().equalsIgnoreCase(p.input.substring(lexeme.getStart(), lexeme.getEnd()))) {
                        // don't add it yet to queue
                        merged.setTypeLoc(lexeme);
                        return SYNTAX_RP;
                        //akar
                    } else if (merged.getAkarLoc() == null && p.input.charAt(lexeme.getStart()) == '\u221A') {
                        //akar
                        merged.setAkarLoc(lexeme);
                        return SYNTAX_UNTYPED_OPERAND;
                    } else {
                        SintakProcessor.simpleErrSet(err, lexeme);
                        err.setMess("ini tidak boleh ditulis di awal");
                        return SYNTAX_ERROR;
                    }
                default:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess(this.getErr());
                    return SYNTAX_ERROR;
            }
        }
    },
    // rp types.. routes
    SYNTAX_OPERATOR(" ini tidak bisa ditulis setelah tanda operasi matematika") {
        @Override
        SSM tokenSelanjutnya(Queue<Lexeme> m, Lexeme lexeme, Lexeme lastLexeme, TypedLexeme merged,
                             Param p, int lvl, ErrMes err, Lexeme nextLexeme) {
            switch (lexeme.getToken()) {
                case WHITESPACE:
                    return this;
                case KURUNGBUKA:
                    m.add(lexeme);
                    return SYNTAX_OPEN_PARENTHESIS;
                case OPERATOR:
                    if (p.input.charAt(lexeme.getStart()) == '-') {
                        // don't add it yet to queue, as it will be added after
                        // merged as an operand
                        merged.setNegative(true);
                        merged.setNegLocation(lexeme);
                        return SYNTAX_NEGATIVE;
                    } else {
                        SintakProcessor.simpleErrSet(err, lexeme);
                        err.setMess("hanya tanda buka kurung atau tanda negatif diizinkan di awal");

                        return SYNTAX_ERROR;
                    }
                case OPERAND:

                    merged.setStart(lexeme.getStart());
                    merged.setEnd(lexeme.getEnd());
                    merged.setKategori(Kategori.unknown);
                    merged.setToken(Token.OPERAND);
                    // System.out.println("is it ERROR--"+lexeme);
                    lastLexeme.setNewValues(lexeme);
                    // sb.append((String) lexeme[SintakProcessor.iIsi]);
                    SintakProcessor.simpleErrSet(err, lexeme);
                    return SYNTAX_UNTYPED_OPERAND;
                case SATUAN:
                    // hanya rp saja yang boleh
                    if (Satuan.rp.toString().equalsIgnoreCase(p.input.substring(lexeme.getStart(), lexeme.getEnd()))) {
                        // don't add it yet to queue
                        merged.setTypeLoc(lexeme);
                        return SYNTAX_RP;
                    } else if (merged.getAkarLoc() == null && p.input.charAt(lexeme.getStart()) == '\u221A') {
                        //akar
                        merged.setAkarLoc(lexeme);
                        return SYNTAX_UNTYPED_OPERAND;
                    } else {
                        SintakProcessor.simpleErrSet(err, lexeme);
                        err.setMess("huruf ini tidak boleh ditulis sebelum angka");
                        return SYNTAX_ERROR;
                    }
                default:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess(this.getErr());
                    return SYNTAX_ERROR;
            }
        }
    },

    SYNTAX_EQUAL("hasil jawabanmu harus menggunakan angka") {
        @Override
        SSM tokenSelanjutnya(Queue<Lexeme> m, Lexeme lexeme, Lexeme lastLexeme, TypedLexeme merged,
                             Param p, int lvl, ErrMes err, Lexeme nextLexeme) {

            // nullify(merged);
            switch (lexeme.getToken()) {
                case WHITESPACE:
                    return this;
                case OPERATOR:
                    if (p.input.charAt(lexeme.getStart()) == '-') {
                        // don't add it yet to queue, as it will be added after
                        // merged as an operand
                        merged.setNegative(true);
                        merged.setNegLocation(lexeme);
                        return SYNTAX_FINAL_NEGATIVE;
                    } else {
                        SintakProcessor.simpleErrSet(err, lexeme);
                        err.setMess("hanya tanda buka kurung atau tanda negatif diizinkan di awal");

                        return SYNTAX_ERROR;
                    }
                case OPERAND:
                    // add to temp instead
                    merged.setStart(lexeme.getStart());
                    merged.setEnd(lexeme.getEnd());
                    merged.setKategori(Kategori.unknown);
                    merged.setToken(Token.OPERAND);

                    lastLexeme.setNewValues(lexeme);

                    SintakProcessor.simpleErrSet(err, lexeme);
                    m.add(merged);
                    return SYNTAX_FINAL_UNTYPED_OPERAND;
                case SATUAN:
                    if (Satuan.rp.toString().equalsIgnoreCase(p.input.substring(lexeme.getStart(), lexeme.getEnd()))) {
                        m.add(merged);

                        merged.setTypeLoc(lexeme);
                        return SYNTAX_FINAL_RP;
                    } else if (merged.getAkarLoc() == null && p.input.charAt(lexeme.getStart()) == '\u221A') {
                        //akar
                        merged.setAkarLoc(lexeme);
                        return SYNTAX_FINAL_UNTYPED_OPERAND;
                    } else {
                        SintakProcessor.simpleErrSet(err, lexeme);
                        err.setMess("huruf ini tidak boleh ditulis diawal");
                        return SYNTAX_ERROR;
                    }

                default:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess(this.getErr());
                    return SYNTAX_ERROR;
            }
        }
    },

    SYNTAX_CLOSE_PARENTHESIS("Setelah tanda Kurung tutup harusnya tanda + - x : = ") {
        @Override
        SSM tokenSelanjutnya(Queue<Lexeme> m, Lexeme lexeme, Lexeme lastLexeme, TypedLexeme merged,
                             Param p, int lvl, ErrMes err, Lexeme nextLexeme) {

            // nullify(merged);
            switch (lexeme.getToken()) {
                case WHITESPACE:
                    return this;
                case KURUNGTUTUP:
                    m.add(lexeme);
                    return SYNTAX_CLOSE_PARENTHESIS;
                case OPERATOR:
                    m.add(lexeme);
                    return SYNTAX_OPERATOR;
                case EQUAL:
                    if (lvl == 0) {
                        m.add(lexeme);
                        return SYNTAX_EQUAL;
                    } else {
                        SintakProcessor.simpleErrSet(err, lexeme);
                        err.setMess("cek tanda kurungmu");
                        return SYNTAX_ERROR;
                    }

                default:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess(this.getErr());
                    return SYNTAX_ERROR;
            }
        }
    },

    SYNTAX_OPEN_PARENTHESIS("Setelah tanda buka kurung harus ada angka") {
        @Override
        SSM tokenSelanjutnya(Queue<Lexeme> m, Lexeme lexeme, Lexeme lastLexeme, TypedLexeme merged,
                             Param p, int lvl, ErrMes err, Lexeme nextLexeme) {

            // nullify(merged);
            switch (lexeme.getToken()) {
                case WHITESPACE:
                    return this;
                case KURUNGBUKA:
                    m.add(lexeme);
                    return SYNTAX_OPEN_PARENTHESIS;
                case OPERATOR:
                    if (isItNegativeSym(lexeme, p)) {
                        // don't add it yet to queue, as it will be added after
                        // merged as an operand
                        merged.setNegLocation(lexeme);
                        merged.setNegative(true);
                        return SYNTAX_NEGATIVE;
                    } else {
                        SintakProcessor.simpleErrSet(err, lexeme);
                        err.setMess("hanya tanda buka kurung atau tanda negatif diizinkan di awal");
                        return SYNTAX_ERROR;
                    }
                case OPERAND:
                    // add to temp instead
                    merged.setStart(lexeme.getStart());
                    merged.setEnd(lexeme.getEnd());
                    merged.setKategori(Kategori.unknown);
                    merged.setToken(Token.OPERAND);

                    // sb.append((String) lexeme[SintakProcessor.iIsi]);
                    SintakProcessor.simpleErrSet(err, lexeme);
                    return SYNTAX_UNTYPED_OPERAND;
                case SATUAN:
                    // hanya rp saja yang boleh
                    if (Satuan.rp.toString().equalsIgnoreCase(p.input.substring(lexeme.getStart(), lexeme.getEnd()))) {
                        // don't add it yet to queue

                        merged.setTypeLoc(lexeme);
                        return SYNTAX_RP;
                    } else if (merged.getAkarLoc() == null && p.input.charAt(lexeme.getStart()) == '\u221A') {
                        //akar
                        merged.setAkarLoc(lexeme);
                        return SYNTAX_UNTYPED_OPERAND;
                    } else {
                        SintakProcessor.simpleErrSet(err, lexeme);
                        err.setMess("huruf ini tidak boleh ditulis diawal");
                        return SYNTAX_ERROR;
                    }
                default:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess(this.getErr());
                    return SYNTAX_ERROR;
            }
        }
    },

    SYNTAX_RP("setelah mata uang rupiah harus angka") {
        @Override
        SSM tokenSelanjutnya(Queue<Lexeme> m, Lexeme lexeme, Lexeme lastLexeme, TypedLexeme merged,
                             Param p, int lvl, ErrMes err, Lexeme nextLexeme) {

            merged.setKategori(Kategori.uang);
            merged.setSatuan(Satuan.rp);
            switch (lexeme.getToken()) {
                case WHITESPACE:
                    return this;
                case OPERAND:
                    // sb.setLength(0);

                    if (SintakProcessor.cekZero(p.input.substring(lexeme.getStart(), lexeme.getEnd()))) {
                        // sb.append(lexeme[SintakProcessor.iIsi]);
                        merged.setToken(Token.OPERAND);
                        merged.setStart(lexeme.getStart());
                        merged.setEnd(lexeme.getEnd());

                        lastLexeme.setNewValues(lexeme);

                        return SYNTAX_RP_OPERAND;
                    } else {
                        SintakProcessor.simpleErrSet(err, lexeme);
                        err.setMess("cek tulisan angka mu, tidak boleh dimulai dengan 0");
                        return SYNTAX_ERROR;
                    }
                default:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess(this.getErr());
                    return SYNTAX_ERROR;
            }
        }
    },

    SYNTAX_RP_OPERAND(" seteleh angka dalam rupiah hanya boleh tanda matematika ") {
        @Override
        SSM tokenSelanjutnya(Queue<Lexeme> m, Lexeme lexeme, Lexeme lastLexeme, TypedLexeme merged,
                             Param p, int lvl, ErrMes err, Lexeme nextLexeme) {

            switch (lexeme.getToken()) {
                case WHITESPACE:
                    return this;
                case KURUNGTUTUP:
                    m.add(merged);
                    m.add(lexeme);
                    return SYNTAX_CLOSE_PARENTHESIS;
                case OPERATOR:
                    m.add(merged);
                    m.add(lexeme);
                    return SYNTAX_OPERATOR;
                case EQUAL:

                    if (lvl == 0) {
                        m.add(merged);
                        m.add(lexeme);
                        return SYNTAX_EQUAL;
                    } else {
                        SintakProcessor.simpleErrSet(err, lexeme);
                        err.setMess("cek tanda kurungmu");
                        return SYNTAX_ERROR;
                    }
                case TITIK:
                    return SYNTAX_RP_DOT;
                case COMMA:
                    return SYNTAX_RP_COMMA;
                default:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess(this.getErr());
                    return SYNTAX_ERROR;
            }
        }
    },

    SYNTAX_RP_DOT("setelah titik harus ada angka ") {
        @Override
        SSM tokenSelanjutnya(Queue<Lexeme> m, Lexeme lexeme, Lexeme lastLexeme, TypedLexeme merged,
                             Param p, int lvl, ErrMes err, Lexeme nextLexeme) {

            switch (lexeme.getToken()) {
                case WHITESPACE:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess("tidak boleh ada spasi setelah tanda titik");
                    return SYNTAX_ERROR;
                case OPERAND:
                    // BACKTRACK to check the operand string length by looking
                    // at variable temp
                    if (lastLexeme.getLength() <= 3 && lastLexeme.getLength() > 0) {

                        // check again if current lexeme has exactly 3 digit (after
                        // dot)
                        if (lexeme.getLength() != 3) {
                            SintakProcessor.simpleErrSet(err, lexeme);
                            err.setMess("setelah titik hanya boleh tiga angka");
                            return SYNTAX_ERROR;
                        }
                        merged.setEnd(lexeme.getEnd());
                        lastLexeme.setNewValues(lexeme);
                        return SYNTAX_RP_OPERAND_AFTER_DOT;

                    } else {
                        // simpleErrSet(err, lexeme);
                        SintakProcessor.simpleErrSet(err, lastLexeme);
                        err.setMess("sebelum titik tidak beoleh lebih dari tiga angka");
                        return SYNTAX_ERROR;
                    }

                default:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess(this.getErr());
                    return SYNTAX_ERROR;
            }
        }
    },

    SYNTAX_RP_OPERAND_AFTER_DOT("setelah titik harus ada angka ") {
        @Override
        SSM tokenSelanjutnya(Queue<Lexeme> m, Lexeme lexeme, Lexeme lastLexeme, TypedLexeme merged,
                             Param p, int lvl, ErrMes err, Lexeme nextLexeme) {

            // last piece of operand after dot is already in temp variable
            // merged operand already appended with that piece as well

            switch (lexeme.getToken()) {
                case WHITESPACE:
                    return this;
                case KURUNGTUTUP:
                    m.add(merged);
                    m.add(lexeme);
                    return SYNTAX_CLOSE_PARENTHESIS;
                case OPERATOR:
                    m.add(merged);
                    m.add(lexeme);
                    return SYNTAX_OPERATOR;
                case EQUAL:
                    if (lvl == 0) {
                        m.add(merged);
                        m.add(lexeme);
                        return SYNTAX_EQUAL;
                    } else {
                        SintakProcessor.simpleErrSet(err, lexeme);
                        err.setMess("cek tanda kurungmu");
                        return SYNTAX_ERROR;
                    }
                case COMMA:
                    return SYNTAX_RP_COMMA;
                case TITIK:
                    return SYNTAX_RP_DOT;
                default:
                    err.setEnd(lexeme.getEnd());
                    err.setMess(this.getErr());
                    return SYNTAX_ERROR;
            }
        }
    },

    SYNTAX_RP_COMMA("setelah koma harus ada angka ") {
        @Override
        SSM tokenSelanjutnya(Queue<Lexeme> m, Lexeme lexeme, Lexeme lastLexeme, TypedLexeme merged,
                             Param p, int lvl, ErrMes err, Lexeme nextLexeme) {

            // merged[SintakProcessor.iEnd] = lexeme[SintakProcessor.iEnd];
            switch (lexeme.getToken()) {
                case WHITESPACE:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess("tidak boleh ada spasi setelah tanda koma");
                    return SYNTAX_ERROR;
                case OPERAND:
                    merged.setEnd(lexeme.getEnd());
                    lastLexeme.setNewValues(lexeme);
                    return SYNTAX_RP_OPERAND_AFTER_COMMA;

                default:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess(this.getErr());
                    return SYNTAX_ERROR;
            }
        }
    },

    SYNTAX_RP_OPERAND_AFTER_COMMA("setelah angka ini haruslah tanda matematika / operator ") {
        @Override
        SSM tokenSelanjutnya(Queue<Lexeme> m, Lexeme lexeme, Lexeme lastLexeme, TypedLexeme merged,
                             Param p, int lvl, ErrMes err, Lexeme nextLexeme) {
            // last piece of operand after dot is already in temp variable
            // merged operand already appended with that piece as well
            // so we just directly add it to queue
            // merged[SintakProcessor.iEnd] = lexeme[SintakProcessor.iEnd];
            switch (lexeme.getToken()) {
                case WHITESPACE:
                    return this;
                case KURUNGTUTUP:
                    m.add(merged);
                    m.add(lexeme);
                    return SYNTAX_CLOSE_PARENTHESIS;
                case OPERATOR:
                    m.add(merged);
                    m.add(lexeme);
                    return SYNTAX_OPERATOR;
                case EQUAL:
                    if (lvl == 0) {
                        m.add(merged);
                        m.add(lexeme);
                        return SYNTAX_EQUAL;
                    } else {
                        SintakProcessor.simpleErrSet(err, lexeme);
                        err.setMess("cek tanda kurungmu");
                        return SYNTAX_ERROR;
                    }
                default:
                    err.setEnd(lexeme.getEnd());
                    err.setMess(this.getErr());
                    return SYNTAX_ERROR;
            }
        }
    },

    SYNTAX_NEGATIVE("setelah tanda negatif harus ada angka") {
        @Override
        SSM tokenSelanjutnya(Queue<Lexeme> m, Lexeme lexeme, Lexeme lastLexeme, TypedLexeme merged,
                             Param p, int lvl, ErrMes err, Lexeme nextLexeme) {

            switch (lexeme.getToken()) {
                case WHITESPACE:
                    return this;
                case OPERAND:

                    if (SintakProcessor.cekZero(p.input.substring(lexeme.getStart(), lexeme.getEnd()))) {
                        merged.setNegative(true);
                        merged.setStart(lexeme.getStart());
                        merged.setEnd(lexeme.getEnd());
                        merged.setKategori(Kategori.unknown);
                        return SYNTAX_UNTYPED_OPERAND;
                    } else {
                        SintakProcessor.simpleErrSet(err, lexeme);
                        err.setMess("cek tulisan angka mu, tidak boleh dimulai dengan 0");
                        return SYNTAX_ERROR;
                    }
                default:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess(this.getErr());
                    return SYNTAX_ERROR;
            }
        }
    },

    SYNTAX_UNTYPED_OPERAND(" harus ada tanda matematika + - x : = ( ) setelah angka  ") {
        @Override
        SSM tokenSelanjutnya(Queue<Lexeme> m, Lexeme lexeme, Lexeme lastLexeme, TypedLexeme merged,
                             Param p, int lvl, ErrMes err, Lexeme nextLexeme) {

            switch (lexeme.getToken()) {
                case WHITESPACE:
                    return this;
                case KURUNGTUTUP:
                    m.add(merged);
                    m.add(lexeme);
                    return SYNTAX_CLOSE_PARENTHESIS;
                case OPERATOR:
                    m.add(merged);
                    m.add(lexeme);
                    return SYNTAX_OPERATOR;
                case EQUAL:

                    if (lvl == 0) {
                        m.add(merged);
                        m.add(lexeme);
                        return SYNTAX_EQUAL;
                    } else {
                        SintakProcessor.simpleErrSet(err, lexeme);
                        err.setMess("cek tanda kurungmu");
                        return SYNTAX_ERROR;
                    }
                case SATUAN:
                    // merged[SintakProcessor.iKategori] =
                    // SintakProcessor.toKategori(lexeme);
                    Kategori k = Converter.toKategori(p.input.substring(lexeme.getStart(),
                            lexeme.getEnd()).toLowerCase());
                    Satuan s = Satuan.fromString(p.input.substring(lexeme.getStart(),
                            lexeme.getEnd()).toLowerCase());
                    boolean isPankgatOnly = Satuan.isPangkatOnly(p.input.substring(lexeme.getStart(),
                            lexeme.getEnd()).toLowerCase());
                    merged.setKategori(k);
                    //memiliki satuan biasa.. yang tentunya memiliki kategori tertentu
                    if (k != Kategori.jmd && k != Kategori.unknown && s != Satuan.unknown) {
                        merged.setSatuan(s);
                        merged.setKategori(k);
                        merged.setTypeLoc(lexeme);
                        return SYNTAX_AFTERTYPE;

                    } else if (k == Kategori.jmd) {
                        merged.setKategori(k);
                        merged.setSatuan(s);
                        merged.pushJMDLexeme(new SubOperand().setSatuan(merged.getSatuan())
                                .setSubEnd(merged.getEnd()).setSubStart(merged.getStart()).setTypeLoc(lexeme));
                        return SYNTAX_JMD_TYPE;

                    } else if (isPankgatOnly) {
                        if (merged.getPangkatLoc() != null) {
                            SintakProcessor.simpleErrSet(err, lexeme);
                            err.setMess("pangkat tidak bolehh ditulis dua kali");
                            return SYNTAX_ERROR;
                        }

                        merged.setPangkatLoc(lexeme);
                        return this;

                    } else {
                        SintakProcessor.simpleErrSet(err, lexeme);
                        err.setMess("cek tulisan satuanmu");
                        return SYNTAX_ERROR;
                    }
                case TITIK:
                    return SYNTAX_UNTYPED_DOT;
                case COMMA:
                    return SYNTAX_UNTYPED_COMMA;

                default:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess(this.getErr());
                    return SYNTAX_ERROR;

            }
        }
    },

    SYNTAX_AFTERTYPE_SECOND(" harus ada tanda matematika + - x : = ( ) setelah satuan ") {
        @Override
        SSM tokenSelanjutnya(Queue<Lexeme> m, Lexeme lexeme, Lexeme lastLexeme, TypedLexeme merged,
                             Param p, int lvl, ErrMes err, Lexeme nextLexeme) {
            // kategori has been set before at syntax operand

            switch (lexeme.getToken()) {
                case WHITESPACE:
                    return this;
                case KURUNGTUTUP:
                    m.add(merged);
                    m.add(lexeme);
                    return SYNTAX_CLOSE_PARENTHESIS;
                case OPERATOR:
                    m.add(merged);
                    m.add(lexeme);
                    return SYNTAX_OPERATOR;
                case EQUAL:

                    if (lvl == 0) {
                        m.add(merged);
                        m.add(lexeme);
                        return SYNTAX_EQUAL;
                    } else {
                        SintakProcessor.simpleErrSet(err, lexeme);
                        err.setMess("cek tanda kurungmu");
                        return SYNTAX_ERROR;
                    }
                default:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess(this.getErr());
                    return SYNTAX_ERROR;
            }
        }
    },
    SYNTAX_AFTERTYPE_PER(" harus ada tanda matematika + - x : = ( ) setelah satuan ") {
        @Override
        SSM tokenSelanjutnya(Queue<Lexeme> m, Lexeme lexeme, Lexeme lastLexeme, TypedLexeme merged,
                             Param p, int lvl, ErrMes err, Lexeme nextLexeme) {
            // kategori has been set before at syntax operand

            switch (lexeme.getToken()) {
                case WHITESPACE:
                    return this;
                case SATUAN:
                    // merged.getTypeLoc().setEnd(lexeme.getEnd());
                    return SYNTAX_AFTERTYPE_SECOND;
                default:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess(this.getErr());
                    return SYNTAX_ERROR;
            }
        }
    },

    SYNTAX_AFTERTYPE(" harus ada tanda matematika + - x : = ( ) setelah satuan ") {
        @Override
        SSM tokenSelanjutnya(Queue<Lexeme> m, Lexeme lexeme, Lexeme lastLexeme, TypedLexeme merged,
                             Param p, int lvl, ErrMes err, Lexeme nextLexeme) {
            // kategori has been set before at syntax operand

            switch (lexeme.getToken()) {
                case WHITESPACE:
                    return this;
                case KURUNGTUTUP:
                    m.add(merged);
                    m.add(lexeme);
                    return SYNTAX_CLOSE_PARENTHESIS;
                case OPERATOR:
                    //lookahead
                    System.out.println("look ahead for next satuan after /");
                    if (nextLexeme.getToken() == Token.SATUAN && p.input.charAt(lexeme.getStart()) == '/') {
                        System.out.println("it is '/' with type after that, checking if current type can be denumerated");

                        merged.setSecondTypeLoc(nextLexeme);
                        merged.setSecondSatuan(Satuan.fromString(p.input.substring(nextLexeme.getStart(),nextLexeme.getEnd())));

                        if (merged.getSecondSatuan()==Satuan.unknown){
                            SintakProcessor.simpleErrSet(err, lastLexeme);
                            err.setMess("satuan setelah per salah");
                            return SYNTAX_ERROR;
                        }

                        return SYNTAX_AFTERTYPE_PER;
                    }
                    m.add(merged);
                    m.add(lexeme);
                    return SYNTAX_OPERATOR;
                case EQUAL:

                    if (lvl == 0) {
                        m.add(merged);
                        m.add(lexeme);
                        return SYNTAX_EQUAL;
                    } else {
                        SintakProcessor.simpleErrSet(err, lexeme);
                        err.setMess("cek tanda kurungmu");
                        return SYNTAX_ERROR;
                    }
                default:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess(this.getErr());
                    return SYNTAX_ERROR;
            }
        }
    },

    SYNTAX_JMD_TYPE(" harus diikuti dengan angka untuk jam, menit, atau detik berikutnya ") {
        @Override
        SSM tokenSelanjutnya(Queue<Lexeme> m, Lexeme lexeme, Lexeme lastLexeme, TypedLexeme merged,
                             Param p, int lvl, ErrMes err, Lexeme nextLexeme) {
            // kategori has been set before at syntax operand
            // this here should be new instance of "merged"
            if (merged.isNegative()) {
                SintakProcessor.simpleErrSet(err, merged);
                err.setMess("tidak boleh negatif");
                return SYNTAX_ERROR;
            }

            switch (lexeme.getToken()) {
                case WHITESPACE:
                    return this;
                case KURUNGTUTUP:
                    m.add(merged);
                    m.add(lexeme);
                    return SYNTAX_CLOSE_PARENTHESIS;
                case OPERATOR:
                    m.add(merged);
                    m.add(lexeme);
                    return SYNTAX_OPERATOR;

                case EQUAL:

                    if (lvl == 0) {
                        m.add(merged);
                        m.add(lexeme);
                        return SYNTAX_EQUAL;
                    } else {
                        SintakProcessor.simpleErrSet(err, lexeme);
                        err.setMess("cek tanda kurungmu");
                        return SYNTAX_ERROR;
                    }

                case OPERAND:
                    if (SintakProcessor.cekZero(p.input.substring(lexeme.getStart(), lexeme.getEnd()))) {

                        merged.setToken(Token.OPERAND);
                        //merged.setStart(lexeme.getStart());
                        merged.setEnd(lexeme.getEnd());
                        lastLexeme.setNewValues(lexeme);
                        // create temporary sub first
                        merged.setTempJ(new SubOperand(lexeme));
                        return SYNTAX_JMD_OPERAND;
                    } else {
                        SintakProcessor.simpleErrSet(err, lexeme);
                        err.setMess("cek tulisan angka mu, tidak boleh dimulai dengan 0");
                        return SYNTAX_ERROR;
                    }

                default:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess(this.getErr());
                    return SYNTAX_ERROR;
            }
        }
    },

    SYNTAX_JMD_OPERAND(" harus ada tanda matematika + - x : = ( ) setelah angka  ") {
        @Override
        SSM tokenSelanjutnya(Queue<Lexeme> m, Lexeme lexeme, Lexeme lastLexeme, TypedLexeme merged,
                             Param p, int lvl, ErrMes err, Lexeme nextLexeme) {

            switch (lexeme.getToken()) {
                case WHITESPACE:
                    return this;
                case KURUNGTUTUP:
                    m.add(merged);
                    m.add(lexeme);
                    return SYNTAX_CLOSE_PARENTHESIS;
                case OPERATOR:
                    m.add(merged);
                    m.add(lexeme);
                    return SYNTAX_OPERATOR;
                case EQUAL:

                    if (lvl == 0) {
                        m.add(merged);
                        m.add(lexeme);
                        return SYNTAX_EQUAL;
                    } else {
                        SintakProcessor.simpleErrSet(err, lexeme);
                        err.setMess("cek tanda kurungmu");
                        return SYNTAX_ERROR;
                    }
                case SATUAN:
                    // merged[SintakProcessor.iKategori] =
                    // SintakProcessor.toKategori(lexeme);
                    Kategori k = Converter.toKategori(p.input.substring(lexeme.getStart(),
                            lexeme.getEnd()).toLowerCase());
                    Satuan s = Satuan.valueOf(p.input.substring(lexeme.getStart(), lexeme.getEnd())
                            .toLowerCase());
                    merged.setKategori(k);
                    if (k == Kategori.jmd) {
                        merged.setKategori(Kategori.jmd);
                        merged.setSatuan(null);
                        // no need to set the end of it since already set before
                        merged.getTempSubOperand().setTypeLoc(lexeme).setSatuan(s);
                        if (!merged.pushTempJMDLexeme()) {
                            SintakProcessor.simpleErrSet(err, lexeme);
                            err.setMess(s.toString() + " sudah di sebutkan sebelum nya");
                            return SYNTAX_ERROR;
                        }

                        return SYNTAX_JMD_TYPE;
                    } else {
                        SintakProcessor.simpleErrSet(err, lexeme);
                        err.setMess("hanya boleh jam, menit dan detik");
                        return SYNTAX_ERROR;
                    }
                case TITIK:
                    return SYNTAX_JMD_DOT;
                case COMMA:
                    return SYNTAX_JMD_COMMA;

                default:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess(this.getErr());
                    return SYNTAX_ERROR;

            }
        }
    },

    SYNTAX_JMD_DOT("setelah titik harus ada angka ") {
        @Override
        SSM tokenSelanjutnya(Queue<Lexeme> m, Lexeme lexeme, Lexeme lastLexeme, TypedLexeme merged,
                             Param p, int lvl, ErrMes err, Lexeme nextLexeme) {
            // TODO Auto-generated method stub
            switch (lexeme.getToken()) {
                case WHITESPACE:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess("tidak boleh ada spasi setelah tanda titik");
                    return SYNTAX_ERROR;
                case OPERAND:
                    if (lastLexeme.getLength() <= 3 && lastLexeme.getLength() > 0) {

                        // check again if current lexeme has exactly 3 digit (after
                        // dot)
                        if (lexeme.getLength() != 3) {
                            SintakProcessor.simpleErrSet(err, lexeme);
                            err.setMess("setelah titik hanya boleh tiga angka");
                            return SYNTAX_ERROR;
                        }
                        merged.setEnd(lexeme.getEnd());
                        merged.getTempSubOperand().setSubEnd(lexeme.getEnd());
                        lastLexeme.setNewValues(lexeme);
                        return SYNTAX_JMD_OPERAND_AFTER_DOT;

                    } else {
                        // simpleErrSet(err, lexeme);
                        SintakProcessor.simpleErrSet(err, lastLexeme);
                        err.setMess("sebelum titik tidak beoleh lebih dari tiga angka");
                        return SYNTAX_ERROR;
                    }

                default:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess(this.getErr());
                    return SYNTAX_ERROR;
            }
        }
    },

    SYNTAX_JMD_OPERAND_AFTER_DOT("setelah titik harus ada angka ") {
        @Override
        SSM tokenSelanjutnya(Queue<Lexeme> m, Lexeme lexeme, Lexeme lastLexeme, TypedLexeme merged,
                             Param p, int lvl, ErrMes err, Lexeme nextLexeme) {
            // vopied from untyped after dot
            switch (lexeme.getToken()) {
                case WHITESPACE:
                    return this;
                case SATUAN:
                    // merged[SintakProcessor.iKategori] =
                    // SintakProcessor.toKategori(lexeme);
                    Kategori k = Converter.toKategori(p.input.substring(lexeme.getStart(),
                            lexeme.getEnd()).toLowerCase());
                    Satuan s = Satuan.valueOf(p.input.substring(lexeme.getStart(), lexeme.getEnd())
                            .toLowerCase());
                    merged.setKategori(k);
                    if (k == Kategori.jmd) {
                        merged.setKategori(Kategori.jmd);
                        merged.setSatuan(s);
                        // no need to set the end of it since already set before
                        merged.getTempSubOperand().setTypeLoc(lexeme).setSatuan(s);
                        if (!merged.pushTempJMDLexeme()) {
                            SintakProcessor.simpleErrSet(err, lexeme);
                            err.setMess(s.toString() + " sudah di sebutkan sebelum nya");
                            return SYNTAX_ERROR;
                        }
                        return SYNTAX_JMD_TYPE;
                    } else {
                        SintakProcessor.simpleErrSet(err, lexeme);
                        err.setMess("cek tulisan satuanmu");
                        return SYNTAX_ERROR;
                    }
                case TITIK:
                    return SYNTAX_JMD_DOT;
                case COMMA:
                    return SYNTAX_JMD_COMMA;

                default:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess(this.getErr());
                    return SYNTAX_ERROR;

            }
        }
    },

    SYNTAX_JMD_COMMA("setelah koma harus ada angka ") {
        @Override
        SSM tokenSelanjutnya(Queue<Lexeme> m, Lexeme lexeme, Lexeme lastLexeme, TypedLexeme merged,
                             Param p, int lvl, ErrMes err, Lexeme nextLexeme) {
            // TODO Auto-generated method stub
            // merged[SintakProcessor.iEnd] = lexeme[SintakProcessor.iEnd];
            switch (lexeme.getToken()) {
                case WHITESPACE:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess("tidak boleh ada spasi setelah tanda koma");
                    return SYNTAX_ERROR;
                case OPERAND:
                    merged.setEnd(lexeme.getEnd());
                    merged.getTempSubOperand().setSubEnd(lexeme.getEnd());
                    lastLexeme.setNewValues(lexeme);
                    return SYNTAX_JMD_OPERAND_AFTER_COMMA;

                default:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess(this.getErr());
                    return SYNTAX_ERROR;
            }

        }
    },

    SYNTAX_JMD_OPERAND_AFTER_COMMA("setelah angka ini haruslah tanda matematika / operator ") {
        @Override
        SSM tokenSelanjutnya(Queue<Lexeme> m, Lexeme lexeme, Lexeme lastLexeme, TypedLexeme merged,
                             Param p, int lvl, ErrMes err, Lexeme nextLexeme) {
            // copied from UNTYPED AFTER COMMA
            switch (lexeme.getToken()) {
                case WHITESPACE:
                    return this;
                case SATUAN:
                    // merged[SintakProcessor.iKategori] =
                    // SintakProcessor.toKategori(lexeme);
                    Kategori k = Converter.toKategori(p.input.substring(lexeme.getStart(),
                            lexeme.getEnd()).toLowerCase());
                    Satuan s = Satuan.valueOf(p.input.substring(lexeme.getStart(), lexeme.getEnd())
                            .toLowerCase());
                    merged.setKategori(k);
                    if (k == Kategori.jmd) {
                        merged.setKategori(Kategori.jmd);
                        merged.setSatuan(s);
                        // no need to set the end of it since already set before
                        merged.getTempSubOperand().setTypeLoc(lexeme).setSatuan(s);
                        if (!merged.pushTempJMDLexeme()) {
                            SintakProcessor.simpleErrSet(err, lexeme);
                            err.setMess(s.toString() + " sudah di sebutkan sebelum nya");
                            return SYNTAX_ERROR;
                        }
                        return SYNTAX_JMD_TYPE;
                    } else {
                        SintakProcessor.simpleErrSet(err, lexeme);
                        err.setMess("cek tulisan satuanmu");
                        return SYNTAX_ERROR;
                    }
                default:
                    err.setEnd(lexeme.getEnd());
                    err.setMess(this.getErr());
                    return SYNTAX_ERROR;
            }
        }
    },

    SYNTAX_FINAL_UNTYPED_DOT("setelah titik harus ada angka ") {
        @Override
        SSM tokenSelanjutnya(Queue<Lexeme> m, Lexeme lexeme, Lexeme lastLexeme, TypedLexeme merged,
                             Param p, int lvl, ErrMes err, Lexeme nextLexeme) {
            // TODO Auto-generated method stub

            switch (lexeme.getToken()) {
                case WHITESPACE:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess("tidak boleh ada spasi setelah tanda titik");
                    return SYNTAX_ERROR;
                case OPERAND:
                    if (lastLexeme.getLength() <= 3 && lastLexeme.getLength() > 0) {
                        if (lastLexeme.getLength() <= 2 && merged.getLength() == 2 && !merged.isNegative()) {
                            //checking jam, jangan lebih dari 24
                            if (Integer.parseInt(p.input.substring(lastLexeme.getStart(), lastLexeme.getEnd())) > 24) {
                                SintakProcessor.simpleErrSet(err, lastLexeme);
                                err.setMess("jam tak boleh lebih dari 24");
                                return SYNTAX_ERROR;
                            }
                            //checking menit
                            if (Integer.parseInt(p.input.substring(lexeme.getStart(), lexeme.getEnd())) > 60) {
                                SintakProcessor.simpleErrSet(err, lexeme);
                                err.setMess("menit tak boleh lebih dari 60");
                                return SYNTAX_ERROR;
                            }
                            //setting kategori for pukul
                            merged.setKategori(Kategori.pukul);
                            merged.setEnd(lexeme.getEnd());
                            lastLexeme.setNewValues(lexeme);
                            return SYNTAX_FINAL_OPERAND_PUKUL;
                        }
                        if (lexeme.getLength() != 3) {
                            SintakProcessor.simpleErrSet(err, lexeme);
                            err.setMess("setelah titik hanya boleh tiga angka");
                            return SYNTAX_ERROR;
                        }
                        merged.setEnd(lexeme.getEnd());
                        lastLexeme.setNewValues(lexeme);
                        return SYNTAX_FINAL_UNTYPED_OPERAND_AFTER_DOT;

                    } else {
                        // simpleErrSet(err, lexeme);
                        SintakProcessor.simpleErrSet(err, lastLexeme);
                        err.setMess("sebelum titik tidak beoleh lebih dari tiga angka");
                        return SYNTAX_ERROR;
                    }

                default:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess(this.getErr());
                    return SYNTAX_ERROR;
            }
        }
    },
    SYNTAX_FINAL_OPERAND_PUKUL("setlah tulisan jam dan menit tidak boleh ada tanda ini ") {
        @Override
        SSM tokenSelanjutnya(Queue<Lexeme> m, Lexeme lexeme, Lexeme lastLexeme, TypedLexeme merged,
                             Param p, int lvl, ErrMes err, Lexeme nextLexeme) {

            switch (lexeme.getToken()) {
                case WHITESPACE:
                    return this;
                case TITIK:
                    return SYNTAX_FINAL_OPERAND_PUKUL_DOT;

                default:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess(this.getErr());
                    return SYNTAX_ERROR;
            }
        }
    },
    SYNTAX_FINAL_OPERAND_PUKUL_DOT("setlah tulisan jam dan menit tidak boleh ada tanda ini ") {
        @Override
        SSM tokenSelanjutnya(Queue<Lexeme> m, Lexeme lexeme, Lexeme lastLexeme, TypedLexeme merged,
                             Param p, int lvl, ErrMes err, Lexeme nextLexeme) {

            switch (lexeme.getToken()) {
                case WHITESPACE:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess("tidak boleh ada spasi setelah tanda titik");
                    return SYNTAX_ERROR;

                case OPERAND:
                    if (lexeme.getLength() != 2) {
                        SintakProcessor.simpleErrSet(err, lexeme);
                        err.setMess("tulisan detikmu terlalu panjang");
                        return SYNTAX_ERROR;
                    }
                    if (Integer.parseInt(p.input.substring(lexeme.getStart(), lexeme.getEnd())) > 60) {
                        SintakProcessor.simpleErrSet(err, lexeme);
                        err.setMess("detik tak boleh lebih dari 60");
                        return SYNTAX_ERROR;
                    }
                    merged.setEnd(lexeme.getEnd());
                    lastLexeme.setNewValues(lexeme);
                    return SYNTAX_FINAL_OPERAND_PUKUL_DOT_DETIK;
                default:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess(this.getErr());
                    return SYNTAX_ERROR;
            }
        }
    },
    SYNTAX_FINAL_OPERAND_PUKUL_DOT_DETIK("setelah tulisan jam menit dan detik, tidak boleh ada tulisan lainnya ") {
        @Override
        SSM tokenSelanjutnya(Queue<Lexeme> m, Lexeme lexeme, Lexeme lastLexeme, TypedLexeme merged,
                             Param p, int lvl, ErrMes err, Lexeme nextLexeme) {

            switch (lexeme.getToken()) {
                case WHITESPACE:
                    return this;
                default:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess(this.getErr());
                    return SYNTAX_ERROR;
            }
        }
    },

    SYNTAX_FINAL_UNTYPED_OPERAND_AFTER_DOT("setelah titik harus ada angka ") {
        @Override
        SSM tokenSelanjutnya(Queue<Lexeme> m, Lexeme lexeme, Lexeme lastLexeme, TypedLexeme merged,
                             Param p, int lvl, ErrMes err, Lexeme nextLexeme) {

            switch (lexeme.getToken()) {
                case WHITESPACE:
                    return this;
                case SATUAN:
                    // merged[SintakProcessor.iKategori] =
                    // SintakProcessor.toKategori(lexeme);
                    Kategori k = Converter.toKategori(p.input.substring(lexeme.getStart(),
                            lexeme.getEnd()).toLowerCase());
                    Satuan s = Satuan.fromString(p.input.substring(lexeme.getStart(),
                            lexeme.getEnd()).toLowerCase());
                    boolean isPankgatOnly = Satuan.isPangkatOnly(p.input.substring(lexeme.getStart(),
                            lexeme.getEnd()).toLowerCase());
                    merged.setKategori(k);
                    //memiliki satuan biasa.. yang tentunya memiliki kategori tertentu
                    if (k != Kategori.jmd && k != Kategori.unknown && s != Satuan.unknown) {
                        merged.setSatuan(s);
                        merged.setKategori(k);
                        merged.setTypeLoc(lexeme);
                        return SYNTAX_FINAL_AFTERTYPE;

                    } else if (k == Kategori.jmd) {
                        merged.setKategori(k);
                        merged.setSatuan(s);
                        merged.pushJMDLexeme(new SubOperand().setSatuan(merged.getSatuan())
                                .setSubEnd(merged.getEnd()).setSubStart(merged.getStart()).setTypeLoc(lexeme));
                        return SYNTAX_FINAL_JMD_TYPE;
                        //tentang pangkat
                    } else if (isPankgatOnly) {
                        if (merged.getPangkatLoc() != null) {
                            SintakProcessor.simpleErrSet(err, lexeme);
                            err.setMess("pangkat tidak bolehh ditulis dua kali");
                            return SYNTAX_ERROR;
                        }
                        merged.setPangkatLoc(lexeme);
                        return this;
                    } else {
                        SintakProcessor.simpleErrSet(err, lexeme);
                        err.setMess("cek tulisan satuanmu");
                        return SYNTAX_ERROR;
                    }
                case TITIK:
                    return SYNTAX_UNTYPED_DOT;
                case COMMA:
                    return SYNTAX_UNTYPED_COMMA;

                default:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess(this.getErr());
                    return SYNTAX_ERROR;
            }
        }
    },

    SYNTAX_FINAL_UNTYPED_COMMA("setelah koma harus ada angka ") {
        @Override
        SSM tokenSelanjutnya(Queue<Lexeme> m, Lexeme lexeme, Lexeme lastLexeme, TypedLexeme merged,
                             Param p, int lvl, ErrMes err, Lexeme nextLexeme) {
            // TODO Auto-generated method stub
            switch (lexeme.getToken()) {
                case WHITESPACE:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess("tidak boleh ada spasi setelah tanda koma");
                    return SYNTAX_ERROR;
                case OPERAND:
                    merged.setEnd(lexeme.getEnd());
                    lastLexeme.setNewValues(lexeme);
                    return SYNTAX_FINAL_UNTYPED_OPERAND_AFTER_COMMA;

                default:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess(this.getErr());
                    return SYNTAX_ERROR;
            }
        }
    },

    SYNTAX_FINAL_UNTYPED_OPERAND_AFTER_COMMA(
            "setelah angka ini haruslah tanda matematika / operator ") {
        @Override
        SSM tokenSelanjutnya(Queue<Lexeme> m, Lexeme lexeme, Lexeme lastLexeme, TypedLexeme merged,
                             Param p, int lvl, ErrMes err, Lexeme nextLexeme) {
            // last piece of operand after dot is already in temp variable
            // merged operand already appended with that piece as well
            // so we just directly add it to queue
            switch (lexeme.getToken()) {
                case WHITESPACE:
                    return this;
                case SATUAN:
                    // merged[SintakProcessor.iKategori] =
                    // SintakProcessor.toKategori(lexeme);
                    Kategori k = Converter.toKategori(p.input.substring(lexeme.getStart(),
                            lexeme.getEnd()).toLowerCase());
                    Satuan s = Satuan.fromString(p.input.substring(lexeme.getStart(),
                            lexeme.getEnd()).toLowerCase());
                    boolean isPankgatOnly = Satuan.isPangkatOnly(p.input.substring(lexeme.getStart(),
                            lexeme.getEnd()).toLowerCase());
                    merged.setKategori(k);
                    //memiliki satuan biasa.. yang tentunya memiliki kategori tertentu
                    if (k != Kategori.jmd && k != Kategori.unknown && s != Satuan.unknown) {
                        merged.setSatuan(s);
                        merged.setKategori(k);
                        merged.setTypeLoc(lexeme);
                        return SYNTAX_FINAL_AFTERTYPE;

                    } else if (k == Kategori.jmd) {
                        merged.setKategori(k);
                        merged.setSatuan(s);
                        merged.pushJMDLexeme(new SubOperand().setSatuan(merged.getSatuan())
                                .setSubEnd(merged.getEnd()).setSubStart(merged.getStart()).setTypeLoc(lexeme));
                        return SYNTAX_FINAL_JMD_TYPE;
                        //tentang pangkat
                    } else if (isPankgatOnly) {
                        if (merged.getPangkatLoc() != null) {
                            SintakProcessor.simpleErrSet(err, lexeme);
                            err.setMess("pangkat tidak bolehh ditulis dua kali");
                            return SYNTAX_ERROR;
                        }

                        merged.setPangkatLoc(lexeme);
                        return this;
                    } else {
                        SintakProcessor.simpleErrSet(err, lexeme);
                        err.setMess("cek tulisan satuanmu");
                        return SYNTAX_ERROR;
                    }

                default:
                    err.setEnd(lexeme.getEnd());
                    err.setMess(this.getErr());
                    return SYNTAX_ERROR;
            }
        }
    },

    SYNTAX_FINAL_RP("setelah rupiah harus angka") {
        @Override
        SSM tokenSelanjutnya(Queue<Lexeme> m, Lexeme lexeme, Lexeme lastLexeme, TypedLexeme merged,
                             Param p, int lvl, ErrMes err, Lexeme nextLexeme) {

            merged.setKategori(Kategori.uang);
            merged.setSatuan(Satuan.rp);
            switch (lexeme.getToken()) {
                case WHITESPACE:
                    return this;
                case OPERAND:
                    // sb.setLength(0);

                    if (SintakProcessor.cekZero(p.input.substring(lexeme.getStart(), lexeme.getEnd()))) {
                        // sb.append(lexeme[SintakProcessor.iIsi]);
                        merged.setToken(Token.OPERAND);
                        merged.setStart(lexeme.getStart());
                        merged.setEnd(lexeme.getEnd());


                        lastLexeme.setNewValues(lexeme);

                        return SYNTAX_FINAL_RP_OPERAND;
                    } else {
                        SintakProcessor.simpleErrSet(err, lexeme);
                        err.setMess("cek tulisan angka mu, tidak boleh dimulai dengan 0");
                        return SYNTAX_ERROR;
                    }
                default:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess(this.getErr());
                    return SYNTAX_ERROR;
            }
        }
    },

    SYNTAX_FINAL_RP_OPERAND(" seteleh angka dalam rupiah hanya boleh tanda matematika ") {
        @Override
        SSM tokenSelanjutnya(Queue<Lexeme> m, Lexeme lexeme, Lexeme lastLexeme, TypedLexeme merged,
                             Param p, int lvl, ErrMes err, Lexeme nextLexeme) {

            switch (lexeme.getToken()) {
                case WHITESPACE:
                    return this;
                case TITIK:
                    return SYNTAX_FINAL_RP_DOT;
                case COMMA:
                    return SYNTAX_FINAL_RP_COMMA;
                default:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess(this.getErr());
                    return SYNTAX_ERROR;
            }
        }
    },

    SYNTAX_FINAL_RP_DOT("setelah titik harus ada angka ") {
        @Override
        SSM tokenSelanjutnya(Queue<Lexeme> m, Lexeme lexeme, Lexeme lastLexeme, TypedLexeme merged,
                             Param p, int lvl, ErrMes err, Lexeme nextLexeme) {

            switch (lexeme.getToken()) {
                case WHITESPACE:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess("tidak boleh ada spasi setelah tanda titik");
                    return SYNTAX_ERROR;
                case OPERAND:
                    // BACKTRACK to check the operand string length by looking
                    // at variable temp
                    if (lastLexeme.getLength() <= 3 && lastLexeme.getLength() > 0) {

                        // check again if current lexeme has exactly 3 digit (after
                        // dot)
                        if (lexeme.getLength() != 3) {
                            SintakProcessor.simpleErrSet(err, lexeme);
                            err.setMess("setelah titik hanya boleh tiga angka");
                            return SYNTAX_ERROR;
                        }
                        merged.setEnd(lexeme.getEnd());
                        lastLexeme.setNewValues(lexeme);
                        return SYNTAX_FINAL_RP_OPERAND_AFTER_DOT;

                    } else {
                        // simpleErrSet(err, lexeme);
                        SintakProcessor.simpleErrSet(err, lastLexeme);
                        err.setMess("sebelum titik tidak beoleh lebih dari tiga angka");
                        return SYNTAX_ERROR;
                    }

                default:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess(this.getErr());
                    return SYNTAX_ERROR;
            }
        }
    },

    SYNTAX_FINAL_RP_OPERAND_AFTER_DOT("setelah titik harus ada angka ") {
        @Override
        SSM tokenSelanjutnya(Queue<Lexeme> m, Lexeme lexeme, Lexeme lastLexeme, TypedLexeme merged,
                             Param p, int lvl, ErrMes err, Lexeme nextLexeme) {

            // last piece of operand after dot is already in temp variable
            // merged operand already appended with that piece as well

            switch (lexeme.getToken()) {
                case WHITESPACE:
                    return this;
                case COMMA:
                    return SYNTAX_FINAL_RP_COMMA;
                case TITIK:
                    return SYNTAX_FINAL_RP_DOT;
                default:
                    err.setEnd(lexeme.getEnd());
                    err.setMess(this.getErr());
                    return SYNTAX_ERROR;
            }
        }
    },

    SYNTAX_FINAL_RP_COMMA("setelah koma harus ada angka ") {
        @Override
        SSM tokenSelanjutnya(Queue<Lexeme> m, Lexeme lexeme, Lexeme lastLexeme, TypedLexeme merged,
                             Param p, int lvl, ErrMes err, Lexeme nextLexeme) {
            // TODO Auto-generated method stub
            // merged[SintakProcessor.iEnd] = lexeme[SintakProcessor.iEnd];
            switch (lexeme.getToken()) {
                case WHITESPACE:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess("tidak boleh ada spasi setelah tanda koma");
                    return SYNTAX_ERROR;
                case OPERAND:
                    merged.setEnd(lexeme.getEnd());
                    lastLexeme.setNewValues(lexeme);
                    return SYNTAX_FINAL_RP_OPERAND_AFTER_COMMA;

                default:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess(this.getErr());
                    return SYNTAX_ERROR;
            }
        }
    },

    SYNTAX_FINAL_RP_OPERAND_AFTER_COMMA("setelah angka ini haruslah tanda matematika / operator ") {
        @Override
        SSM tokenSelanjutnya(Queue<Lexeme> m, Lexeme lexeme, Lexeme lastLexeme, TypedLexeme merged,
                             Param p, int lvl, ErrMes err, Lexeme nextLexeme) {
            // last piece of operand after dot is already in temp variable
            // merged operand already appended with that piece as well
            // so we just directly add it to queue
            // merged[SintakProcessor.iEnd] = lexeme[SintakProcessor.iEnd];
            switch (lexeme.getToken()) {
                case WHITESPACE:
                    return this;
                default:
                    err.setEnd(lexeme.getEnd());
                    err.setMess(this.getErr());
                    return SYNTAX_ERROR;
            }
        }
    },

    SYNTAX_FINAL_NEGATIVE("setelah tanda negatif harus ada angka") {
        @Override
        SSM tokenSelanjutnya(Queue<Lexeme> m, Lexeme lexeme, Lexeme lastLexeme, TypedLexeme merged,
                             Param p, int lvl, ErrMes err, Lexeme nextLexeme) {

            switch (lexeme.getToken()) {
                case WHITESPACE:
                    return this;
                case OPERAND:

                    if (SintakProcessor.cekZero(p.input.substring(lexeme.getStart(), lexeme.getEnd()))) {
                        merged.setNegative(true);
                        merged.setStart(lexeme.getStart());
                        merged.setEnd(lexeme.getEnd());
                        merged.setKategori(Kategori.unknown);


                        return SYNTAX_FINAL_UNTYPED_OPERAND;
                    } else {
                        SintakProcessor.simpleErrSet(err, lexeme);
                        err.setMess("cek tulisan angka mu, tidak boleh dimulai dengan 0");
                        return SYNTAX_ERROR;
                    }
                default:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess(this.getErr());
                    return SYNTAX_ERROR;
            }
        }
    },

    SYNTAX_FINAL_UNTYPED_OPERAND(" harus ada tanda matematika + - x : = ( ) setelah angka  ") {
        @Override
        SSM tokenSelanjutnya(Queue<Lexeme> m, Lexeme lexeme, Lexeme lastLexeme, TypedLexeme merged,
                             Param p, int lvl, ErrMes err, Lexeme nextLexeme) {

            switch (lexeme.getToken()) {
                case WHITESPACE:
                    return this;
                case SATUAN:
                    Kategori k = Converter.toKategori(p.input.substring(lexeme.getStart(),
                            lexeme.getEnd()).toLowerCase());
                    Satuan s = Satuan.fromString(p.input.substring(lexeme.getStart(),
                            lexeme.getEnd()).toLowerCase());
                    boolean isPankgatOnly = Satuan.isPangkatOnly(p.input.substring(lexeme.getStart(),
                            lexeme.getEnd()).toLowerCase());
                    merged.setKategori(k);
                    //memiliki satuan biasa.. yang tentunya memiliki kategori tertentu
                    if (k != Kategori.jmd && k != Kategori.unknown && s != Satuan.unknown) {
                        merged.setSatuan(s);
                        merged.setKategori(k);
                        merged.setTypeLoc(lexeme);
                        return SYNTAX_FINAL_AFTERTYPE;

                    } else if (k == Kategori.jmd) {
                        merged.setKategori(k);
                        merged.setSatuan(s);
                        merged.pushJMDLexeme(new SubOperand().setSatuan(merged.getSatuan())
                                .setSubEnd(merged.getEnd()).setSubStart(merged.getStart()).setTypeLoc(lexeme));
                        return SYNTAX_FINAL_JMD_TYPE;

                    } else {
                        SintakProcessor.simpleErrSet(err, lexeme);
                        err.setMess("cek tulisan satuanmu");
                        return SYNTAX_ERROR;
                    }
                case TITIK:
                    return SYNTAX_FINAL_UNTYPED_DOT;
                case COMMA:
                    return SYNTAX_FINAL_UNTYPED_COMMA;

                default:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess(this.getErr());
                    return SYNTAX_ERROR;

            }
        }
    },
    SYNTAX_FINAL_JMD_TYPE(" harus diikuti dengan angka untuk jam, menit, atau detik berikutnya ") {
        @Override
        SSM tokenSelanjutnya(Queue<Lexeme> m, Lexeme lexeme, Lexeme lastLexeme, TypedLexeme merged,
                             Param p, int lvl, ErrMes err, Lexeme nextLexeme) {
            // kategori has been set before at syntax operand
            // this here should be new instance of "merged"
            if (merged.isNegative()) {
                SintakProcessor.simpleErrSet(err, merged);
                err.setMess("tidak boleh negatif");
                return SYNTAX_ERROR;
            }

            switch (lexeme.getToken()) {
                case WHITESPACE:
                    return this;
                case OPERAND:
                    if (SintakProcessor.cekZero(p.input.substring(lexeme.getStart(), lexeme.getEnd()))) {

                        merged.setToken(Token.OPERAND);
                        merged.setStart(lexeme.getStart());
                        merged.setEnd(lexeme.getEnd());
                        lastLexeme.setNewValues(lexeme);
                        // create temporary sub first
                        merged.setTempJ(new SubOperand(lexeme));
                        return SYNTAX_FINAL_JMD_OPERAND;
                    } else {
                        SintakProcessor.simpleErrSet(err, lexeme);
                        err.setMess("cek tulisan angka mu, tidak boleh dimulai dengan 0");
                        return SYNTAX_ERROR;
                    }

                default:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess(this.getErr());
                    return SYNTAX_ERROR;
            }
        }
    },
    SYNTAX_FINAL_JMD_COMMA("setelah koma harus ada angka ") {
        @Override
        SSM tokenSelanjutnya(Queue<Lexeme> m, Lexeme lexeme, Lexeme lastLexeme, TypedLexeme merged,
                             Param p, int lvl, ErrMes err, Lexeme nextLexeme) {
            // TODO Auto-generated method stub
            // merged[SintakProcessor.iEnd] = lexeme[SintakProcessor.iEnd];
            switch (lexeme.getToken()) {
                case WHITESPACE:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess("tidak boleh ada spasi setelah tanda koma");
                    return SYNTAX_ERROR;
                case OPERAND:
                    merged.setEnd(lexeme.getEnd());
                    merged.getTempSubOperand().setSubEnd(lexeme.getEnd());
                    lastLexeme.setNewValues(lexeme);
                    return SYNTAX_FINAL_JMD_OPERAND_AFTER_COMMA;

                default:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess(this.getErr());
                    return SYNTAX_ERROR;
            }

        }
    },
    SYNTAX_FINAL_JMD_DOT("setelah titik harus ada angka ") {
        @Override
        SSM tokenSelanjutnya(Queue<Lexeme> m, Lexeme lexeme, Lexeme lastLexeme, TypedLexeme merged,
                             Param p, int lvl, ErrMes err, Lexeme nextLexeme) {
            // TODO Auto-generated method stub
            switch (lexeme.getToken()) {
                case WHITESPACE:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess("tidak boleh ada spasi setelah tanda titik");
                    return SYNTAX_ERROR;
                case OPERAND:
                    if (lastLexeme.getLength() <= 3 && lastLexeme.getLength() > 0) {

                        // check again if current lexeme has exactly 3 digit (after
                        // dot)
                        if (lexeme.getLength() != 3) {
                            SintakProcessor.simpleErrSet(err, lexeme);
                            err.setMess("setelah titik hanya boleh tiga angka");
                            return SYNTAX_ERROR;
                        }
                        merged.setEnd(lexeme.getEnd());
                        merged.getTempSubOperand().setSubEnd(lexeme.getEnd());
                        lastLexeme.setNewValues(lexeme);
                        return SYNTAX_FINAL_JMD_OPERAND_AFTER_DOT;

                    } else {
                        // simpleErrSet(err, lexeme);
                        SintakProcessor.simpleErrSet(err, lastLexeme);
                        err.setMess("sebelum titik tidak beoleh lebih dari tiga angka");
                        return SYNTAX_ERROR;
                    }

                default:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess(this.getErr());
                    return SYNTAX_ERROR;
            }
        }
    },
    SYNTAX_FINAL_JMD_OPERAND(" harus dilengkapi dengan jam menit atau detik  ") {
        @Override
        SSM tokenSelanjutnya(Queue<Lexeme> m, Lexeme lexeme, Lexeme lastLexeme, TypedLexeme merged,
                             Param p, int lvl, ErrMes err, Lexeme nextLexeme) {

            switch (lexeme.getToken()) {
                case WHITESPACE:
                    return this;
                case SATUAN:
                    // merged[SintakProcessor.iKategori] =
                    // SintakProcessor.toKategori(lexeme);
                    Kategori k = Converter.toKategori(p.input.substring(lexeme.getStart(),
                            lexeme.getEnd()).toLowerCase());
                    Satuan s = Satuan.valueOf(p.input.substring(lexeme.getStart(), lexeme.getEnd())
                            .toLowerCase());
                    merged.setKategori(k);
                    if (k == Kategori.jmd) {
                        merged.setKategori(Kategori.jmd);
                        merged.setSatuan(s);
                        // no need to set the end of it since already set before
                        merged.getTempSubOperand().setTypeLoc(lexeme).setSatuan(s);
                        if (!merged.pushTempJMDLexeme()) {
                            SintakProcessor.simpleErrSet(err, lexeme);
                            err.setMess(s.toString() + " sudah di sebutkan sebelum nya");
                            return SYNTAX_ERROR;
                        }
                        return SYNTAX_FINAL_JMD_TYPE;
                    } else {
                        SintakProcessor.simpleErrSet(err, lexeme);
                        err.setMess("hanya boleh jam, menit dan detik");
                        return SYNTAX_ERROR;
                    }
                case TITIK:
                    return SYNTAX_FINAL_JMD_DOT;
                case COMMA:
                    return SYNTAX_FINAL_JMD_COMMA;

                default:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess(this.getErr());
                    return SYNTAX_ERROR;

            }
        }
    },

    SYNTAX_FINAL_JMD_OPERAND_AFTER_DOT("harus dilengkapi dengan jam menit atai detik") {
        @Override
        SSM tokenSelanjutnya(Queue<Lexeme> m, Lexeme lexeme, Lexeme lastLexeme, TypedLexeme merged,
                             Param p, int lvl, ErrMes err, Lexeme nextLexeme) {
            // vopied from untyped after dot
            switch (lexeme.getToken()) {
                case WHITESPACE:
                    return this;
                case SATUAN:
                    // merged[SintakProcessor.iKategori] =
                    // SintakProcessor.toKategori(lexeme);
                    Kategori k = Converter.toKategori(p.input.substring(lexeme.getStart(),
                            lexeme.getEnd()).toLowerCase());
                    Satuan s = Satuan.valueOf(p.input.substring(lexeme.getStart(), lexeme.getEnd())
                            .toLowerCase());
                    merged.setKategori(k);
                    if (k == Kategori.jmd) {
                        merged.setKategori(Kategori.jmd);
                        merged.setSatuan(s);
                        // no need to set the end of it since already set before
                        merged.getTempSubOperand().setTypeLoc(lexeme).setSatuan(s);
                        if (!merged.pushTempJMDLexeme()) {
                            SintakProcessor.simpleErrSet(err, lexeme);
                            err.setMess(s.toString() + " sudah di sebutkan sebelum nya");
                            return SYNTAX_ERROR;
                        }
                        return SYNTAX_FINAL_JMD_TYPE;
                    } else {
                        SintakProcessor.simpleErrSet(err, lexeme);
                        err.setMess("cek tulisan satuanmu");
                        return SYNTAX_ERROR;
                    }
                case TITIK:
                    return SYNTAX_FINAL_JMD_DOT;
                case COMMA:
                    return SYNTAX_FINAL_JMD_COMMA;

                default:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess(this.getErr());
                    return SYNTAX_ERROR;

            }
        }
    },

    SYNTAX_FINAL_JMD_OPERAND_AFTER_COMMA("harus dilengkapi dengan jam, menit atau detik") {
        @Override
        SSM tokenSelanjutnya(Queue<Lexeme> m, Lexeme lexeme, Lexeme lastLexeme, TypedLexeme merged,
                             Param p, int lvl, ErrMes err, Lexeme nextLexeme) {
            // copied from UNTYPED AFTER COMMA
            switch (lexeme.getToken()) {
                case WHITESPACE:
                    return this;
                case SATUAN:
                    // merged[SintakProcessor.iKategori] =
                    // SintakProcessor.toKategori(lexeme);
                    Kategori k = Converter.toKategori(p.input.substring(lexeme.getStart(),
                            lexeme.getEnd()).toLowerCase());
                    Satuan s = Satuan.valueOf(p.input.substring(lexeme.getStart(), lexeme.getEnd())
                            .toLowerCase());
                    merged.setKategori(k);
                    if (k == Kategori.jmd) {
                        merged.setKategori(Kategori.jmd);
                        merged.setSatuan(s);
                        // no need to set the end of it since already set before
                        merged.getTempSubOperand().setTypeLoc(lexeme).setSatuan(s);
                        if (!merged.pushTempJMDLexeme()) {
                            SintakProcessor.simpleErrSet(err, lexeme);
                            err.setMess(s.toString() + " sudah di sebutkan sebelum nya");
                            return SYNTAX_ERROR;
                        }
                        return SYNTAX_FINAL_JMD_TYPE;
                    } else {
                        SintakProcessor.simpleErrSet(err, lexeme);
                        err.setMess("cek tulisan satuanmu");
                        return SYNTAX_ERROR;
                    }
                default:
                    err.setEnd(lexeme.getEnd());
                    err.setMess(this.getErr());
                    return SYNTAX_ERROR;
            }
        }
    },

    SYNTAX_UNTYPED_DOT("setelah titik harus ada angka ") {
        @Override
        SSM tokenSelanjutnya(Queue<Lexeme> m, Lexeme lexeme, Lexeme lastLexeme, TypedLexeme merged,
                             Param p, int lvl, ErrMes err, Lexeme nextLexeme) {
            // TODO Auto-generated method stub

            switch (lexeme.getToken()) {
                case WHITESPACE:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess("tidak boleh ada spasi setelah tanda titik");
                    return SYNTAX_ERROR;
                case OPERAND:

                    //check before me, if it has 3 digit
                    if (lastLexeme.getLength() <= 3 && lastLexeme.getLength() > 0) {
                        // check again if current lexeme has exactly 3 digit (after
                        // dot)
                        //kategori pukul, seperti 6.05 12.60
                        if (lastLexeme.getLength() <= 2 && merged.getLength() == 2 && !merged.isNegative()) {
                            //checking jam, jangan lebih dari 24
                            if (Integer.parseInt(p.input.substring(lastLexeme.getStart(), lastLexeme.getEnd())) > 24) {
                                SintakProcessor.simpleErrSet(err, lastLexeme);
                                err.setMess("jam tak boleh lebih dari 24");
                                return SYNTAX_ERROR;
                            }
                            //checking menit
                            if (Integer.parseInt(p.input.substring(lexeme.getStart(), lexeme.getEnd())) > 60) {
                                SintakProcessor.simpleErrSet(err, lexeme);
                                err.setMess("menit tak boleh lebih dari 60");
                                return SYNTAX_ERROR;
                            }
                            //setting kategori for pukul
                            merged.setKategori(Kategori.pukul);
                            merged.setEnd(lexeme.getEnd());
                            lastLexeme.setNewValues(lexeme);
                            return SYNTAX_OPERAND_PUKUL;
                        }
                        if (lexeme.getLength() != 3) {
                            SintakProcessor.simpleErrSet(err, lexeme);
                            err.setMess("setelah titik hanya boleh tiga angka");
                            return SYNTAX_ERROR;
                        }

                        merged.setEnd(lexeme.getEnd());
                        lastLexeme.setNewValues(lexeme);
                        return SYNTAX_UNTYPED_OPERAND_AFTER_DOT;

                    } else {
                        // simpleErrSet(err, lexeme);
                        SintakProcessor.simpleErrSet(err, lastLexeme);
                        err.setMess("sebelum titik tidak boleh lebih dari tiga angka");
                        return SYNTAX_ERROR;
                    }

                default:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess(this.getErr());
                    return SYNTAX_ERROR;
            }
        }
    },

    SYNTAX_OPERAND_PUKUL("setlah tulisan jam dan menit tidak boleh ada tanda ini ") {
        @Override
        SSM tokenSelanjutnya(Queue<Lexeme> m, Lexeme lexeme, Lexeme lastLexeme, TypedLexeme merged,
                             Param p, int lvl, ErrMes err, Lexeme nextLexeme) {

            switch (lexeme.getToken()) {
                case WHITESPACE:
                    return this;
                case KURUNGTUTUP:
                    m.add(merged);
                    m.add(lexeme);
                    return SYNTAX_CLOSE_PARENTHESIS;
                case OPERATOR:
                    m.add(merged);
                    m.add(lexeme);
                    return SYNTAX_OPERATOR;
                case EQUAL:

                    if (lvl == 0) {
                        m.add(merged);
                        m.add(lexeme);
                        return SYNTAX_EQUAL;
                    } else {
                        SintakProcessor.simpleErrSet(err, lexeme);
                        err.setMess("cek tanda kurungmu");
                        return SYNTAX_ERROR;
                    }
                case TITIK:
                    return SYNTAX_OPERAND_PUKUL_DOT;

                default:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess(this.getErr());
                    return SYNTAX_ERROR;
            }
        }
    },

    SYNTAX_OPERAND_PUKUL_DOT("setlah tulisan jam dan menit tidak boleh ada tanda ini ") {
        @Override
        SSM tokenSelanjutnya(Queue<Lexeme> m, Lexeme lexeme, Lexeme lastLexeme, TypedLexeme merged,
                             Param p, int lvl, ErrMes err, Lexeme nextLexeme) {

            switch (lexeme.getToken()) {
                case WHITESPACE:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess("tidak boleh ada spasi setelah tanda titik");
                    return SYNTAX_ERROR;

                case OPERAND:
                    if (lexeme.getLength() != 2) {
                        SintakProcessor.simpleErrSet(err, lexeme);
                        err.setMess("tulisan detikmu terlalu panjang");
                        return SYNTAX_ERROR;
                    }
                    if (Integer.parseInt(p.input.substring(lexeme.getStart(), lexeme.getEnd())) > 60) {
                        SintakProcessor.simpleErrSet(err, lexeme);
                        err.setMess("detik tak boleh lebih dari 60");
                        return SYNTAX_ERROR;
                    }
                    merged.setEnd(lexeme.getEnd());
                    lastLexeme.setNewValues(lexeme);
                    return SYNTAX_OPERAND_PUKUL_DOT_DETIK;
                default:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess(this.getErr());
                    return SYNTAX_ERROR;
            }
        }
    },


    SYNTAX_OPERAND_PUKUL_DOT_DETIK("setelah tulisan jam menit dan detik, tidak boleh ada tanda ini ") {
        @Override
        SSM tokenSelanjutnya(Queue<Lexeme> m, Lexeme lexeme, Lexeme lastLexeme, TypedLexeme merged,
                             Param p, int lvl, ErrMes err, Lexeme nextLexeme) {

            switch (lexeme.getToken()) {
                case WHITESPACE:
                    return this;
                case KURUNGTUTUP:
                    m.add(merged);
                    m.add(lexeme);
                    return SYNTAX_CLOSE_PARENTHESIS;
                case OPERATOR:
                    m.add(merged);
                    m.add(lexeme);
                    return SYNTAX_OPERATOR;
                case EQUAL:

                    if (lvl == 0) {
                        m.add(merged);
                        m.add(lexeme);
                        return SYNTAX_EQUAL;
                    } else {
                        SintakProcessor.simpleErrSet(err, lexeme);
                        err.setMess("cek tanda kurungmu");
                        return SYNTAX_ERROR;
                    }


                default:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess(this.getErr());
                    return SYNTAX_ERROR;
            }
        }
    },

    SYNTAX_UNTYPED_OPERAND_AFTER_DOT("setelah titik harus ada angka ") {
        @Override
        SSM tokenSelanjutnya(Queue<Lexeme> m, Lexeme lexeme, Lexeme lastLexeme, TypedLexeme merged,
                             Param p, int lvl, ErrMes err, Lexeme nextLexeme) {

            switch (lexeme.getToken()) {
                case WHITESPACE:
                    return this;
                case KURUNGTUTUP:
                    m.add(merged);
                    m.add(lexeme);
                    return SYNTAX_CLOSE_PARENTHESIS;
                case OPERATOR:
                    m.add(merged);
                    m.add(lexeme);
                    return SYNTAX_OPERATOR;
                case EQUAL:

                    if (lvl == 0) {
                        m.add(merged);
                        m.add(lexeme);
                        return SYNTAX_EQUAL;
                    } else {
                        SintakProcessor.simpleErrSet(err, lexeme);
                        err.setMess("cek tanda kurungmu");
                        return SYNTAX_ERROR;
                    }
                case SATUAN:
                    // merged[SintakProcessor.iKategori] =
                    // SintakProcessor.toKategori(lexeme);
                    Kategori k = Converter.toKategori(p.input.substring(lexeme.getStart(),
                            lexeme.getEnd()).toLowerCase());
                    Satuan s = Satuan.fromString(p.input.substring(lexeme.getStart(),
                            lexeme.getEnd()).toLowerCase());
                    boolean isPankgatOnly = Satuan.isPangkatOnly(p.input.substring(lexeme.getStart(),
                            lexeme.getEnd()).toLowerCase());
                    merged.setKategori(k);
                    //memiliki satuan biasa.. yang tentunya memiliki kategori tertentu
                    if (k != Kategori.jmd && k != Kategori.unknown && s != Satuan.unknown) {
                        merged.setSatuan(s);
                        merged.setKategori(k);
                        merged.setTypeLoc(lexeme);
                        return SYNTAX_AFTERTYPE;

                    } else if (k == Kategori.jmd) {
                        merged.setKategori(k);
                        merged.setSatuan(s);
                        merged.pushJMDLexeme(new SubOperand().setSatuan(merged.getSatuan())
                                .setSubEnd(merged.getEnd()).setSubStart(merged.getStart()).setTypeLoc(lexeme));
                        return SYNTAX_JMD_TYPE;
                    } else if (isPankgatOnly) {
                        if (merged.getPangkatLoc() != null) {
                            SintakProcessor.simpleErrSet(err, lexeme);
                            err.setMess("pangkat tidak bolehh ditulis dua kali");
                            return SYNTAX_ERROR;
                        }

                        merged.setPangkatLoc(lexeme);
                        return this;
                    } else {
                        SintakProcessor.simpleErrSet(err, lexeme);
                        err.setMess("cek tulisan satuanmu");
                        return SYNTAX_ERROR;
                    }
                case TITIK:
                    return SYNTAX_UNTYPED_DOT;
                case COMMA:
                    return SYNTAX_UNTYPED_COMMA;

                default:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess(this.getErr());
                    return SYNTAX_ERROR;

            }
        }
    },

    SYNTAX_UNTYPED_COMMA("setelah koma harus ada angka ") {
        @Override
        SSM tokenSelanjutnya(Queue<Lexeme> m, Lexeme lexeme, Lexeme lastLexeme, TypedLexeme merged,
                             Param p, int lvl, ErrMes err, Lexeme nextLexeme) {
            // TODO Auto-generated method stub
            switch (lexeme.getToken()) {
                case WHITESPACE:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess("tidak boleh ada spasi setelah tanda koma");
                    return SYNTAX_ERROR;
                case OPERAND:
                    merged.setEnd(lexeme.getEnd());
                    lastLexeme.setNewValues(lexeme);
                    return SYNTAX_UNTYPED_OPERAND_AFTER_COMMA;

                default:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess(this.getErr());
                    return SYNTAX_ERROR;
            }
        }
    },

    SYNTAX_UNTYPED_OPERAND_AFTER_COMMA("setelah angka ini haruslah tanda matematika / operator ") {
        @Override
        SSM tokenSelanjutnya(Queue<Lexeme> m, Lexeme lexeme, Lexeme lastLexeme, TypedLexeme merged,
                             Param p, int lvl, ErrMes err, Lexeme nextLexeme) {
            // last piece of operand after dot is already in temp variable
            // merged operand already appended with that piece as well
            // so we just directly add it to queue
            switch (lexeme.getToken()) {
                case WHITESPACE:
                    return this;
                case KURUNGTUTUP:
                    m.add(merged);
                    m.add(lexeme);
                    return SYNTAX_CLOSE_PARENTHESIS;
                case OPERATOR:
                    m.add(merged);
                    m.add(lexeme);
                    return SYNTAX_OPERATOR;
                case SATUAN:
                    // merged[SintakProcessor.iKategori] =
                    // SintakProcessor.toKategori(lexeme);
                    Kategori k = Converter.toKategori(p.input.substring(lexeme.getStart(),
                            lexeme.getEnd()).toLowerCase());
                    Satuan s = Satuan.fromString(p.input.substring(lexeme.getStart(),
                            lexeme.getEnd()).toLowerCase());
                    boolean isPankgatOnly = Satuan.isPangkatOnly(p.input.substring(lexeme.getStart(),
                            lexeme.getEnd()).toLowerCase());
                    merged.setKategori(k);
                    //memiliki satuan biasa.. yang tentunya memiliki kategori tertentu
                    if (k != Kategori.jmd && k != Kategori.unknown && s != Satuan.unknown) {
                        merged.setSatuan(s);
                        merged.setKategori(k);
                        merged.setTypeLoc(lexeme);
                        return SYNTAX_AFTERTYPE;

                    } else if (k == Kategori.jmd) {
                        merged.setKategori(k);
                        merged.setSatuan(s);
                        merged.pushJMDLexeme(new SubOperand().setSatuan(merged.getSatuan())
                                .setSubEnd(merged.getEnd()).setSubStart(merged.getStart()).setTypeLoc(lexeme));
                        return SYNTAX_JMD_TYPE;
                    } else if (isPankgatOnly) {
                        if (merged.getPangkatLoc() != null) {
                            SintakProcessor.simpleErrSet(err, lexeme);
                            err.setMess("pangkat tidak bolehh ditulis dua kali");
                            return SYNTAX_ERROR;
                        }

                        merged.setPangkatLoc(lexeme);
                        return this;
                    } else {
                        SintakProcessor.simpleErrSet(err, lexeme);
                        err.setMess("cek tulisan satuanmu");
                        return SYNTAX_ERROR;
                    }
                case EQUAL:
                    if (lvl == 0) {
                        m.add(merged);
                        m.add(lexeme);
                        return SYNTAX_EQUAL;
                    } else {
                        SintakProcessor.simpleErrSet(err, lexeme);
                        err.setMess("cek tanda kurungmu");
                        return SYNTAX_ERROR;
                    }
                default:
                    err.setEnd(lexeme.getEnd());
                    err.setMess(this.getErr());
                    return SYNTAX_ERROR;
            }
        }
    },

    SYNTAX_FINAL_AFTERTYPE(" setelah satuan tidak boleh ada tulisan lainnya  ") {
        @Override
        SSM tokenSelanjutnya(Queue<Lexeme> m, Lexeme lexeme, Lexeme lastLexeme, TypedLexeme merged,
                             Param p, int lvl, ErrMes err, Lexeme nextLexeme) {
            // kategori has been set before at syntax operand

            switch (lexeme.getToken()) {
                case OPERATOR:
                    //lookahead
                    System.out.println("look ahead for next satuan after /");
                    if (nextLexeme.getToken() == Token.SATUAN && p.input.charAt(lexeme.getStart()) == '/') {
                        System.out.println("it is '/' with type after that, checking if current type can be denumerated");

                        merged.setSecondTypeLoc(nextLexeme);
                        merged.setSecondSatuan(Satuan.fromString(p.input.substring(nextLexeme.getStart(),nextLexeme.getEnd())));

                        if (merged.getSecondSatuan()==Satuan.unknown){
                            SintakProcessor.simpleErrSet(err, lastLexeme);
                            err.setMess("satuan setelah per salah");
                            return SYNTAX_ERROR;
                        }
                        return SYNTAX_FINAL_AFTERTYPE_PER;
                    }
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess(this.getErr());
                    return SYNTAX_ERROR;
                default:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess(this.getErr());
                    return SYNTAX_ERROR;
            }
        }
    },

    SYNTAX_FINAL_AFTERTYPE_SECOND(" harus ada tanda matematika + - x : = ( ) setelah satuan ") {
        @Override
        SSM tokenSelanjutnya(Queue<Lexeme> m, Lexeme lexeme, Lexeme lastLexeme, TypedLexeme merged,
                             Param p, int lvl, ErrMes err, Lexeme nextLexeme) {
            // kategori has been set before at syntax operand

            switch (lexeme.getToken()) {
                case WHITESPACE:
                    return this;
                default:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess(this.getErr());
                    return SYNTAX_ERROR;
            }
        }
    },

    SYNTAX_FINAL_AFTERTYPE_PER(" harus ada tanda matematika + - x : = ( ) setelah satuan ") {
        @Override
        SSM tokenSelanjutnya(Queue<Lexeme> m, Lexeme lexeme, Lexeme lastLexeme, TypedLexeme merged,
                             Param p, int lvl, ErrMes err, Lexeme nextLexeme) {
            // kategori has been set before at syntax operand

            switch (lexeme.getToken()) {
                case WHITESPACE:
                    return this;
                case SATUAN:
                    // merged.getTypeLoc().setEnd(lexeme.getEnd());
                    return SYNTAX_AFTERTYPE_SECOND;
                default:
                    SintakProcessor.simpleErrSet(err, lexeme);
                    err.setMess(this.getErr());
                    return SYNTAX_ERROR;
            }
        }
    },

    SYNTAX_ERROR("Ada yang salah dengan jawaban mu Karena: ") {
        @Override
        SSM tokenSelanjutnya(Queue<Lexeme> m, Lexeme lexeme, Lexeme lastLexeme, TypedLexeme tLex,
                             Param p, int lvl, ErrMes em, Lexeme nextLexeme) {

            return SYNTAX_ERROR;
        }
    };

    private String errM;

    SSM(String b) {
        this.errM = b;
    }

    String getErr() {
        return errM;
    }

    static boolean isItNegativeSym(Lexeme l, Param p) {
        return p.input.substring(l.getStart(), l.getEnd()).equalsIgnoreCase("-");
    }

    abstract SSM tokenSelanjutnya(Queue<Lexeme> m, Lexeme lexeme, Lexeme lastLexeme,
                                  TypedLexeme tLex, Param p, int lvl, ErrMes em, Lexeme nextLexeme);
}