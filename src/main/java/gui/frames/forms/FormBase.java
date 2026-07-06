package gui.frames.forms;

import controllers.ConstantController;
import files.FileUtils;
import gui.components.IconButton;
import gui.theme.Theme;
import gui.theme.Themed;

import javax.swing.*;
import java.awt.*;


public abstract class FormBase extends JDialog {

    protected final JPanel contentPanel;

    protected final JButton btnCancel;

    protected final JButton btnReady;

    protected FormBase(Window window) {
        super(window);

        this.contentPanel = new JPanel(new BorderLayout(Theme.SPACING_DEFAULT, Theme.SPACING_DEFAULT));
        this.contentPanel.setBorder(BorderFactory.createEmptyBorder(
                Theme.SPACING_LARGE, Theme.SPACING_LARGE,
                Theme.SPACING_LARGE, Theme.SPACING_LARGE));
        Themed.background(this.contentPanel, () -> Theme.BACKGROUND);

        this.btnCancel = new IconButton(
                ConstantController.getString("formBase.cancelButton"),
                null, IconButton.Variant.DEFAULT);
        this.btnReady = new IconButton(
                ConstantController.getString("formBase.readyButton"),
                null, IconButton.Variant.PRIMARY);

        try {
            this.setIconImage(new ImageIcon(String.valueOf(FileUtils.getDBestLogo())).getImage());
        } catch (Exception ignored) {
        }

        this.initBottomButtons();
    }

    protected void initBottomButtons() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, Theme.SPACING_SMALL, 0));
        Themed.background(bottomPanel, () -> Theme.BACKGROUND);
        bottomPanel.add(this.btnCancel);
        bottomPanel.add(this.btnReady);

        this.contentPanel.add(bottomPanel, BorderLayout.SOUTH);
        this.setContentPane(this.contentPanel);
    }
}
