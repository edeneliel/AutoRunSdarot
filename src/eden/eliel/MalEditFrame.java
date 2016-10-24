package eden.eliel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by Eden on 10/24/2016.
 */
public class MalEditFrame extends JFrame {
    private Application _application;
    private JTextField _input;
    private JButton _submitBtn;
    private String _currentSeries;

    public MalEditFrame(Application app, String currentSeries) {
        _application = app;
        _currentSeries = currentSeries;

        addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e) {
                _application.updateSeries();
            }
        });

        setLayout(new BoxLayout(getContentPane(),BoxLayout.PAGE_AXIS));

        getRootPane().setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        _input = new JTextField();
        _input.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(_input);

        _submitBtn = new JButton("Submit");
        getRootPane().setDefaultButton(_submitBtn);
        _submitBtn.addActionListener(e -> {
            _application.setMalId(_currentSeries, _input.getText());
            dispose();
            _application.updateSeries();
        });
        _submitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(_submitBtn);

        pack();
    }
}
