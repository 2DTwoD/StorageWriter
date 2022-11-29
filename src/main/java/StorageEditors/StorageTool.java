package StorageEditors;

import SwingComponents.Dialogs;
import SwingComponents.Logger;
import com.sourceforge.snap7.moka7.S7;
import com.sourceforge.snap7.moka7.S7Client;

import java.nio.charset.StandardCharsets;

public class StorageTool {
    final int sizeOfCellInBytes = 30;
    final int startingDB = 801;
    //тестовый адрес
    String PLCip = "192.168.137.2";
    int DBnum = startingDB;
    int DBbytesOffset = 0;
    Dialogs dialog = new Dialogs();
    String[] storageComboItems;
    Logger logger;
    public StorageTool(String[] storageComboItems, Logger logger){
        this.storageComboItems = storageComboItems;
        this.logger = logger;
    }

    public CellInfo readCellInfoFromPLC(CellCoordinates coordinates){
        setStorage(coordinates);
        byte[] data = new byte[sizeOfCellInBytes];
        S7Client client = new S7Client();
        client.SetConnectionType (S7.S7_BASIC);
        try {
            int result = client.ConnectTo(PLCip, 0, 2);
            if(result != 0) {
                String troubleText = String.format("Проблема чтения: %s, %s", coordinates.getString(),
                        S7Client.ErrorText(result));
                logger.newLog(troubleText);
                dialog.errorDialog(troubleText);
                return null;
            }
            client.ReadArea(S7.S7AreaDB, DBnum, DBbytesOffset, sizeOfCellInBytes, data);
        }
        catch (Exception e){
            String troubleText = String.format("Проблема чтения: %s, %s", coordinates.getString(), e.getMessage());
            logger.newLog(troubleText);
            dialog.errorDialog(troubleText);
            return null;
        }
        finally {
            client.Disconnect();
        }
        return new CellInfo(data);
    }
    public void writeCellInfoToPLC(CellCoordinates coordinates, CellInfo cellInfo){
        setStorage(coordinates);
        byte[] data = new byte[sizeOfCellInBytes];
        S7Client client = new S7Client();
        client.SetConnectionType (S7.S7_BASIC);
        try {
            int result = client.ConnectTo(PLCip, 0, 2);
            if(result != 0) {
                dialog.errorDialog(String.format("Проблема записи: %s, %s", coordinates.storage, S7Client.ErrorText(result)));
                return;
            }
            S7.SetWordAt(data, 0, cellInfo.position);
            S7.SetWordAt(data, 2, cellInfo.depth);
            byte[] numberChars = cellInfo.number.getBytes(StandardCharsets.US_ASCII);
            for (int i = 0; i < 20; i++) {
                if (i < numberChars.length) {
                    data[4 + i] = numberChars[i];
                } else {
                    data[4 + i] = 32;
                }
            }
            S7.SetWordAt(data, 24, cellInfo.height);
            S7.SetDIntAt(data, 26, cellInfo.mass);
            client.WriteArea(S7.S7AreaDB, DBnum, DBbytesOffset, sizeOfCellInBytes, data);
        }
        catch (Exception e){
            dialog.errorDialog(String.format("Проблема записи: %s, %s", coordinates.storage, e.getMessage()));
        }
        finally {
            client.Disconnect();
        }
    }
    void setStorage(CellCoordinates coordinates){
        int numOfY;
        int numOfZ;
        int shiftX = 0;
        if(coordinates.storage.equals(storageComboItems[0])){
            PLCip = "192.168.32.22";
            numOfY = 4;
            numOfZ = 5;
            if(coordinates.xPos > 3){
                shiftX = 2;
            } else if (coordinates.xPos > 1){
                shiftX = 1;
            }
        } else if(coordinates.storage.equals(storageComboItems[1])){
            PLCip = "192.168.32.23";
            numOfY = 8;
            numOfZ = 2;
        } else {
            PLCip = "192.168.32.24";
            numOfY = 8;
            numOfZ = 2;
        }
        int sideOffset = coordinates.sPos.equals("Правая сторона")? numOfZ * numOfY * sizeOfCellInBytes: 0;
        DBnum = startingDB - 1 + coordinates.xPos + shiftX;
        DBbytesOffset = sideOffset + (coordinates.yPos - 1) * numOfZ * sizeOfCellInBytes
                + (coordinates.zPos - 1) * sizeOfCellInBytes;
        //System.out.println(PLCip + " " + DBnum + " " + DBbytesOffset);
    }
}
