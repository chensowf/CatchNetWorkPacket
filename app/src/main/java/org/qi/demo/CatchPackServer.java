package org.qi.demo;

import android.content.Intent;
import android.net.VpnService;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.system.OsConstants;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2017/8/30.
 */

public class CatchPackServer extends VpnService implements Runnable{

    boolean isRunning;
    ParcelFileDescriptor fd;

    Thread catchPackThread;
    IPHeader ipHeader;
    TCPHeader tcpHeader;
    UDPHeader udpHeader;

    private FileOutputStream netWorkoutputStream;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = true;
        catchPackThread = new Thread(this,"catch packet thread");
        catchPackThread.start();
    }

    ParcelFileDescriptor openTun()
    {
        Builder builder = new Builder();
        builder.setMtu(20000);   //设置网络包的大小 我这里设置了最大的值最后是在1400到20000这个区间.
        builder.addAddress("10.8.0.2", 32); //设置虚拟网卡的ip地址
        builder.addDnsServer("8.8.8.8"); //设置dns地址
        builder.addRoute("0.0.0.0",0); //设置路由这里代表所有的数据包都通过这个虚拟网卡
        builder.setSession("虚拟网卡"); //设置一下字
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.allowFamily(OsConstants.AF_INET);  //设置协议ipv4的协议  这个设置是5.0之后才有的，可添加可不添加
        }
        return builder.establish();  //要是创建成功就会拿到一个读取虚拟网卡数据的文件描述符(因为操作底层硬件也是读取文件的概念哦)
    }

    @Override
    public void run() {
        fd = openTun();
        int size = 0;
        byte[] packet = new byte[20000]; //创建一个长度为20000的buffer
        ipHeader = new IPHeader(packet,0); //创建ip包头
        udpHeader = new UDPHeader(packet,20);//偏移20位
        tcpHeader = new TCPHeader(packet,20);//偏移20位
        netWorkoutputStream = new FileOutputStream(fd.getFileDescriptor()); //创建一个网卡的写入文件流
        FileInputStream netWorkinputStream = new FileInputStream(fd.getFileDescriptor());//创建一个网卡的读取文件流
        try {
        while (size != -1 && isRunning)
        {
            while((size = netWorkinputStream.read(packet)) > 0 && isRunning)
            {
                //这里处理读取出来的网卡数据进行数据的分析
                recvIpPacket(ipHeader);
            }
            Thread.sleep(100);
        }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            netWorkinputStream.close();
            netWorkoutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void recvIpPacket(IPHeader ipHeader)
    {
        switch (ipHeader.getProtocol())
        {
            case IPHeader.TCP:
                Log.d("协议类型",ipHeader.getProtocol()+" :这是tcp");
                Log.d("源ip","数据包是从:"+CommonMethods.ipIntToString(ipHeader.getSourceIP())+" 发出");
                Log.d("目标ip","数据包目的地:"+CommonMethods.ipIntToString(ipHeader.getDestinationIP()));
                break;
            case IPHeader.UDP:
                Log.d("协议类型",ipHeader.getProtocol()+" :这是udp");
                Log.d("源ip","数据包是从:"+CommonMethods.ipIntToString(ipHeader.getSourceIP())+" 发出");
                Log.d("目标ip","数据包目的地:"+CommonMethods.ipIntToString(ipHeader.getDestinationIP()));
                break;
        }
    }
}
