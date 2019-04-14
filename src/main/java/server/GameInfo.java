package server;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GameInfo {

	private Game game;
	private int turn;
	private Board board;
	private Snake you;

	public GameInfo() {

	}

	public GameInfo(Game game, int turn, Board board, Snake you) {
		this.game = game;
		this.turn = turn;
		this.board = board;
		this.you = you;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public int getTurn() {
		return turn;
	}

	public void setTurn(int turn) {
		this.turn = turn;
	}

	public Board getBoard() {
		return board;
	}

	public void setBoard(Board board) {
		this.board = board;
	}

	public Snake getYou() {
		return you;
	}

	public void setYou(Snake you) {
		this.you = you;
	}

}
