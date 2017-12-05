package ie.dkit.rssnewsfeed;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class NewsService extends Service {
    private final IBinder myBinder = new MyLocalBinder();
    List headlines = new ArrayList();
    List links = new ArrayList();
    private NotificationManager mNM;
    private int NOTIFICATION = 1;
    Context context = this;
    public String keyword = readFromFile(this);

    public NewsService(Context context)
    {
        this.context = context;
        //Toast.makeText(this.context, "News Service Parameterized Constructor used", Toast.LENGTH_SHORT).show();
    }

    public NewsService(Context context, String keyword)
    {
        this.context = context;
        this.keyword = keyword;
        //Toast.makeText(this.context, "News Service Parameterized Constructor used", Toast.LENGTH_SHORT).show();
    }

    public NewsService()
    {
        //Toast.makeText(this.context, "News Service Constructor used", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "Service onBind");
        return myBinder;
    }

    public class MyLocalBinder extends Binder {
        NewsService getService()
        {
            return NewsService.this;
        }
    }

    public InputStream getInputStream(URL url) {
        try {
            return url.openConnection().getInputStream();
        } catch (IOException e) {
            return null;
        }
    }

    public void getNews() {

        keyword =  readFromFile(context);
        Runnable r = new Runnable() {
            public void run() {
                try {
                    URL url = new URL("http://feeds.bbci.co.uk/news/world/rss.xml");

                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(false);
                    XmlPullParser xpp = factory.newPullParser();

                    //Get XML from an input stream
                    xpp.setInput(getInputStream(url), "UTF_8");

                    boolean insideItem = false;

                    // Returns the type of current event: START_TAG, END_TAG, etc..
                    int eventType = xpp.getEventType();
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        if (eventType == XmlPullParser.START_TAG) {

                            if (xpp.getName().equalsIgnoreCase("item")) {
                                insideItem = true;
                            } else if (xpp.getName().equalsIgnoreCase("title")) {
                                String title = "";

                                if (insideItem) {
                                    title = xpp.nextText();
                                    headlines.add(title); //extract the headline


                                           // Toast.makeText(context, "Searching for " + uNews.get(i).toString(), Toast.LENGTH_SHORT).show();

                                            if (title.toLowerCase().contains(keyword.toLowerCase())) {
                                                mNM = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

                                                showNotification(title);
                                            }

                                }


                            } else if (xpp.getName().equalsIgnoreCase("link")) {
                                if (insideItem)
                                    links.add(xpp.nextText()); //extract the link of article
                            }
                        } else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")) {
                            insideItem = false;
                        }

                        eventType = xpp.next(); //move to next element
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "GetNews method running the service");

                stopSelf();
            }
        };

        Thread t = new Thread(r);
        t.start();
    }

    @Override
    public void onCreate()
    {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getNews();
        Toast.makeText(this, "onStartCommand reached", Toast.LENGTH_SHORT).show();
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Cancel the persistent notification.
        mNM.cancel(NOTIFICATION);

        // Tell the user we stopped.
        Toast.makeText(this, "Local service stopped", Toast.LENGTH_SHORT).show();
    }

    private void showNotification(String title) {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = ("Read more now");

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        Notification notification = new Notification.Builder(context)
                .setTicker(text)  // the status text
                .setSmallIcon(R.mipmap.ic_launcher) // the icon
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle(title)  // the label of the entry
                .setContentText(text)  // the contents of the entry
                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .build();

        // Send the notification.
        mNM.notify(NOTIFICATION, notification);
    }

    public List getLinks()
    {
        return links;
    }

    public List getHeadlines()
    {
        return headlines;
    }

    private String readFromFile(Context context) {

        String ret = "";
        ArrayList topics = new ArrayList();

        try {
            FileInputStream inputStream = this.context.openFileInput("Users_News.txt");
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        //Toast.makeText(context, "Reading from file", Toast.LENGTH_SHORT).show();
        return ret;
    }



}
