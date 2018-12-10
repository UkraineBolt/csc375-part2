/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.hashtableperformancetest;
/**
 *
 * @author alex
 */

import java.util.Hashtable;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

public class MyBenchmark {
    
    @State(Scope.Thread)
    public static class MyState{
        @Setup(Level.Trial)
        public void setup() throws InterruptedException{
            ht = new Hashtable<>();
            pht = new ParallelHashTable();
            for(int i=0;;i++){
                Data t = new Data("name",i,i*.75);
                ht.put(Integer.toString(i), t);
                pht.put(Integer.toString(i), t);
            }
        }
        @TearDown(Level.Trial)
        public void tearDown(){
            
        }
        Hashtable<String, Data> ht;
        ParallelHashTable pht;
    }
    
    //add
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public Data jdkAdd(MyState m){
        return m.ht.put("101", new Data("name",101,101*.75));
    }
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public boolean myAdd(MyState m) throws InterruptedException{
        return m.pht.put("101", new Data("name",101,101*.75));
    }
    //getting
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public Data jdkGet(MyState m){
        return m.ht.get("1");
    }
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public Data myGet(MyState m) throws InterruptedException{
        return m.pht.get("1");
    }
}
