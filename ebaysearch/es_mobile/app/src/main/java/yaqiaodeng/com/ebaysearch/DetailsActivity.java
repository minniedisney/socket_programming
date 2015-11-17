package yaqiaodeng.com.ebaysearch;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.io.InputStream;
import java.util.HashMap;


public class DetailsActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailsFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailsFragment extends Fragment {
        CallbackManager callbackManager;
        ShareDialog shareDialog;
        HashMap<String, String> itemMap;

        public DetailsFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_details, container, false);
            rootView.setBackgroundColor(getResources().getColor(android.R.color.white));
            //initialize facebook sdk
            FacebookSdk.sdkInitialize(getActivity());
            callbackManager = CallbackManager.Factory.create();  //callback used to handle response from post
            shareDialog = new ShareDialog(this);
            // this part is optional
            shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {

                @Override
                public void onSuccess(Sharer.Result Result){
                    String res = Result.toString();
                    int i=0;
                    while(res.charAt(i)!='@') i++;
                    //String ID = res.substring(i+1, res.length());
                    Toast.makeText(getActivity(), "Successfully Posted", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onCancel(){
                    Toast.makeText(getActivity(), "Cancelled Post", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(FacebookException error){

                }
            });

            //obtain HashMap with item Info that is passed in intent from Result Activity
            Intent intent = getActivity().getIntent();
            if (intent != null) {
                itemMap = (HashMap<String, String>)intent.getSerializableExtra("itemInfo");
            }


            final RelativeLayout basicInfoPane = (RelativeLayout) rootView.findViewById(R.id.basicInfo_pane);
            final RelativeLayout sellerInfoPane = (RelativeLayout) rootView.findViewById(R.id.sellerInfo_pane);
            final RelativeLayout shippingInfoPane = (RelativeLayout) rootView.findViewById(R.id.shippingInfo_pane);
            sellerInfoPane.setVisibility(View.GONE);
            shippingInfoPane.setVisibility(View.GONE);

            final Button basicInfoButton = (Button) rootView.findViewById(R.id.details_basicInfoButton);
            final Button sellerInfoButton = (Button) rootView.findViewById(R.id.details_sellerButton);
            final Button shippingInfoButton = (Button) rootView.findViewById(R.id.details_shippingButton);

            basicInfoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    basicInfoPane.setVisibility(View.VISIBLE);
                    sellerInfoPane.setVisibility(View.GONE);
                    shippingInfoPane.setVisibility(View.GONE);
                }
            });
            sellerInfoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    basicInfoPane.setVisibility(View.GONE);
                    sellerInfoPane.setVisibility(View.VISIBLE);
                    shippingInfoPane.setVisibility(View.GONE);
                }
            });
            shippingInfoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    basicInfoPane.setVisibility(View.GONE);
                    sellerInfoPane.setVisibility(View.GONE);
                    shippingInfoPane.setVisibility(View.VISIBLE);
                }
            });



            // Obtain Views to populate in fragment details
            //BasicInfo
            ImageView itemBigPic = (ImageView) rootView.findViewById(R.id.details_itemBigPic);
            TextView itemTitle = (TextView) rootView.findViewById(R.id.details_title);
            TextView itemPrice = (TextView) rootView.findViewById(R.id.details_price);
            TextView itemLoc = (TextView) rootView.findViewById(R.id.details_location);
            TextView itemCat = (TextView) rootView.findViewById(R.id.details_categoryName);
            TextView itemCond = (TextView) rootView.findViewById(R.id.details_condition);
            TextView itemBuyingFormat = (TextView) rootView.findViewById(R.id.details_buyingFormat);
            ImageView topRatedIcon = (ImageView) rootView.findViewById(R.id.details_topSellerIcon);
            //Seller Info
            TextView itemUserName = (TextView) rootView.findViewById(R.id.details_userName);
            TextView item_feedbackScore = (TextView) rootView.findViewById(R.id.details_feedbackScore);
            TextView item_positiveFeedback = (TextView) rootView.findViewById(R.id.details_positiveFeedback);
            TextView item_feedbackRating = (TextView) rootView.findViewById(R.id.details_feedbackRating);
            ImageView item_topRated = (ImageView) rootView.findViewById(R.id.details_topRated);
            TextView item_store = (TextView) rootView.findViewById(R.id.details_store);
            //shipping Info
            TextView itemShippingType = (TextView) rootView.findViewById(R.id.details_shippingType);
            TextView itemHandlingTime = (TextView) rootView.findViewById(R.id.details_handlingTime);
            TextView itemShippingLocations = (TextView) rootView.findViewById(R.id.details_shippingLocations);
            ImageView itemExpeditedShipping = (ImageView) rootView.findViewById(R.id.details_expeditedShipping);
            ImageView itemOneDayShipping = (ImageView) rootView.findViewById(R.id.details_oneDayShipping);
            ImageView itemReturnsAccepted = (ImageView) rootView.findViewById(R.id.details_returnedAccepted);

            //find Buy it Now button and FB image and assign clickables to them
            Button buyNowButton = (Button) rootView.findViewById(R.id.details_buyItNowButton);

            //make 'buy now' button clickable to ebay page of item
            buyNowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(itemMap.get("viewItemURL")));
                    getActivity().startActivity(i);
                }
            });

            ImageView fbIcon = (ImageView) rootView.findViewById(R.id.details_fbIcon);
            fbIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fbShare();
                }
            });


            //populate views in detail fragment with info from item info HashMap sent from result Activity via intent
            DownloadImageTask dlImgTask = new DownloadImageTask(itemBigPic);
            dlImgTask.setContext(getActivity());
            String picURL = (!itemMap.get("pictureURLSuperSize").equals(""))?itemMap.get("pictureURLSuperSize"):itemMap.get("galleryURL");
            dlImgTask.execute(picURL);
            itemTitle.setText(itemMap.get("title"));
            itemPrice.setText(itemMap.get("priceStr"));
            itemLoc.setText(itemMap.get("location"));
            if(itemMap.get("topRatedListing").equals("true")){
                topRatedIcon.setVisibility(View.VISIBLE);
            }else{
                topRatedIcon.setVisibility(View.GONE);
            }

            itemCat.setText(itemMap.get("categoryName"));
            if(!itemMap.get("conditionDisplayName").equals("")) {
                itemCond.setText(itemMap.get("conditionDisplayName"));
            }else{
                itemCond.setText("N/A");
            }

            itemBuyingFormat.setText(itemMap.get("listingType"));

            itemUserName.setText(itemMap.get("sellerUserName"));
            item_feedbackScore.setText(itemMap.get("feedbackScore"));
            item_positiveFeedback.setText(itemMap.get("positiveFeedbackPercent"));
            item_feedbackRating.setText(itemMap.get("feedbackRatingStar"));

            if(itemMap.get("topRatedSeller").equals("true")){
                item_topRated.setImageResource(R.drawable.checkmark);
            }else{
                item_topRated.setImageResource(R.drawable.redx);
            }
            String storeName = (!itemMap.get("sellerStoreName").equals(""))?itemMap.get("sellerStoreName"):"N/A";
            item_store.setText(storeName);
            itemShippingType.setText(itemMap.get("shippingType"));
            itemHandlingTime.setText(itemMap.get("handlingTime"));
            itemShippingLocations.setText(itemMap.get("shipToLocations"));
            if(itemMap.get("expeditedShipping").equals("true")){
                itemExpeditedShipping.setImageResource(R.drawable.checkmark);
            }else{
                itemExpeditedShipping.setImageResource(R.drawable.redx);
            }
            if(itemMap.get("oneDayShippingAvailable").equals("true")){
                itemOneDayShipping.setImageResource(R.drawable.checkmark);
            }else{
                itemOneDayShipping.setImageResource(R.drawable.redx);
            }
            if(itemMap.get("returnsAccepted").equals("true")){
                itemReturnsAccepted.setImageResource(R.drawable.checkmark);
            }else{
                itemReturnsAccepted.setImageResource(R.drawable.redx);
            }


            return rootView;
        }
        /*
            creates share dialog to post on FB
         */
        private void fbShare(){
            if (ShareDialog.canShow(ShareLinkContent.class)) {
                String description = itemMap.get("priceStr") + ", Location: " + itemMap.get("location");
                ShareLinkContent content = new ShareLinkContent.Builder()
                        .setContentTitle((itemMap.get("title")))
                        .setContentUrl(Uri.parse(itemMap.get("viewItemURL")))
                        .setImageUrl(Uri.parse(itemMap.get("galleryURL")))
                        .setContentDescription(description)
                        .build();
                shareDialog.show(content);
            }
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    /*
        Downloads images from urls in the background
     */
    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
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
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage());
                e.printStackTrace();
            }

            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            if(result!=null) {
                bmImage.setImageBitmap(result);
            }else{
                bmImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.no_image));
            }
        }
    }
}
