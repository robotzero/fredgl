package com.robotzero;

import com.robotzero.infrastructure.Window;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class Main {



  public static void main(String[] args) {
    String[] customArgs = {
        "-Dorg.lwjgl.util.Debug=true"
    };

    try {
      Window window = Window.getWindow();
      window.run();
    } catch (Throwable e) {
      System.out.println("Oh no! It looks like an error has occurred.");

      try {
        BufferedWriter writer = new BufferedWriter(new FileWriter("error.log"));
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        writer.write(sw.toString());
        writer.close();
      } catch (IOException e2) {
        System.out.println("A truly fatal error has occurred. I cannot open a file to write the original application error. Please log this to the same website as above.");
        e2.printStackTrace();
      }

      e.printStackTrace();
    }
  }
}
