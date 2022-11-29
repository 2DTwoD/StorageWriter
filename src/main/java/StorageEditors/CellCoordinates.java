package StorageEditors;

public class CellCoordinates{
    public String storage;
    public int xPos;
    public int yPos;
    public String sPos;
    public int zPos;
    public CellCoordinates(String storage, int xPos, int yPos, String sPos, int zPos){
        this.storage = storage;
        this.xPos = xPos;
        this.yPos = yPos;
        this.sPos = sPos;
        this.zPos = zPos;
    }
    @Override
    public boolean equals(Object obj){
        try {
            CellCoordinates cellCoordinates = (CellCoordinates) obj;
            return cellCoordinates.storage.equals(storage) &&
                    cellCoordinates.xPos == xPos &&
                    cellCoordinates.yPos == yPos &&
                    cellCoordinates.sPos.equals(sPos) &&
                    cellCoordinates.zPos == zPos;
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }
    String getString(){
        return String.format("Склад: %s, X: %d, Y: %d, S: %s, Z: %d", storage, xPos, yPos, sPos, zPos);
    }
}
