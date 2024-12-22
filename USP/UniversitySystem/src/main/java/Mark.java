public class Mark {
    private double mark;
    private double attestation1;
    private double attestation2;
    private double finalExam;
    private double total;

    public Mark(double mark, double attestation1, double attestation2, double finalExam, double total) {
        this.mark = mark;
        this.attestation1 = attestation1;
        this.attestation2 = attestation2;
        this.finalExam = finalExam;
        this.total = total;
    }

    public double getMark() {
        return mark;
    }

    public double getAttestation1() {
        return attestation1;
    }

    public double getAttestation2() {
        return attestation2;
    }

    public double getFinalExam() {
        return finalExam;
    }

    public double getTotal() {
        return total;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    public void setAttestation1(int attestation1) {
        this.attestation1 = attestation1;
    }

    public void setAttestation2(int attestation2) {
        this.attestation2 = attestation2;
    }

    public void setFinalExam(int finalExam) {
        this.finalExam = finalExam;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public double calculateTotal() {
        return attestation1 + attestation2 + finalExam;
    }

    public boolean hasPassed() {
        return total >= 50;
    }
}
