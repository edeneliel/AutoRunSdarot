package eden.eliel;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Created by Eden on 7/30/2016.
 */
public class AddSeriesFrame extends JFrame {
    private SearchUrlSdarot _searchSdarot;
    private ArrayList<SearchSeriesBox> _resultSeries;
    private JTextField _inputField;
    private JLabel _allSeriesLabel;
    private JButton _submitBtn;

    public AddSeriesFrame(){
        setLayout(new BoxLayout(getContentPane(),BoxLayout.PAGE_AXIS));

        _searchSdarot = new SearchUrlSdarot();

        _allSeriesLabel = new JLabel();
        _inputField = new JTextField();
        _submitBtn = new JButton("Submit");
        _submitBtn.addActionListener(e -> {
            String a = "";
            _resultSeries = _searchSdarot.SearchSeries(_inputField.getText());
            for (SearchSeriesBox t:_resultSeries){
                a += t.getEngName()+",";
            }
            System.out.println(a);
            _allSeriesLabel.setText(a);
            pack();
        });

        getRootPane().setDefaultButton(_submitBtn);
        add(_inputField);
        add(_submitBtn);
        add(_allSeriesLabel);

        pack();
    }
}
