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
public class Data {
    String name;
    int id;
    double cost;
    Data(String name, int id,  double cost){
        this.name=name;
        this.id=id;
        this.cost=cost;
    }
    @Override
        public String toString(){
            return name+id+cost;
        }
}
