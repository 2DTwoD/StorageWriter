package StorageEditors;

import com.sourceforge.snap7.moka7.S7;

public class CellInfo {
    public int position;
    public int depth;
    public String number;
    public int height;
    public int mass;
    public CellInfo(byte[] data){
        position = S7.GetWordAt(data, 0);
        depth = S7.GetWordAt(data, 2);
        number = S7.GetStringAt(data, 4, 20).trim();
        height = S7.GetWordAt(data, 24);
        mass = S7.GetDIntAt(data, 26);
    }
    public CellInfo(int position, int depth, String number, int height, int mass){
        this.position = position;
        this.depth = depth;
        this.number = number;
        this.height = height;
        this.mass = mass;
    }
}
