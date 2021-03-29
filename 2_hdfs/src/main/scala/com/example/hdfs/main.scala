package com.example.hdfs

import java.io.BufferedInputStream
import java.io.InputStream
import org.apache.hadoop.conf._
import org.apache.hadoop.fs._

object main extends App {

  private val conf = new Configuration()
  private val hdfsCoreSitePath = new Path("core-site.xml")
  private val hdfsHDFSSitePath = new Path("hdfs-site.xml")

  conf.addResource(hdfsCoreSitePath)
  conf.addResource(hdfsHDFSSitePath)

  private val fileSystem = FileSystem.get(conf)

  def lsFolder(path: String): Array[Path] = {
    val fs = fileSystem.listStatus(new Path(path))
    FileUtil.stat2Paths(fs)
  }

  def getFileNames(path: String): Array[String] = {
    lsFolder(path)
      .filter(fileSystem.getFileStatus(_).isFile())
      .map(_.getName)
  }

  def getDirNames(path: String): Array[String] = {
    lsFolder(path)
      .filter(fileSystem.getFileStatus(_).isDirectory)
      .map(_.getName)
  }

  def createFile(fileName: String): Unit = {
    val path = new Path(fileName)
    if (!fileSystem.exists(path)) {
      fileSystem.createNewFile(path)
      println(s"Create file $fileName")
    } else {
      println(s"File $fileName already exists")
    }
  }

  def removeFile(filename: String): Boolean = {
    val path = new Path(filename)
    fileSystem.delete(path, true)
  }

  def getFile(filename: String): InputStream = {
    val path = new Path(filename)
    fileSystem.open(path)
  }

  def createFolder(folderPath: String): Unit = {
    val path = new Path(folderPath)
    if (!fileSystem.exists(path)) {
      fileSystem.mkdirs(path)
    }
  }

  def removeAllFiles(inputHDFSFolderPath: String): Unit = {
    getFileNames(inputHDFSFolderPath).foreach { file =>
      removeFile(s"$inputHDFSFolderPath/$file")
    }
  }

  def copyToOutFile(inputHDFSFolderPath: String, outFilePath: String, files: Array[String]): Unit = {
    val outFile = fileSystem.append(new Path(outFilePath))
    files.foreach { file =>
      val curFilePath = s"$inputHDFSFolderPath/$file"
      val inFile = new BufferedInputStream(getFile(curFilePath))
      val b = new Array[Byte](1024)
      var numBytes = inFile.read(b)
      while (numBytes > 0) {
        outFile.write(b, 0, numBytes)
        numBytes = inFile.read(b)
      }
      inFile.close()
    }
    outFile.close()
  }

  def copyAndUnionFilesInSubDir(inputHDFSFolderPath: String, outputHDFSPath: String): Unit = {
    // create out folder
    createFolder(outputHDFSPath)

    val files = getFileNames(inputHDFSFolderPath)
      .filter(_.contains(".csv"))
      .filter(!_.contains(".inprogress"))

    if (files.length > 0) {
      val outputFileName = files.head
      val outFilePath = s"$outputHDFSPath/$outputFileName"

      // Create out file
      createFile(outFilePath)

      // Copy to out file
      copyToOutFile(inputHDFSFolderPath, outFilePath, files)

    }
  }

  def copyAndUnionFiles(inputHDFSFolder: String, outputHDFSFolder: String): Unit = {
    // input dir cycle
    getDirNames(inputHDFSFolder).foreach { dir =>

      val inputHDFSFolderPath = s"$inputHDFSFolder/$dir"
      val outputHDFSPath = s"$outputHDFSFolder/$dir"

      copyAndUnionFilesInSubDir(inputHDFSFolderPath, outputHDFSPath)

      // remove all file
      removeAllFiles(inputHDFSFolderPath)
    }
  }

  copyAndUnionFiles("/stage", "/ods")

  fileSystem.close()
}