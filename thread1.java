
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.io.File;





public class thread1 implements Runnable {
    ArrayList<String> defect = new ArrayList();
    private String RFID;
    public static String IP;
    public static String mncse = "http://192.168.99.100:82/~/mn-cse/mn_namew/mn_ae/";
    public static String mnname = "http://192.168.99.100:82/~/mn-cse/mn_namew/";
    public static String mn_cse = "http://192.168.99.100:82/~/mn-cse";
    public static String mn_ae;

    public static String outport = "";
    public static String mn_name;
    public static String ae_name;
    public static String second_port;


    static ExecutorService executor = Executors.newFixedThreadPool(5);

    public thread1(String RFID) {
        this.RFID = RFID;
    }

    public static void main(String[] args) {
        try {
            outport = args[0];
            mn_name = args[1];
            ae_name = args[2];
            second_port = args[3];
            IP = args[4];
            IP = "localhost";
            mncse = "http://" + IP + ":" + outport + "/~/mn-cse/" + mn_name + "/" + ae_name + "/";
            mnname = "http://" + IP + ":" + outport + "/~/mn-cse/" + mn_name + "/";
            mn_cse = "http://" + IP + ":" + outport + "/~/mn-cse";
            create_ae();
            //int tmp = Integer.valueOf(outport);
            while(true) {
                try{
		    
                    get_R();
                    Thread.sleep(1000);
                }
                catch(InterruptedException e){
                    System.out.println("interrupt");
                }
            }
            //serversocket(tmp);

            /*
             * //System.out.println(mncse); File file = new File(""); String path =
             * file.getAbsolutePath(); // path = file.getPath(); // path = path +
             * "data.txt"; // String path = path = path + "/data.txt";
             * System.out.println(path); FileReader in = new FileReader(path);
             * BufferedReader br = new BufferedReader(in);
             * 
             * while (br.ready()) { String RFID = br.readLine(); System.out.println(RFID);
             * 
             * }
             * 
             * br.close();
             */
        } catch (Exception E) {

        }
    }

    // thread start point
    public void run() {
        int flag = 0;
        send_request_post(RFID);
        flag = stage1(RFID, flag);
        flag = stage2(RFID, flag);
        flag = stage3(RFID, flag);
        stage4(RFID, flag);
    }

    public synchronized int stage1(String RFID, int flag) {
       // sensor1 sensor = new sensor1();
       // sensor.send_Data(mncse, RFID, 1);
        flag = send_request_mnae("sensor1", RFID, flag);
        return flag;
    }

    public synchronized int stage2(String RFID, int flag) {
       // sensor1 sensor = new sensor1();
       // sensor.send_Data(mncse, RFID, 2);
        flag = send_request_mnae("sensor2", RFID, flag);
        return flag;
    }

    public synchronized int stage3(String RFID, int flag) {
       // sensor1 sensor = new sensor1();
       // sensor.send_Data(mncse, RFID, 3);
        flag = send_request_mnae("sensor3", RFID, flag);
        return flag;
    }

    public synchronized void stage4(String RFID, int flag) {
    	
  	 if (flag == 1) {
            send_second_level_mncse(RFID);
        }

    }

    public static void get_R(){
        try {
            // http://192.168.99.100:788/~/mn-cse/mn-name/mn_ae/defected?fu=1&ty=4
            String get_ip = "http://localhost:877" +"/~/mn-cse/mn-name/mn_ae/?fu=1&ty=3";
            URL url = new URL(get_ip);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
	                
	    
            http.setRequestProperty("Accept", "application/xml");
            http.setRequestProperty("X-M2M-Origin", "admin:admin");
            http.setDoInput(true);
	    http.setRequestMethod("GET");
	    
            InputStream in = http.getInputStream();
            
	    if (in != null) {
                InputStreamReader reader = new InputStreamReader(in, "UTF-8");
                BufferedReader input = new BufferedReader(reader);
                String line = "";
                String result = "";
                while ((line = input.readLine()) != null) {
                    result += (line + "\n");
                }
                reader.close();
                int status = http.getResponseCode();
                System.out.println(status);
		
                get_value(result);
            } else
                System.out.println("failed");

            in.close();

        } catch (IOException e) {
            //System.out.println(e);
        }
    }

    public static void get_value(String result) {
        ArrayList<String> list = new ArrayList<String>();
        String tmp[] = result.split("mn_ae/");
        String tmp2[];
        int i = 1;
        for (i = 1; i < tmp.length - 1; i++) {
            tmp2 = tmp[i].split(" /mn-cse");
            //System.out.println(tmp2[0]);
            list.add(tmp2[0]);

            // ////////////////////////////////////////////
            Runnable t1 = new thread1(tmp2[0]);
            executor.execute(t1);
            // ///////////////////////////////////////////
        }
        System.out.println(list);
    }



    public static void serversocket(int outport) {
        while (true) {
            // server socket
            try {
                ServerSocket socket = new ServerSocket(outport);
                Socket sock = socket.accept();
                BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                String RFID = "";
                while ((RFID = br.readLine()) != null) {
                    System.out.println(RFID);
                    Runnable t1 = new thread1(RFID);
                    executor.execute(t1);

                }
            }
            catch (IOException e) {

            }
            
        }
    }

    // there are 10% defect rate
    public void rand(String RFID) {
        Random rand = new Random();
        int r = rand.nextInt(9);
        // System.out.println(r);
        if (r == 0) {
            // flag=1;
        }
    }

    public static void create_ae() {
        try {
            URL url = new URL(mn_cse);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setDoOutput(true);
            // http.setRequestProperty("Accept", "application/json");
            http.setRequestProperty("X-M2M-Origin", "admin:admin");
            http.setRequestProperty("Content-Type", "application/json;ty=2");
            try {
                http.setRequestMethod("POST");
                http.connect();
                DataOutputStream out = new DataOutputStream(http.getOutputStream());
                //'{"m2m:ae": {"rn": "mn_ae", "api": "placeholder", "rr": "TRUE"}}'
                String request = "{" + '"' + "m2m:ae" + '"' + ": {" + '"' + "rn" + '"' + ": " + '"' + ae_name + '"'
                        +","+ '"'+"api"+'"'+":"+'"'+"test"+'"'+","+'"'+"rr"+'"'+":"+'"'+"FALSE"+'"'+"}}";
                out.write(request.toString().getBytes("UTF-8"));
                out.flush();
                out.close();
                int satus = http.getResponseCode();
                System.out.println(satus);
                // System.out.println(mncse);
                // System.out.println(request);

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {

        }

    }



    
    public void send_request_post(String RFID) {

        try {
            URL url = new URL(mncse);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setDoOutput(true);
            http.setRequestProperty("X-M2M-Origin", "admin:admin");
            http.setRequestProperty("Content-Type", "application/json;ty=3");
            try {
                http.setRequestMethod("POST");
                http.connect();
                DataOutputStream out = new DataOutputStream(http.getOutputStream());
                String request = "{" + '"' + "m2m:cnt" + '"' + ": {" + '"' + "rn" + '"' + ": " + '"' + RFID + '"'
                        + "}}";
                out.write(request.toString().getBytes("UTF-8"));
                out.flush();
                out.close();
                int satus = http.getResponseCode();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {

        }

    }

    public int send_request_mnae(String sensor, String RFID, int flag) {
        try {
            //URL url = new URL(mncse + RFID + "/" + sensor);
    	   // System.out.println(RFID + " "+sensor);
	    String path = "http://localhost:877/~/mn-cse/mn-name/mn_ae/"+RFID+"/"+sensor;
	   // System.out.println(path);
	    URL url = new URL(path);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setRequestProperty("Accept", "application/xml");
            http.setRequestProperty("X-M2M-Origin", "admin:admin");
            // http.setRequestProperty("Authorization",basicAuth);
            http.setDoInput(true);
            http.setRequestMethod("GET");

           // int satus = http.getResponseCode();
           // System.out.println(satus);
            InputStream in = http.getInputStream();

            if (in != null) {
                InputStreamReader reader = new InputStreamReader(in, "UTF-8");
                BufferedReader input = new BufferedReader(reader);
                String line = "";
                String result = "";
                while ((line = input.readLine()) != null) {
                    result += (line + "\n");
                }
	    //System.out.println(result);
                flag = get_value(result, RFID, sensor, flag);
            } else
                System.out.println("failed");
	    //System.out.println("asd");
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            System.out.println("mal");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IOE");
        }
        return flag;
    }

/////////////////////////////
//////////////////////////////+
//////////////////////////////

    public static void send_second_level_mncse(String RFID) {
        try {
            String path = "http://" +"192.168.99.103" + ":"+ second_port +"/~/mn-cse/mn-name/mn_ae/defected";
            URL url = new URL(path);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setDoOutput(true);
            // http.setRequestProperty("Accept", "application/json");
            http.setRequestProperty("X-M2M-Origin", "admin:admin");
            http.setRequestProperty("Content-Type", "application/json;ty=4");
            try {
                http.setRequestMethod("POST");
                http.connect();
                DataOutputStream out = new DataOutputStream(http.getOutputStream());
                String request = "{" + '"' + "m2m:cin" + '"' + ": {" + '"' + "con" + '"' + ": " + '"' + RFID + '"'
                        + ", " + '"' + "cnf" + '"' + ": " + '"' + "text/plain:0" + '"' + "," + '"' + "rn" + '"' + ": "
                        + '"' + RFID + '"' + "}}";
                // '{"m2m:cin": {"con": "EXAMPLE_VALUE", "cnf": "text/plain:0"}}'
                out.write(request.toString().getBytes("UTF-8"));
                out.flush();
                out.close();
                int satus = http.getResponseCode();
                // System.out.println(satus);
                // System.out.println(mncse);
                // System.out.println(request);

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {

        }
    }

    public int get_value(String result, String RFID, String sensor, int flag) {
        String[] sp = result.split("<con>");
        String value = sp[1];
        String[] sp2 = value.split("</con>");
        value = sp2[0];
        // System.out.println(value);
	
	if(flag == 1){
	return flag;
	}
	
        if (sensor == "sensor1") {
            if (Integer.valueOf(value) < 605) {
                flag = 1;
                // System.out.println("sensor1");
            }

        }
        if (sensor == "sensor2") {
            if (value.equals("0"))
                flag = 1;
        }
        if (sensor == "sensor3") {
            if (value.equals("0"))
                flag = 1;
        }
	//System.out.println(flag);
        return flag;
    }

}
