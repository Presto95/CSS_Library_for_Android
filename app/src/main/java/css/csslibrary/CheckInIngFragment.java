package css.csslibrary;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import css.csslibrary.Adapter.Adapter;
import css.csslibrary.model.Book;

public class CheckInIngFragment extends Fragment {

    private ListView listView;
    private List<Book> items;
    private Adapter adapter;
    private TextView textView;
    private String data;
    private boolean isNoData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_check_in_ing,container,false);
    }

    @Override
    public void onStart() {
        super.onStart();

        textView = (TextView) getView().findViewById(R.id.text_checkining);

        try {
            CustomTask task=new CustomTask();
            String result=task.execute().get();
            items=setList(result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        listView=(ListView)getView().findViewById(R.id.list_view_checkining);
        adapter=new Adapter(getActivity(),R.layout.list_checkin,items);
        if(isNoData){
            textView.setText("검색 결과가 없습니다.");
        }
        else{
            textView.setVisibility(View.GONE);
            listView.setAdapter(adapter);
        }

    }

    private List<Book> setList(String result){
        List<Book> list=new ArrayList<>();
        data=result;
        if(result.equals("")) {
            isNoData = true;
            list.add(new Book(0, "", "", "", ""));
        }else{
            isNoData=false;
            String[] array1;
            String[] array2;
            array1=data.split(":");
            for(int i=0;i<array1.length;++i){
                array2=array1[i].split(";");
                list.add(new Book(0,array2[0],array2[1],array2[2],array2[3]));
            }
        }
        return list;

    }

    class CustomTask extends AsyncTask<String, Void, String> {
        String sendMsg, receiveMsg;
        @Override
        protected String doInBackground(String... strings) {
            try {
                String str;
                URL url = new URL("http://220.149.124.129:8080/CSSLibrary/checkining.jsp");//보낼 jsp 주소를 ""안에 작성합니다
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");//데이터를 POST 방식으로 전송합니다.
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                //sendMsg = "loginId="+strings[0]+"&loginPassword="+strings[1];//보낼 정보인데요. GET방식으로 작성합니다. ex) "id=rain483&pwd=1234";
                //회원가입처럼 보낼 데이터가 여러 개일 경우 &로 구분하여 작성합니다.
                //osw.write(sendMsg);//OutputStreamWriter에 담아 전송합니다.
                osw.flush();
                //jsp와 통신이 정상적으로 되었을 때 할 코드들입니다.
                if(conn.getResponseCode() == conn.HTTP_OK) {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuffer buffer = new StringBuffer();
                    //jsp에서 보낸 값을 받겠죠?
                    while ((str = reader.readLine()) != null) {
                        buffer.append(str);
                    }
                    receiveMsg = buffer.toString();

                } else {
                    Log.i("통신 결과", conn.getResponseCode()+"에러");
                    // 통신이 실패했을 때 실패한 이유를 알기 위해 로그를 찍습니다.
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //jsp로부터 받은 리턴 값입니다.
            return receiveMsg;
        }
    }
}
