package yaqiaodeng.com.ebaysearch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;


public class ResultListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final ArrayList<String> itemTitlesList;
    private final ArrayList<String> itemPricesList;
    private final ArrayList<String> itemImagesURLList;
    private final ArrayList<HashMap<String, String>> itemsArr;
    private HashMap<String, String> itemMap;


    public ResultListAdapter(Activity context, ArrayList<String> titles, ArrayList<String> imageURLs, ArrayList<String> prices, ArrayList<HashMap<String, String>> itemsInfoArr) {
        super(context, R.layout.results_row, titles);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.itemTitlesList=titles;
        this.itemImagesURLList=imageURLs;
        this.itemPricesList = prices;
        this.itemsArr = itemsInfoArr;
    }

    public View getView(int position,View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.results_row, null,true);

        TextView title = (TextView) rowView.findViewById(R.id.title);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.item_image);
        TextView price = (TextView) rowView.findViewById(R.id.price);

        title.setText(itemTitlesList.get(position));
        String imgURL = itemImagesURLList.get(position);

        itemMap = itemsArr.get(position);
        String viewItemURL = itemMap.get("viewItemURL");

        DownloadImageTask dlImgTask = new DownloadImageTask(imageView);
        dlImgTask.setContext(context);
        dlImgTask.execute(imgURL, viewItemURL);

        //imageView.setImageDrawable(itemImagesList.get(position));
        price.setText(itemPricesList.get(position));

        //make title clickable and start new DetailsActivity
        SetTitleAction st = new SetTitleAction(context, title, itemsArr.get(position));
        st.setAction();
        return rowView;
    }

    /*
        Async task used to download images from urls and assign them to an imageView
     */
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        private final String LOG_TAG = DownloadImageTask.class.getSimpleName();
        ImageView bmImage;
        String itemEbayURL;
        Context mContext;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        private void setContext(Context context1){
            mContext = context1;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            itemEbayURL = urls[1];
            Bitmap mIcon11 = null;
            if(!urldisplay.equals("")) {
                try {
                    InputStream in = new java.net.URL(urldisplay).openStream();
                    mIcon11 = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    Log.e(LOG_TAG, e.getMessage());
                    e.printStackTrace();
                }
                return mIcon11;
            }else{
                return null;
            }

        }

        protected void onPostExecute(Bitmap result) {

            if(result!=null) {
                bmImage.setImageBitmap(result);
            }else{
                bmImage.setImageDrawable(context.getResources().getDrawable(R.drawable.no_image));
            }
            //make image clickable to open ebay webpage of the item
            bmImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // open the desired ebay page with intent
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(itemEbayURL));
                    mContext.startActivity(i);
                }
            });
        }
    }

    /*
        Make title in TextView clickable to start a DetailsActivity and item Info for populating
     */
    private class SetTitleAction{
        private final HashMap<String, String> itemInfo;
        private final TextView title;
        private final Context context;

        protected SetTitleAction(Context context, TextView title, HashMap<String, String> itemInfo){
            this.title = title;
            this.context = context;
            this.itemInfo = itemInfo;
        }

        protected void setAction() {
            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent detailsIntent = new Intent(context, DetailsActivity.class)
                            .putExtra("itemInfo", itemInfo);
                    context.startActivity(detailsIntent);
                }
            });
        }

    }
}
