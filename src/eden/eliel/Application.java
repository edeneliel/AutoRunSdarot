package eden.eliel;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Eden on 7/27/2016.
 */
public class Application extends JFrame {
    AutoSdarot _autoSdarot;
    JPanel _details;
    JLabel _seasonTag;
    JLabel _episodeTag;
    JButton _watchBtn;
    JComboBox _comboBox;

    public Application(){
        setLayout(new FlowLayout());

        _autoSdarot = new AutoSdarot();

        setTitle("AutoSdarot");
        setVisible(true);
        setSize(250, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setComboBox();
        setWatchBtn();
        setDetailsPanel();

        add(_comboBox);
        add(_details);
        add(_watchBtn);

        pack();
    }

    private void setComboBox(){
        _comboBox = new JComboBox(_autoSdarot.getAllSeries());
        _comboBox.addActionListener(e -> {
            _seasonTag.setText("Season: " + _autoSdarot.getKeyBySeries(_comboBox.getSelectedItem().toString(), "Season"));
            _episodeTag.setText("Episode: " + _autoSdarot.getKeyBySeries(_comboBox.getSelectedItem().toString(), "Episode"));
            pack();
        });
        AutoCompleteDecorator.decorate(_comboBox);
    }
    private void setWatchBtn(){
        _watchBtn = new JButton("Watch");
        _watchBtn.addActionListener(e -> {
            try {
                _autoSdarot.execute(_comboBox.getSelectedItem().toString());
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        });
    }
    private void setDetailsPanel(){
        _details = new JPanel();
        _seasonTag = new JLabel("Season: "+_autoSdarot.getKeyBySeries(_comboBox.getSelectedItem().toString(),"Season"));
        _episodeTag = new JLabel("Episode: "+_autoSdarot.getKeyBySeries(_comboBox.getSelectedItem().toString(),"Episode"));

        _details.add(_seasonTag);
        _details.add(_episodeTag);
    }
}
