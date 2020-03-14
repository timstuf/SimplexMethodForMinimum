package com.nure;


class SimplexTable {
    private double[][] table;
    private int[] cKoefs;
    private boolean[] wasInBazis;
    private int[][] equations;
    private int[] bazis;
    private int maxIndex;
    private int minEval;


    public SimplexTable() {
        table = new double[Constants.m + 1][Constants.n + 3];
        bazis = new int[]{6, 5};
        wasInBazis = new boolean[6];
        wasInBazis[5] = true;
        equations = new int[Constants.m][Constants.n + 1];
        fillEquations();
        cKoefs = new int[]{-3, 2, 1, 0, 0, Constants.M, 8};
        createTable();
    }

    private void fillEquations() {
        equations[0][0] = -1;
        equations[0][1] = -1;
        equations[0][2] = 1;
        equations[0][3] = -1;
        equations[0][4] = 0;
        equations[0][5] = 1;
        equations[0][6] = 4;
        equations[1][0] = 1;
        equations[1][1] = 2;
        equations[1][2] = 1;
        equations[1][3] = 0;
        equations[1][4] = 1;
        equations[1][5] = 0;
        equations[1][6] = 8;
    }

    private boolean isNotOptimized() {

        for (int i = 0; i < Constants.n; i++) {
            if (wasInBazis[i]) continue;
            if (table[2][i + 2] > 0) return true;
        }
        return false;
    }

    private void countdeltaJ() {
        for (int i = 1; i < Constants.n; i++) {
            int evaluation = 0;
            for (int j = 0; j < Constants.m; j++) {
                evaluation += table[j][0] * table[j][i];
            }
            if (i != 1) evaluation = evaluation - cKoefs[i - 2];
            table[Constants.m][i] = evaluation;
        }
    }

    private void countEvaluations() {
        int maxIndex = 0; //number of направляющий столбенц
        for (int i = 2; i < Constants.n + 2; i++) {
            if (table[2][i] > table[2][maxIndex]) maxIndex = i;
        }
        for (int i = 0; i < Constants.m; i++) {
            if (table[i][maxIndex] <= 0) table[i][8] = Constants.DONT_COUNT;
            table[i][8] = table[i][1] / table[i][maxIndex];
        }
        this.maxIndex = maxIndex;
    }

    private void recountTable() {
        int minEval = 0;
        double min = Double.MAX_VALUE;
        for (int i = 0; i < Constants.m; i++) {
            if (table[i][8] < min && table[i][8] > Constants.DONT_COUNT) {
                minEval = i;
                min = table[i][8];
            }
        }
        bazis[minEval] = maxIndex - 1;
        table[minEval][0] = cKoefs[maxIndex - 2];
        double superElement = table[minEval][maxIndex];
        for (int i = 1; i < Constants.n + 2; i++) { //направляющая строка
            table[minEval][i] /= superElement;
        }
        double[][] newTable = new double[Constants.m + 1][Constants.n + 3];
        for (int i = 0; i < newTable.length; i++) {
            System.arraycopy(table[i], 0, newTable[i], 0, newTable[0].length);
        }
        for (int i = 0; i < Constants.m + 1; i++) {
            for (int j = 1; j < Constants.n + 2; j++) {
                if (i != minEval)
                    newTable[i][j] = table[i][j] - table[i][maxIndex] * table[minEval][j];
            }
        }
        table = newTable;
    }

    private void createTable() {
        table[0][0] = cKoefs[5];
        table[1][0] = cKoefs[4];

        for (int i = 0; i < Constants.m; i++) { //x
            table[i][1] = equations[i][6];
        }
        for (int i = 0; i < Constants.m; i++) { //rest
            for (int j = 2; j < Constants.n + 2; j++)
                table[i][j] = equations[i][j - 2];
        }
        countdeltaJ();
        countEvaluations();
    }

    private void printTable() {
        System.out.println("_____________________________________________________________________________");
        int j;
        System.out.print("                   ");
        for (int i = 0; i < cKoefs.length - 1; i++) {
            System.out.print(String.format("%8s", cKoefs[i]));
        }
        System.out.print('\n');
        System.out.println(" Base     C       X      x1      x2      x3      x4      x5      x6      0");
        for (int i = 0; i < table.length; i++) {
            if (i < table.length - 1) {
                System.out.print("  x" + bazis[i]);
                j = 0;
            } else {
                System.out.print("            ");
                j = 1;
            }

            for (; j < table[0].length; j++) {
                System.out.print(String.format("%8.1f", table[i][j]));

            }
            System.out.print('\n');
        }
        System.out.println("_____________________________________________________________________________");
    }

    private double[] getSolutions() {
        double[] solution = new double[Constants.n];
        solution[0] = table[2][0] + cKoefs[cKoefs.length - 1];
        System.arraycopy(table[2], 2, solution, 1, 5);
        return solution;
    }

    private void printSolution() {
        double[] solution = getSolutions();
        System.out.println("The optimal value of the function: " + solution[0]);
        for (int i = 1; i < Constants.n; i++) {
            System.out.println("x" + (i) + " has value " + String.format("%.1f", solution[i]));
        }
    }

    void go() {
        printTable();
        while (isNotOptimized()) {
            recountTable();
            if (isNotOptimized()) countEvaluations();
            printTable();
        }
        printSolution();
    }
}
