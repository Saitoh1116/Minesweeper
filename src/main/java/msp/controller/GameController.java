package msp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import msp.model.GameState;
import msp.model.MoveRequest;
import msp.service.GameService;

@Controller
public class GameController {

	@Autowired
	private GameService gameService;

	/**
	 * ゲームのメインページを表示
	 */
	@GetMapping("/")
	public String game(Model model) {
		GameState initialState = gameService.newGame();
		System.out.println(initialState); // デバッグ用出力
		model.addAttribute("gameState", initialState);
		return "game";
	}

	/**
	 * 新しいゲームを開始
	 */
	@PostMapping("/api/newGame")
	@ResponseBody
	public GameState newGame() {
		return gameService.newGame();
	}

	/**
	 * セルをクリックした時の処理
	 */
	@PostMapping("/api/click")
	@ResponseBody
	public ResponseEntity<GameState> handleClick(@RequestBody MoveRequest request) {
	    GameState state = gameService.handleClick(
	            request.getRow(),
	            request.getCol(),
	            request.isRightClick());  // isRightClick が true の場合、フラグを置く
	    return ResponseEntity.ok(state);
	}

	/**
	 * 現在のゲーム状態を取得
	 */
	@GetMapping("/api/state")
	@ResponseBody
	public GameState getState() {
		return gameService.getCurrentState();
	}
}