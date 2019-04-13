package org.leolo.jmodbot.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.ArrayUtils;

public class PriorityQueue implements Queue<String>{
	
	private Queue<String> normalQueue;
	private Queue<String> priorityQueue;
	
	public PriorityQueue(int standardSize) {
		normalQueue = new ArrayBlockingQueue<>(standardSize);
		priorityQueue = new LinkedList<>();
	}

	@Override
	public int size() {
		return normalQueue.size()+priorityQueue.size();
	}

	@Override
	public boolean isEmpty() {
		return normalQueue.isEmpty() && priorityQueue.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return normalQueue.contains(o)||priorityQueue.contains(o);
	}

	@Override
	public Iterator<String> iterator() {
		return null;
	}

	@Override
	public Object[] toArray() {
		Object parray [] = priorityQueue.toArray();
		Object narray [] = normalQueue.toArray();
		return ArrayUtils.addAll(parray, narray);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] a) {
		if (a.length < size())
            a = (T[])java.lang.reflect.Array.newInstance(
                                a.getClass().getComponentType(), size());
		Object o [] = toArray();
		for(int i=0;i<size();i++){
			a[i] = (T) o[i];
		}
		return a;
	}

	@Override
	public boolean remove(Object o) {
		return normalQueue.remove(o)||priorityQueue.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for(Object o:c) {
			if(!normalQueue.contains(o)&&priorityQueue.contains(o)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends String> c) {
		return normalQueue.addAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return normalQueue.removeAll(c)|priorityQueue.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return normalQueue.retainAll(c)|priorityQueue.retainAll(c);
	}

	@Override
	public void clear() {
		normalQueue.clear();
		priorityQueue.clear();
	}

	@Override
	public boolean add(String e) {
		return normalQueue.add(e);
	}

	@Override
	public boolean offer(String e) {
		return normalQueue.offer(e);
	}

	@Override
	public String remove() {
		return priorityQueue.isEmpty()?normalQueue.remove():priorityQueue.remove();
	}

	@Override
	public String poll() {
		return priorityQueue.isEmpty()?normalQueue.poll():priorityQueue.poll();
	}

	@Override
	public String element() {
		return priorityQueue.isEmpty()?normalQueue.element():priorityQueue.element();
	}

	@Override
	public String peek() {
		return priorityQueue.isEmpty()?normalQueue.peek():priorityQueue.peek();
	}
	
	class QueueIterator implements Iterator<String>{
		
		String [] array;
		
		int pos=0;
		
		public QueueIterator() {
			array = toArray(new String[size()]);
		}
		
		@Override
		public boolean hasNext() {
			return pos==array.length;
		}

		@Override
		public String next() {
			// TODO Auto-generated method stub
			return array[pos++];
		}
		
	}
	
	public void addPriority(String e) {
		priorityQueue.add(e);
	}
}
