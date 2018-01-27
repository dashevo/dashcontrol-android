package com.dash.dashapp.utils;

import android.content.Context;
import android.os.AsyncTask;

import com.dash.dashapp.interfaces.RSSUpdateListener;
import com.dash.dashapp.models.News;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sebas on 8/5/2017.
 */

public class XmlUtil {

    private static final String TAG = "XmlUtil";
    private static final int NUMBER_FIRST_BATCH = 20;
    private String title = "title";
    private String link = "link";
    private String description = "description";
    private XmlPullParserFactory xmlFactoryObject;
    public volatile boolean parsingComplete = true;
    public Context context = null;
    private RSSUpdateListener dbRSSListener;

    public XmlUtil(Context context) {
        this.context = context;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }

    public void fetchRSSXML(RSSUpdateListener dbListener) {
        this.dbRSSListener = dbListener;
        UpdateRSSDB updateDB = new UpdateRSSDB(dbListener);
        updateDB.execute();
    }

    public class UpdateRSSDB extends AsyncTask<Void, Void, Void> { //change Object to required type
        private RSSUpdateListener dbListener;

        public UpdateRSSDB(RSSUpdateListener dbListener) {
            this.dbListener = dbListener;
        }

        // required methods

        @Override
        protected void onPreExecute() {
            dbListener.onUpdateStarted();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                InputStream xmlContent = HttpUtil.httpRequest(SharedPreferencesManager.getLanguageRSS(context));

                xmlFactoryObject = XmlPullParserFactory.newInstance();
                XmlPullParser myparser = xmlFactoryObject.newPullParser();

                myparser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                myparser.setInput(xmlContent, null);

                parseRSSXMLAndStoreIt(myparser);

                xmlContent.close();
            } catch (Exception e) {
                e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            dbListener.onDatabaseUpdateCompleted();
            super.onPostExecute(aVoid);
        }
    }



    public void parseRSSXMLAndStoreIt(XmlPullParser myParser) {
        int event;
        News news = null;
        String text = null;
        int articleNumber = 0;
        List<News> newsList = new ArrayList<>();
        MyDBHandler dbHandler = new MyDBHandler(context, null);

        try {
            event = myParser.getEventType();

            while (event != XmlPullParser.END_DOCUMENT) {
                String name = myParser.getName();

                switch (event) {
                    case XmlPullParser.START_TAG:
                        if ("item".equals(name)) {
                            news = new News();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        text = myParser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (news != null) {
                            switch (name) {
                                case "item":
                                    // If that news doesn't exist
                                    if (dbHandler.findNews(news.getGuid()) == null) {
                                        // Add it to the database
                                        dbHandler.addNews(news);
                                    }
                                    // Add it to the first batch no matter what
                                    newsList.add(news);
                                    articleNumber++;
                                    if (articleNumber == NUMBER_FIRST_BATCH){
                                        dbRSSListener.onFirstBatchNewsCompleted(newsList);
                                    }
                                    break;
                                case "guid":
                                    news.setGuid(text);
                                    break;
                                case "title":
                                    news.setTitle(text);
                                    break;
                                case "pubDate":
                                    news.setPubDate(text);
                                    break;
                                case "content:encoded":
                                    news.setContent(text);
                                    break;
                            }
                        }
                        break;
                }

                event = myParser.next();
            }
            parsingComplete = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}