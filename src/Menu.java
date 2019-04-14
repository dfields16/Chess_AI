import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Menu extends JPanel {
  private static final long serialVersionUID = 872538032335422368L;

  JFrame frame;
  JPanel menu;
  Game game;
  JLabel bg;
  JLabel lblChessGame;
  JLabel lblChessGame2;
  JLabel teamNames;

  public int gameType = 0;

  public Menu(JFrame frame, Game game) {
    this.frame = frame;
    this.game = game;
    this.menu = this;
    lblChessGame = new JLabel("Chess Game!");
    lblChessGame.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 64));
    lblChessGame.setForeground(Color.white);
    lblChessGame.setHorizontalAlignment(SwingConstants.CENTER);
    lblChessGame.setLayout(null);
    lblChessGame.setBounds(260, 15, 500, 100);

    lblChessGame2 = new JLabel("Chess Game!");
    lblChessGame2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 64));
    lblChessGame2.setForeground(Color.black);
    lblChessGame2.setHorizontalAlignment(SwingConstants.CENTER);
    lblChessGame2.setLayout(null);
    lblChessGame2.setBounds(257, 18, 500, 100);

    teamNames = new JLabel("Abigail Dougherty, Dawson Fields, Dustin Ladd");
    teamNames.setHorizontalAlignment(SwingConstants.CENTER);
    teamNames.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
    teamNames.setForeground(Color.white);
    teamNames.setBounds(335, 100, 350, 20);

    ImageIcon menuImg = new ImageIcon("./img/v2/MainMenu.png");
    Image fixedMenu = menuImg.getImage().getScaledInstance(500, 500, Image.SCALE_DEFAULT);
    JLabel mImg = new JLabel(new ImageIcon(fixedMenu));
    mImg.setBounds(260, 135, 500, 500);

    JButton btnSinglePlayer = new JButton("Single Player");
    btnSinglePlayer.setBounds(210, 650, 150, 50);

    JButton btnMultiPlayer = new JButton("Multi Player");
    btnMultiPlayer.setBounds(435, 650, 150, 50);

    JButton btnAiVsAi = new JButton("Ai vs Ai");
    btnAiVsAi.setBounds(655, 650, 150, 50);

    BufferedImage img;
    try {
      img = ImageIO.read(new File("./img/v2/bg.png"));
      bg = new JLabel(new ImageIcon(img));

    } catch (IOException e1) {
      e1.printStackTrace();
    }

    bg.add(lblChessGame);
    bg.add(lblChessGame2);
    bg.add(teamNames);

    bg.add(btnSinglePlayer);
    bg.add(btnMultiPlayer);
    bg.add(btnAiVsAi);
    bg.add(mImg);
    menu.add(bg);

    btnSinglePlayer.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        System.out.println("Single");
        frame.remove(menu);
        frame.add(game);
        frame.revalidate();
        frame.repaint();
      }
    });
    btnMultiPlayer.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        System.out.println("Dating");
        frame.remove(menu);
                frame.add(game); // change to call with client/server
                // game.connetToServer();
        frame.revalidate();
        frame.repaint();
      }
    });
    btnAiVsAi.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        System.out.println("SkyNet");
        frame.remove(menu);
        frame.add(game); // change to call AI - vs - AI
        frame.revalidate();
        frame.repaint();
      }
    });

  }
}
