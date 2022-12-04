package ClavarChat.Models.PackedArray;

import java.util.ArrayList;
import java.util.HashMap;

public class PackedArray<T>
{
    private int id;
    private final ArrayList<T> datas;
    private final HashMap<Integer, Integer> idToIndex;
    private final HashMap<Integer, Integer> indexToId;

    public PackedArray()
    {
        this.datas = new ArrayList<>();
        this.idToIndex = new HashMap<>();
        this.indexToId = new HashMap<>();
    }

    public int add(T object)
    {
        this.datas.add(object);
        this.idToIndex.put(this.id, this.datas.size() - 1);
        this.indexToId.put(this.datas.size() - 1, this.id);
        return this.id++;
    }

    public ArrayList<T> getDatas()
    {
        return datas;
    }

    public T get(int id)
    {
        if (!this.idToIndex.containsKey(id)) return null;
        int index = this.idToIndex.get(id);
        return this.datas.get(index);
    }

    public T remove(int id)
    {
        if (!this.idToIndex.containsKey(id)) return null;

        int indexOfItem = this.idToIndex.get(id);
        int indexOfLastItem = this.datas.size() - 1;
        int idOfLastItem = this.indexToId.get(indexOfLastItem);

        T lastItem = this.datas.get(indexOfLastItem);
        T item = this.datas.get(indexOfItem);

        this.datas.set(indexOfItem, lastItem);
        this.datas.remove(lastItem);

        this.idToIndex.put(idOfLastItem, indexOfItem);
        this.indexToId.put(indexOfItem, idOfLastItem);

        this.idToIndex.remove(id);
        this.indexToId.remove(indexOfItem);

        this.id = indexOfLastItem;

        return item;
    }

    public void clear()
    {
        this.datas.clear();
        this.indexToId.clear();
        this.idToIndex.clear();
        this.id = 0;
    }
}
