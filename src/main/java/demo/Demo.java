package demo;

import spinacht.Params;
import spinacht.data.*;
import spinacht.subclu.DumbSUBCLU;
import spinacht.subclu.SUBCLU;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class Demo {


    public static void main(String[] args) throws Exception {

        SimpleLabeledDatabase<String> db = DataUtil.fromELKIFile(Integer.parseInt(args[0]), Paths.get(args[1]));


    }

}
