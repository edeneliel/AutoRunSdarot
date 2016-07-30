package eden.eliel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Eden on 7/30/2016.
 */
public class AddSeriesFrame extends JFrame {
    private SearchUrlSdarot _searchSdarot;
    private ArrayList<SearchSeriesBox> _resultSeries;
    private JPanel _searchPanel,_searchResult;
    private JTextField _inputField;
    private JButton _submitBtn;

    public AddSeriesFrame(){
        setLayout(new BoxLayout(getContentPane(),BoxLayout.PAGE_AXIS));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        setMaximumSize(new Dimension(400, 200));

        _searchSdarot = new SearchUrlSdarot();
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
        });

        getRootPane().setDefaultButton(_submitBtn);

        _searchPanel.add(_inputField);
        _searchPanel.add(Box.createRigidArea(new Dimension(15,0)));
        _searchPanel.add(_submitBtn);
    }
    private void setSearchResult(){
        _searchResult = new JPanel();
        _searchResult.setLayout(new GridLayout(0,2));

        addResultToArray();
    }
    private void addResultToArray(){
        _searchResult.removeAll();
        if (!_inputField.getText().equals("") && _inputField.getText().length() >= 3) {
            _resultSeries = _searchSdarot.SearchSeries(_inputField.getText());
            for (SearchSeriesBox seriesBox : _resultSeries) {
                _searchResult.add(new JLabel(seriesBox.getEngName()));
            }
        }
    }
}
