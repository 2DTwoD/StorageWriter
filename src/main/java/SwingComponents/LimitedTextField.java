package SwingComponents;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LimitedTextField extends JTextField {
    static final int ONLY_NUMBER = 1;
    static final int ONLY_ASCII = 2;
    public LimitedTextField(String text, int charLim) {
        super(text);
        addCaretListener(e -> {
            if (getText().length() > charLim) {
                SwingUtilities.invokeLater(() ->setText(getText().substring(0, charLim)));
            }
        });
    }
    public LimitedTextField(String text, int charLim, int charFilt) {
        this(text, charLim);
        if (charFilt == ONLY_NUMBER) {
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    checkChar(e, 48, 57);
                }
            });
        } else if (charFilt == ONLY_ASCII){
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    checkChar(e, 33, 126);
                }
            });
        }
    }
    void checkChar(KeyEvent e, int lim1, int lim2){
        if (e.getKeyCode() == 8 || e.getKeyCode() == 127 || e.getKeyCode() == 37 ||e.getKeyCode() == 39) {
            return;
        }
        int c = e.getKeyChar();
        String oldText = getText();
        int carretPosition = getCaretPosition();
        if (c < lim1 || c > lim2) {
            SwingUtilities.invokeLater(() -> {
                setText(oldText);
                setCaretPosition(carretPosition);
            });
        }
    }
}
