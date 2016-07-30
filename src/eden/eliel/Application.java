package eden.eliel;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Eden on 7/27/2016.
 */
public class Application extends JFrame {
    private AutoSdarot _autoSdarot;
    private JPanel _details, _buttons;
    private JLabel _seasonTag,_episodeTag;
    private JButton _watchBtn,_addBtn;
    private JComboBox _comboBox;

    public Application(){
        setLayout(new BoxLayout(getContentPane(),BoxLayout.LINE_AXIS));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        _autoSdarot = new AutoSdarot();

        setTitle("AutoSdarot");
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setDetailsPanel();
        setButtonsPanel();

        _details.setAlignmentY(0f);
        add(_details);
        _buttons.setAlignmentY(0f);
        add(_buttons);

        pack();
    }

    public void addSeries(SearchSeriesBox seriesBox){
        _autoSdarot.addSeries(seriesBox.getEngName(),seriesBox.getId());
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
    private void setAddBtn(){
        _addBtn = new JButton("Add Series");
        _addBtn.addActionListener(e -> {
            AddSeriesFrame addSeriesFrame = new AddSeriesFrame(this);
            addSeriesFrame.setVisible(true);
        });
    }
    private void setDetailsPanel(){
        _details = new JPanel();
        _details.setLayout(new BoxLayout(_details,BoxLayout.PAGE_AXIS));
        setComboBox();
        _seasonTag = new JLabel("Season");
        _episodeTag = new JLabel("Episode");
        _seasonTag = new JLabel("Season: "+_autoSdarot.getKeyBySeries(_comboBox.getSelectedItem().toString(),"Season"));
        _episodeTag = new JLabel("Episode: "+_autoSdarot.getKeyBySeries(_comboBox.getSelectedItem().toString(),"Episode"));

        // ComBox has a promblem with alignment.
        _comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        _details.add(_comboBox);
        _details.add(Box.createRigidArea(new Dimension(0,15)));
        _details.add(_seasonTag);
        _details.add(_episodeTag);
    }
    private void setButtonsPanel() {
        _buttons = new JPanel();
        _buttons.setLayout(new BoxLayout(_buttons,BoxLayout.PAGE_AXIS));
        _buttons.setAlignmentX(RIGHT_ALIGNMENT);

        _buttons.setBorder(BorderFactory.createEmptyBorder(0,10,0,0));

        setAddBtn();
        setWatchBtn();

        _watchBtn.setAlignmentX(Component.RIGHT_ALIGNMENT);
        _buttons.add(_watchBtn);
        _addBtn.setAlignmentX(Component.RIGHT_ALIGNMENT);
        _buttons.add(_addBtn);
    }
}
