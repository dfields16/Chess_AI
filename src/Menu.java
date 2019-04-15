import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
  JLabel bg, bgAI, bgU, bgMulti;
  JLabel lblChessGame;
  JLabel lblChessGame2;
  JLabel teamNames;
  JLabel mImg;
  JLabel gameVersion, IP_PortU, IP_PortAI;

  ImageIcon menuImg;
  Image fixedMenu;

  JButton btnSinglePlayer, btnMultiPlayer, btnAiVsAi;
  JButton IPAddrAI, IPAddrU, UPlay, AiPlay;
  JButton mainMenu;

  JTextField portFieldU, portFieldAI,  IPFieldU, IPFieldAI;
	JComboBox<String[]> diff;

  public int gameType = 0;

  public Menu(JFrame frame) {
	this.frame = frame;
	//this.game = game;
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

	menuImg = new ImageIcon("./img/v2/MainMenu.png");
	fixedMenu = menuImg.getImage().getScaledInstance(500, 500, Image.SCALE_DEFAULT);
	mImg = new JLabel(new ImageIcon(fixedMenu));
	mImg.setBounds(260, 135, 500, 500);

	btnSinglePlayer = new JButton("Single Player");
	btnSinglePlayer.setBounds(335, 650, 150, 50);

	btnMultiPlayer = new JButton("Multi Player");
	btnMultiPlayer.setBounds(550, 650, 150, 50);

	btnAiVsAi = new JButton("Ai vs Ai");
	btnAiVsAi.setBounds(655, 650, 150, 50);

	String[] difficulty = {"Easy", "Medium", "Hard"};
	diff = new JComboBox(difficulty);
	diff.setBounds(365, 450, 100, 50);

	portFieldU = new JTextField("port number");
	portFieldU.setBounds(365, 350, 200, 50);

	portFieldAI = new JTextField("port number");
	portFieldAI.setBounds(365, 350, 200, 50);


	IPAddrAI = new JButton("Enter");
	IPAddrAI.setBounds(600, 250, 100, 50);

	IPAddrU = new JButton("Enter");
	IPAddrU.setBounds(600, 250, 100, 50);

	IPFieldU = new JTextField("IPv4");
	IPFieldU.setBounds(365, 250, 200, 50);

	IPFieldAI = new JTextField("IPv4");
	IPFieldAI.setBounds(365, 250, 200, 50);

	IP_PortAI = new JLabel("Enter an IP address and Port to connect to");
	IP_PortAI.setHorizontalAlignment(SwingConstants.CENTER);
	IP_PortAI.setLayout(null);
	IP_PortAI.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
	IP_PortAI.setForeground(Color.white);
	IP_PortAI.setBounds(335, 200, 350, 30);

	IP_PortU = new JLabel("Enter an IP address and Port to connect to");
	IP_PortU.setHorizontalAlignment(SwingConstants.CENTER);
	IP_PortU.setLayout(null);
	IP_PortU.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
	IP_PortU.setForeground(Color.white);
	IP_PortU.setBounds(335, 200, 350, 30);

	gameVersion = new JLabel("Will the AI or the User be playing?");
	gameVersion.setHorizontalAlignment(SwingConstants.CENTER);
	gameVersion.setLayout(null);
	gameVersion.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
	gameVersion.setForeground(Color.white);
	gameVersion.setBounds(310, 300, 350, 30);
	AiPlay = new JButton("AI Playing");
	UPlay = new JButton("User Playing");

	AiPlay.setBounds(335, 350, 100, 50);
	UPlay.setBounds(535, 350, 100, 50);

	BufferedImage img;
	try {
	  img = ImageIO.read(new File("./img/v2/bg.png"));
	  bg = new JLabel(new ImageIcon(img));
	  bgMulti = new JLabel(new ImageIcon(img));
	  bgAI = new JLabel(new ImageIcon(img));
	  bgU = new JLabel(new ImageIcon(img));

	} catch (IOException e1) {
	  e1.printStackTrace();
	}

	mainMenu = new JButton("Main Menu");
	mainMenu.setBounds(50, 50, 100, 50);

	bgU.add(IP_PortU);
	bgU.add(IPFieldU);
	bgU.add(IPAddrU);
	bgU.add(portFieldU);
	bgU.add(bgMulti);

	bgAI.add(IP_PortAI);
	bgAI.add(IPFieldAI);
	bgAI.add(IPAddrAI);
	bgAI.add(portFieldAI);
	bgAI.add(bgMulti);
	bgAI.add(diff);

	bgMulti.add(gameVersion);
	bgMulti.add(AiPlay);
	bgMulti.add(UPlay);
	//bgMulti.add(mainMenu);

	bg.add(lblChessGame);
	bg.add(lblChessGame2);
	bg.add(teamNames);

	bg.add(btnSinglePlayer);
	bg.add(btnMultiPlayer);
	//bg.add(btnAiVsAi);
	bg.add(mImg);
	menu.add(bg);

	btnSinglePlayer.addMouseListener(new MouseAdapter() {
	  @Override
	  public void mouseClicked(MouseEvent e) {
		frame.remove(menu);

		//Start BroadcastServer
		class RunBCS implements Runnable{
					@Override
					public void run() {
						String[] args = {"singlePlayer"};
						BroadcastServer.main(args);
					}
		}
		Thread bcsThread = new Thread(new RunBCS());
		bcsThread.start();

		game = new Game();
		frame.add(game);
		frame.revalidate();
		frame.repaint();
	  }
	});
	btnMultiPlayer.addMouseListener(new MouseAdapter() {
	  @Override
	  public void mouseClicked(MouseEvent e) {
		//frame.remove(menu);
		menu.remove(bg);
		menu.revalidate();
		menu.repaint();
		bgMulti.add(mainMenu);
		menu.add(bgMulti);



//                frame.add(game); // change to call with client/server
                // game.connectToServer();
		frame.revalidate();
		frame.repaint();
	  }
	});

	btnAiVsAi.addMouseListener(new MouseAdapter() {
	  @Override
	  public void mouseClicked(MouseEvent e) {
		frame.remove(menu);
		frame.add(game); // change to call AI - vs - AI
		frame.revalidate();
		frame.repaint();
	  }
	});

	mainMenu.addMouseListener(new MouseAdapter() {
	  @Override
	  public void mouseClicked(MouseEvent e) {
		menu.removeAll();
		menu.add(bg);
		menu.revalidate();
		menu.repaint();
	  }
	});

	IPAddrU.addMouseListener(new MouseAdapter() {
   	  public void mouseClicked(MouseEvent e) {
		game = new Game(IPFieldU.getText(), Integer.parseInt(portFieldU.getText()));
		frame.remove(menu);
		frame.add(game);
		frame.revalidate();
		frame.repaint();

	  }
	});

	IPAddrAI.addMouseListener(new MouseAdapter() {
	  public void mouseClicked(MouseEvent e) {
		int d = diff.getSelectedIndex() + 1;
		game = new Game(IPFieldAI.getText(), Integer.parseInt(portFieldAI.getText()), d);
		frame.remove(menu);
		frame.add(game);
		frame.revalidate();
		frame.repaint();

	  }
	});

	AiPlay.addMouseListener(new MouseAdapter() {
	  public void mouseClicked(MouseEvent e) {
		menu.remove(bgMulti);
		bgMulti.remove(mainMenu);
		bgAI.add(mainMenu);
		menu.add(bgAI);
		menu.revalidate();
		menu.repaint();
	  }
	});

	UPlay.addMouseListener(new MouseAdapter() {
	  public void mouseClicked(MouseEvent e) {
		menu.remove(bgMulti);
		bgMulti.remove(mainMenu);
		bgU.add(mainMenu);
		menu.add(bgU);
		menu.revalidate();
		menu.repaint();
	  }
    });

  }
}
