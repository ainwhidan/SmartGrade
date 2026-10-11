public class StudentModel {

    private int id;
    private int teacherId;
    private String ic;
    private String name;
    private String level;

    private int bm, bi, math, sci, his, pipm;
    private String gBM, gBI, gMath, gSci, gHis, gPIPM;

    private int total;
    private int rank;

    // Constructor lengkap termasuk teacherId
    public StudentModel(String ic, String name, String level,
                        int bm, String gBM,
                        int bi, String gBI,
                        int math, String gMath,
                        int sci, String gSci,
                        int his, String gHis,
                        int pipm, String gPIPM) {

        this.teacherId = 1;
        this.ic = ic;
        this.name = name;
        this.level = level;

        this.bm = bm; this.gBM = gBM;
        this.bi = bi; this.gBI = gBI;
        this.math = math; this.gMath = gMath;
        this.sci = sci; this.gSci = gSci;
        this.his = his; this.gHis = gHis;
        this.pipm = pipm; this.gPIPM = gPIPM;

        this.total = bm + bi + math + sci + his + pipm;
    }
    public StudentModel(int id, String ic, String name, String level, int teacherId,
                        int bm, String gBM,
                        int bi, String gBI,
                        int math, String gMath,
                        int sci, String gSci,
                        int his, String gHis,
                        int pipm, String gPIPM,
                        int total, int rank) {
        this.id = id;
        this.ic = ic;
        this.name = name;
        this.level = level;
        this.teacherId = teacherId;
        this.bm = bm; this.gBM = gBM;
        this.bi = bi; this.gBI = gBI;
        this.math = math; this.gMath = gMath;
        this.sci = sci; this.gSci = gSci;
        this.his = his; this.gHis = gHis;
        this.pipm = pipm; this.gPIPM = gPIPM;
        this.total = total;
        this.rank = rank;
    }
    // Getter & Setter
    public int getTeacherId() { return teacherId; }
    public void setTeacherId(int teacherId) { this.teacherId = teacherId; }

    public String getIc() { return ic; }
    public void setIc(String ic) { this.ic = ic; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public int getBm() { return bm; }
    public void setBm(int bm) { this.bm = bm; }

    public String getGBM() { return gBM; }
    public void setGBM(String gBM) { this.gBM = gBM; }

    public int getBi() { return bi; }
    public void setBi(int bi) { this.bi = bi; }

    public String getGBI() { return gBI; }
    public void setGBI(String gBI) { this.gBI = gBI; }

    public int getMath() { return math; }
    public void setMath(int math) { this.math = math; }

    public String getGMath() { return gMath; }
    public void setGMath(String gMath) { this.gMath = gMath; }

    public int getSci() { return sci; }
    public void setSci(int sci) { this.sci = sci; }

    public String getGSci() { return gSci; }
    public void setGSci(String gSci) { this.gSci = gSci; }

    public int getHis() { return his; }
    public void setHis(int his) { this.his = his; }

    public String getGHis() { return gHis; }
    public void setGHis(String gHis) { this.gHis = gHis; }

    public int getPipm() { return pipm; }
    public void setPipm(int pipm) { this.pipm = pipm; }

    public String getGPipm() { return gPIPM; }
    public void setGPipm(String gPIPM) { this.gPIPM = gPIPM; }

    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }

    // Methods for database compatibility
    public int getSubject1() { return bm; }
    public String getGrade1() { return gBM; }

    public int getSubject2() { return bi; }
    public String getGrade2() { return gBI; }

    public int getSubject3() { return math; }
    public String getGrade3() { return gMath; }

    public int getSubject4() { return sci; }
    public String getGrade4() { return gSci; }

    public int getSubject5() { return his; }
    public String getGrade5() { return gHis; }

    public int getSubject6() { return pipm; }
    public String getGrade6() { return gPIPM; }
}
