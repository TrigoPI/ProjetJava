package ClavarChat.Utils.PackedArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class PackedArray<T>
{
//    private int id;
    private final ArrayList<T> datas;
    private final LinkedList<Integer> freeId;
    private final HashMap<Integer, Integer> idToIndex;
    private final HashMap<Integer, Integer> indexToId;

    public PackedArray()
    {
//        this.id = 0;
        this.datas = new ArrayList<>();
        this.freeId = new LinkedList<>();
        this.idToIndex = new HashMap<>();
        this.indexToId = new HashMap<>();
    }

    public void debug()
    {
        int i = 0;

        for (T data : this.datas)
        {
            System.out.println("index : " + i + " --> " + data);
            i++;
        }

        System.out.println(" ");
        System.out.println("ID | INDEX | DATA");

        for (int key : this.idToIndex.keySet())
        {
            int index = this.idToIndex.get(key);
            T data = this.datas.get(index);
            System.out.println(key  + "  | " + index + "     | " + data);
        }
    }

    public int add(T object)
    {
        int id = (this.freeId.isEmpty())?this.datas.size():this.freeId.poll();
        int index = this.datas.size();

        this.datas.add(object);
        this.idToIndex.put(id, index);
        this.indexToId.put(index, id);

        return id;
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
        this.freeId.add(id);
//        this.id = indexOfLastItem;

        return item;
    }

    public void clear()
    {
        this.datas.clear();
        this.indexToId.clear();
        this.idToIndex.clear();
//        this.id = 0;
    }
}
