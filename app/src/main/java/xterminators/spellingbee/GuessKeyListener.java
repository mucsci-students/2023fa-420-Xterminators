package xterminators.spellingbee;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GuessKeyListener implements KeyListener {
    private char[] allowedLetters;
    static final int LETTER_COUNT = 7;

    public GuessKeyListener() {
        allowedLetters = new char[LETTER_COUNT];
    }

    public void setAllowedLetters(char[] allowedLetters) {
        this.allowedLetters = allowedLetters;
    }

    @Override 
    public void keyTyped(KeyEvent e) {
        char keyChar = e.getKeyChar();
        if (!Character.isLetter(keyChar)) {
            e.consume();
        } else {
            for (char c : allowedLetters) {
                if (keyChar == c) {
                    return;
                }
            }
            e.consume();
        }
    }

    @Override 
    public void keyPressed(KeyEvent e) {
        // This handler is not needed currently
    }

    @Override 
    public void keyReleased(KeyEvent e) {
        // This handler is not needed currently
    }
}