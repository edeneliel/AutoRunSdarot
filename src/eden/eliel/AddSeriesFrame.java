package eden.eliel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

/**
 * Created by Eden on 7/30/2016.
 */
public class AddSeriesFrame extends JFrame {
    private Application _application;
    private SearchUrlSdarot _searchSdarot;
    private ArrayList<SearchSeriesBox> _resultSeries;
    private JPanel _searchPanel,_searchResult;
    private JTextField _inputField;
    private JButton _submitBtn;

    public AddSeriesFrame(Application app){
        _application = app;

        addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e) {
                app.updateSeries();
            }
        });

        setLayout(new BoxLayout(getContentPane(),BoxLayout.PAGE_AXIS));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

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
            if (getHeight()>350)
                setSize(getWidth(),350);

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
                JButton btn = new JButton(seriesBox.getEngName());
                btn.addActionListener(e -> {
                    _application.addSeries(seriesBox);
                    JOptionPane.showMessageDialog(this, "Series has been added");
                });
                _searchResult.add(btn);
            }
        }
    }
}
