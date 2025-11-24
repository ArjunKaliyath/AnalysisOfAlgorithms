import java.util.*;
import java.io.FileWriter;
import java.io.IOException;


public class ExperimentRunner {

    public static void appendToCSV(String filename, String row) {
    try (FileWriter fw = new FileWriter(filename, true)) {
        fw.write(row + "\n");
    } catch (IOException e) {
        e.printStackTrace();
    }
}

public static void initScenario1CSV() {
    try (FileWriter fw = new FileWriter("scenario1.csv")) {
        fw.write("S,T,Time,Memory,Length\n");
    } catch (IOException e) {
        e.printStackTrace();
    }
}

public static void initScenario2CSV() {
    try (FileWriter fw = new FileWriter("scenario2.csv")) {
        fw.write("S,T,Time,Memory,Length,Delta\n");
    } catch (IOException e) {
        e.printStackTrace();
    }
}


    public static String randomString(int length, Random rng) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append((char) ('A' + rng.nextInt(26)));
        }
        return sb.toString();
    }

    public static double[] scenario1Weights() {
        double[] w = new double[26];
        Arrays.fill(w, 1.0);
        return w;
    }

    public static double[] scenario2Weights() {
        return new double[]{
                8.17, 1.49, 2.78, 4.25, 12.70, 2.23, 2.02,
                6.09, 6.97, 0.15, 0.77, 4.03, 2.41, 6.75,
                7.51, 1.93, 0.10, 5.99, 6.33, 9.06,
                2.76, 0.98, 2.36, 0.15, 1.97, 0.07
        };
    }

    public static long getUsedMemoryMB() {
        Runtime rt = Runtime.getRuntime();
        return (rt.totalMemory() - rt.freeMemory()) / (1024 * 1024);
    }

    /* =========================================================== */
    /*                     SCENARIO 1 EXPERIMENTS                  */
    /* =========================================================== */
    public static void runScenario1() {
        initScenario1CSV();

        System.out.println("\n==================== Scenario 1 ====================");
        System.out.printf("%-10s %-10s %-12s %-12s %-10s %-10s\n",
                "S", "T", "Time(ms)", "Memory(MB)", "Length", "Extra");

        double[] w = scenario1Weights();
        double delta = 10.0;
        
        WeightedApproxCommonSubstring.bestWeightedSubstring("ABC", "XYZ", w, delta);

        int[][] sizes = {
                {10, 10},
                {10, 100},
                {10, 1000},
                {100, 1000},
                {1000, 1000}
        };

        Random rng = new Random(42);

        for (int[] sz : sizes) {
            int m = sz[0], n = sz[1];
            String s1 = randomString(m, rng);
            String s2 = randomString(n, rng);

            long t1 = System.nanoTime();
            WeightedApproxCommonSubstring.Result res =
                    WeightedApproxCommonSubstring.bestWeightedSubstring(s1, s2, w, delta);
            long t2 = System.nanoTime();

            double ms = (t2 - t1) / 1_000_000.0;
            long mem = getUsedMemoryMB();

            // write to csv
            String csvRow = String.format("%d,%d,%.4f,%d,%d",
                    m, n, ms, mem, res.length);
            appendToCSV("scenario1.csv", csvRow);

            // print to console
            System.out.printf("%-10d %-10d %-12.3f %-12d %-10d %-10s\n",
                    m, n, ms, mem, res.length, "-");
        }
    }

    /* =========================================================== */
    /*                     SCENARIO 2 EXPERIMENTS                  */
    /* =========================================================== */
    public static void runScenario2() {
        initScenario2CSV();
        System.out.println("\n==================== Scenario 2 ====================");
        System.out.printf("%-10s %-10s %-12s %-12s %-10s %-10s\n",
                "S", "T", "Time(ms)", "Memory(MB)", "Length", "Delta");

        double[] w = scenario2Weights();
        double minW = Arrays.stream(w).min().getAsDouble();
        double maxW = Arrays.stream(w).max().getAsDouble();

        WeightedApproxCommonSubstring.bestWeightedSubstring("ABC", "XYZ", w, 10.0);


        int[][] sizes = {
                {10, 10},
                {10, 100},
                {10, 1000},
                {100, 1000},
                {1000, 1000}
        };

        Random rng = new Random(99);
        int NUM_DELTAS = 10;

        for (int[] sz : sizes) {
            int m = sz[0], n = sz[1];
            String s1 = randomString(m, rng);
            String s2 = randomString(n, rng);

            for (int k = 0; k < NUM_DELTAS; k++) {
                double delta = minW + (maxW - minW) * k / (double) (NUM_DELTAS - 1);

                long t1 = System.nanoTime();
                WeightedApproxCommonSubstring.Result res =
                        WeightedApproxCommonSubstring.bestWeightedSubstring(s1, s2, w, delta);
                long t2 = System.nanoTime();

                double ms = (t2 - t1) / 1_000_000.0;
                long mem = getUsedMemoryMB();

                // write to csv
                String csvRow = String.format("%d,%d,%.4f,%d,%d,%.3f",
                m, n, ms, mem, res.length, delta);

                appendToCSV("scenario2.csv", csvRow);

                System.out.printf("%-10d %-10d %-12.3f %-12d %-10d %-10.3f\n",
                        m, n, ms, mem, res.length, delta);
            }
        }
    }

    public static void main(String[] args) {
        runScenario1();
        runScenario2();
    }
}
