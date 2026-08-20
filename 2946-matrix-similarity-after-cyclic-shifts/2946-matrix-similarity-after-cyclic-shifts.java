class Solution {
    public boolean areSimilar(int[][] mat, int k) {

        int rows = mat.length;
        int cols = mat[0].length;

        k = k % cols;

        for (int i = 0; i < rows; i++) {

            for (int j = 0; j < cols; j++) {

                int newIndex;

                if (i % 2 == 0) {
                    // Even row: shift right
                    newIndex = (j + k) % cols;
                } else {
                    // Odd row: shift left
                    newIndex = (j - k + cols) % cols;
                }

                if (mat[i][j] != mat[i][newIndex]) {
                    return false;
                }
            }
        }

        return true;
    }
}