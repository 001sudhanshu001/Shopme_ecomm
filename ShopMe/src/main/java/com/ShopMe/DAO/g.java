package com.ShopMe.DAO;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class g {
    static class Test{
        String name;
        String address;
        public Test(String name, String address){
            this.name = name;
            this.address = address;
        }
    }
    public static void main(String[] args) {
        /*int[] arr = {546,55,46};
        for(int a : arr){
            System.out.println(a);
        }

        arr = new int[]{45, 45};
        for(int a : arr){
            System.out.println(a);
        }*/
    /*    int[][][] arr = {{{56}}, {{164,44}}};
        System.out.println(Arrays.deepToString(arr));
*/
        Map map = new HashMap();
        int[] arr = {1,48,98};

        map.put("hello",arr);
        map.put(false,true);
        System.out.println("hello".substring(2));
        Test t = new Test("Ram","Delhi");
        map.put("New Delhi",t);
        System.out.println(map);

    }

}
