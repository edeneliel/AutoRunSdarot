package eden.eliel.Forms;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by Eden on 10/24/2016.
 */
public class MalEditFrame extends JFrame {
    private Application application;
    private JTextField input;
    private JButton submitBtn;
    private String currentSeries;

    public MalEditFrame(Application app, String currentSeries) {
        application = app;
        this.currentSeries = currentSeries;

        addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e) {
                application.updateSeries();
            }
        });

        setLayout(new BoxLayout(getContentPane(),BoxLayout.PAGE_AXIS));

        getRootPane().setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        input = new JTextField();
        input.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(input);

        submitBtn = new JButton("Submit");
        getRootPane().setDefaultButton(submitBtn);
        submitBtn.addActionListener(e -> {
            application.setMalId(this.currentSeries, input.getText());
            dispose();
            application.updateSeries();
        });
        submitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(submitBtn);

        pack();
    }
}
