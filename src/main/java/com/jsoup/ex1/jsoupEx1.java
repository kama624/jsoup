package com.jsoup.ex1;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jsoup.crawler.utils.Const;
import com.jsoup.crawler.utils.Crawler;

public class jsoupEx1 {

	private static Properties options = null;
	private static String tottalTitle = "과거를 훔치는 천재배우";
	public static void main(String[] args) throws Exception {

        // 파일을 저장할 경로
        File file = new File(Const.DEFAULT_DOWNSLOADS + tottalTitle + ".txt"); // File객체 생성 Test.txt
        if(!file.exists()){ // 파일이 존재하지 않으면
            file.createNewFile(); // 신규생성
        }
        // BufferedWriter 생성
        // 1754958 재벌집막내아들 1화
        // 정지됨 회차 : https://booktoki166.com/novel/1755069
        int pageNum = 9506136; //2170845;
        int treeHit = 0;
        int cnt = 1;
        StringBuilder stringBuilder = new StringBuilder();
        String inflearnUrl = "";
        String crawkubgOageUrl = "https://booktoki293.com/novel/";
        // int prevPageNum = 0;
        // int retryCnt = 0;
        try {
            while (treeHit < 382) {
		        BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
		        if(treeHit > 0 ) {
		        	pageNum = pageNum+3;
		        }
                //prevPageNum = pageNum;
				inflearnUrl = crawkubgOageUrl + pageNum ;
                LocalTime now = LocalTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH시 mm분 ss초");
                String time = now.format(formatter);
				System.out.println(time+" : inflearnUrl : " + inflearnUrl);
                try {

                    stringBuilder  = run(inflearnUrl);
                    writer.write(stringBuilder.toString());
                } catch (NullPointerException nie) {
                    System.out.println(" 해당 페이지 객체 없을 경우 처리 ");
                    // if (retryCnt > 3) {
                        pageNum = pageNum+3;
                        inflearnUrl = crawkubgOageUrl + pageNum;
                        System.out.println("inflearnUrl : " + inflearnUrl);
                        stringBuilder = run(inflearnUrl);
                        writer.write(stringBuilder.toString());
                        //prevPageNum = pageNum;
                        //nie.printStackTrace();
                        //retryCnt=0;

                    //} else {
                    //    retryCnt++;
                    //}
                }

				// 버퍼 및 스트림 뒷정리
				writer.flush(); // 버퍼의 남은 데이터를 모두 쓰기
				writer.close(); // 스트림 종료
                if (cnt == 20) {
                    System.out.println("초기화 treeHit" + treeHit);
                    System.out.println("초기화 pageNum" + pageNum);
                    Thread.sleep(300000);
                    cnt=1;
                }else {
                    cnt++;
                }
				treeHit++;
			}

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
	

    // Crawler 실행
    public static StringBuilder run(String URL) throws Exception {
        // 1. Crawler 옵션 설정
        Properties options = new Properties();
        options.put("Content-Type", "application/html;charset=UTF-8");
        options.put("downloads", Const.DEFAULT_DOWNSLOADS);
        options.put("timeout", 30*1000);
 
        // 2. Crawler 생성
        Crawler crawler = new Crawler(URL, options);
 
        // 3. HTML 파싱
        Document document = crawler.get();
 
        // 4. <a> 태그 추출.
       // Elements title =  document.getElementsByClass("toon-title");
        Elements titles =  document.selectXpath("//*[@id=\"at-main\"]/div[2]/section/article/div[1]/div/div[2]/div");
        String title = "";
        StringBuilder stringBuilder = new StringBuilder();
         for(Element novelText : titles) {
            title = novelText.text();
            System.out.println(novelText.text());

        }
/*         System.out.println(" title : " + "[ "  + title + "]");
         System.out.println(" tottalTitle : " + "[ "  + tottalTitle + "]");
         System.out.println(" 결과 tottalTitle.indexOf(title) : " + "[ "  + tottalTitle.indexOf(title) + "]");*/
         if (title.indexOf(tottalTitle) == -1 ){
             System.out.println("조회:"+title + "과 요청타이틀 : " + tottalTitle+ "달라 패스");
         }else{
             Elements novelContents =  document.getElementById("novel_content").select("P");
             stringBuilder.append(title.replaceAll(tottalTitle, "")).append("\n");

             Map<String, String> download_info = null;
             for(Element novelText : novelContents) {
                 stringBuilder.append(novelText.text()).append("\n");
             }
         }


		return stringBuilder;

    }

}
