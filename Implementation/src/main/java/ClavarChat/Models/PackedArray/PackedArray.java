package ClavarChat.Models.PackedArray;

import java.util.ArrayList;
import java.util.HashMap;

public class PackedArray<T>
{
    private int id;
    private final ArrayList<T> datas;
    private final HashMap<Integer, Integer> datasMap;

    public PackedArray()
    {
        this.datas = new ArrayList<>();
        this.datasMap = new HashMap<>();
    }

    public int add(T object)
    {
        this.datas.add(object);
        this.datasMap.put(this.id, this.datas.size() - 1);
        return this.id++;
    }

    public ArrayList<T> getDatas()
    {
        return datas;
    }

    public T get(int id)
    {
        if (!this.datasMap.containsKey(id)) return null;
        int index = this.datasMap.get(id);
        return this.datas.get(index);
    }

    public T remove(int id)
    {
        if (!this.datasMap.containsKey(id)) return null;

        if (this.datas.size() > 1)
        {
            int index = this.datasMap.get(id);
            int lastIndex = this.datas.size() - 1;

            T lastItem = this.datas.get(lastIndex);
            T item = this.datas.get(index);

            this.datas.set(index, lastItem);

            this.datas.remove(lastIndex);
            this.id = lastIndex;
            this.datasMap.put(id, index);

            return item;
        }

        T item = this.datas.get(0);
        this.datas.remove(0);
        this.id = 0;
        this.datasMap.clear();

        return item;
    }

    public void clear()
    {
        this.datas.clear();
        this.datasMap.clear();
        this.id = 0;
    }
}
