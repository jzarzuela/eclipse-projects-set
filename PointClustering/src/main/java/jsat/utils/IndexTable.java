
package jsat.utils;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * The index table provides a way of accessing the sorted view of an array or list,
 * without ever sorting the elements of said list. Given an array of elements, the 
 * index table creates an array of index values, and sorts the indices based on 
 * the values they point to. The IndexTable can then be used to find the index 
 * of the i'th sorted element in the array. 
 * 
 * @author Edward Raff
 */
public class IndexTable implements Serializable
{
    static private final Comparator defaultComp = new Comparator()        
    {

        @Override
        public int compare(Object o1, Object o2)
        {
            Comparable co1 = (Comparable) o1;
            Comparable co2 = (Comparable) o2;
            return co1.compareTo(co2);
        }
    };
    
    /**
     * Obtains the reverse order comparator
     * @param <T> the data type
     * @param cmp the original comparator
     * @return the reverse order comparator
     */
    public static <T> Comparator<T> getReverse(final Comparator<T> cmp)
    {
        return new Comparator<T>() 
        {
            @Override
            public int compare(T o1, T o2)
            {
                return -cmp.compare(o1, o2);
            }
        };
    }
    
    /**
     * We use an array of Integer objects instead of integers because we need 
     * the arrays.sort function that accepts comparators. 
     */
    private IntList index;
    
    /**
     * Creates a new index table of a specified size that is in linear order. 
     * @param size the size of the index table to create
     */
    public IndexTable(int size)
    {
        index = new IntList(size);
        ListUtils.addRange(index, 0, size, 1);
    }

    /**
     * Creates a new index table based on the given array. The array will not be altered. 
     * @param array the array to create an index table for. 
     */
    public IndexTable(double[] array)
    {
        this(DoubleList.unmodifiableView(array, array.length));
    }
    
    /**
     * Creates a new index table based on the given array. The array will not be altered. 
     * @param array the array to create an index table for
     */
    public <T extends Comparable<T>> IndexTable(T[] array)
    {
        index = new IntList(array.length);
        ListUtils.addRange(index, 0, array.length, 1);
        Collections.sort(index, new IndexViewCompG(array));
    }
    
    /**
     * Creates a new index table based on the given list. The list will not be altered. 
     * @param list the list to create an index table for
     */
    public <T extends Comparable<T>> IndexTable(List<T> list)
    {
        this(list, defaultComp);
    }
    
    /**
     * Creates a new index table based on the given list and comparator. The 
     * list will not be altered. 
     * 
     * @param list the list of points to obtain a sorted IndexTable for
     * @param comparator the comparator to determined the sorted order
     */
    public <T> IndexTable(List<T> list, Comparator<T> comparator)
    {
        index = new IntList(list.size());
        ListUtils.addRange(index, 0, list.size(), 1);
        sort(list, comparator);
    }
    
    /**
     * Resets the index table so that the returned indices are in linear order, 
     * meaning the original input would be returned in its original order 
     * instead of sorted order. 
     */
    public void reset()
    {
        for(int i = 0; i < index.size(); i++)
            index.set(i, i);
    }
    
    /**
     * Reverse the current index order
     */
    public void reverse()
    {
        Collections.reverse(index);
    }
    
    /**
     * Adjusts this index table to contain the sorted index order for the given 
     * array
     * @param array the input to get sorted order of
     */
    public void sort(double[] array)
    {
        sort(DoubleList.unmodifiableView(array, array.length));
    }
    
    /**
     * Adjusts this index table to contain the reverse sorted index order for 
     * the given array
     * @param array the input to get sorted order of
     */
    public void sortR(double[] array)
    {
        sortR(DoubleList.unmodifiableView(array, array.length));
    }
    
    /**
     * Adjust this index table to contain the sorted index order for the given 
     * list
     * @param <T> the data type
     * @param list the list of objects
     */
    public <T extends Comparable<T>> void sort(List<T> list)
    {
        sort(list, defaultComp);
    }
    
    /**
     * Adjusts this index table to contain the reverse sorted index order for 
     * the given list
     * @param <T> the data type
     * @param list the list of objects
     */
    public <T extends Comparable<T>> void sortR(List<T> list)
    {
        sort(list, getReverse(defaultComp));
    }
    
    
    /**
     * Sets up the index table based on the given list of the same size and 
     * comparator. 
     * 
     * @param <T> the type in use
     * @param list the list of points to obtain a sorted IndexTable for
     * @param cmp the comparator to determined the sorted order
     */
    public <T> void sort(List<T> list, Comparator<T> cmp)
    {
        if(list.size() != index.size())
            throw new IllegalArgumentException("Input list is not the same size as index table");
        Collections.sort(index, new IndexViewCompList(list, cmp));
    }
    
    private class IndexViewCompG<T extends Comparable<T>> implements Comparator<Integer> 
    {
        T[] base;

        public IndexViewCompG(T[] base)
        {
            this.base = base;
        }

        @Override
        public int compare(Integer t, Integer t1)
        {
            return base[t].compareTo(base[t1]);
        }        
    }
    
    private class IndexViewCompList<T> implements Comparator<Integer> 
    {
        final List<T> base;
        final Comparator<T> comparator;
        
        public IndexViewCompList(List<T> base, Comparator<T> comparator)
        {
            this.base = base;
            this.comparator = comparator;
        }

        @Override
        public int compare(Integer t, Integer t1)
        {
            return comparator.compare(base.get(t), base.get(t1));
        }        
    }
    
    /**
     * Swaps the given indices in the index table. 
     * @param i the second index to swap 
     * @param j the first index to swap
     */
    public void swap(int i, int j)
    {
        Collections.swap(index, i, j);
    }
    
    /**
     * Given the index <tt>i</tt> into what would be the sorted array, the index in the unsorted original array is returned. <br>
     * If the original array was a double array, <i>double[] vals</i>, then the sorted order can be printed with <br>
     * <code><pre>
     * for(int i = 0; i &lt; indexTable.{@link #length() length}(); i++)
     *     System.out.println(vals[indexTable.get(i)]);
     * </pre></code>
     * @param i the index of the i'th sorted value
     * @return the index in the original list that would be in the i'th position
     */
    public int index(int i)
    {
        return index.get(i);
    }
    
    /**
     * The length of the original array that was sorted
     * @return the length of the original array 
     */
    public int length()
    {
        return index.size();
    }
    
    /**
     * Applies this index table to the specified target. The application will unsorted the index
     * table, returning it to a state of representing the un ordered list. 
     * @throws RuntimeException if the length of the target array is not the same as the index table
     */
    public void apply(double[] target)
    {
        if(target.length != length())
            throw new RuntimeException("target array does not have the same length as the index table");
        for(int i = 0; i < target.length; i++)
        {
            double tmp = target[i];
            target[i] = target[index(i)];
            target[index(i)] = tmp;
            swap(i, index(i));
        }
    }
    
    /**
     * Applies this index table to the specified target. The application will unsorted the index
     * table, returning it to a state of representing the un ordered list. 
     * @throws RuntimeException if the length of the target List is not the same as the index table
     */
    public void apply(List target)
    {
        if(target.size() != length())
            throw new RuntimeException("target array does not have the same length as the index table");
        for(int i = 0; i < target.size(); i++)
        {
            ListUtils.swap(target, i, index(i));
            swap(i, index(i));
        }
    }
    
}
