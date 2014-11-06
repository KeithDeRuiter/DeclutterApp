/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package declutterapp;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Keith
 */
public class TESTY {
    
    public int x;
    
    public TESTY(int p) {
        x = p;
    }
    
    public static void main(String[] args) {
        List<TESTY> a = new ArrayList<>();
        a.add(new TESTY(1));
        a.add(new TESTY(2));
        TESTY siz = new TESTY(6);
        a.add(siz);
        
        List<TESTY> c = proc(a);
        
        for(TESTY t : a) {
            System.out.println(t.x);
        }
        
        for(TESTY t : c) {
            System.out.println(t.x);
        }
    }
    
    public static List<TESTY> proc(List<TESTY> b) {
        b.get(2).x = 5;
        b.get(1).x = 4;
        
        return b;
    }

}