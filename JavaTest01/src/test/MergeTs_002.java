package test;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
 

//https://blog.csdn.net/weixin_46062098/article/details/116356617

public class MergeTs_002 {
   //安装ffmpeg的存放路径
   private static final String FFMPEG = "C:\\Users\\MI\\Downloads\\ffmpeg-5.0.1-full_build\\bin\\ffmpeg.exe";
   //提取的音频、合成的视频存放路径，不存在会自动创建
   private static final int MAX_FILE_COUNT=850;
   private static  String tsFilePath = "C:\\Users\\MI\\Downloads\\index10\\";
   private static final String MP4_PATH_FOLDER = tsFilePath+"mp4\\";
   
   static {
      //如果没有文件夹，则创建
      File saveMediaFile = new File(tsFilePath);
      if (!saveMediaFile.exists() || !saveMediaFile.isDirectory()) {
         saveMediaFile.mkdirs();
      }
      File mp4Dir = new File(MP4_PATH_FOLDER);
      if (!mp4Dir.exists() || !mp4Dir.isDirectory()) {
    	  mp4Dir.mkdirs();
      }
   }
   
   public static void main(String[] args) throws IOException {
      //查找目录下的所有ts文件
      List<String> videoList = new ArrayList<>();
      File tsDir = new File(tsFilePath);
      for(int i=0; i<tsDir.listFiles().length;i++) {
    	  String path = tsDir.listFiles()[i].getAbsolutePath();
    	  if(path.endsWith(".ts")) {
    		  videoList.add(path);
    	  }
      }
      
      //排序
      for(int i=0; i<videoList.size();i++) {
    	  int index01 =Integer.valueOf(videoList.get(i).substring(videoList.get(i).lastIndexOf("\\")+1,videoList.get(i).lastIndexOf(".")));
    	  for(int j=i+1;j<videoList.size();j++) {
	    	  int index02 =Integer.valueOf(videoList.get(j).substring(videoList.get(j).lastIndexOf("\\")+1,videoList.get(j).lastIndexOf(".")));
//	    	  System.out.println(index01+"--"+index02);
	    	  if(index01>index02) {
	    		  String tmp = videoList.get(i);
	    		  videoList.set(i,videoList.get(j));
	    		  videoList.set(j,tmp);
	        	  index01 =Integer.valueOf(videoList.get(i).substring(videoList.get(i).lastIndexOf("\\")+1,videoList.get(i).lastIndexOf(".")));
	    	  }
    	  }
      }
      for(int i=0; i<videoList.size();i++) {
    	  System.out.println(videoList.get(i));
      }      
      

//    //多视频拼接合并为一个mp4格式视频
      List<String> tmpTSVideoList = new ArrayList<>();
//      for(int i=0; i<(videoList.size()/MAX_FILE_COUNT+1);i++) {
//    	  List<String> partTsVideoList = null;
//    	  if(i==videoList.size()/MAX_FILE_COUNT) {
//    		  partTsVideoList =videoList.subList(i*MAX_FILE_COUNT, videoList.size());
//    	  }else {
//    		  partTsVideoList=videoList.subList(i*MAX_FILE_COUNT, (i+1)*MAX_FILE_COUNT);
//    	  }
//    	  
//    	  String mp4AbsolutePath = mergelistVideos(partTsVideoList, false);
//    	  
//          List<String> tmpMp4VideoList = new ArrayList<>();
//          tmpMp4VideoList.add(mp4AbsolutePath);
//    	  String tmpTSAbsolutePath = mergelistVideos(tmpMp4VideoList, true);
//    	  tmpTSVideoList.add(tmpTSAbsolutePath);
//      }
      tmpTSVideoList.add("C:\\Users\\MI\\Downloads\\index10\\mp4\\视频合并2022-08-13-17-16-16.ts");
      tmpTSVideoList.add("C:\\Users\\MI\\Downloads\\index10\\mp4\\视频合并2022-08-13-17-16-24.ts");
//      tmpTSVideoList.add("");
      mergelistVideos(tmpTSVideoList, false);
   }
   
   public static String mergelistVideos(List<String> videoList,  boolean transferMp4ToTS) {
      //时间作为合并后的视频名
	  DateTimeFormatter f3 = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
      LocalDate nowdata = LocalDate.now();
      LocalTime nowTime = LocalTime.now();
      String getdatatime = nowdata.atTime(nowTime).format(f3);
      String outputMp4AbsolutePath = MP4_PATH_FOLDER+"视频合并" + getdatatime;
      if(transferMp4ToTS) {
    	  outputMp4AbsolutePath = outputMp4AbsolutePath+ ".ts";
      }else {
    	  outputMp4AbsolutePath = outputMp4AbsolutePath+ ".mp4";    	  
      }
      
      List<String> command1List = new ArrayList<>();
      command1List.add(FFMPEG);
      //多个ts文件合并成一个mp4文件
//    ffmpeg -i "concat:test1.ts|test2.ts" -c copy -bsf:a aac_adtstoasc -movflags +faststart output.mp4
      //将单个mp4文件转换成一个ts文件
//    ffmpeg -i test1.mp4 -vcodec copy -acodec copy -vbsf  h264_mp4toannexb test1.ts
      command1List.add("-i");
      StringBuffer inputParamsStringBbuffer=null;
      if(transferMp4ToTS) {
    	  inputParamsStringBbuffer = new StringBuffer("\"");
      }else {
    	  inputParamsStringBbuffer = new StringBuffer("\"concat:");
      }
      for (int i = 0; i < videoList.size(); i++) {
         inputParamsStringBbuffer.append(videoList.get(i));
         if (i != videoList.size() - 1) {
            inputParamsStringBbuffer.append("|");
         } else {
            inputParamsStringBbuffer.append("\"");
         }
      }
      command1List.add(String.valueOf(inputParamsStringBbuffer));
      if(transferMp4ToTS) {
    	  command1List.add("-vcodec");
    	  command1List.add("copy");
    	  command1List.add("-acodec");
    	  command1List.add("copy");
    	  command1List.add("-vbsf");
    	  command1List.add(" h264_mp4toannexb");
//      command1List.add("-f");
//       command1List.add("mpegts");
      }else {
	      command1List.add("-c");
	      command1List.add("copy");
	      command1List.add("-bsf:a");
	      command1List.add("aac_adtstoasc");
	      command1List.add("-movflags");
	      command1List.add("+faststart");
      }
      command1List.add(outputMp4AbsolutePath);
      commandStart(command1List);
      return outputMp4AbsolutePath;
   }
 
   public static void commandStart(List<String> commandList) {
      commandList.forEach(v -> System.out.print(v + " "));
      System.out.println();
      System.out.println();
      ProcessBuilder builder = new ProcessBuilder();
      //正常信息和错误信息合并输出
      builder.redirectErrorStream(true);
      builder.command(commandList);
      try {
    	  //开始执行命令
    	  Process process = builder.start();
         //如果你想获取到执行完后的信息，那么下面的代码也是需要的
         String line = "";
         BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
         while ((line = br.readLine()) != null) {
//            System.out.println(line);
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
}

