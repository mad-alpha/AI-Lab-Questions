import java.util.*;

class State implements Comparable<State> {
	int[][] board;
	int queens;
	int cost;

	public State(int[][] board, int queens, int cost) {
		this.board = board;
		this.queens = queens;
		this.cost = cost;
	}

	@Override
	public int compareTo(State other) {
		return Integer.compare(this.cost, other.cost);
	}
}

public class NQueensAStar {
	public static void main(String[] args) {
		int n = 20;
		int[][] board = new int[n][n];

		if (solveNQueens(board)) {
			printBoard(board);
		} else {
			System.out.println("No solution exists.");
		}
	}

	public static boolean solveNQueens(int[][] board) {
		int n = board.length;
		PriorityQueue<State> priorityQueue = new PriorityQueue<>();
		priorityQueue.add(new State(board, 0, 0));

		while (!priorityQueue.isEmpty()) {
			State currentState = priorityQueue.poll();
			int queens = currentState.queens;
			if (queens == n) {
				copyBoard(currentState.board, board);
				return true;
			}

			for (int col = 0; col < n; col++) {
				if (isSafe(currentState.board, queens, col)) {
					int[][] newBoard = new int[n][n];
					copyBoard(currentState.board, newBoard);
					newBoard[queens][col] = 1;
					priorityQueue.add(new State(newBoard, queens + 1, heuristic(newBoard)));
				}
			}
		}
		return false;
	}

	public static boolean isSafe(int[][] board, int row, int col) {
		int n = board.length;

		for (int i = 0; i < row; i++) {
			if (board[i][col] == 1) {
				return false;
			}
		}

		for (int i = row, j = col; i >= 0 && j >= 0; i--, j--) {
			if (board[i][j] == 1) {
				return false;
			}
		}

		for (int i = row, j = col; i >= 0 && j < n; i--, j++) {
			if (board[i][j] == 1) {
				return false;
			}
		}

		return true;
	}

	public static int heuristic(int[][] board) {
		int n = board.length;
		int attacks = 0;

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (board[i][j] == 1) {
					for (int k = 0; k < n; k++) {
						if (board[i][k] == 1 || board[k][j] == 1 || (i + k < n && j + k < n && board[i + k][j + k] == 1)
								|| (i - k >= 0 && j - k >= 0 && board[i - k][j - k] == 1)
								|| (i + k < n && j - k >= 0 && board[i + k][j - k] == 1)
								|| (i - k >= 0 && j + k < n && board[i - k][j + k] == 1)) {
							attacks++;
						}
					}
				}
			}
		}
		return -attacks;
	}

	public static void copyBoard(int[][] src, int[][] dest) {
		int n = src.length;
		for (int i = 0; i < n; i++) {
			System.arraycopy(src[i], 0, dest[i], 0, n);
		}
	}

	public static void printBoard(int[][] board) {
		int n = board.length;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				System.out.print(board[i][j] == 1 ? "Q " : ". ");
			}
			System.out.println();
		}
	}
}