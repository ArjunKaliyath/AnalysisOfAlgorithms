import java.util.Arrays;
import java.util.Random;

public class WeightedApproxCommonSubstring {

    /** Holds result of DP. */
    static class Result {
        double bestScore;
        int start1;
        int start2;
        int length;
        String sub1;
        String sub2;

        @Override
        public String toString() {
            return "Score = " + bestScore +
                    "\nString 1 substring: \"" + sub1 + "\" starting at " + start1 +
                    "\nString 2 substring: \"" + sub2 + "\" starting at " + start2 +
                    "\nLength = " + length;
        }
    }

    /**
     * Core DP algorithm.
     * @param s1 first string (A-Z)
     * @param s2 second string (A-Z)
     * @param weights length-26 array weights['A'..'Z']
     * @param delta mismatch penalty (positive number)
     */
    public static Result bestWeightedSubstring(String s1, String s2,
                                               double[] weights, double delta) {
        int m = s1.length();
        int n = s2.length();

        double[][] dp = new double[m][n];
        int[][] len = new int[m][n];

        double bestScore = Double.NEGATIVE_INFINITY;
        int bestI = -1, bestJ = -1, bestLen = 0;

        for (int i = 0; i < m; i++) {
            char c1 = s1.charAt(i);
            int idx1 = c1 - 'A';
            for (int j = 0; j < n; j++) {
                char c2 = s2.charAt(j);

                // per-position score
                double score;
                if (c1 == c2) {
                    score = weights[idx1];
                } else {
                    score = -delta;
                }

                if (i == 0 || j == 0) {
                    // Cannot extend from diagonal, must start new substring here
                    dp[i][j] = score;
                    len[i][j] = 1;
                } else {
                    double extendScore = dp[i - 1][j - 1] + score;
                    if (extendScore > score) {
                        dp[i][j] = extendScore;
                        len[i][j] = len[i - 1][j - 1] + 1;
                    } else {
                        dp[i][j] = score;
                        len[i][j] = 1;
                    }
                }

                // Update global best
                if (dp[i][j] > bestScore) {
                    bestScore = dp[i][j];
                    bestI = i;
                    bestJ = j;
                    bestLen = len[i][j];
                }
            }
        }

        Result res = new Result();
        res.bestScore = bestScore;
        res.length = bestLen;

        if (bestLen > 0) {
            int start1 = bestI - bestLen + 1;
            int start2 = bestJ - bestLen + 1;
            res.start1 = start1;
            res.start2 = start2;
            res.sub1 = s1.substring(start1, bestI + 1);
            res.sub2 = s2.substring(start2, bestJ + 1);
        } else {
            // Should not really happen if there is at least one character,
            // but just to be safe:
            res.start1 = res.start2 = -1;
            res.sub1 = res.sub2 = "";
        }
        return res;
    }

    /* ---------------- Scenario 1 weights ---------------- */

    // Scenario 1: Wi = 1 for all letters, delta = 10.
    public static double[] scenario1Weights() {
        double[] w = new double[26];
        Arrays.fill(w, 1.0);
        return w;
    }

    /* ---------------- Scenario 2 weights ---------------- */

    /**
     * Scenario 2: Wi proportional to English letter frequencies.
     * You can use raw percentages or normalized values; only relative
     * magnitudes matter for the DP.
     */
    public static double[] scenario2Weights() {
        double[] w = new double[26];
        // Approximate English frequencies (percent): A..Z
        double[] freq = {
                8.17, 1.49, 2.78, 4.25, 12.70, 2.23, 2.02,
                6.09, 6.97, 0.15, 0.77, 4.03, 2.41, 6.75,
                7.51, 1.93, 0.10, 5.99, 6.33, 9.06,
                2.76, 0.98, 2.36, 0.15, 1.97, 0.07
        };
        // Use frequencies directly as weights
        System.arraycopy(freq, 0, w, 0, 26);
        return w;
    }

    /**
     * Example random string generator (uniform over A-Z).
     */
    public static String randomString(int length, Random rng) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char c = (char) ('A' + rng.nextInt(26));
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * Example main to show usage.
     */
    public static void main(String[] args) {
        // Example from the assignment
        String s1 = "ABCAABCAA";
        String s2 = "ABBCAACCBBBBBB";

        double[] w1 = scenario1Weights();
        double delta1 = 10.0;

        Result r1 = bestWeightedSubstring(s1, s2, w1, delta1);
        System.out.println("Example, Scenario 1:");
        System.out.println(r1);

        // Example: Scenario 2 on random data
        double[] w2 = scenario2Weights();
        double minW = Double.POSITIVE_INFINITY;
        double maxW = Double.NEGATIVE_INFINITY;
        for (double v : w2) {
            minW = Math.min(minW, v);
            maxW = Math.max(maxW, v);
        }

        Random rng = new Random(42);
        String rs1 = randomString(1000, rng);
        String rs2 = randomString(1000, rng);

        System.out.println("\nScenario 2 random experiment:");
        int numDeltas = 10;
        for (int k = 0; k < numDeltas; k++) {
            double delta = minW + (maxW - minW) * k / (double) (numDeltas - 1);
            long startTime = System.nanoTime();
            Result res = bestWeightedSubstring(rs1, rs2, w2, delta);
            long endTime = System.nanoTime();
            double millis = (endTime - startTime) / 1_000_000.0;
            System.out.printf("delta=%.3f, time=%.3f ms, bestScore=%.3f, len=%d%n",
                    delta, millis, res.bestScore, res.length);
        }
    }
}
