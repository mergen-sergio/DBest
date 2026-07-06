package gui.frames;

import files.FileUtils;
import gui.components.IconButton;
import gui.theme.Theme;
import gui.theme.Themed;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;

public class ProgressFrame extends JDialog {

    private final JProgressBar progressBar = new JProgressBar(0, 100);
    private final JButton btnLoadTuples = new IconButton("Carregar tuplas", null, IconButton.Variant.PRIMARY);

    public ProgressFrame() {
        super((Window) null, "Progresso");
        setModal(true);
        try {
            this.setIconImage(new ImageIcon(String.valueOf(FileUtils.getDBestLogo())).getImage());
        } catch (Exception ignored) {
        }

        JPanel root = new JPanel(new FlowLayout(FlowLayout.CENTER, Theme.SPACING_MEDIUM, Theme.SPACING_MEDIUM));
        root.setBorder(javax.swing.BorderFactory.createEmptyBorder(
                Theme.SPACING_LARGE, Theme.SPACING_LARGE, Theme.SPACING_LARGE, Theme.SPACING_LARGE));
        Themed.background(root, () -> Theme.BACKGROUND);

        progressBar.setStringPainted(true);
        root.add(progressBar);
        root.add(btnLoadTuples);
        setContentPane(root);
    }

    public void setBtnLoadTuplesListener(ActionListener event) {
        btnLoadTuples.addActionListener(e -> btnLoadTuples.setEnabled(false));
        btnLoadTuples.addActionListener(event);
    }

    public void setWindowListener(WindowAdapter event) {
        addWindowListener(event);
    }

    public void updateLoadBar(int percentage, String text) {
        progressBar.setValue(percentage);
        progressBar.setString(text);
    }

    public void adjust() {
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
