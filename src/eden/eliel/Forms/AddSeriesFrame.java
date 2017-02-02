package eden.eliel.Forms;

import eden.eliel.Api.JsonManager;
import eden.eliel.Search.SearchAnimeTake;
import eden.eliel.Search.SearchSeriesBox;
import eden.eliel.Search.SearchUrlSdarot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

/**
 * Created by Eden on 7/30/2016.
 */
public class AddSeriesFrame extends JFrame {
    private Application application;
    private JsonManager jsonManager;
    private SearchUrlSdarot searchUrlSdarot;
    private SearchAnimeTake searchAnimeTake;
    private ArrayList<SearchSeriesBox> resultSeries;
    private JPanel searchPanel;
    private JPanel searchResult;
    private JComboBox platfrom;
    private JTextField inputField;
    private JButton submitBtn;

    public AddSeriesFrame(Application application){
        this.application = application;
        jsonManager = this.application.getJsonManager();

        addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e) {
                AddSeriesFrame.this.application.updateSeries();
            }
        });

        setLayout(new BoxLayout(getContentPane(),BoxLayout.PAGE_AXIS));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        searchUrlSdarot = new SearchUrlSdarot();
        searchAnimeTake = new SearchAnimeTake();
        resultSeries = new ArrayList<>();

        setSearchPanel();
        setSearchResult();

        add(searchPanel);
        add(new JScrollPane(searchResult));

        pack();
    }

    private void setSearchPanel(){
        searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel,BoxLayout.X_AXIS));

        inputField = new JTextField(30);
        submitBtn = new JButton("Submit");
        submitBtn.addActionListener(e -> {
            addResultToArray();
            pack();
            if (getHeight()>350)
                setSize(getWidth(),350);

        });

        getRootPane().setDefaultButton(submitBtn);
        setComboBoxPlatform();

        searchPanel.add(inputField);
        searchPanel.add(platfrom);
        searchPanel.add(Box.createRigidArea(new Dimension(15,0)));
        searchPanel.add(submitBtn);
    }
    private void setSearchResult(){
        searchResult = new JPanel();
        searchResult.setLayout(new GridLayout(0,2));
    }
    private void addResultToArray(){
        searchResult.removeAll();

        switch (platfrom.getSelectedItem().toString()) {
            case ("AnimeTake"):
                resultSeries = searchAnimeTake.SearchSeries(inputField.getText());
                break;
            case ("Sdarot"):
                if (!inputField.getText().equals("") && inputField.getText().length() >= 3) {
                    resultSeries = searchUrlSdarot.SearchSeries(inputField.getText());
                }
                else{
                    resultSeries.clear();
                }
        }

        for (SearchSeriesBox seriesBox : resultSeries) {
            JButton btn = new JButton(seriesBox.getEngName());
            btn.addActionListener(e -> {
                jsonManager.setKeyBySeries(seriesBox.getEngName(),"Id",seriesBox.getId());
                jsonManager.setKeyBySeries(seriesBox.getEngName(),"Platform", platfrom.getSelectedItem().toString().toLowerCase());
                if (platfrom.getSelectedItem().toString().equals("AnimeTake") &&
                        !searchAnimeTake.getAnimeWatchId(seriesBox.getId()).equals(seriesBox.getId())) {
                    jsonManager.setKeyBySeries(seriesBox.getEngName(),"WatchId", searchAnimeTake.getAnimeWatchId(seriesBox.getId()));
                }

                JOptionPane.showMessageDialog(this, "Series has been added");
            });
            searchResult.add(btn);
        }
    }
    private void setComboBoxPlatform(){
        platfrom = new JComboBox(new String [] {"AnimeTake","Sdarot"});
    }
}
