package com.archinamon.api

import groovy.io.FileVisitResult
import groovy.transform.CompileStatic

@CompileStatic
public class FileMethods {
  public static String relativize(final File parent, final File child) {
    final URI relativeUri = parent.toURI().relativize(child.toURI())
    return relativeUri.toString()
  }

  public static File resolve(final File parent, final String path) {
    return new File(parent, path)
  }

  public static void createParentDirectories(final File file) {
    file.parentFile?.mkdirs()
  }

  public static void copyTo(final File source, final File destination, final boolean replaceExisting = false) {
    if (!replaceExisting && destination.exists()) {
      return
    }

    source.withDataInputStream { final sourceStream ->
      destination.withDataOutputStream { final destinationStream ->
        destinationStream << sourceStream
      }
    }

    destination.lastModified = source.lastModified()
  }

  public static void copyDirectoryTo(final File source, final File destination, final boolean replaceExisting = false) {
    source.traverse { final inputFile ->
      if (!inputFile.isDirectory()) {
        final String relativePath = relativize(source, inputFile)
        final File outputFile = resolve(destination, relativePath)
        createParentDirectories(outputFile)
        copyTo(inputFile, outputFile, true)
      }
      return FileVisitResult.CONTINUE
    }
  }

  public static void deleteDirectoryIfEmpty(final File directory) {
    if (!directory.isDirectory()) {
      return
    }

    directory.delete()
  }
}
