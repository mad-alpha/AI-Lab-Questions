import java.util.*;

class State {
	int monkeyX;
	int monkeyY;
	int boxX;
	int boxY;
	boolean hasBanana;
	boolean hasBox;

	public State(int monkeyX, int monkeyY, int boxX, int boxY,  boolean hasBanana, boolean hasBox) {
		this.monkeyX = monkeyX;
		this.monkeyY = monkeyY;
		this.boxX = boxX;
		this.boxY = boxY;
		this.hasBanana = hasBanana;
		this.hasBox = hasBox;
	}

	@Override
	public int hashCode() {
		return Objects.hash(monkeyX, monkeyY, boxX, boxY, hasBanana, hasBox);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		State other = (State) obj;
		return  monkeyX == other.monkeyX &&
				monkeyY == other.monkeyY &&
				boxX == other.boxX &&
				boxY == other.boxY &&
				hasBanana == other.hasBanana &&
				hasBox == other.hasBox;

	}
}

class Node {
	State state;
	Node parent;
	int cost;
	int heuristic;

	public Node(State state, Node parent, int cost, int heuristic) {
		this.state = state;
		this.parent = parent;
		this.cost = cost;
		this.heuristic = heuristic;
	}
}

public class MonkeyBananaAOStar {
	private static final int roomLowerLeftX = 0;
	private static final int roomLowerLeftY = 0;
	private static final int roomUpperRightX = 10;
	private static final int roomUpperRightY = 10;
	private static final int bananaX = 5;
	private static final int bananaY = 5;
	private static final int[][] DIRECTIONS = {{0, 1}, {-1, 0}, {0, -1}, {1, 0}};
	private static final int MAX_COST = 100000;

	public static void main(String[] args) {
		// Define the initial state
		State initialState = new State(1, 1, 0, 7, false, false);

		// Solve the problem using AO* algorithm
		Node solutionNode = aoStar(initialState);

		// Print the solution path
		if (solutionNode != null) {
			List<String> actions = extractActions(solutionNode);
			Collections.reverse(actions);
			System.out.println("Solution Path:");
			for (String action : actions) {
				System.out.println(action);
			}
		} else {
			System.out.println("No solution found.");
		}
	}

	private static Node aoStar(State initialState) {
		PriorityQueue<Node> openList = new PriorityQueue<>(Comparator.comparingInt(node -> node.cost + node.heuristic));
		Map<State, Integer> gValues = new HashMap<>();
		openList.add(new Node(initialState, null, 0, heuristic(initialState)));
		gValues.put(initialState, 0);

		while (!openList.isEmpty()) {
			Node currentNode = openList.poll();
			State currentState = currentNode.state;

			if (currentState.hasBox && currentState.hasBanana) {
				return currentNode;
			}

			if (currentNode.cost >= MAX_COST) {
				continue;
			}

			for (int[] direction : DIRECTIONS) {
				int newMonkeyX = currentState.monkeyX + direction[0];
				int newMonkeyY = currentState.monkeyY + direction[1];

				if (isValidPosition(newMonkeyX, newMonkeyY)) {
					boolean newHasBanana = newMonkeyX == bananaX && newMonkeyY == bananaY;
					boolean newHasBox = currentState.hasBox || (newMonkeyX == currentState.boxX && newMonkeyY == currentState.boxY);
					int newBoxX = newHasBox ? newMonkeyX : currentState.boxX;
					int newBoxY = newHasBox ? newMonkeyY : currentState.boxY;
					State nextState = new State(newMonkeyX, newMonkeyY, newBoxX, newBoxY, newHasBanana, newHasBox);
					int cost = currentNode.cost + 1;

					if (!nextState.equals(currentState) && cost < gValues.getOrDefault(nextState, Integer.MAX_VALUE)) {
						int heuristicValue = heuristic(nextState);
						openList.add(new Node(nextState, currentNode, cost, heuristicValue));
						gValues.put(nextState, cost);
					}
				}
			}
		}

		return null; // No solution found
	}

	private static boolean isValidPosition(int x, int y) {
		return x >= roomLowerLeftX && x <= roomUpperRightX && y >= roomLowerLeftY && y <= roomUpperRightY;
	}

	private static int heuristic(State state) {
		int monkeyBananaDistance = Math.abs(state.monkeyX - bananaX) + Math.abs(state.monkeyY - bananaY);
		int boxBananaDistance = Math.abs(state.boxX - bananaX) + Math.abs(state.boxY - bananaY);
		int monkeyBoxDistance = Math.abs(state.monkeyX - state.boxX) + Math.abs(state.monkeyY - state.boxY);
		return monkeyBoxDistance + boxBananaDistance;
	}

	private static List<String> extractActions(Node solutionNode) {
		List<String> actions = new ArrayList<>();
		Node currentNode = solutionNode;

		while (currentNode.parent != null) {
			State currentState = currentNode.state;
			Node parent = currentNode.parent;
			State parentState = parent.state;

			if (currentState.monkeyX < parentState.monkeyX) {
				actions.add("Move left");
			} else if (currentState.monkeyX > parentState.monkeyX) {
				actions.add("Move right");
			} else if (currentState.monkeyY < parentState.monkeyY) {
				actions.add("Move down");
			} else if (currentState.monkeyY > parentState.monkeyY) {
				actions.add("Move up");
			}
			if(parentState.hasBox) {
				int lastActionIndex = actions.size() - 1;
				String lastAction = actions.get(lastActionIndex);
				actions.set(lastActionIndex, lastAction + " with box");
			}
			currentNode = parent;
		}

		return actions;
	}
}