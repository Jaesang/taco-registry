package com.registry.util;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.regex.Pattern;

/**
 * Created by boozer on 2019. 6. 18
 */
@Component
public class FileUtil {

    protected static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Public Method
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    // 파일 생성
    public void createFile(String path, String content) {

        // temp 파일 생성
        File file = null;
        FileWriter fileWriter = null;
        BufferedWriter bw = null;
        try {
            file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            } else {
                file.delete();
                file.createNewFile();
            }

            // 내용 입력
            fileWriter = new FileWriter(file);
            bw = new BufferedWriter(fileWriter);
            bw.write(content);
            bw.flush();

        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage());
        } finally {
            if(fileWriter != null) try {fileWriter.close(); } catch (IOException e) {}
            if(bw != null) try {bw.close(); } catch (IOException e) {}
        }
    }

    // 파일을 복사
    public void fileCopy(String inFilePath, String outFilePath) {

        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(inFilePath);
            fos = new FileOutputStream(outFilePath);

            int data = 0;
            while((data=fis.read())!=-1) {
                fos.write(data);
            }
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage());
        } finally {
            if(fis != null) try {fis.close(); } catch (IOException e) {}
            if(fos != null) try {fos.close(); } catch (IOException e) {}
        }
    }

    // 파일 내용 읽기
    public String readFileContent(String path) {

        BufferedReader bufferedTextFileReader = null;
        FileReader fileReader = null;
        StringBuilder contentReceiver = new StringBuilder();

        try {
            fileReader = new FileReader(path);
            bufferedTextFileReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedTextFileReader.readLine()) != null) {
                contentReceiver.append(line);
                contentReceiver.append(System.getProperty("line.separator"));
            }

        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage());
        } finally {
            if(bufferedTextFileReader != null) try {bufferedTextFileReader.close(); } catch (IOException e) {}
            if(fileReader != null) try {fileReader.close(); } catch (IOException e) {}
        }

        return contentReceiver.toString();
    }

    // template file read
    public String getTemplateFile(String path) {

        String result = "";

        ClassLoader classLoader = getClass().getClassLoader();
        try {
            result = IOUtils.toString(classLoader.getResourceAsStream(path));
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        return result;
    }

    // rename file
    public void renameFile(String path, String renamePath) {
        File file;
        File destFile;
        try {
            file = new File(path);
            destFile = new File(renamePath);
            if (file.exists()) {
                file.renameTo(destFile);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    static final String ILLEGAL_EXP = "[:\\\\/%*?:|\"<> ]";
    public boolean isValidFileName(String fileName) {
        if(fileName == null || fileName.trim().length() == 0)
            return false;

        return !Pattern.compile(ILLEGAL_EXP).matcher(fileName).find();
    }

    public String makeValidFileName(String fileName) {
        if(fileName == null || fileName.trim().length() == 0)
            return String.valueOf(System.currentTimeMillis());

        return fileName.replaceAll(ILLEGAL_EXP, "_");
    }
}
