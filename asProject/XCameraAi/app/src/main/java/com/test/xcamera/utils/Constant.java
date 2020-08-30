package com.test.xcamera.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DELL on 2019/7/5.
 */

public class Constant {


    public  static  final  boolean isdebug=true;//控制是否打印Log
   public static List<String>  list=new ArrayList<>();

   private static String s1="http://v1-dy.bytecdn.cn/b59eeca284f58bcaa50ba8ca13300379/5d1f4f74/video/m/220072c8a552d5c4ba6bc0fdf19bd921f841162c060800005f3e15cc1902/?rc=anllOTw4dDVwbjMzNGkzM0ApQHRAbzZFNzwzMzwzNDY2Mzg6PDNAKXUpQGczdSlAZjN2KUBmcHcxZnNoaGRmOzRAcXNucF9kMmpiXy0tNC0vc3MtbyNvIzUzNi4wLi4tLTIvLy0tLi9pOmIucCM6YS1xIzpgLW8jYmZoXitqdDojLy5e";

    private static String s2="http://v1-dy.bytecdn.cn/ab2725c1b64ab298c6580525f535f55e/5d1f4f7b/video/m/220c9085775d15c422bab01ed7c05e8ca641162c21b90000112daf4b6e6b/?rc=Mzt4cnJ4O2pvbjMzNmkzM0ApQHRAbzZFNzwzMzwzNDY2Mzg6PDNAKXUpQGczdSlAZjN2KUBmcHcxZnNoaGRmOzRAMHA1ZmgucGxhXy0tLS0vc3MtbyNvIzUzNi4wLi4tLTIvLy0tLi9pOmIucCM6YS1xIzpgLW8jYmZoXitqdDojLy5e";
    private static String s3="http://v3-dy-x.bytecdn.cn/9248f2f01c440ccbc08dde356186030e/5d1f4f7e/video/m/2203a96354034b44eb09e4013b5245b78491162c0b9d0000ab14e14f7d43/?rc=M203ZnVkbGZnbjMzOGkzM0ApQHRAbzszPDs0NTkzNDY3Mzg6PDNAKXUpQGczdSlAZjN2KUBmcHcxZnNoaGRmOzRAL19oa3FoL2hjXy0tNS0vc3MtbyNvIzIwLTM2LS0tLS0vLy0tLi9pOmIucCM6YS1xIzpgLW8jYmZoXitqdDojLy5e";
    private static String s4="http://v3-dy-x.bytecdn.cn/ffba1dc9c21c1f93f75fc8bd8fffae54/5d1f4f81/video/m/2208690be5d20174c81b470f1d2151f74891162be579000008935ba128b6/?rc=M2d3OjY0aWY7bjMzZ2kzM0ApQHRAbzszPDs0NTkzNDY3Mzg6PDNAKXUpQGczdSlAZjN2KUBmcHcxZnNoaGRmOzRANmFycGszb2pgXy0tMC0wc3MtbyNvIzIwLTM2LS0tLS0vLy0tLi9pOmIucCM6YS1xIzpgLW8jYmZoXitqdDojLy5e";
    private  static String s5="http://v3-dy-x.bytecdn.cn/3fe65e849bc2293d0e33fc278429079c/5d1f4fae/video/m/22010be773112544530a0859f89d8dd03f81162c84690000013f0a109c9c/?rc=MzU5OGRmamp2bjMzNGkzM0ApQHRAbzszPDs0NTkzNDY3Mzg6PDNAKXUpQGczdSlAZjN2KUBmcHcxZnNoaGRmOzRANC9zMWxrYm1iXy0tNC0vc3MtbyNvIzIwLTM2LS0tLS0vLy0tLi9pOmIvcCM6YS1xIzpgLW8jYmZoXitqdDojLy5e";
    private static String s6="http://v3-dy-x.bytecdn.cn/0740fe6e7d39e21f7c4b7309d74d5570/5d1f4f7e/video/m/22070784010714c477e96ffa9f7d338737f1162c1dfa00006bf5506c0c71/?rc=anc6eXYzZnA8bjMzN2kzM0ApQHRAbzszPDs0NTkzNDY3Mzg6PDNAKXUpQGczdSlAZjN2KUBmcHcxZnNoaGRmOzRAL2ZpLS0xcjZiXy0tMC0wc3MtbyNvIzIwLTM2LS0tLS0vLy0tLi9pOmIucCM6YS1xIzpgLW8jYmZoXitqdDojLy5e";
    private  static String s7="http://v3-dy-x.bytecdn.cn/bcf3cb358e86df65588340ee3423281b/5d1f4f7b/video/m/22090458cb39dfd4bdbbb8e57ff114ee44f11629dc2d0000af529c44f02a/?rc=MzZmPGp0cGRlbjMzNmkzM0ApQHRAbzszPDs0NTkzNDY3Mzg6PDNAKXUpQGczdSlAZjN2KUBmcHcxZnNoaGRmOzRAamYxYjVyMmI1Xy0tYS0wc3MtbyNvIzIwLTM2LS0tLS0vLy0tLi9pOmIucCM6YS1xIzpgLW8jYmZoXitqdDojLy5e";

   public static List<String>  getList(){
       list.add(s1);
       list.add(s2);
       list.add(s3);
       list.add(s4);
       list.add(s5);
       list.add(s6);
       list.add(s7);
       return  list;
   }
}
