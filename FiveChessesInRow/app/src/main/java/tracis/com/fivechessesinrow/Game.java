package tracis.com.fivechessesinrow;

public class Game {
    private int MAX_LINE;

    private int[][] mTable;
    private boolean[][][] mWins;
    private int mWinPossibilityNum = 0;

    private boolean mIsPlayer = true;

    private int[] mPlayerWin;
    private int[] mComputerWin;
    private int mOver = 0;

    private int mCount = 0;

    public Game(int MAX_LINE) {
        this.MAX_LINE = MAX_LINE;
        init();
    }

    public int[][] getTable() {
        return mTable;
    }

    public void init() {
        mTable = new int[MAX_LINE][MAX_LINE];
        mWins = new boolean[MAX_LINE][MAX_LINE][10 * MAX_LINE * (MAX_LINE - 4) * (MAX_LINE - 4)];

        for (int i = 0; i < MAX_LINE; i++) {
            for (int j = 0; j < MAX_LINE - 4; j++) {
                for (int k = 0; k < 5; k++) {
                    mWins[i][j + k][mWinPossibilityNum] = true;
                }
                mWinPossibilityNum++;
            }
        }

        for (int i = 0; i < MAX_LINE; i++) {
            for (int j = 0; j < MAX_LINE - 4; j++) {
                for (int k = 0; k < 5; k++) {
                    mWins[j + k][i][mWinPossibilityNum] = true;
                }
                mWinPossibilityNum++;
            }
        }

        for (int i = 0; i < MAX_LINE - 4; i++) {
            for (int j = MAX_LINE - 1; j > 3; j--) {
                for (int k = 0; k < 5; k++) {
                    mWins[i + k][j - k][mWinPossibilityNum] = true;
                }
                mWinPossibilityNum++;
            }
        }

        for (int i = 0; i < MAX_LINE - 4; i++) {
            for (int j = 0; j < MAX_LINE - 4; j++) {
                for (int k = 0; k < 5; k++) {
                    mWins[i + k][j + k][mWinPossibilityNum] = true;
                }
                mWinPossibilityNum++;
            }
        }

        mPlayerWin = new int[mWinPossibilityNum];
        mComputerWin = new int[mWinPossibilityNum];
    }

    public String checkGameStatus() {
        String text = null;
        if (mOver != 0) {
            switch (mOver) {
                case 1:
                    text = "You win!";
                    break;
                case 2:
                    text = "Computer win!";
                    break;
                case 3:
                    text = "Game tie!";
                    break;
                default:
                    break;
            }
        }
        return text;
    }

    public boolean onTouchEvent(int x, int y, float lineHeight) {
        if (mCount >= MAX_LINE * MAX_LINE) return false;
        if (mIsPlayer) {

            int i = (int) (x / lineHeight);
            int j = (int) (y / lineHeight);

            if (mTable[i][j] == 0) {
                mTable[i][j] = 1;
                mCount++;
                if (mCount >= MAX_LINE * MAX_LINE) {
                    mOver = 3;
                    return false;
                }
                for (int m = 0; m < mWinPossibilityNum; m++) {
                    if (mWins[i][j][m]) {
                        mPlayerWin[m]++;
                        mComputerWin[m] = 6;
                        if (mPlayerWin[m] == 5) {
//                                Player win
                            mOver = 1;
                        }
                    }
                }
                if (mOver == 0) {
                    mIsPlayer = !mIsPlayer;
                    computerAI();
                }
            }

            return true;
        }
        return false;
    }

    private void computerAI() {
        int[][] myScore = new int[MAX_LINE][MAX_LINE];
        int[][] computerScore = new int[MAX_LINE][MAX_LINE];
        int max = 0;
        int u = 0, v = 0;

        for (int i = 0; i < MAX_LINE; i++) {
            for (int j = 0; j < MAX_LINE; j++) {
                if (mTable[i][j] == 0) {
                    for (int k = 0; k < mWinPossibilityNum; k++) {
                        if (mWins[i][j][k]) {
                            if (mPlayerWin[k] == 1) {
                                myScore[i][j] += 200;
                            } else if (mPlayerWin[k] == 2) {
                                myScore[i][j] += 400;
                            } else if (mPlayerWin[k] == 3) {
                                myScore[i][j] += 2000;
                            } else if (mPlayerWin[k] == 4) {
                                myScore[i][j] += 10000;
                            }

                            if (mComputerWin[k] == 1) {
                                computerScore[i][j] += 220;
                            } else if (mComputerWin[k] == 2) {
                                computerScore[i][j] += 420;
                            } else if (mComputerWin[k] == 3) {
                                computerScore[i][j] += 2100;
                            } else if (mComputerWin[k] == 4) {
                                computerScore[i][j] += 20000;
                            }
                        }
                    }

                    if (myScore[i][j] > max) {
                        max = myScore[i][j];
                        u = i;
                        v = j;
                    } else if (myScore[i][j] == max) {
                        if (computerScore[i][j] > computerScore[u][v]) {
                            u = i;
                            v = j;
                        }
                    }
                    if (computerScore[i][j] > max) {
                        max = computerScore[i][j];
                        u = i;
                        v = j;
                    } else if (computerScore[i][j] == max) {
                        if (myScore[i][j] > myScore[u][v]) {
                            u = i;
                            v = j;
                        }
                    }
                }
            }
        }
        mTable[u][v] = 2;

        mCount++;
        if (mCount >= MAX_LINE * MAX_LINE) {
            mOver = 3;
        }

        for (int k = 0; k < mWinPossibilityNum; k++) {
            if (mWins[u][v][k]) {
                mComputerWin[k]++;
                mPlayerWin[k] = 6;
                if (mComputerWin[k] == 5) {
                    mOver = 2;
                }
            }
        }
        if (mOver == 0) {
            mIsPlayer = !mIsPlayer;
        }
    }
}
