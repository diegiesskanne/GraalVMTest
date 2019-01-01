package de.eso.dsi;

import com.google.gson.Gson;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import io.vavr.collection.Array;

import java.util.List;

public final class GraalDSIListenerDynamicProxyJsonGenerator {
  public static void main(String[] args) {
    List<String[]> strings =
        Array.ofAll(extractDsiListenerInterfaces())
            .map(ClassInfo::getName)
            .map(s -> new String[] {s})
            .toJavaList();

    Gson gson = new Gson();
    String s = gson.toJson(strings);

    System.out.println(s);
  }

  private static ClassInfoList extractDsiListenerInterfaces() {
    String pkg = "de.eso.dsi";
    try (ScanResult scanResult =
        new ClassGraph()
            // .verbose() // Log to stderr
            .enableAllInfo() // Scan classes, methods, fields, annotations
            .whitelistPackages(pkg) // Scan com.xyz and subpackages (omit to scan all packages)
            .scan()) { // Start the scan
      return scanResult.getClassesImplementing("de.eso.api.DSIListener");
    }
  }
}
