package SwingComponents;

import StorageEditors.CellCoordinates;
import StorageEditors.CellInfo;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class EditPanel extends JPanel {

    CellCoordinates cellCoordinates;
    CellInfo currentCellInfo;

    JLabel currentPositionLabel = new JLabel("Не считано");
    JLabel currentDepthLabel = new JLabel("Не считано");
    JLabel currentNumberLabel = new JLabel("Не считано");
    JLabel currentHeightLabel = new JLabel("Не считано");
    JLabel currentMassLabel = new JLabel("Не считано");
    JTextField newPositionField = new LimitedTextField("0", 5, LimitedTextField.ONLY_NUMBER);
    JTextField newDepthField = new LimitedTextField("0", 5, LimitedTextField.ONLY_NUMBER);
    JTextField newNumberField = new LimitedTextField("0", 20, LimitedTextField.ONLY_ASCII);
    JTextField newHeightField = new LimitedTextField("0", 5, LimitedTextField.ONLY_NUMBER);
    JTextField newMassField = new LimitedTextField("0", 10, LimitedTextField.ONLY_NUMBER);
    JButton copyValuesButton = new JButton("Скопировать поля =>");
    JButton clearAllButton = new JButton("Обнулить поля (=> 0)");

    Dialogs dialog = new Dialogs();
    public EditPanel(){
        setLayout(new GridLayout(7, 3, 5, 5));
        Dimension mid = new Dimension(155, 20);
        Border border = BorderFactory.createLineBorder(Color.BLACK, 1);

        currentPositionLabel.setBorder(border);
        currentDepthLabel.setBorder(border);
        currentNumberLabel.setBorder(border);
        currentHeightLabel.setBorder(border);
        currentMassLabel.setBorder(border);
        currentPositionLabel.setPreferredSize(mid);
        currentDepthLabel.setPreferredSize(mid);
        currentNumberLabel.setPreferredSize(mid);
        currentHeightLabel.setPreferredSize(mid);
        currentMassLabel.setPreferredSize(mid);

        newPositionField.setPreferredSize(mid);
        newDepthField.setPreferredSize(mid);
        newNumberField.setPreferredSize(mid);
        newHeightField.setPreferredSize(mid);
        newMassField.setPreferredSize(mid);


        clearAllButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(dialog.confirmDialog("Обнулить все поля ввода?") != 0){
                    return;
                }
                setNewFields(new CellInfo(0, 0, "0", 0, 0));
            }
        });

        copyValuesButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(dialog.confirmDialog("Перенести текущие значения в поля ввода?") != 0){
                    return;
                }
                if(currentCellInfo == null){
                    dialog.messageDialog("Текущие значения некорректные, попробуйте считать ячейку");
                    return;
                }
                setNewFields(currentCellInfo);
            }
        });
        add(new JLabel(""));
        add(new JLabel("Текущее значение:", JLabel.CENTER));
        add(new JLabel("Изменить на:", JLabel.CENTER));
        add(new JLabel("Номер груза:", JLabel.RIGHT));
        add(currentNumberLabel);
        add(newNumberField);
        add(new JLabel("Позиция:", JLabel.RIGHT));
        add(currentPositionLabel);
        add(newPositionField);
        add(new JLabel("Глубина:", JLabel.RIGHT));
        add(currentDepthLabel);
        add(newDepthField);
        add(new JLabel("Высота:", JLabel.RIGHT));
        add(currentHeightLabel);
        add(newHeightField);
        add(new JLabel("Масса:", JLabel.RIGHT));
        add(currentMassLabel);
        add(newMassField);
        add(new JLabel(""));
        add(copyValuesButton);
        add(clearAllButton);
    }
    void setCurrentFields(CellInfo cellInfo){
        currentCellInfo = cellInfo;
        currentPositionLabel.setText(String.valueOf(cellInfo.position));
        currentDepthLabel.setText(String.valueOf(cellInfo.depth));
        currentNumberLabel.setText(cellInfo.number);
        currentHeightLabel.setText(String.valueOf(cellInfo.height));
        currentMassLabel.setText(String.valueOf(cellInfo.mass));
    }

    void setNoRead() {
        String noRead = "Не считано";
        currentPositionLabel.setText(noRead);
        currentDepthLabel.setText(noRead);
        currentNumberLabel.setText(noRead);
        currentHeightLabel.setText(noRead);
        currentMassLabel.setText(noRead);
    }

    void setNewFields(CellInfo cellInfo){
        newPositionField.setText(String.valueOf(cellInfo.position));
        newDepthField.setText(String.valueOf(cellInfo.depth));
        newNumberField.setText(cellInfo.number);
        newHeightField.setText(String.valueOf(cellInfo.height));
        newMassField.setText(String.valueOf(cellInfo.mass));
    }

    void setCellCoordinates(CellCoordinates cellCoordinates){
        this.cellCoordinates = cellCoordinates;
    }

    public CellCoordinates getCellCoordinates(){
        return cellCoordinates;
    }

    public CellInfo getCurrentCellInfo(){
        return currentCellInfo;
    }

    CellInfo getNewCellInfo(){
        long position = getLongFromText(newPositionField.getText());
        long depth = getLongFromText(newDepthField.getText());
        String number = newNumberField.getText();
        long height = getLongFromText(newHeightField.getText());
        long mass = getLongFromText(newMassField.getText());
        if (number.length() == 0){
            number = "0";
        }
        if(!isValid(position, 0, 32767, "'позиция'")) return null;
        if(!isValid(depth, 0, 32767, "'глубина'")) return null;
        for (char c: number.toCharArray()){
            if((int) c > 127){
                dialog.messageDialog("Некорректно введенный символ в поле 'номер груза' (используйте ASCII)," +
                        "исправьте ошибку!");
                return null;
            }
        }
        if(!isValid(height, 0, 32767, "'высота'")) return null;
        if(!isValid(mass, 0, 2147483647, "'масса'")) return null;

        CellInfo cellInfo = new CellInfo((int) position, (int) depth, number, (int) height, (int) mass);
        setNewFields(cellInfo);

        return cellInfo;
    }

    long getLongFromText(String text){
        String[] textArray = text.split("\\D+");
        for(String txtLong: textArray){
            if (txtLong.length() != 0){
                return Long.parseLong(txtLong);
            }
        }
        return 0;
    }

    boolean isValid(long value, long lowLim, long highLim, String fieldName){
        if (value > highLim || value < lowLim){
            dialog.messageDialog(String.format("Значение в поле %s должно находится в пределах от %d до %d," +
                            " исправьте ошибку!",
            fieldName, lowLim, highLim));
            return false;
        }
        return true;
    }
}
