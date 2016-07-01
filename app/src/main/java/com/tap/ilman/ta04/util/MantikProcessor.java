package com.tap.ilman.ta04.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;

public class MantikProcessor {

    static PExpr[] changeParent(ArrayList<ReferenceItem> DepthTracker,
                                PExpr midExpr, PExpr innerExpr, Object[] feed) {
        // every reference is equal at first

        System.out.println("pointer juggling...to change parent: ..."
                + Arrays.toString(feed));

        // if this is true, there is no need to alter the parent pointer, return
        // immediately
        if (feed[1] instanceof Integer || feed[1] instanceof TypedLexeme)
            return new PExpr[]{midExpr, innerExpr};

        TokenM bukaAtauTutup = (TokenM) feed[1];

        if (bukaAtauTutup == TokenM.BUKA_KURUNG) {
            System.out.println("--change parent--");
            TokenM operatorAwal = (TokenM) feed[0];

            // add and save appropriate reference
            PExpr p = new PExpr(false);

            midExpr.addParenthesizedExpr(p, operatorAwal);

            DepthTracker.add(new ReferenceItem().setParentExp(p).setBefore(
                    operatorAwal));

            // save reference before last item

            midExpr = DepthTracker.get(DepthTracker.size() - 2).getExp();
            innerExpr = p;

        }

        if (bukaAtauTutup == TokenM.TUTUP_KURUNG) {
            System.out.println("--change parent-')'-");
            TokenM m2 = (TokenM) feed[2];

            // prepare the feed, the operator sign is saved in DepthTracker
            Object[] newFeed = new Object[]{
                    DepthTracker.get(DepthTracker.size() - 1).getBefore(),
                    innerExpr, m2};

            // after closed parenthesis add this parenthesized Expr based
            // on appropriate reference to insert it at right pos and depth of
            // tree hierarchy

            // getting current parent (mid) and add the inner as child within
            // mid PExpr

            System.out.println("%%-SPECIAL--BUILD--Tree--')'--%%");
            if (midExpr != DepthTracker.get(0).getExp())
                MantikProcessor.addAppropriateTerm(
                        DepthTracker.get(0).getExp(), midExpr, newFeed);

                // special case: theres no factor term because of special exclusion
                // above
            else if ((m2 == TokenM.MULTIPLE || m2 == TokenM.DIVIDE)) {
                System.out
                        .println("special tree convergence..adding and deleting...");
                MantikProcessor.addAppropriateTerm(
                        DepthTracker.get(0).getExp(), midExpr, newFeed);
                if (midExpr.getExpr().get(0) instanceof PExpr) {
                    midExpr.getExpr().remove(0);
                }
            }

            // set current parent as child again, and the new parent is the
            // element before it in reference tracker
            innerExpr = midExpr;
            DepthTracker.remove(DepthTracker.size() - 1);
            midExpr = DepthTracker.get(DepthTracker.size() - 1).getExp();
        }
        System.out.println("--------------------------------------");
        System.out.println(Arrays.toString(DepthTracker.toArray()));
        System.out.println("--------------------------------------");
        return new PExpr[]{midExpr, innerExpr};
    }

    static SimpleTerm createSimpleTerm(PExpr Outer, Object[] Lexeme, TokenM t) {
        SimpleTerm x = new SimpleTerm();
        x.setToken(t);
        x.setValueLoc((TypedLexeme) Lexeme[1], Outer);
        x.setOperatorLoc((Lexeme) Lexeme[0]);

        return x;
    }

    static SimpleTerm createESimpleTerm(PExpr Outer, Object[] Lexeme, TokenM t) {
        SimpleTerm x = new SimpleTerm();
        x.setToken(t);
        x.setValueLoc((TypedLexeme) Lexeme[2], Outer);
        x.setOperatorLoc((Lexeme) Lexeme[1]);
        return x;
    }

    static PExpr createPExprTerm(PExpr x, TokenM t) {
        x.setToken(t);
        return x;
    }

    static void addAppropriateTerm(PExpr outer, PExpr innerExpr,
                                   Object[] mlexeme) {
        System.out.println("--add term--");
        System.out.println(Arrays.toString(mlexeme));
        System.out.println("current parrent is..." + innerExpr);
        System.out.println("while outer parrent is..." + outer);

        Object[] lexeme = new Object[mlexeme.length];
        lexeme[0] = (mlexeme[0] instanceof Lexeme) ? outer
                .getTokenMFromLexeme((Lexeme) mlexeme[0]) : mlexeme[0];
        lexeme[1] = mlexeme[1];
        lexeme[2] = (mlexeme[2] instanceof Lexeme) ? outer
                .getTokenMFromLexeme((Lexeme) mlexeme[2]) : mlexeme[2];

        // jika token di awal adalah ( ataupun diakhir adalah ) maka ganti
        // dengan +, agar child element ditambahkan seperti biasa
        lexeme[0] = (lexeme[0] == TokenM.BUKA_KURUNG || lexeme[0] == TokenM.TUTUP_KURUNG) ? lexeme[0] = TokenM.ADD
                : lexeme[0];
        lexeme[2] = (lexeme[2] == TokenM.BUKA_KURUNG || lexeme[2] == TokenM.TUTUP_KURUNG) ? lexeme[2] = TokenM.ADD
                : lexeme[2];

        if (lexeme[0] == TokenM.ADD
                && (lexeme[2] == TokenM.ADD || lexeme[2] == TokenM.SUBTRACT || lexeme[2] == TokenM.EQUAL)) {
            innerExpr.getExpr().add(
                    (lexeme[1] instanceof PExpr) ? createPExprTerm(
                            (PExpr) lexeme[1], TokenM.ADDER)
                            : createSimpleTerm(outer, mlexeme, TokenM.ADDER));
        } else if (lexeme[0] == TokenM.ADD && lexeme[2] == TokenM.DIVIDE) {
            FactorTerm x = new FactorTerm();
            x.setToken(TokenM.FACTOR);

            // x.setToken(TokenM.POSITIVE);


            DivTerm y = new DivTerm();
            y.setNumerator((lexeme[1] instanceof PExpr) ? createPExprTerm(
                    (PExpr) lexeme[1], TokenM.NUMERATOR) : createSimpleTerm(
                    outer, mlexeme, TokenM.NUMERATOR));
            x.addTerms(y);
            innerExpr.getExpr().add(x);
        } else if (lexeme[0] == TokenM.ADD && lexeme[2] == TokenM.MULTIPLE) {
            FactorTerm x = new FactorTerm();

            // create a factor term and add that factor to innermost expression

            x.setToken(TokenM.ADDER);
            x.addTerm((lexeme[1] instanceof PExpr) ? createPExprTerm(
                    (PExpr) lexeme[1], TokenM.FACTOR) : createSimpleTerm(outer,
                    mlexeme, TokenM.FACTOR));
            innerExpr.getExpr().add(x);
        }
        // subtraction PART
        else if (lexeme[0] == TokenM.SUBTRACT
                && (lexeme[2] == TokenM.ADD || lexeme[2] == TokenM.SUBTRACT || lexeme[2] == TokenM.EQUAL)) {
            innerExpr.getExpr().add(
                    (lexeme[1] instanceof PExpr) ? createPExprTerm(
                            (PExpr) lexeme[1], TokenM.SUBTRACTOR)
                            : createSimpleTerm(outer, mlexeme,
                            TokenM.SUBTRACTOR));
        } else if (lexeme[0] == TokenM.SUBTRACT && lexeme[2] == TokenM.DIVIDE) {
            FactorTerm x = new FactorTerm();
            x.setToken(TokenM.SUBTRACTOR);
            DivTerm y = new DivTerm();
            y.setNumerator((lexeme[1] instanceof PExpr) ? createPExprTerm(
                    (PExpr) lexeme[1], TokenM.NUMERATOR) : createSimpleTerm(
                    outer, mlexeme, TokenM.NUMERATOR));
            x.addTerms(y);
            innerExpr.getExpr().add(x);
        } else if (lexeme[0] == TokenM.SUBTRACT && lexeme[2] == TokenM.MULTIPLE) {
            FactorTerm x = new FactorTerm();
            x.setToken(TokenM.SUBTRACTOR);
            x.addTerm((lexeme[1] instanceof PExpr) ? createPExprTerm(
                    (PExpr) lexeme[1], TokenM.FACTOR) : createSimpleTerm(outer,
                    mlexeme, TokenM.FACTOR));
            innerExpr.getExpr().add(x);
        }

        // after division or multiplication
        else if (lexeme[0] == TokenM.MULTIPLE) {

            ((FactorTerm) innerExpr.getExpr().get(
                    innerExpr.getExpr().size() - 1)).setAfterMultiple(
                    (lexeme[1] instanceof PExpr) ? createPExprTerm(
                            (PExpr) lexeme[1], TokenM.FACTOR)
                            : createSimpleTerm(outer, mlexeme, TokenM.FACTOR),
                    (TokenM) lexeme[2]);
        } else if (lexeme[0] == TokenM.DIVIDE) {
            ((FactorTerm) innerExpr.getExpr().get(
                    innerExpr.getExpr().size() - 1))
                    .setAfterDivision((lexeme[1] instanceof PExpr) ? createPExprTerm(
                            (PExpr) lexeme[1], TokenM.DENUMERATOR)
                            : createSimpleTerm(outer, mlexeme,
                            TokenM.DENUMERATOR));
        }
        // hasil, it cast lexeme directly to tokenM equal
        else if (lexeme[1] == ((mlexeme[1] instanceof Lexeme) ? outer
                .getTokenMFromLexeme((Lexeme) mlexeme[1]) : mlexeme[1])) {
            outer.result = createESimpleTerm(outer, mlexeme, TokenM.ADDER);
        }
    }

    static PExpr createExpr(SemanticParam sp) {
        Queue<Lexeme> lexTable = sp.getQ();
        PExpr root = new PExpr(sp.getInput());
        PExpr mid = root;
        PExpr inner = root;

        ArrayList<ReferenceItem> DepthTracker = new ArrayList<ReferenceItem>();
        // first feed all positive
        Object[] feed = {null,
                TokenM.ADD,
                lexTable.remove()};
        Lexeme lexeme;

        // first feed
        // int[] pos = { 0, 0, 0 };

        // to save reference to current parrent in parse-tree
        PExpr[] reference = {mid, inner};

        // insert outer expression as root in reference tracker
        DepthTracker.add(new ReferenceItem().setParentExp(root).setBefore(
                TokenM.ADD));

        while (!lexTable.isEmpty()) {
            lexeme = lexTable.remove();

            feed[0] = feed[1];
            feed[1] = feed[2];
            feed[2] = lexeme;
            // System.out.println( "actual feed is>>>>> "
            // +Arrays.toString(lexeme));
            // if ((lexeme.getToken() == Token.OPERATOR || lexeme.getToken() ==
            // Token.EQUAL)
            // && pos[0] != 0)
            // pos[0] = lexeme.getStart();
            // if (lexeme.getToken() == Token.OPERAND) {
            // pos[1] = lexeme.getStart();
            // pos[2] = lexeme.getEnd();
            // }
            // feed = rotator(feed, lexeme, input);

            // this will change parent reference as necessary and only triggered
            // only
            // if feed contains open or closed parenthesis
            reference = MantikProcessor.changeParent(DepthTracker, mid, inner,
                    feed);
            mid = reference[0];
            inner = reference[1];
            System.out.println("now feed is " + feed[0] + feed[1] + feed[2]);

            // anggota hanya akan di tambahkan jika
            // (operator--operand--operator) atau
            // (anything--samaDengan--operand)
            // ( operator--buka/tutup kurung --operator/samaDengan)
            if (checkFeed(feed)) {
                // System.out.println("feed is : " + Arrays.toString(feed));
                // System.out.println("outer and inner Exp is : " + root + "\t"
                // + inner);
                MantikProcessor.addAppropriateTerm(root, inner, feed);
                // pos[0] = 0;
                // pos[1] = 0;
                // pos[2] = 0;
            }
        }
        root.printTree(1);
        return root;
    }

    public static TokenM translateToTokenM(char c) {

        switch (c) {
            case '+':
                return TokenM.ADD;
            case '-':
                return TokenM.SUBTRACT;
            case 'x':
                return TokenM.MULTIPLE;
            case ':':
                return TokenM.DIVIDE;
            case '=':
                return TokenM.EQUAL;
            case ')':
                return TokenM.TUTUP_KURUNG;
            case '(':
                return TokenM.BUKA_KURUNG;
            default:
                return TokenM.ANGKA;
        }
    }

    private static boolean checkFeed(Object[] t) {
        System.out.println("MantikProcessor.checkFeed: ");

        if (t[0] instanceof TokenM && t[2] instanceof TokenM
                && t[1] instanceof Integer) {
            // System.out.println("it's checked--chechkOpr--Operand-Syntax");
            return true;
        } else if (t[1] == TokenM.EQUAL && t[2] instanceof Integer) {
            // System.out.println("it's checked--chechkOpr--Result Syntax");
            return true;
        } else
            return false;
    }

    public static String printBranch(int i) {
        // System.out.println("i is"+i);
        int k = 0;
        StringBuilder br = new StringBuilder();

        while (k < i) {
            // System.out.println(k+"<<k i>>"+i);
            br.append(" ");
            k++;
        }
        // br.append("|_");
        return br.toString();
    }

    public static String typeCode(Term t) {
        if (t instanceof SimpleTerm)
            return "";
        else if (t instanceof PExpr)
            return "P";
        else if (t instanceof FactorTerm)
            return "F";
        else if (t instanceof DivTerm)
            return "D";
        else
            return "U";
    }

    // private static Object[] rotator(Object[] feed, Lexeme lexeme, String
    // input) {
    // // first insertion always begin with tokenm.add
    //
    // feed[0] = feed[1];
    // feed[1] = feed[2];
    //
    // if (lexeme.getToken() == Token.OPERATOR
    // || lexeme.getToken() == Token.EQUAL
    // || lexeme.getToken() == Token.KURUNGBUKA
    // || lexeme.getToken() == Token.KURUNGTUTUP) {
    //
    // feed[2] = new Object[] { trans(input.charAt(lexeme.getStart())),
    // lexeme.getStart() };
    // }
    // // berarti adalah angka dalam bentuk string
    // else
    // feed[2] = lexeme;
    // // System.out.println("after rotation : ..."+Arrays.toString(m));
    // return feed;
    // }

}

class ReferenceItem {
    private PExpr exp;
    private asSemanticToken before;
    private asSemanticToken after;

    public PExpr getExp() {
        return exp;
    }

    public ReferenceItem setParentExp(PExpr exp) {
        this.exp = exp;
        return this;
    }

    public asSemanticToken getBefore() {
        return before;
    }

    public ReferenceItem setBefore(asSemanticToken before) {
        this.before = before;
        return this;
    }

    public asSemanticToken getAfter() {
        return after;
    }

    public ReferenceItem setAfter(asSemanticToken after) {
        this.after = after;
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("before: ").append(before).append(" ").append(exp)
                .append(" ").append("after: ").append(after);

        return sb.toString();
    }
}
