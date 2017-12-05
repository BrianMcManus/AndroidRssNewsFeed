package ie.dkit.rssnewsfeed;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ListActivity {
    List headlines = new ArrayList();
    List links = new ArrayList();
    NewsService myService = new NewsService(this);
    boolean isBound = false;
    EditText inputSearch;
    ArrayAdapter adapter;
    Button button;
    String keyword;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myService.getNews();
        links = myService.getLinks();
        headlines = myService.getHeadlines();


        // Binding data
        adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, headlines);
        inputSearch = (EditText) findViewById(R.id.inputSearch);
        button = (Button) findViewById(R.id.inputButton);

        setListAdapter(adapter);
        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text

                //myService.keyword = inputSearch.getText().toString();
                //myService.getNews();
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                keyword = inputSearch.getText().toString();
                MainActivity.this.adapter.getFilter().filter(keyword);

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            String filename = "Users_News";
            String outputString = keyword;
            @Override
            public void onClick(View v) {
                myService.keyword = inputSearch.getText().toString();
                myService.getNews();

                writeToFile(inputSearch.getText().toString(),context);
            }
        });

    }


    /*@Override
    protected  void onStart()
    {
        super.onStart();
        Intent intent = new Intent(this, NewsService.class);
        startService(intent);
        bindService(intent, myConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop()
    {
        super.onStop();;
        if(isBound)
        {
            unbindService(myConnection);
            isBound = false;
        }
    }*/

    protected void onListItemClick(ListView l, View v, int position, long id) {
        Uri uri = Uri.parse((String) links.get(position));
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private void writeToFile(String data, Context context)
    {
        FileOutputStream fos = null;
        try {
            fos = openFileOutput("Users_News.txt",Context.MODE_PRIVATE);
            //fos = context.openFileOutput("Users_News.txt", Context.MODE_PRIVATE);
            fos.write(data.getBytes());
            //fos.flush();
            fos.close();
            Toast.makeText(context, "Writing to file", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {

        } finally {
            if (fos != null) {
                fos = null;
            }
        }
    }
}
