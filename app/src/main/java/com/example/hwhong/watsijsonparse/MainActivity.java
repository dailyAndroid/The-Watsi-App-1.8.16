package com.example.hwhong.watsijsonparse;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);

        /*
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
*/

        actionBar.setIcon(R.drawable.icon);

        new JSONTask().execute("https://watsi.org/fund-treatments.json");
    }

    public class JSONTask extends AsyncTask<String, String, List<ProfileView>> {

        @Override
        protected List<ProfileView> doInBackground(String... urls) {
            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;

            try {
                URL url = new URL(urls[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while((line = bufferedReader.readLine()) !=  null) {
                    buffer.append(line);
                }

                String json = buffer.toString();

                JSONObject jsonObject = new JSONObject(json);
                JSONArray array = jsonObject.getJSONArray("profiles");

                List<ProfileView> profileViewList = new ArrayList<>();

                for (int i = 0; i < array.length(); i++) {
                    ProfileView profileView = new ProfileView();

                    JSONObject childJson = array.getJSONObject(i);

                    profileView.setName(childJson.getString("name"));
                    profileView.setHeader(childJson.getString("header"));
                    profileView.setPartner_name(childJson.getString("partner_name"));
                    profileView.setPhoto_url(childJson.getString("photo_url"));

                    profileView.setTarget_amount(childJson.getInt("target_amount"));
                    profileView.setAmount_raised(childJson.getInt("amount_raised"));

                    profileView.setLaunch_url(childJson.getString("url"));

                    profileViewList.add(profileView);
                }

                return profileViewList;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                try {
                    if (bufferedReader != null) {
                        bufferedReader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<ProfileView> profileViews) {
            //handling the UI Works i.e updating the row xml
            super.onPostExecute(profileViews);

            final WatsiAdapter adapter = new WatsiAdapter(getApplicationContext(),
                    R.layout.profileview, profileViews);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Uri uri = Uri.parse(adapter.profileViewList.get(i).getLaunch_url());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
        }

        public class WatsiAdapter extends ArrayAdapter {

            private List<ProfileView> profileViewList;
            private int resource;
            private LayoutInflater inflater;

            public class ProfileViewHolder {
                private TextView name, partner_name, header, amount_raised, target_amount;
                private ImageView photo;
            }

            public WatsiAdapter(Context context, int resource, List profiles) {
                super(context, resource, profiles);
                this.profileViewList = profiles;
                this.resource = resource;
                this.inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ProfileViewHolder holder = null;

                if (convertView == null ){
                    holder = new ProfileViewHolder();
                    convertView = inflater.inflate(resource, null);


                    holder.name = (TextView) convertView.findViewById(R.id.name);
                    holder.partner_name = (TextView) convertView.findViewById(R.id.partner_name);
                    holder.header = (TextView) convertView.findViewById(R.id.header);
                    holder.amount_raised = (TextView) convertView.findViewById(R.id.amount_raised);
                    holder.target_amount = (TextView) convertView.findViewById(R.id.target_amount);
                    holder.photo = (ImageView) convertView.findViewById(R.id.imageView);

                    convertView.setTag(holder);
                } else {
                    holder = (ProfileViewHolder) convertView.getTag();
                }

                holder.name.setText(profileViewList.get(position).getName());
                holder.partner_name.setText(profileViewList.get(position).getPartner_name());
                holder.header.setText(profileViewList.get(position).getHeader());
                holder.amount_raised.setText("Amount Raised: " + profileViewList.get(position).getAmount_raised());
                holder.target_amount.setText("Target Amount: " + profileViewList.get(position).getTarget_amount());

                new ImageLoadTask(profileViewList.get(position).getPhoto_url(), holder.photo).execute();

                return convertView;
            }

            public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

                private String url;
                private ImageView imageView;

                public ImageLoadTask(String url, ImageView imageView) {
                    this.url = url;
                    this.imageView = imageView;
                }

                @Override
                protected Bitmap doInBackground(Void... params) {
                    try {
                        URL urlConnection = new URL(url);
                        HttpURLConnection connection = (HttpURLConnection) urlConnection
                                .openConnection();
                        connection.setDoInput(true);
                        connection.connect();
                        InputStream input = connection.getInputStream();
                        Bitmap myBitmap = BitmapFactory.decodeStream(input);
                        return myBitmap;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Bitmap result) {
                    super.onPostExecute(result);
                    imageView.setImageBitmap(result);
                }
            }
        }
    }
}
