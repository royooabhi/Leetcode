class Solution {

    // Prime exponent contribution of digits 0..9:
    // columns = powers of 2, 3, 5, 7
    private static final int[][] F = {
        {0,0,0,0}, // 0 (not allowed)
        {0,0,0,0}, // 1
        {1,0,0,0}, // 2
        {0,1,0,0}, // 3
        {2,0,0,0}, // 4
        {0,0,1,0}, // 5
        {1,1,0,0}, // 6
        {0,0,0,1}, // 7
        {3,0,0,0}, // 8
        {0,2,0,0}  // 9
    };

    public String smallestNumber(String num, long t) {
        int[] need = new int[4];

        // Factorize t into 2, 3, 5, 7.
        int[] primes = {2, 3, 5, 7};

        for (int i = 0; i < 4; i++) {
            while (t % primes[i] == 0) {
                need[i]++;
                t /= primes[i];
            }
        }

        // Digits 1..9 cannot provide any other prime factor.
        if (t != 1) {
            return "-1";
        }

        int n = num.length();

        // pref[i][j] = exponent of prime j contributed by
        // num[0 ... i-1].
        int[][] pref = new int[n + 1][4];

        // Number of zeros in prefix.
        int[] zeroPref = new int[n + 1];

        for (int i = 0; i < n; i++) {
            int d = num.charAt(i) - '0';

            zeroPref[i + 1] = zeroPref[i] + (d == 0 ? 1 : 0);

            for (int j = 0; j < 4; j++) {
                pref[i + 1][j] = pref[i][j] + F[d][j];
            }
        }

        // num itself is already valid.
        if (zeroPref[n] == 0) {
            boolean ok = true;

            for (int j = 0; j < 4; j++) {
                if (pref[n][j] < need[j]) {
                    ok = false;
                    break;
                }
            }

            if (ok) {
                return num;
            }
        }

        /*
         * Try changing one digit of num.
         *
         * We go from right to left because we want the smallest
         * possible number >= num.
         */
        for (int i = n - 1; i >= 0; i--) {

            // Prefix before i must already be zero-free.
            if (zeroPref[i] > 0) {
                continue;
            }

            int original = num.charAt(i) - '0';

            // Replace current digit by the smallest digit > original.
            for (int d = Math.max(1, original + 1); d <= 9; d++) {

                int[] rem = new int[4];

                for (int j = 0; j < 4; j++) {
                    rem[j] = Math.max(
                        0,
                        need[j] - pref[i][j] - F[d][j]
                    );
                }

                int positionsLeft = n - i - 1;

                if (minDigits(rem) <= positionsLeft) {

                    StringBuilder ans = new StringBuilder(n);

                    ans.append(num, 0, i);
                    ans.append((char) ('0' + d));

                    ans.append(buildSmallest(rem, positionsLeft));

                    return ans.toString();
                }
            }
        }

        /*
         * No answer having the same length.
         *
         * Build the smallest valid number with a larger length.
         */
        int required = minDigits(need);

        int newLength = Math.max(n + 1, required);

        return buildSmallest(need, newLength);
    }


    /*
     * Minimum number of digits needed to supply the required
     * powers of 2,3,5,7.
     *
     * Useful digits:
     *
     * 8 = 2^3
     * 9 = 3^2
     * 6 = 2*3
     * 4 = 2^2
     * 2 = 2
     * 3 = 3
     * 5 = 5
     * 7 = 7
     */
    private int minDigits(int[] r) {

        int a = r[0]; // power of 2
        int b = r[1]; // power of 3
        int c = r[2]; // power of 5
        int d = r[3]; // power of 7

        // 5 and 7 always require separate digits.
        int result = c + d;

        // For 2 and 3, maximize how much can be packed per digit.
        //
        // We can test the small remainder interaction after using
        // as many 8s and 9s as possible. A compact exact formula:
        result += a / 3;
        a %= 3;

        result += b / 2;
        b %= 2;

        // Remaining:
        // a is 0..2
        // b is 0..1
        //
        // If both exist, digit 6 can cover one 2 and one 3.
        if (a > 0 && b > 0) {
            result++;
            a--;
            b--;
        }

        // Remaining powers of 2 can be covered by 2 or 4.
        if (a > 0) {
            result++;
        }

        // Remaining power of 3 requires digit 3.
        if (b > 0) {
            result++;
        }

        return result;
    }


    /*
     * Build lexicographically smallest zero-free string of exactly
     * len digits whose digit product supplies all exponents in need.
     *
     * Since all candidates have equal length, lexicographically
     * smallest = numerically smallest.
     */
    private String buildSmallest(int[] need, int len) {

        StringBuilder sb = new StringBuilder(len);

        int[] rem = need.clone();

        for (int pos = 0; pos < len; pos++) {

            int left = len - pos - 1;

            // Try digits from smallest to largest.
            for (int d = 1; d <= 9; d++) {

                int[] next = new int[4];

                for (int j = 0; j < 4; j++) {
                    next[j] = Math.max(
                        0,
                        rem[j] - F[d][j]
                    );
                }

                if (minDigits(next) <= left) {
                    sb.append((char) ('0' + d));
                    rem = next;
                    break;
                }
            }
        }

        return sb.toString();
    }
}