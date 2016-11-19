package eden.eliel.Forms;

import eden.eliel.Api.JsonManager;
import eden.eliel.Platforms.AutoAnimeTake;
import eden.eliel.Platforms.AutoSdarot;
import eden.eliel.Search.SearchSeriesBox;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Eden on 7/27/2016.
 */
public class Application extends JFrame {
    private JsonManager _jm;
    private AutoSdarot _autoSdarot;
    private AutoAnimeTake _autoAnimeTake;
    private JPanel _details, _buttons;
    private JLabel _seasonTag,_episodeTag;
    private JButton _watchBtn,_addBtn,_removeBtn,_editMAL;
    private JComboBox _comboBox;

    public Application(){
        setLayout(new BoxLayout(getContentPane(),BoxLayout.LINE_AXIS));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        _jm = new JsonManager("config.json");
        _autoSdarot = new AutoSdarot(_jm);
        _autoAnimeTake = new AutoAnimeTake(_jm);

        setTitle("AutoSdarot");
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setDetailsPanel();
        setButtonsPanel();
        updateSeries();

        _details.setAlignmentY(0f);
        add(_details);
        _buttons.setAlignmentY(0f);
        add(_buttons);

        pack();
    }

    public JsonManager getJsonManager() {
        return _jm;
    }
    public void addSeries(SearchSeriesBox seriesBox, String platform){
        _jm.setKeyBySeries(seriesBox.getEngName(),"Id",seriesBox.getId());
        _jm.setKeyBySeries(seriesBox.getEngName(),"Platform",platform);
    }
    public void setMalId(String Series,String MalId){
        _jm.setKeyBySeries(Series,"MAL",MalId);
    }
    public void updateSeries(){
        _comboBox.setModel(new DefaultComboBoxModel(_jm.getSeriesNames().toArray()));
        _comboBox.setSelectedIndex(0);

        _seasonTag.setText("Season: " + _autoSdarot.getKeyBySeries(_comboBox.getSelectedItem().toString(), "Season"));
        _episodeTag.setText("Episode: " + _autoSdarot.getKeyBySeries(_comboBox.getSelectedItem().toString(), "Episode"));
        if (_jm.getPlatformOfSeries(_comboBox.getSelectedItem().toString()).equals("animetake")) {
            _seasonTag.setText("MAL: " + _autoSdarot.getKeyBySeries(_comboBox.getSelectedItem().toString(), "MAL"));
            _editMAL.setVisible(true);
        }
        pack();
    }

    private void setComboBox(){
        _comboBox = new JComboBox(_jm.getSeriesNames().toArray());
        _comboBox.addActionListener(e -> {
            _editMAL.setVisible(false);
            _editMAL.setVisible(false);
            _seasonTag.setText("Season: " + _autoSdarot.getKeyBySeries(_comboBox.getSelectedItem().toString(), "Season"));
            _episodeTag.setText("Episode: " + _autoSdarot.getKeyBySeries(_comboBox.getSelectedItem().toString(), "Episode"));
            if (_jm.getPlatformOfSeries(_comboBox.getSelectedItem().toString()).equals("animetake")) {
                _seasonTag.setText("MAL: " + _autoSdarot.getKeyBySeries(_comboBox.getSelectedItem().toString(), "MAL"));
                _editMAL.setVisible(true);
            }
            pack();
        });
        AutoCompleteDecorator.decorate(_comboBox);
    }
    private void setWatchBtn(){
        _watchBtn = new JButton("Watch");
        _watchBtn.addActionListener(e -> {
            try {
                switch (_jm.getPlatformOfSeries(_comboBox.getSelectedItem().toString())){
                    case("sdarot"):
                        _autoSdarot.execute(_comboBox.getSelectedItem().toString());
                        break;
                    case("animetake"):
                        _autoAnimeTake.execute(_comboBox.getSelectedItem().toString());
                        break;
                }

            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        });
    }
    private void setMalBtn(){
        _editMAL = new JButton("Edit MAL");
        _editMAL.addActionListener(e -> {
            MalEditFrame malEditFrame = new MalEditFrame(this,_comboBox.getSelectedItem().toString());
            malEditFrame.setVisible(true);
        });
    }
    private void setAddBtn(){
        _addBtn = new JButton("Add Series");
        _addBtn.addActionListener(e -> {
            AddSeriesFrame addSeriesFrame = new AddSeriesFrame(this);
            addSeriesFrame.setVisible(true);
        });
    }
    private void setRemoveBtn(){
        _removeBtn = new JButton("Remove Series");
        _removeBtn.addActionListener(e -> {
            int selectedOption = JOptionPane.showConfirmDialog(null,
                    "Are you sure you want to remove \"" + _comboBox.getSelectedItem().toString() + "\"?",
                    "Conformation",
                    JOptionPane.YES_NO_OPTION);
            if (selectedOption == JOptionPane.YES_OPTION) {
                _autoSdarot.removeSeries(_comboBox.getSelectedItem().toString());
                updateSeries();
            }
        });
    }
    private void setDetailsPanel(){
        _details = new JPanel();
        _details.setLayout(new BoxLayout(_details,BoxLayout.PAGE_AXIS));
        setComboBox();
        _seasonTag = new JLabel();
        _episodeTag = new JLabel();

        // ComBox has a promblem with alignment.
        _comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        _details.add(_comboBox);
        _details.add(Box.createRigidArea(new Dimension(0,80)));
        _details.add(_seasonTag);
        _details.add(_episodeTag);
    }
    private void setButtonsPanel() {
        _buttons = new JPanel();
        _buttons.setLayout(new BoxLayout(_buttons,BoxLayout.PAGE_AXIS));
        _buttons.setAlignmentX(RIGHT_ALIGNMENT);

        _buttons.setBorder(BorderFactory.createEmptyBorder(0,10,0,0));

        setWatchBtn();
        setAddBtn();
        setRemoveBtn();
        setMalBtn();


        _watchBtn.setAlignmentX(Component.RIGHT_ALIGNMENT);
        _buttons.add(_watchBtn);
        _buttons.add(Box.createRigidArea(new Dimension(0,10)));
        _addBtn.setAlignmentX(Component.RIGHT_ALIGNMENT);
        _buttons.add(_addBtn);
        _buttons.add(Box.createRigidArea(new Dimension(0,10)));
        _removeBtn.setAlignmentX(Component.RIGHT_ALIGNMENT);
        _buttons.add(_removeBtn);
        _buttons.add(Box.createRigidArea(new Dimension(0,10)));
        _editMAL.setAlignmentX(Component.RIGHT_ALIGNMENT);
        _buttons.add(_editMAL);
    }
}