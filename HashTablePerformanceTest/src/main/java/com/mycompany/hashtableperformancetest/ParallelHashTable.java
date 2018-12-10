/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.hashtableperformancetest;

import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;


/**
 *
 * @author alex
 */
public class ParallelHashTable {
    private HeadNode[] row;
    private final AtomicInteger counter;
    private final AtomicInteger threshold;
    private final AtomicBoolean resizing;
    ParallelHashTable(){
        row = new HeadNode[8];
        initializeRow(row);
        counter = new AtomicInteger(0);
        threshold = new AtomicInteger((int) (row.length*.75));
        resizing = new AtomicBoolean(false);
    }
    public void clear(){
        row = new HeadNode[8];
        initializeRow(row);
        counter.set(0);
        threshold.set((int) (row.length*.75));
        resizing.set(false);
    }
    public int getCount(){
        return counter.get();
    }
    private void initializeRow(HeadNode[] x){
        for(int i=0;i<x.length;i++){
            x[i]=new HeadNode();
        }
    }
    
    public boolean put(String key, Data data) throws InterruptedException{//write
        doResize();
        Node dataPoint = new Node(data,key);
        int hash = Math.abs(key.hashCode());
        int tableIndex = hash & (row.length - 1);
        while(resizing.get()){Thread.sleep(2);}
        HeadNode head = row[tableIndex];
        try{
            head.binLock.lock();
        if(head.firstNode != null){
            if(insert(head.firstNode,dataPoint)){
                counter.getAndIncrement();
            }
        }else{
            head.firstNode=dataPoint;
            counter.getAndIncrement();
        }
        }finally{head.binLock.unlock();}
        return true;
    }
    private boolean insert(Node list,Node data){
        if(data.toString().compareTo(list.toString()) < 0 || list.next==null){
            data.next = list.next; //attaches tail to new node
            list.next = data;//attaches new node where tail started
            return true;
        }else{
            return insert(list.next,data);
        }
    }
    private boolean doResize() throws InterruptedException{
        if(counter.get()==threshold.get()){
            if(resizing.compareAndSet(false, true)){//two ifs because if b is true and a is false, resizing's value is then changes when it shouldnt have.
                resize();
                return resizing.compareAndSet(true, false);
            }return false;
        }else{
            return false;
        }
    }
    private void resize() throws InterruptedException{
        counter.getAndSet(0);
        HeadNode[] newTable = new HeadNode[row.length*2];
        HeadNode[] oldTable = row;
        initializeRow(newTable);
        row = newTable;
        threshold.getAndSet((int) (row.length*.75));
        for (HeadNode x : oldTable) {
            while(!x.binLock.tryLock()&&x.amountOfThread.get()==0){Thread.sleep(2);}
            Node node = x.firstNode;
            while(node != null){
                int hash = Math.abs(node.key.hashCode());
                int tableIndex = hash & (row.length - 1);
                if(row[tableIndex].firstNode!=null){
                    insert(row[tableIndex].firstNode,new Node(node.data,node.key));
                    counter.getAndIncrement();
                }else{
                    row[tableIndex].firstNode=new Node(node.data,node.key);
                    counter.getAndIncrement();
                }
                node = node.next;
            }
        }
    }
    
    public Data get(String key) throws InterruptedException{//read
        int hash = Math.abs(key.hashCode());
        int tableIndex = hash & (row.length - 1);
        while(resizing.get()){Thread.sleep(10);}
        HeadNode head = row[tableIndex];
        while(!head.binLock.tryLock()){Thread.sleep(3);}
        head.amountOfThread.getAndIncrement();
        head.binLock.unlock();
        Node node = head.firstNode;
        while(node != null){//change this to a while at some point
            if(node.key.equals(key)){
                head.amountOfThread.getAndDecrement();
                return node.data;
            }else{
                node = node.next;
            }
        }
        head.amountOfThread.getAndDecrement();
        return null;
    }
    
    private class Node{
        Node next;
        Data data;
        String key;
        Node(Data x,String k){
            data=x;key=k;next=null;
        }
        @Override
        public String toString(){
            return data.toString();
        }
    }
    private class HeadNode{
        Node firstNode;
        final Lock binLock;
        final AtomicInteger amountOfThread;
        HeadNode(){
            firstNode = null;
            binLock = new ReentrantLock();
            amountOfThread = new AtomicInteger(0);
        }
    }
}
