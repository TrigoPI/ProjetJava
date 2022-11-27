package ClavarChat.Utils.PackedArray;

import java.util.ArrayList;
import java.util.HashMap;

public class PackedArray<T>
{
    private int id;
    private ArrayList<T> datas;
    private HashMap<Integer, Integer> datasMap;

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

    public T get(int id)
    {
        if (!this.datasMap.containsKey(id)) return null;
        int index = this.datasMap.get(id);
        return this.datas.get(index);
    }

    public T remove(int id)
    {
        if (!this.datasMap.containsKey(id)) return null;

        int index = this.datasMap.get(id);
        int lastIndex = this.datas.size() - 1;
        T lastItem = this.datas.get(lastIndex);
        T item = this.datas.get(index);

        this.datas.remove(lastIndex);
        this.datas.set(index, lastItem);
        this.datasMap.put(id, index);

        return item;
    }
}
