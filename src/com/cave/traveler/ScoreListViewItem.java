package com.cave.traveler;

public class ScoreListViewItem {
	private int score;
	private String player;
	private int distance;
	private String version;
	private int position;
	private boolean myself = false;

	public ScoreListViewItem() {
		// dummy
	}

	public ScoreListViewItem(int score, String player, int distance, String version, int position, boolean myself) {
		super();
		this.score = score;
		this.player = player;
		this.distance = distance;
		this.version = version;
		this.position = position;
		this.myself = myself;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getPlayer() {
		return player;
	}

	public void setPlayer(String player) {
		this.player = player;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public boolean isMyself() {
		return myself;
	}

	public void setMyself(boolean myself) {
		this.myself = myself;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
}
