package com.bluetouch.function;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluetouch.R;
import com.bluetouch.util.SharedPrefsUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class WeatherActivity extends Activity implements TextToSpeech.OnInitListener {

    private TextView tv;
    private TextView wfKor_text;
    private TextView tmn_text;
    private TextView r24_text;
    private TextView ws_text;
    private TextView wdKor_text;
    private LinearLayout weather_layout;
    private LinearLayout weather_background;
    private ImageView weather_image;
    private RelativeLayout progress_layout;

    private String result;
    private String day = "";
    private String hour = "";
    private String sky = "";
    private String temp = "";
    private String ws = "";
    private String wdKor = "";
    private String r24 = "";
    private String tmx = "";
    private String tmn = "";
    private Location myLocation = null;
    private double latPoint, lngPoint;
    private LocationManager locManager;
    private Geocoder geoCoder;
    private StringBuffer mAddress = new StringBuffer();
    private TextToSpeech textToSpeech;
    private  String speech;

    static double RE = 6371.00877; // 지구 반경(km)
    static double GRID = 5.0; // 격자 간격(km)
    static double SLAT1 = 30.0; // 투영 위도1(degree)
    static double SLAT2 = 60.0; // 투영 위도2(degree)
    static double OLON = 126.0; // 기준점 경도(degree)
    static double OLAT = 38.0; // 기준점 위도(degree)
    static double XO = 43; // 기준점 X좌표(GRID)
    static double YO = 136; // 기1준점 Y좌표(GRID)
    static double gridx;
    static double gridy;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        tv = (TextView) findViewById(R.id.tv);
        wfKor_text = (TextView) findViewById(R.id.wfKor_text);
        tmn_text = (TextView) findViewById(R.id.tmn_text);
        r24_text = (TextView) findViewById(R.id.r24_text);
        ws_text = (TextView) findViewById(R.id.ws_text);
        wdKor_text = (TextView) findViewById(R.id.wdKor_text);
        weather_layout = (LinearLayout) findViewById(R.id.weather_layout);
        weather_background = (LinearLayout) findViewById(R.id.weather_background);
        weather_image = (ImageView) findViewById(R.id.weather_image);
        progress_layout = (RelativeLayout) findViewById(R.id.progress_layout);

        textToSpeech = new TextToSpeech(this, this);

        new ProcessFacebookTask().execute();
        weather_background.setVisibility(View.GONE);
        progress_layout.setVisibility(View.VISIBLE);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("WEATHER_DATA");
        registerReceiver(mRecevier, intentFilter);
    }

    private void test() {  //기상청에서 정보 읽어오는 함수
        try {
            String html = loadKmaData();

            ByteArrayInputStream bai = new ByteArrayInputStream(html.getBytes());
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbf.newDocumentBuilder();
            Document parse = builder.parse(bai);//DOM 파서
            NodeList datas = parse.getElementsByTagName("data");
            result = "";
            for (int idx = 0; idx < 1; idx++) {

                Node node = datas.item(idx);//data 태그 추출
                int childLength = node.getChildNodes().getLength();
                NodeList childNodes = node.getChildNodes();
                for (int childIdx = 0; childIdx < childLength; childIdx++) {
                    Node childNode = childNodes.item(childIdx);
                    int count = 0;
                    if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                        // count ++;
                        //태그인 경우만 처리
                        //금일,내일,모레 구분(시간정보 포함)
                        if (childNode.getNodeName().equals("day")) {
                            int su = Integer.parseInt(childNode.getFirstChild().getNodeValue());
                            switch (su) {
                                case 0:
                                    day = "금일";
                                    break;
                                case 1:
                                    day = "내일";
                                    break;
                                case 2:
                                    day = "모레";
                                    break;
                            }
                        } else if (childNode.getNodeName().equals("hour")) { //시
                            hour = childNode.getFirstChild().getNodeValue();
                            //하늘상태코드 분석
                        } else if (childNode.getNodeName().equals("wfKor")) { //날씨 상태
                            sky = childNode.getFirstChild().getNodeValue();
                        } else if (childNode.getNodeName().equals("temp")) { //기온
                            temp = childNode.getFirstChild().getNodeValue();
                        } else if (childNode.getNodeName().equals("ws")) {  //풍속
                            ws = childNode.getFirstChild().getNodeValue();
                        } else if (childNode.getNodeName().equals("wdKor")) {  //풍향
                            wdKor = childNode.getFirstChild().getNodeValue();
                        } else if (childNode.getNodeName().equals("r12")) {  //강수량
                            r24 = childNode.getFirstChild().getNodeValue();
                        } else if (childNode.getNodeName().equals("tmx")) {  //최고기온
                            tmx = childNode.getFirstChild().getNodeValue();
                        } else if (childNode.getNodeName().equals("tmn")) {  //최저기온
                            tmn = childNode.getFirstChild().getNodeValue();
                        }
                    }
                }
                result += day + " " + hour + "시 (" + sky + "," + temp + "도)\n";
            }
        } catch (Exception e) {
            tv.setText("오류" + e.getMessage());
            e.printStackTrace();
        }
    }


    //기상청 날씨정보 추출
    private String loadKmaData() throws Exception {
        String page = "http://www.kma.go.kr/wid/queryDFS.jsp?gridx=" + String.valueOf(gridx).substring(0, String.valueOf(gridx).indexOf(".")) + "&gridy=" + String.valueOf(gridy).substring(0, String.valueOf(gridy).indexOf("."));
        Log.e("x+y", String.valueOf(gridx).substring(0, String.valueOf(gridx).indexOf(".")) + "," + gridy);
        URL url = new URL(page);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        if (urlConnection == null) return null;
        urlConnection.setConnectTimeout(10000);//최대 10초 대기
        urlConnection.setUseCaches(false);//매번 서버에서 읽어오기
        StringBuilder sb = new StringBuilder();//고속 문자열 결합체
        if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = urlConnection.getInputStream();
            InputStreamReader isr = new InputStreamReader(inputStream);

            //한줄씩 읽기
            BufferedReader br = new BufferedReader(isr);
            while (true) {
                String line = br.readLine();//웹페이지의 html 코드 읽어오기
                if (line == null) break;//스트림이 끝나면 null리턴
                sb.append(line + "\n");
            }//end while
            br.close();
        }//end if
        return sb.toString();
    }

    public void getGeoLocation() { //위도 경도 읽는 함수

        if (myLocation != null) {
            latPoint = myLocation.getLatitude();
            lngPoint = myLocation.getLongitude();
            try {
                // 위도,경도를 이용하여 현재 위치의 주소를 가져온다.
                List<Address> addresses;
                addresses = geoCoder.getFromLocation(latPoint, lngPoint, 1);
                for (Address addr : addresses) {
                    int index = addr.getMaxAddressLineIndex();
                    for (int i = 0; i <= index; i++) {
                        mAddress.append(addr.getAddressLine(i));
                        mAddress.append(" ");
                    }
                    mAddress.append("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onInit(int status) {  //스피치 초기화 함수
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.KOREA);
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("error", "This Language is not supported");
            } else {
                // convertTextToSpeech("");
            }
        } else {
            Log.e("error", "Initilization Failed!");
        }
    }

    private class ProcessFacebookTask extends AsyncTask<Void, Void, Void> implements LocationListener {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            }
            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 60000, this);
            locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 60000, this);
            geoCoder = new Geocoder(getApplicationContext(), Locale.KOREA);
        }

        @Override
        protected Void doInBackground(Void... params) {

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        public void onLocationChanged(Location location) {
            myLocation = location;
            latPoint = myLocation.getLatitude();
            lngPoint = myLocation.getLongitude();

            Dif_XY_Conv(latPoint, lngPoint); //위뎡도 ->격자정보 변경 함수
            getGeoLocation();  //주소 get 함수
            new Finish_Location().execute();

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    private class Finish_Location extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                test();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            tv.setText(mAddress);
            wfKor_text.setText(sky);
            tmn_text.setText(temp + "º");
            r24_text.setText(r24 + "mm");
            if (ws.length() >= 3) {
                ws_text.setText(ws.substring(0, 3) + "m/s");
            } else {
                ws_text.setText(ws + "m/s");
            }
            wdKor_text.setText(wdKor);
            if (sky.equals("맑음")) {
                // weather_background.setBackground(getResources().getDrawable(R.drawable.sun_background));
                weather_image.setBackground(getResources().getDrawable(R.drawable.sun));
            } else if (sky.equals("흐림")) {
                //  weather_background.setBackground(getResources().getDrawable(R.drawable.cloud_background));
                weather_image.setBackground(getResources().getDrawable(R.drawable.cloud_image));
            } else if (sky.equals("비")) {
                // weather_background.setBackground(getResources().getDrawable(R.drawable.rain_background));
                weather_image.setBackground(getResources().getDrawable(R.drawable.rain_image));
            } else if (sky.equals("구름 많음")) {
                //   weather_background.setBackground(getResources().getDrawable(R.drawable.cloud_hieght_background));
                weather_image.setBackground(getResources().getDrawable(R.drawable.cloud_height_lmage));
            } else if (sky.equals("구름 조금")) {
                //   weather_background.setBackground(getResources().getDrawable(R.drawable.cloud_low_background));
                weather_image.setBackground(getResources().getDrawable(R.drawable.cloud_height_lmage));
            } else if (sky.equals("눈")) {
                weather_image.setBackground(getResources().getDrawable(R.drawable.snow_image));
            }
            weather_background.setVisibility(View.VISIBLE);
            progress_layout.setVisibility(View.GONE);
            // String speech = "안녕하세요 주인님 날씨 브리핑 하겠습니다. 현재 위치는 " + mAddress + " 이며 " + " 날씨 상태는 " + sky + "입니다 기온은" + temp + " 도 이고 풍속은 " + ws + " 밀리 세크 풍향은 " + wdKor + " 입니다.";
            if(sky.equals("비")){
               speech = "안녕하세요 주인님 날씨 브리핑 하겠습니다. 현재 위치는 " + mAddress + " 이며 " + " 날씨 상태는 " + sky + "가 옵니다 외출 하실 때 우산을 챙기세요. 기온은" + temp + " 도 입니다. 좋은 하루 보내세요.";
            }else{
                speech = "안녕하세요 주인님 날씨 브리핑 하겠습니다. 현재 위치는 " + mAddress + " 이며 " + " 날씨 상태는 " + sky + "입니다 기온은" + temp + " 도 입니다. 좋은 하루 보내세요.";
            }
            convertTextToSpeech(speech);
        }
    }

    public static void Dif_XY_Conv(double v1, double v2) {  //위경도 -> 기상청 격자 정보
        double DEGRAD = Math.PI / 180.0;
        double RADDEG = 180.0 / Math.PI;

        double re = RE / GRID;
        double slat1 = SLAT1 * DEGRAD;
        double slat2 = SLAT2 * DEGRAD;
        double olon = OLON * DEGRAD;
        double olat = OLAT * DEGRAD;


        double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
        double sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
        double ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
        ro = re * sf / Math.pow(ro, sn);

        double ra = Math.tan(Math.PI * 0.25 + (v1) * DEGRAD * 0.5);
        ra = re * sf / Math.pow(ra, sn);
        double theta = v2 * DEGRAD - olon;
        if (theta > Math.PI) theta -= 2.0 * Math.PI;
        if (theta < -Math.PI) theta += 2.0 * Math.PI;
        theta *= sn;
        gridx = Math.floor(ra * Math.sin(theta) + XO + 0.5);
        gridy = Math.floor(ro - ra * Math.cos(theta) + YO + 0.5);
    }

    private void convertTextToSpeech(String text) {

        //String text = "현재 날씨";
        if (null == text || "".equals(text)) {
            // text = "Please give some input.";
        }
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    private BroadcastReceiver mRecevier = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("WEATHER_DATA")) {
                int data = intent.getIntExtra("data_weather", 0);
                Log.e("WEATHER_DATA", String.valueOf(data));
                if (data == 1) {
                } else if (data == 2) {
                } else if (data == 3) {
                    finish();
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mRecevier);
        SharedPrefsUtils.setStringPreference(getApplicationContext(), "Activity_flag", null);
    }
}
