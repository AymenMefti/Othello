import javax.swing.*;
import java.awt.*;

public class DiscButton extends JButton {
    private char piece = ' ';

    public void setPiece(char p) {
        this.piece = p;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (piece == 'B' || piece == 'W') {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(piece == 'B' ? Color.BLACK : Color.WHITE);
            int margin = 8;
            g2.fillOval(margin, margin, getWidth() - 2 * margin, getHeight() - 2 * margin);
            g2.setColor(Color.DARK_GRAY);
            g2.drawOval(margin, margin, getWidth() - 2 * margin, getHeight() - 2 * margin);
        }
    }
}
