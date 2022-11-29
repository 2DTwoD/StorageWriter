package SwingComponents;

import StorageEditors.CellCoordinates;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.TreeSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CellCombo extends JPanel {
    String[] storageArray = {
            "Склад рулонов, МРД2",
            "Склад паллет, МРД3",
            "Склад паллет, МРД4"
    };
    String[] sideArray = {
            "Левая сторона",
            "Правая сторона"
    };
    TreeSet<Integer> xSet = new TreeSet<>();
    TreeSet<Integer> ySet = new TreeSet<>();
    TreeSet<String> sSet = new TreeSet<>(Arrays.stream(sideArray).toList());
    TreeSet<Integer> zSet = new TreeSet<>();
    JComboBox<String> storageCombo;
    JComboBox<Integer> xCombo = new JComboBox<>();
    JComboBox<Integer> yCombo = new JComboBox<>();
    JComboBox<String> sCombo;
    JComboBox<Integer> zCombo = new JComboBox<>();

    public int change = 0;

    public CellCombo(){
        storageCombo = new JComboBox<>(storageArray);
        sCombo = new JComboBox<>(sSet.toArray(String[]::new));
        Dimension dim = new Dimension(50, 20);
        xCombo.setPreferredSize(dim);
        yCombo.setPreferredSize(dim);
        zCombo.setPreferredSize(dim);
        updateComboSets();
        storageCombo.addActionListener(e -> updateComboSets());
        add(new JLabel("Склад:"));
        add(storageCombo);
        add(new JLabel("Пролёт (коорд. X):"));
        add(xCombo);
        add(new JLabel("Этаж (коорд. Y):"));
        add(yCombo);
        add(new JLabel("Сторона (коорд. S):"));
        add(sCombo);
        add(new JLabel("Глубина (коорд. Z):"));
        add(zCombo);
        storageCombo.addActionListener(e -> change = 0);
        xCombo.addActionListener(e -> change = 0);
        yCombo.addActionListener(e -> change = 0);
        sCombo.addActionListener(e -> change = 0);
        zCombo.addActionListener(e -> change = 0);
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            if (change > 0) {
                change--;
            } else {
                change = 0;
            }
        },0,  2, TimeUnit.MINUTES);
    }

    boolean itChanged(){
        return change == 0;
    }

    void noChange(){
        change = 2;
    }

    void updateComboSets() {
        int xNum;
        int yNum;
        int zNum;
        String storage = (String) storageCombo.getSelectedItem();
        storage = storage == null? "": storage;
        if (storage.equals(storageArray[0])) {
            xNum = 20;
            yNum = 4;
            zNum = 5;
        } else {
            xNum = 20;
            yNum = 7;
            zNum = 2;
        }
        xSet.clear();
        ySet.clear();
        zSet.clear();
        for (int i = 1; i <= xNum; i++){
            xSet.add(i);
        }
        for (int i = 1; i <= yNum; i++){
            ySet.add(i);
        }
        for (int i = 1; i <= zNum; i++){
            zSet.add(i);
        }
        xCombo.setModel(new DefaultComboBoxModel<>(xSet.toArray(Integer[]::new)));
        yCombo.setModel(new DefaultComboBoxModel<>(ySet.toArray(Integer[]::new)));
        zCombo.setModel(new DefaultComboBoxModel<>(zSet.toArray(Integer[]::new)));
    }

    public CellCoordinates getSelectedCoordinates(){
        String storage = storageCombo.getSelectedItem() == null? "": (String) storageCombo.getSelectedItem();
        int xPos = xCombo.getSelectedItem() == null? 0: (Integer) xCombo.getSelectedItem();
        int yPos = yCombo.getSelectedItem() == null? 0: (Integer) yCombo.getSelectedItem();
        String sPos = sCombo.getSelectedItem() == null? "": (String) sCombo.getSelectedItem();
        int zPos = zCombo.getSelectedItem() == null? 0: (Integer) zCombo.getSelectedItem();
        return new CellCoordinates(storage, xPos, yPos, sPos, zPos);
    }

}
