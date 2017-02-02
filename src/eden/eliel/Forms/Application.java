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
    private final String TITLE_NAME = "AutoSdarot";
    private final String JSON_PATH = "config.json";

    private JsonManager jsonManager;
    private AutoSdarot autoSdarot;
    private AutoAnimeTake autoAnimeTake;
    private JPanel details;
    private JPanel buttons;
    private JLabel seasonTag;
    private JLabel episodeTag;
    private JButton watchBtn;
    private JButton addBtn;
    private JButton removeBtn;
    private JButton editMalBtn;
    private JComboBox combobox;

    public Application(){
        setLayout(new BoxLayout(getContentPane(),BoxLayout.LINE_AXIS));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        jsonManager = new JsonManager(JSON_PATH);
        autoSdarot = new AutoSdarot(jsonManager);
        autoAnimeTake = new AutoAnimeTake(jsonManager);

        setTitle(TITLE_NAME);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setDetailsPanel();
        setButtonsPanel();
        updateSeries();

        details.setAlignmentY(0f);
        add(details);
        buttons.setAlignmentY(0f);
        add(buttons);

        pack();
    }

    public JsonManager getJsonManager() {
        return jsonManager;
    }
    public void addSeries(SearchSeriesBox seriesBox, String platform){
        jsonManager.setKeyBySeries(seriesBox.getEngName(),"Id",seriesBox.getId());
        jsonManager.setKeyBySeries(seriesBox.getEngName(),"Platform",platform);
    }
    public void setMalId(String Series,String MalId){
        jsonManager.setKeyBySeries(Series,"MAL",MalId);
    }
    public void updateSeries(){
        combobox.setModel(new DefaultComboBoxModel(jsonManager.getSeriesNames().toArray()));
        combobox.setSelectedIndex(0);

        seasonTag.setText("Season: " + autoSdarot.getKeyBySeries(combobox.getSelectedItem().toString(), "Season"));
        episodeTag.setText("Episode: " + autoSdarot.getKeyBySeries(combobox.getSelectedItem().toString(), "Episode"));
        if (jsonManager.getPlatformOfSeries(combobox.getSelectedItem().toString()).equals("animetake")) {
            seasonTag.setText("MAL: " + autoSdarot.getKeyBySeries(combobox.getSelectedItem().toString(), "MAL"));
            editMalBtn.setVisible(true);
        }
        pack();
    }

    private void setComboBox(){
        combobox = new JComboBox(jsonManager.getSeriesNames().toArray());
        combobox.addActionListener(e -> {
            editMalBtn.setVisible(false);
            editMalBtn.setVisible(false);
            seasonTag.setText("Season: " + autoSdarot.getKeyBySeries(combobox.getSelectedItem().toString(), "Season"));
            episodeTag.setText("Episode: " + autoSdarot.getKeyBySeries(combobox.getSelectedItem().toString(), "Episode"));
            if (jsonManager.getPlatformOfSeries(combobox.getSelectedItem().toString()).equals("animetake")) {
                seasonTag.setText("MAL: " + autoSdarot.getKeyBySeries(combobox.getSelectedItem().toString(), "MAL"));
                editMalBtn.setVisible(true);
            }
            pack();
        });
        AutoCompleteDecorator.decorate(combobox);
    }
    private void setWatchBtn(){
        watchBtn = new JButton("Watch");
        watchBtn.addActionListener(e -> {
            try {
                switch (jsonManager.getPlatformOfSeries(combobox.getSelectedItem().toString())){
                    case("sdarot"):
                        autoSdarot.execute(combobox.getSelectedItem().toString());
                        break;
                    case("animetake"):
                        autoAnimeTake.execute(combobox.getSelectedItem().toString());
                        break;
                }
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        });
    }
    private void setMalBtn(){
        editMalBtn = new JButton("Edit MAL");
        editMalBtn.addActionListener(e -> {
            MalEditFrame malEditFrame = new MalEditFrame(this, combobox.getSelectedItem().toString());
            malEditFrame.setVisible(true);
        });
    }
    private void setAddBtn(){
        addBtn = new JButton("Add Series");
        addBtn.addActionListener(e -> {
            AddSeriesFrame addSeriesFrame = new AddSeriesFrame(this);
            addSeriesFrame.setVisible(true);
        });
    }
    private void setRemoveBtn(){
        removeBtn = new JButton("Remove Series");
        removeBtn.addActionListener(e -> {
            int selectedOption = JOptionPane.showConfirmDialog(null,
                    "Are you sure you want to remove \"" + combobox.getSelectedItem().toString() + "\"?",
                    "Conformation",
                    JOptionPane.YES_NO_OPTION);
            if (selectedOption == JOptionPane.YES_OPTION) {
                removeSeries(combobox.getSelectedItem().toString());
                updateSeries();
            }
        });
    }
    private void setDetailsPanel(){
        details = new JPanel();
        details.setLayout(new BoxLayout(details,BoxLayout.PAGE_AXIS));
        setComboBox();
        seasonTag = new JLabel();
        episodeTag = new JLabel();

        // ComBox has a promblem with alignment.
        combobox.setAlignmentX(Component.LEFT_ALIGNMENT);
        details.add(combobox);
        details.add(Box.createRigidArea(new Dimension(0,80)));
        details.add(seasonTag);
        details.add(episodeTag);
    }
    private void setButtonsPanel() {
        buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons,BoxLayout.PAGE_AXIS));
        buttons.setAlignmentX(RIGHT_ALIGNMENT);

        buttons.setBorder(BorderFactory.createEmptyBorder(0,10,0,0));

        setWatchBtn();
        setAddBtn();
        setRemoveBtn();
        setMalBtn();


        watchBtn.setAlignmentX(Component.RIGHT_ALIGNMENT);
        buttons.add(watchBtn);
        buttons.add(Box.createRigidArea(new Dimension(0,10)));
        addBtn.setAlignmentX(Component.RIGHT_ALIGNMENT);
        buttons.add(addBtn);
        buttons.add(Box.createRigidArea(new Dimension(0,10)));
        removeBtn.setAlignmentX(Component.RIGHT_ALIGNMENT);
        buttons.add(removeBtn);
        buttons.add(Box.createRigidArea(new Dimension(0,10)));
        editMalBtn.setAlignmentX(Component.RIGHT_ALIGNMENT);
        buttons.add(editMalBtn);
    }
    private boolean removeSeries(String series) {
        return jsonManager.removeSeries(series);
    }
}
