package net.endarium.api.utils.commands;

/**
 * Used to represent a pair of objects.
 */
public class Pair<L, R> {

	private L left;
	private R right;

	public Pair(L left, R right) {
		this.left = left;
		this.right = right;
	}

	public Pair() {
	}

	public L getLeft() {
		return left;
	}

	public void setLeft(L left) {
		this.left = left;
	}

	public R getRight() {
		return right;
	}

	public void setRight(R riht) {
		this.right = riht;
	}
}