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

    public void debug()
    {
        System.out.println("ID --> INDEX : ITEM");

        for (int id : this.idToIndex.keySet())
        {
            int index = this.idToIndex.get(id);
            T item = this.datas.get(index);

            System.out.println(id + " --> " + index + " : " + item);
        }
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

        int indexOfRemovingItem = this.idToIndex.get(id);
        int indexOfLastItem = this.datas.size() - 1;
        int idOfLastItem = this.indexToId.get(indexOfLastItem);

        T lastItem = this.datas.get(indexOfLastItem);
        T item = this.datas.get(indexOfRemovingItem);

        this.datas.set(indexOfRemovingItem, lastItem);
        this.datas.remove(indexOfLastItem);

        this.idToIndex.put(idOfLastItem, indexOfRemovingItem);
        this.indexToId.put(indexOfRemovingItem, idOfLastItem);

        this.idToIndex.remove(id);
        this.indexToId.remove(indexOfLastItem);

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
