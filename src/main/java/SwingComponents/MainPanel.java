package SwingComponents;

import StorageEditors.CellCoordinates;
import StorageEditors.CellInfo;
import StorageEditors.StorageTool;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainPanel extends JPanel {
    CellCombo cellCombo = new CellCombo();
    EditPanel editPanel = new EditPanel();
    Logger logger = new Logger();
    JButton readCellButton = new JButton("Считать ячейку с ПЛК (PLC => PG)");
    JButton writeCellButton = new JButton("Записать ячейку в ПЛК (PG => PLC)");
    JPanel storagePanel = new JPanel(new FlowLayout());
    JLabel storageLabel = new JLabel("Не считано");

    StorageTool storageTool = new StorageTool(cellCombo.storageArray, logger);

    ExecutorService thread = Executors.newSingleThreadExecutor();
    Dialogs dialog = new Dialogs();
    public MainPanel(){
        setLayout(new VerticalLayout(this, 10, 5, VerticalLayout.CENTER));
        Border border = BorderFactory.createLineBorder(Color.BLACK, 1);
        storageLabel.setBorder(border);
        storageLabel.setPreferredSize(new Dimension(350, 20));
        createPanel();
        readCellButton.setForeground(Color.BLUE);
        writeCellButton.setForeground(Color.RED);
        thread.shutdown();
        readCellButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!thread.isTerminated()) {
                    dialog.messageDialog("Программа выполняет действия, подождите");
                    return;
                }
                thread = Executors.newSingleThreadExecutor();
                thread.submit(() -> {
                    CellCoordinates coordinates = cellCombo.getSelectedCoordinates();
                    String coordText = String.format(
                            "%s; X: %d; Y: %d; S: %s; Z: %d;",
                            coordinates.storage, coordinates.xPos, coordinates.yPos, coordinates.sPos, coordinates.zPos);
                    if(dialog.confirmDialog("Считать ячейку с ПЛК?\nЯчейка: " + coordText) != 0){
                        return;
                    }
                    CellInfo cellInfo = storageTool.readCellInfoFromPLC(coordinates);
                    if (cellInfo == null){
                        return;
                    }
                    storageLabel.setText("Не считано");
                    editPanel.setNoRead();
                    editPanel.setCurrentFields(cellInfo);
                    editPanel.setCellCoordinates(coordinates);
                    storageLabel.setText(coordText);
                    String messText = String.format("""
                            Ячейка считана:
                            %s
                            Позиция: %d;
                            Глубина: %d;
                            Номер: %s;
                            Высота: %d;
                            Масса: %d;""",
                            coordText,
                            cellInfo.position, cellInfo.depth, cellInfo.number, cellInfo.height, cellInfo.mass);
                    dialog.messageDialog("Ячейка считана!");
                    logger.newLog(messText);
                    cellCombo.noChange();
                });
                thread.shutdown();
            }
        });
        writeCellButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!thread.isTerminated()) {
                    dialog.messageDialog("Программа выполняет действия, подождите");
                    return;
                }
                CellCoordinates cellCoordinates = editPanel.getCellCoordinates();
                CellInfo newCellInfo = editPanel.getNewCellInfo();
                CellInfo currentCellInfo = editPanel.getCurrentCellInfo();
                if (newCellInfo == null){
                    return;
                }
                if(!cellCombo.getSelectedCoordinates().equals(cellCoordinates) || cellCombo.itChanged()){
                    dialog.messageDialog("Чтобы записать ячейку, сначала считайте её с ПЛК");
                    return;
                }
                String changeText = String.format("""
                                %s; X: %d; Y: %d; S: %s; Z: %d;
                                Позиция: %d => %d;
                                Глубина: %d => %d;
                                Номер: %s => %s;
                                Высота: %d => %d;
                                Масса: %d => %d;""",
                        cellCoordinates.storage,
                        cellCoordinates.xPos, cellCoordinates.yPos, cellCoordinates.sPos, cellCoordinates.zPos,
                        currentCellInfo.position, newCellInfo.position, currentCellInfo.depth, newCellInfo.depth,
                        currentCellInfo.number, newCellInfo.number, currentCellInfo.height, newCellInfo.height,
                        currentCellInfo.mass, newCellInfo.mass);
                if(dialog.confirmDialog("Записать ячейку?\n" + changeText) != 0){
                    return;
                }
                thread = Executors.newSingleThreadExecutor();
                thread.submit(() -> {
                    storageTool.writeCellInfoToPLC(cellCoordinates, newCellInfo);
                    editPanel.setCurrentFields(storageTool.readCellInfoFromPLC(cellCoordinates));
                    cellCombo.noChange();
                    logger.newLog("Ячейка записана:\n" + changeText);
                    dialog.messageDialog("Ячейка записана!");
                });
                thread.shutdown();
            }
        });
    }
    void createPanel(){
        storagePanel.add(new JLabel("Координаты:"));
        storagePanel.add(storageLabel);
        add(getTitle("Редактор складов Hörmann"));
        add(new JLabel("Выбрать координаты ячейки:"));
        add(cellCombo);
        add(readCellButton);
        add(new Label("-".repeat(200)));
        add(new JLabel("Ячейка:"));
        add(storagePanel);
        add(editPanel);
        add(new Label("-".repeat(200)));
        add(writeCellButton);
        add(new Label("-".repeat(200)));
        add(logger);
    }
    JLabel getTitle(String text){
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font(label.getFont().getFontName(), Font.ITALIC, 16));
        return label;
    }
}
