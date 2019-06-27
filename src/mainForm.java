//import com.oracle.tools.packager.Log;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class mainForm extends JFrame {
    private JPanel panel1;

    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;
    private JFormattedTextField textField4;
    private JSlider slider1;

    private JButton dodajButton;
    private JButton wyczyscButton;

    private JTable table1;
    private JButton edytujButton;
    private JScrollPane scrollPanel;
    private JButton usuńButton;

    private ScheduleSQL scheduleSQL;

    public mainForm() {

        scrollPanel.setViewportView(table1);
        Color color = UIManager.getColor("Table.gridColor");
        MatteBorder border = new MatteBorder(1, 1, 1, 1, color);
        table1.setBorder(border);
        table1.setDefaultEditor(Object.class, null);

        slider1.setMinorTickSpacing(1);
        slider1.setMajorTickSpacing(1);
        slider1.setSnapToTicks(true);

        dodajButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertScheduleSQL();
                JOptionPane.showMessageDialog(null, "Dodano nowy wpis.");
                clearField();
            }
        });
        wyczyscButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearField();
            }
        });
        edytujButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        usuńButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String pole = table1.getValueAt(table1.getSelectedRow(), 0).toString();
                int idpola = Integer.parseInt(pole);
                int dialogResult = JOptionPane.showConfirmDialog (null, "Czy napewno chcesz usunąc pole o ID:"+idpola+"\n","Warning",JOptionPane.YES_NO_OPTION);
                if(dialogResult == JOptionPane.YES_OPTION){
                    deleteScheduleSQL(idpola);
                }
            }
        });
        textField4.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { fix(); }
            public void removeUpdate(DocumentEvent e) { fix(); }
            public void insertUpdate(DocumentEvent e) { fix(); }
            public void fix() {
                if (textField4.getText().length() > 0){

                }
            }
        });
        table1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                String pole = table1.getValueAt(table1.getSelectedRow(), 0).toString();
                int ipole = Integer.parseInt(pole);
                System.out.println(pole);
            }
        });

        scheduleSQL = new ScheduleSQL();
        scheduleSQL.connect();
        loadScheduleSQL();
    }

    private void clearField() {
        textField1.setText("");
        textField2.setText("");
        textField3.setText("");
        textField4.setText("");
        slider1.setValue(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("mainForm");
                frame.setContentPane(new mainForm().panel1);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setSize(1440, 900);
                frame.setVisible(true);
            }
        });
    }

    private void loadScheduleSQL()
    {
        scheduleSQL.selectAll();
        ResultSet result = scheduleSQL.selectAllResult();
        if(result != null) {
            try {
                table1.setModel(buildTableModel(result));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void insertScheduleSQL() {
        String title = textField1.getText();
        String desc = textField2.getText();
        String cat = textField3.getText();
        int prio = slider1.getValue();
        String dead = textField4.getText();
        Date date = null;
        try {
            date = new SimpleDateFormat("dd/MM/yyyy hh:mm").parse(dead);
        } catch (ParseException e) {
            date = new Date("dd/MM/yyyy hh:mm");
            e.printStackTrace();
        }
        Schedule schedule = new Schedule(-1, title, desc, cat, prio, date);
        int newid = scheduleSQL.insert(schedule);
        DefaultTableModel model = (DefaultTableModel) table1.getModel();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        model.addRow(new Object[]{newid, title, desc, cat, dateFormat.format(date), prio});

    }

    private void deleteScheduleSQL(int idpola) {
        if(idpola > 0 && table1.getSelectedRow() >= 0) {
            try {
                scheduleSQL.delete(idpola);
                DefaultTableModel model = (DefaultTableModel) table1.getModel();
                model.removeRow(table1.getSelectedRow());
            } catch(ArrayIndexOutOfBoundsException e) {

            }
        }
    }


    public static DefaultTableModel buildTableModel(ResultSet rs)
            throws SQLException {

        ResultSetMetaData metaData = rs.getMetaData();

        Vector<String> columnNames = new Vector<String>();
        int columnCount = metaData.getColumnCount();
        for (int column = 1; column <= columnCount; column++) {
            String columnSQL = metaData.getColumnName(column);
            columnNames.add(columnSQL.substring(0, 1).toUpperCase() + columnSQL.substring(1));
        }

        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        while (rs.next()) {
            Vector<Object> vector = new Vector<Object>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                vector.add(rs.getObject(columnIndex));
            }
            data.add(vector);
        }

        return new DefaultTableModel(data, columnNames);
    }
}
