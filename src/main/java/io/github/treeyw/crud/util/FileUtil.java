package io.github.treeyw.crud.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FileUtil {
    private FileUtil() {
    }

    public static void writeTxt(BufferedWriter bf, String content) {
        try {
            BufferedWriter output = bf;
            output.write(content);
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeTxt(String filePath, String content) {
        try {
            File f = new File(filePath);
            if (f.exists()) {
            } else {
                f.createNewFile();// 不存在则创建
            }
            BufferedWriter output = new BufferedWriter(new FileWriter(f));
            output.write(content);
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeFile(String filePath, InputStream is) {
        try {
            File f = new File(filePath);
            if (f.exists()) {
            } else {
                f.createNewFile();// 不存在则创建
            }
            FileOutputStream fos = new FileOutputStream(f);
            byte[] b = new byte[1024];
            while ((is.read(b)) != -1) {
                fos.write(b);// 写入数据
            }
            is.close();
            fos.close();// 保存数据
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeTxtByList(String filePath, List<String> list) {
        try {
            File f = new File(filePath);
            if (f.exists()) {
            } else {
                f.createNewFile();// 不存在则创建
            }
            BufferedWriter output = new BufferedWriter(new FileWriter(f));
            int j = list.size() - 1;
            for (int i = 0; i < list.size(); i++) {
                output.write(list.get(i));
                if (i != j) {
                    output.newLine();
                }
            }
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static List<String> beginLoadFile(String url) {
        if (StringUtils.isEmpty(url)) return null;
        try {
            List<String> mPaths = new ArrayList<>();

            //File file = new File( "D://nd-20200713.txt");
            File file = new File(url);
            FileInputStream fis = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line;
            while ((line = br.readLine()) != null) {
                mPaths.add(line);
            }
            fis.close();
            return mPaths;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String readTxt(InputStreamReader inputStreamReader) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(inputStreamReader);
            String lineTxt = null;
            while ((lineTxt = br.readLine()) != null) {
                sb.append(lineTxt);
            }
            return sb.toString();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static String readTxt(String file) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(file)),
                "UTF-8"))) {
            String lineTxt = null;
            while ((lineTxt = br.readLine()) != null) {
                sb.append(lineTxt);
            }
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public static List<String> readTxt2Llist(String file) {
        List<String> list = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(file)),
                    "UTF-8"));
            String lineTxt = null;
            while ((lineTxt = br.readLine()) != null) {
                list.add(lineTxt);
            }
            return list;
        } catch (Exception e) {
            return null;
        }
    }

    public static byte[] readIO(String pathStr) {
        InputStream is = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            is = new FileInputStream(pathStr);// pathStr 文件路径
            byte[] b = new byte[1024];
            int n;
            while ((n = is.read(b)) != -1) {
                out.write(b, 0, n);
            }
        } catch (Exception e) {
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                }// end try
            }// end if
        }// end try
        return out.toByteArray();
    }
}
