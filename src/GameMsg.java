import javax.swing.JPanel;
import javax.swing.JLabel;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameMsg extends JPanel {
	private static final long serialVersionUID = -6844209919657245216L;
	private String msg;
	public GameMsg(String message) {
		this.msg = message;
		setLayout(null);
		this.setBounds(0, 0, 300, 100);
		JLabel bckgrdLbl = new JLabel();
		try {
			BufferedImage img = ImageIO.read(new File("./img/v2/bg.png"));
			bckgrdLbl.setIcon(new ImageIcon(img));
		} catch (IOException e) {
			e.printStackTrace();
		}
		bckgrdLbl.setBounds(0,0, 300, 100);


		JLabel msgLabel = new JLabel(msg);
		msgLabel.setVerticalAlignment(SwingConstants.TOP);
		msgLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 40));
		msgLabel.setForeground(Color.WHITE);
		msgLabel.setHorizontalAlignment(SwingConstants.CENTER);
		msgLabel.setLayout(null);
		msgLabel.setBounds(0,0,300,100);

		JButton btnQuit = new JButton("Quit");
		btnQuit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				System.exit(0);
			}
		});
		btnQuit.setBounds(87, 65, 117, 29);
		bckgrdLbl.add(btnQuit);
		bckgrdLbl.add(msgLabel);
		add(bckgrdLbl);

	}

}