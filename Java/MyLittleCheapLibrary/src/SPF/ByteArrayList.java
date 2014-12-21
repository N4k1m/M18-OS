package SPF;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author nakim
 */
public class ByteArrayList
{
    //<editor-fold defaultstate="collapsed" desc="Private varibles">
    private List<Byte> bytesList;
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Constructor">
    public ByteArrayList()
    {
        this.bytesList = new ArrayList<>();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Public methods">
    public void add(byte[] bytes)
    {
        add(bytes, 0, bytes.length);
    }

    public void add(byte[] bytes, int offset, int length)
    {
        for (int i = offset; i < (offset + length); i++)
        {
            this.bytesList.add(bytes[i]);
        }
    }

    public byte[] getArray()
    {
        byte[] bytes = new byte[this.bytesList.size()];
        for (int i = 0; i < this.bytesList.size(); i++)
        {
            bytes[i] = this.bytesList.get(i);
        }

        return bytes;
    }
    
    public boolean isEmpty()
    {
        return this.bytesList.isEmpty();
    }
    
    public void clear()
    {
        this.bytesList.clear();
    }
    
    public int size()
    {
        return this.bytesList.size();
    }
    //</editor-fold>
}
