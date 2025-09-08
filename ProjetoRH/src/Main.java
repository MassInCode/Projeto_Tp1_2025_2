import java.awt.*;
import javax.swing.*;//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        JFrame frame = new JFrame("RHidle");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 300);
        frame.setLayout(null);

        // Criar checkbox
        JCheckBox chkFullscreen = new JCheckBox("Ativar Tela Cheia");
        chkFullscreen.setBounds(150, 100, 200, 30);

        // Ação do checkbox
        chkFullscreen.addActionListener(e -> {
            GraphicsDevice device = GraphicsEnvironment
                    .getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice();

            if (chkFullscreen.isSelected()) {
                // Ativar tela cheia
                frame.dispose(); // precisa fechar para mudar propriedades
                frame.setUndecorated(true);
                frame.setResizable(false);
                device.setFullScreenWindow(frame);
            } else {
                // Desativar tela cheia
                frame.dispose();
                frame.setUndecorated(false);
                frame.setResizable(true);
                device.setFullScreenWindow(null);
                frame.setVisible(true);
            }
        });

        // Carregar o ícone
        ImageIcon icon = new ImageIcon("src/imagens/icon.png");
        frame.setIconImage(icon.getImage()); // Define o ícone da janela

        frame.setVisible(true);

        // Adicionar checkbox à janela
        frame.add(chkFullscreen);

        // Mostrar janela
        frame.setVisible(true);

    }
}