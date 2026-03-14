import java.util.ArrayList;
import java.util.HashMap;

class Criterion {
    public String metricName;
    public double measuredValue;
    public double minVal;
    public double maxVal;
    public boolean higherIsBetter;

    public Criterion(String metricName, double measuredValue, double minVal, double maxVal, boolean higherIsBetter) {
        this.metricName = metricName;
        this.measuredValue = measuredValue;
        this.minVal = minVal;
        this.maxVal = maxVal;
        this.higherIsBetter = higherIsBetter;
    }

    public double calculateScore() {
        double result = 0;
        if (maxVal == minVal) return 1.0;

        if (higherIsBetter) {
            result = 1 + 4 * ((measuredValue - minVal) / (maxVal - minVal));
        } else {
            result = 1 + 4 * ((maxVal - measuredValue) / (maxVal - minVal));
        }

        if (result > 5) result = 5;
        if (result < 1) result = 1;

        return result;
    }
}

class QualityDimension {
    public String isoCode;
    public String name;
    public int weight;
    public ArrayList<Criterion> criteriaList = new ArrayList<>();

    public QualityDimension(String isoCode, String name, int weight) {
        this.isoCode = isoCode;
        this.name = name;
        this.weight = weight;
    }

    public double getScore() {
        if (criteriaList.isEmpty()) return 0;
        double toplam = 0;
        for (Criterion c : criteriaList) {
            toplam += c.calculateScore();
        }
        return toplam / criteriaList.size();
    }
}

class SWSystem {
    public String systemName;
    public ArrayList<QualityDimension> dimensions = new ArrayList<>();

    public SWSystem(String systemName) {
        this.systemName = systemName;
    }

    public void printReport() {
        System.out.println("--- Sistem Raporu: " + systemName + " ---");
        double toplamPuan = 0;
        int toplamAgirlik = 0;

        if (dimensions.isEmpty()) return;

        for (QualityDimension qd : dimensions) {
            double dScore = qd.getScore();
            System.out.printf("[%s] %s Puani: %.2f%n", qd.isoCode, qd.name, dScore);
            toplamPuan += dScore * qd.weight;
            toplamAgirlik += qd.weight;
        }

        double genelSkor = (toplamAgirlik == 0) ? 0 : toplamPuan / toplamAgirlik;
        System.out.printf("GENEL SKOR: %.2f%n", genelSkor);

        QualityDimension enKotu = dimensions.get(0);
        for (QualityDimension qd : dimensions) {
            if (qd.getScore() < enKotu.getScore()) {
                enKotu = qd;
            }
        }
        System.out.println("En Zayif Yon: " + enKotu.name);
        System.out.println("------------------------------------------");
    }
}

public class SWSystemData {
    public static void main(String[] args) {
        HashMap<String, SWSystem> sistemler = new HashMap<>();

        SWSystem s1 = new SWSystem("E-Ticaret");
        QualityDimension d1 = new QualityDimension("QC.RE", "Reliability", 50);
        d1.criteriaList.add(new Criterion("Defect Density", 2.1, 0, 10, false));
        s1.dimensions.add(d1);

        QualityDimension d2 = new QualityDimension("QC.MA", "Maintainability", 50);
        d2.criteriaList.add(new Criterion("Test Coverage", 72, 0, 100, true));
        s1.dimensions.add(d2);

        sistemler.put("Sistem1", s1);

        SWSystem s2 = new SWSystem("Mobil Banka");
        QualityDimension d3 = new QualityDimension("QC.SE", "Security", 100);
        d3.criteriaList.add(new Criterion("Vulnerability Rate", 1.2, 0, 5, false));
        s2.dimensions.add(d3);

        sistemler.put("Sistem2", s2);

        for (SWSystem s : sistemler.values()) {
            s.printReport();
        }
    }
}