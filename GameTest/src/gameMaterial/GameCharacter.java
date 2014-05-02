package gameMaterial;

import javax.swing.*;
import gameMaterial.UI.*;
import java.awt.*;
import gameMaterial.characterPaint.*;
public class GameCharacter {
	private static GameCharacter singleton = null;

	public static GameCharacter getInstance() {
		if (singleton == null)
			singleton = new GameCharacter();
		return singleton;
	}
	public Body bodyPaint;
	public Head headPaint;
	public UINameTag uiNameTag;
	public UIChatBalloon uiChatBalloon;
	public int speedX, speedY;
	public int X, Y;
	public int shiftX, shiftY;
	public int runCounter = 0;
	public int state, roleDir;

	class FSM {
		private StateMode[] states = { new StopMode(), new RunMode(),
				new JumpMode(), new ClimbMode(), new DropMode() }; // 2. states
		private int[][] transition = { { 0, 1, 1, 2, 3, 3, 4 },
				{ 0, 1, 1, 2, 3, 3, 4 }, { 0, 1, 1, 2, 3, 3, 4 },
				{ 0, 1, 1, 2, 3, 3, 4 }, { 0, 1, 1, 2, 3, 3, 4 } }; // 4.
		// transitions
		private int current = 0; // 3. current

		private void next(int msg) {
			if (states[current].isComplete(msg)) {
				current = transition[current][msg];
				on();
			}
		}

		// 5. All client requests are simply delegated to the current state
		// object
		public void on() {
			states[current].on();
		}

		public void off() {
			states[current].off();
		}

		public void ack() {
			states[current].ack();
		}

		public StateMode getStateMode() {
			return states[current];
		}
	}

	private FSM fsm;
	private String msgKeyin = "";

	private GameCharacter() {
		fsm = new FSM();
		roleDir = 0;
		state = 0;
		speedX = 0;
		speedY = 0;
		shiftX = 0;
		shiftY = 0;
		bodyPaint = new Body();
		bodyPaint = new LongCoat(bodyPaint);
		headPaint = new Head();
		headPaint = new Face(headPaint);
		headPaint = new Hair(headPaint);
		uiNameTag = new UINameTag("Name-Tag ");
		uiChatBalloon = new UIChatBalloon("English1234567中文字型七個字中E夾雜7個字?.WWW等寬字元測試");
	}

	public void nextState(String msg) {
		msgKeyin = msg;
		if (msg.equals("released"))
			fsm.next(0);
		else if (msg.equals("right")) {
			roleDir = 0;
			fsm.next(1);
		} else if (msg.equals("left")) {
			roleDir = 1;
			fsm.next(2);
		} else if (msg.equals("space")) {
			fsm.next(3);
		} else if (msg.equals("up")) {
			fsm.next(4);
			roleDir = 2;
		} else if (msg.equals("down")) {
			fsm.next(5);
			roleDir = 3;
		} else if (msg.equals("drop")) {
			fsm.next(6);
		}
	}

	public void ack() {
		fsm.ack();
	}

	public void timerAction() {
		ack();
	}

	public String getActionCommand() {
		return fsm.getStateMode().getCommand();
	}

	public String getPrevKey() {
		return msgKeyin;
	}
	
	public void paint(Graphics g) {
		/*g.setColor(Color.RED);
		for(int i = 10; i < 15; i++)
			g.drawOval(X - i, Y - i, 2*i, 2*i);*/
		fsm.getStateMode().paint(g);
		uiNameTag.paint(g, X, Y);
		uiChatBalloon.paint(g, X, Y);
	}

}
