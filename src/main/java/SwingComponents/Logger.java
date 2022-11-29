package SwingComponents;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger extends JPanel {
    JTextArea textArea = new JTextArea();
    JButton saveButton = new JButton("Сохранить историю событий в файл");
    Dialogs dialog = new Dialogs();
    public Logger(){
        newLog("Старт программы");
        setLayout(new VerticalLayout(this, 10, 10, VerticalLayout.CENTER));
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        JScrollPane scroller = new JScrollPane(textArea);
        scroller.getVerticalScrollBar().setUnitIncrement(20);
        int scrollerHeight = Math.max((Toolkit.getDefaultToolkit().getScreenSize().height - 660), 75);
        scroller.setPreferredSize(new Dimension(800, scrollerHeight));
        add(new JLabel("История событий:"));
        add(scroller);
        add(saveButton);
        saveButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Сохранить файл");
                if (fileChooser.showSaveDialog(textArea) == JFileChooser.APPROVE_OPTION){
                    File file = fileChooser.getSelectedFile();
                    if (file == null){
                        dialog.messageDialog("Выбран некорректный путь при сохранении истории");
                        return;
                    }
                    if (!file.getName().toLowerCase().endsWith(".txt")){
                        file = new File(file.getParentFile(), file.getName() + ".txt");
                    }
                    try (FileOutputStream fos = new FileOutputStream(file)){
                        textArea.write(new OutputStreamWriter(fos, StandardCharsets.UTF_8));
                    }
                    catch(Exception ex) {
                        dialog.errorDialog("Проблемы с сохранением файла истории");
                    }
                    dialog.messageDialog("Файл истории сохранён!");
                }
            }
        });
    }
    public void newLog(String text){
        textArea.append(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss: ").format(new Date()) + text + "\n\n");
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }
}
