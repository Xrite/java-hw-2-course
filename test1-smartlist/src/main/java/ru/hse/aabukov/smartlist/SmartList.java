package ru.hse.aabukov.smartlist;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class SmartList<E> extends AbstractList<E> {

    private int size = 0;
    private Object data = null;

    public SmartList() {}

    public SmartList(Collection<? extends E> c) {
        for(E element : c) {
            add(element);
        }
    }

    @Override
    public E get(int index) {
        if(index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        if(size == 1) {
            return (E)data;
        }
        if(size >= 2 && size <= 5) {
            Object[] array = (Object[])data;
            return (E) array[index];
        }
        ArrayList<E> arrayList = (ArrayList<E>)data;
        return arrayList.get(index);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean contains(Object o) {
        if(size == 0) {
            return false;
        }
        if(size == 1) {
            return o.equals(data);
        }
        if(size >= 2 && size <= 5) {
            Object[] array = (Object[])data;
            for(Object element : array) {
                if(o.equals(element)) {
                    return true;
                }
            }
            return false;
        }
        ArrayList<E> arrayList = (ArrayList<E>)data;
        return arrayList.contains(o);
    }

    @Override
    public void add(int index, E e) {
        if(index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }
        if(size == 0) {
            data = e;
        } else if(size == 1) {
            Object[] array = new Object[5];
            array[index] = e;
            array[1 - index] = data;
            data = array;
        } else if(size < 5) {
            Object[] array = (Object[]) data;
            for(int i = size; i > index; i--) {
                Object temp = array[i];
                array[i] = array[i - 1];
                array[i - 1] = temp;
            }
            array[index] = e;
        } else if(size == 5) {
            Object[] array = (Object[]) data;
            var tempList = Arrays.asList(array);
            data = new ArrayList<E>((Collection<? extends E>) tempList);
            ((ArrayList<E>)data).add(index, e);
        } else {
            ((ArrayList<E>)data).add(index, e);
        }
        size++;
    }

    @Override
    public boolean add(E e) {
        if(size == 0) {
            data = e;
        } else if(size == 1) {
            Object[] array = new Object[5];
            array[1] = e;
            array[0] = data;
            data = array;
        } else if(size < 5) {
            Object[] array = (Object[]) data;
            array[size] = e;
        } else if(size == 5) {
            Object[] array = (Object[]) data;
            var tempList = Arrays.asList(array);
            data = new ArrayList<E>((Collection<? extends E>) tempList);
            ((ArrayList) data).add(e);
        } else {
            ((ArrayList<E>)data).add(e);
        }
        size++;
        return true;
    }

    @Override
    public E set(int index, E element) {
        if(index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        Object returnValue = null;
        if(size == 1) {
            returnValue = data;
            data = element;
        } else if(size >= 2 && size <= 5) {
            Object[] array = (Object[]) data;
            returnValue = array[index];
            array[index] = element;
        } else {
            var arrayList = (ArrayList<E>)data;
            returnValue = arrayList.set(index, element);
        }
        return (E) returnValue;
    }

    @Override
    public E remove(int index) {
        if(index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        E returnValue = null;
        if(size == 1) {
            returnValue = (E)data;
            data = null;
        } else if(size == 2) {
            Object[] array = (Object[])data;
            returnValue = (E) array[index];
            data = array[1 - index];

        } else if(size <= 5) {
            Object[] array = (Object[])data;
            returnValue = (E) array[index];
            for(int i = index; i < 4; i++) {
                Object temp = array[i];
                array[i] = array[i + 1];
                array[i + 1] = temp;
            }
        } else if (size == 6){
            ArrayList<E> arrayList = (ArrayList<E>) data;
            returnValue = arrayList.remove(index);
            data = arrayList.toArray();
        } else {
            ArrayList<E> arrayList = (ArrayList<E>) data;
            returnValue = arrayList.remove(index);
        }
        size--;
        return returnValue;
    }

}
