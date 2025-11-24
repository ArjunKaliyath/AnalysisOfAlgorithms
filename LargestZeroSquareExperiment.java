import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class LargestZeroSquareExperiment {

    // Result holder
    public static class Result {
        public int size;
        public int topRow;
        public int leftCol;

        public Result(int size, int topRow, int leftCol) {
            this.size = size;
            this.topRow = topRow;
            this.leftCol = leftCol;
        }
    }

    // DP algorithm
    public static Result largestZeroSquare(byte[][] B) {
        int m = B.length;
        int n = B[0].length;
        short[][] dp = new short[m][n];

        int bestSize = 0;
        int bestRow = -1;
        int bestCol = -1;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {

                if (B[i][j] == 1) {
                    dp[i][j] = 0;
                } else {
                    if (i == 0 || j == 0) {
                        dp[i][j] = 1;
                    } else {
                        short up = dp[i-1][j];
                        short left = dp[i][j-1];
                        short diag = dp[i-1][j-1];
                        dp[i][j] = (short) (1 + Math.min(up, Math.min(left, diag)));
                    }

                    if (dp[i][j] > bestSize) {
                        bestSize = dp[i][j];
                        bestRow = i;
                        bestCol = j;
                    }
                }
            }
        }

        return new Result(bestSize, bestRow - bestSize + 1, bestCol - bestSize + 1);
    }

    // Random matrix generator
    public static byte[][] generateRandomMatrix(int m, int n, double probZero, Random rng) {
        byte[][] B = new byte[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                B[i][j] = (rng.nextDouble() < probZero) ? (byte)0 : (byte)1;
            }
        }
        return B;
    }

    public static void main(String[] args) throws IOException {

        // Matrix sizes for the experiment
        int[][] sizes = {
            {10, 10},
            {10, 100},
            {10, 1000},
            {100, 1000},
            {1000, 1000}
        };

        // JVM warm-up
        Random warmRng = new Random(123);
        for (int i = 0; i < 5; i++) {
            byte[][] W = generateRandomMatrix(200, 200, 0.5, warmRng);
            largestZeroSquare(W);  // Warm-up run (ignored results)
        }
        System.gc(); 

        Random rng = new Random(42);
        double pZero = 0.5;

        FileWriter fw = new FileWriter("results.csv");
        fw.write("rows,cols,time_ms,memory_MB,k\n");

        System.out.printf("%10s %10s %15s %15s %5s\n",
                "rows", "cols", "time_ms", "memory_MB", "k");

        for (int[] sz : sizes) {
            int m = sz[0];
            int n = sz[1];

            // Generate input
            byte[][] B = generateRandomMatrix(m, n, pZero, rng);

            Runtime rt = Runtime.getRuntime();
            System.gc();
            long before = rt.totalMemory() - rt.freeMemory();

            long start = System.nanoTime();
            Result res = largestZeroSquare(B);
            long end = System.nanoTime();

            long after = rt.totalMemory() - rt.freeMemory();
            double timeMs = (end - start) / 1_000_000.0;

            double memoryMB = (3.0 * m * n) / (1024.0 * 1024.0);

            System.out.printf("%10d %10d %15.4f %15.4f %5d\n",
                    m, n, timeMs, memoryMB, res.size);

            fw.write(m + "," + n + "," + timeMs + "," + memoryMB + "," + res.size + "\n");
        }

        fw.close();
        System.out.println("\nSaved results → results.csv");
    }
}
