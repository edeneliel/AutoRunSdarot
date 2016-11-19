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
    private Application _application;
    private JsonManager _jm;
    private SearchUrlSdarot _searchSdarot;
    private SearchAnimeTake _searchAnimeTake;
    private ArrayList<SearchSeriesBox> _resultSeries;
    private JPanel _searchPanel,_searchResult;
    private JComboBox _platfrom;
    private JTextField _inputField;
    private JButton _submitBtn;

    public AddSeriesFrame(Application application){
        _application = application;
        _jm = _application.getJsonManager();

        addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e) {
                _application.updateSeries();
            }
        });

        setLayout(new BoxLayout(getContentPane(),BoxLayout.PAGE_AXIS));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        _searchSdarot = new SearchUrlSdarot();
        _searchAnimeTake = new SearchAnimeTake();
        _resultSeries = new ArrayList<>();

        setSearchPanel();
        setSearchResult();

        add(_searchPanel);
        add(new JScrollPane(_searchResult));

        pack();
    }

    private void setSearchPanel(){
        _searchPanel = new JPanel();
        _searchPanel.setLayout(new BoxLayout(_searchPanel,BoxLayout.X_AXIS));

        _inputField = new JTextField(30);
        _submitBtn = new JButton("Submit");
        _submitBtn.addActionListener(e -> {
            addResultToArray();
            pack();
            if (getHeight()>350)
                setSize(getWidth(),350);

        });

        getRootPane().setDefaultButton(_submitBtn);
        setComboBoxPlatform();

        _searchPanel.add(_inputField);
        _searchPanel.add(_platfrom);
        _searchPanel.add(Box.createRigidArea(new Dimension(15,0)));
        _searchPanel.add(_submitBtn);
    }
    private void setSearchResult(){
        _searchResult = new JPanel();
        _searchResult.setLayout(new GridLayout(0,2));
    }
    private void addResultToArray(){
        _searchResult.removeAll();

        switch (_platfrom.getSelectedItem().toString()) {
            case ("AnimeTake"):
                _resultSeries = _searchAnimeTake.SearchSeries(_inputField.getText());
                break;
            case ("Sdarot"):
                if (!_inputField.getText().equals("") && _inputField.getText().length() >= 3) {
                    _resultSeries = _searchSdarot.SearchSeries(_inputField.getText());
                }
                else{
                    _resultSeries.clear();
                }
        }

        for (SearchSeriesBox seriesBox : _resultSeries) {
            JButton btn = new JButton(seriesBox.getEngName());
            btn.addActionListener(e -> {
                _jm.setKeyBySeries(seriesBox.getEngName(),"Id",seriesBox.getId());
                _jm.setKeyBySeries(seriesBox.getEngName(),"Platform",_platfrom.getSelectedItem().toString().toLowerCase());
                if (_platfrom.getSelectedItem().toString().equals("AnimeTake") &&
                        !_searchAnimeTake.getAnimeWatchId(seriesBox.getId()).equals(seriesBox.getId())) {
                    _jm.setKeyBySeries(seriesBox.getEngName(),"WatchId",_searchAnimeTake.getAnimeWatchId(seriesBox.getId()));
                }

                JOptionPane.showMessageDialog(this, "Series has been added");
            });
            _searchResult.add(btn);
        }
    }
    private void setComboBoxPlatform(){
        _platfrom = new JComboBox(new String [] {"AnimeTake","Sdarot"});
    }
}
