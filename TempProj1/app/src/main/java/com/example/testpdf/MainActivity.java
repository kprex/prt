package com.example.testpdf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;

import static java.io.FileDescriptor.err;
import static java.io.FileDescriptor.in;
import static java.io.FileDescriptor.out;

/**
 * 生成pdf文档到SD卡下，byte.pdf,可以生成中文字符 所用jar包是自己修改过的，将字体植入jar包内
 *
 * @author yt
 * @date 2015-1-15
 */
public class MainActivity extends Activity {

    Button btnPdf, btnFile, btnPrint;
    private static FileInputStream fileInput;
    ArrayList<String> fileName;
    ArrayList<String> safeFileName;
    ByteArrayOutputStream byteArrayOutputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        fileName = new ArrayList<String>();
        safeFileName = new ArrayList<String>();
    }

    void init() {
        btnPdf = (Button) findViewById(R.id.button_pdf);
        btnFile = (Button) findViewById(R.id.btn_file);
        btnPrint = (Button) findViewById(R.id.btn_print);

        btnPdf.setOnClickListener(new MyListener());
        btnFile.setOnClickListener(new MyListener());
        btnPrint.setOnClickListener(new MyListener());
    }

    public class MyListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.btn_file:
                    Intent intent = new Intent(MainActivity.this,
                            FilesViewActivity.class);
                    MainActivity.this.startActivityForResult(intent, 0);
                    break;

                case R.id.button_pdf:
                    new PDFThread().start();
                    break;
                case R.id.btn_print:
                    new PrintThread().start();
                    break;
                default:
                    break;
            }
        }

    }

    String path = "";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        // 选择了文件发送
        System.out.println("选择了文件发送");
        if (resultCode == RESULT_OK) {
            Log.i("resultCode", "" + resultCode);
            fileName = data.getStringArrayListExtra("fileName");
            safeFileName = data.getStringArrayListExtra("safeFileName");
            Log.i("fileName", "" + fileName);
            try {
                for (int i = 0; i < fileName.size(); i++) {
                    String picName = fileName.get(i);
                    path = safeFileName.get(i);
                    System.out.println("图片名字：" + picName);
                    System.out.println("图片路径：" + safeFileName.get(i));
                    fileInput = new FileInputStream(safeFileName.get(i));
                }
                new PicThread().start();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String str = msg.obj.toString();
            Toast.makeText(MainActivity.this, str, Toast.LENGTH_LONG).show();
        }
    };

    class PrintThread extends Thread {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();
            if (TextUtils.isEmpty(pathPdf)) {
                pathPdf = Environment.getExternalStorageDirectory()
                        .getAbsolutePath() + "/droidText";
            }
            String s = "gs -q -dBATCH -dSAFER -dQUIET -dNOPAUSE -sPAPERSIZE=a4 -r600x750 -sDEVICE=pbmraw -sOutputFile=/mnt/sdcard/test.pbm "
                    + pathPdf + "/dwin.pdf";
//            String s =
//                    "gs -q -dBATCH -dSAFER -dQUIET -dNOPAUSE -sPAPERSIZE=a4 -r300x140 -sDEVICE=pbmraw -sOutputFile=/storage/sdcard0/test.pbm " + pathPdf + "/dwin.pdf";
//            Log.i("lhf", "执行格式转换命令：" + s);
            execCommand(s);
//            final String res = linux_cmd.execute(s);
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(MainActivity.this, res, Toast.LENGTH_LONG).show();
//                }
//            });
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
//             String s2 =
//             "foo2zjs -z3 -p9 -r600x600 /storage/sdcard0/test.pbm > /dev/usb/lp0";
            String s2 = "foo2zjs -z3 -p9 -r600x600 /mnt/sdcard/test.pbm > /dev/usb/lp0";
            Log.i("lhf", "执行打印命令：" + s2);
            execCommand(s2);
//            final String res2 = linux_cmd.execute(s2);
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(MainActivity.this, res2, Toast.LENGTH_LONG).show();
//                }
//            });
//            String cmd = "gs -q -dBATCH -dSAFER -dQUIET -dNOPAUSE -sPAPERSIZE=letter-r1200x600 -sDEVICE=pbmraw -sOutputFile=- - < " + pathPdf + "/dwin.pdf" + " | foo2xqx -r1200x600 -g10200x6600 -p1 >/dev/lp0";

//            final String res = linux_cmd.execute(cmd);
//            Message msg = Message.obtain();
//            msg.obj = res;
//            handler.handleMessage(msg);
//            Log.d("MainActivity.class", res);
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(MainActivity.this, res, Toast.LENGTH_LONG).show();
//                }
//            });
        }
    }

    public static class linux_cmd {
        public static String execute(String cmd) {
            ProcessBuilder pb = new ProcessBuilder("/system/bin/sh");
            String str = new String();
            pb.directory(new File("/"));//设置shell的当前目录。
            try {
                Process proc = pb.start();
//获取输入流，可以通过它获取SHELL的输出。
                BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                BufferedReader err = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
//获取输出流，可以通过它向SHELL发送命令。
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(proc.getOutputStream())), true);
                out.println(cmd);
                out.println("exit");
                String line;
                while ((line = in.readLine()) != null) {
                    str += line;
                }
                while ((line = err.readLine()) != null) {
                    System.out.println(line); //打印错误输出结果
                    str += line;
                }
                in.close();
                out.close();
                proc.destroy();
            } catch (Exception e) {
                System.out.println("exception:" + e);
            }
            return str;
        }
    }

    class PicThread extends Thread {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();
            SendFile(fileName, safeFileName);
            Message message = new Message();
            message.what = 2;
            myHandler.sendMessage(message);
        }
    }

    /**
     * 发送选择的文件
     *
     * @param fileName
     * @param path
     */
    @SuppressLint("DefaultLocale")
    public void SendFile(ArrayList<String> fileName, ArrayList<String> path) {
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            for (int i = 0; i < fileName.size(); i++) {

                // 获得文件流
                fileInput = new FileInputStream(path.get(i));
                byte[] buffer = new byte[4096];
                int size = 0;
                // 循环发送，每次发送1024byte，即1K
                while ((size = fileInput.read(buffer, 0, 4096)) > 0) {
                    Thread.sleep(100);
                    byteArrayOutputStream.write(buffer);
                    byteArrayOutputStream.flush();
                    Log.i("tag", "SendFile>>>size===" + size);
                }
            }
            byteArrayOutputStream.close();
        } catch (Exception e) {
        }
    }

    String pathPdf;

    public void createPDF() {
        Document doc = new Document();
        try {
            pathPdf = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/droidText";
            System.out.println(pathPdf);
            File dir = new File(pathPdf);
            if (!dir.exists()) {
                dir.mkdirs();
                System.out.println("创建文件夹");
            }
            File file = new File(dir, "dwin.pdf");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            PdfWriter.getInstance(doc, fileOutputStream);
            doc.open();

            Image image = Image
                    .getInstance(byteArrayOutputStream.toByteArray());
            image.setAlignment(Image.MIDDLE);
            // doc.setPageCount(3);
            doc.add(image);

        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (DocumentException de) {
            de.printStackTrace();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {
            doc.close();
            System.out.println("pdf文件已生成");
        }
    }

    class PDFThread extends Thread {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();
            createPDF();
            Message message = new Message();
            message.what = 1;
            myHandler.sendMessage(message);
        }
    }

    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Toast.makeText(MainActivity.this, "pdf文件已生成", Toast.LENGTH_LONG)
                            .show();
                    break;

                case 2:
                    Toast.makeText(MainActivity.this, "图片已传完", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }

        ;
    };

    // 产生PDF字体
    public static Font setChineseFont() {
        BaseFont bf = null;
        Font fontChinese = null;
        try {
            bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H",
                    BaseFont.NOT_EMBEDDED);
            fontChinese = new Font(bf, 12, Font.NORMAL);
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return fontChinese;
    }

    /**
     * execute command, the phone must be root,it can exctue the adb command
     *
     * @param command
     */
    private void execCommand(String command) {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");//
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
