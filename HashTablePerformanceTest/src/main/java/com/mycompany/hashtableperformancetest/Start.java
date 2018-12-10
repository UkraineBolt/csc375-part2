/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.hashtableperformancetest;

import java.io.IOException;
import org.openjdk.jmh.Main;
import org.openjdk.jmh.runner.RunnerException;

/**
 *
 * @author alex
 */
public class Start {

    /**
     * @param args the command line arguments
     * @throws org.openjdk.jmh.runner.RunnerException
     * @throws java.io.IOException
     * 
     */
    private static final String TEST = ".*MyBenchmark.*";
    public static void main(String[] args) throws RunnerException, IOException{
        Main.main(getArguments(TEST, 5, 5000, 1));
    }
    private static String[] getArguments(String className, int nRuns, int runForMilliseconds, int nThreads) {
        return new String[]{className,
            "-i", "" + nRuns,
            "-r", runForMilliseconds + "ms",
            "-t", "" + nThreads,
            "-w", "5000ms",
            "-wi", "3",
            "-v"
        };
    }
    
    
}
