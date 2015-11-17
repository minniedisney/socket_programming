package yaqiaodeng.com.ebaysearch;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class ResultActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ResultsFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_result, menu);
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
    public static class ResultsFragment extends Fragment {

        private static final String LOG_TAG = ResultsFragment.class.getSimpleName();
        private String jsonResult;
        private ArrayList<String> itemTitleList = new ArrayList<>();
        private ArrayList<String> itemPriceStrList = new ArrayList<>();
        private ArrayList<String> galleryURLList = new ArrayList<>();
        private String shippingCost=null;
        private String keywords=null;

        //collect json results into arraylist, each element contains item information
        private ArrayList<HashMap<String, String>> items = new ArrayList<>();
        public ResultsFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_result, container, false);
            rootView.setBackgroundColor(getResources().getColor(android.R.color.white));
            try {
                Intent intent = getActivity().getIntent();
                if (intent != null) {
                    Bundle extras = intent.getExtras();
                    //jsonResult = intent.getStringExtra(Intent.EXTRA_TEXT);
                    jsonResult = extras.getString("jsonResultStr");
                    keywords = extras.getString("keywords");
                }

               String resultTitleStr = "Results for '" + keywords +"'";

                   TextView resultTitleText = (TextView) rootView.findViewById(R.id.resultsTitle);
                   resultTitleText.setText(resultTitleStr);
                getResultsFromJson(jsonResult);
                ResultListAdapter resultAdapter=new ResultListAdapter(getActivity(),itemTitleList,galleryURLList,itemPriceStrList,items);
                ListView resultList= (ListView) rootView.findViewById(R.id.result_list);
                resultList.setAdapter(resultAdapter);
            }
            catch(JSONException e){
                Log.e(LOG_TAG, "Error", e);
            }

           // Toast.makeText(getActivity(), "Started results Activity", Toast.LENGTH_SHORT).show();
            return rootView;
        }


        /*
            getResultsFromJson
            obtain information for each item and store in 'items' above
         */
        private void getResultsFromJson(String resultsJsonStr)
            throws JSONException{
            JSONObject resultsJson = new JSONObject(resultsJsonStr);

            for(int i=4; i<resultsJson.length(); i++) {
                int n = i-4;
                String itemNo = "item"+n;
                JSONObject itemJsonObj = resultsJson.getJSONObject(itemNo);
                JSONObject basicInfoJsonObj = itemJsonObj.getJSONObject("basicInfo");

                HashMap<String, String> curItem = new HashMap<>();
                curItem.put("title", basicInfoJsonObj.getString("title"));
                String itemTitle = basicInfoJsonObj.getString("title");
                itemTitleList.add(itemTitle);

                String picURL = (!basicInfoJsonObj.getString("pictureURLSuperSize").equals(""))?basicInfoJsonObj.getString("pictureURLSuperSize"):basicInfoJsonObj.getString("galleryURL");
               // String picURL = basicInfoJsonObj.getString("galleryURL");
                curItem.put("galleryURL", basicInfoJsonObj.getString("galleryURL"));
                galleryURLList.add(picURL);

                curItem.put("viewItemURL", basicInfoJsonObj.getString("viewItemURL"));
                curItem.put("pictureURLSuperSize", basicInfoJsonObj.getString("pictureURLSuperSize"));
                curItem.put("convertedCurrentPrice", basicInfoJsonObj.getString("convertedCurrentPrice"));
                String priceStr = "Price: $"+basicInfoJsonObj.getString("convertedCurrentPrice");
                curItem.put("shippingServiceCost", basicInfoJsonObj.getString("shippingServiceCost"));
                shippingCost = basicInfoJsonObj.getString("shippingServiceCost");
                if(shippingCost.equals("") || Float.parseFloat(shippingCost)==0.0){
                    priceStr += " (FREE Shipping)";
                }else{
                    priceStr += "(+$"+basicInfoJsonObj.getString("shippingServiceCost")+" Shipping )";
                }
                itemPriceStrList.add(priceStr);
                curItem.put("priceStr", priceStr);
                curItem.put("conditionDisplayName", basicInfoJsonObj.getString("conditionDisplayName"));
                curItem.put("listingType", basicInfoJsonObj.getString("listingType"));
                curItem.put("location", basicInfoJsonObj.getString("location"));
                curItem.put("categoryName", basicInfoJsonObj.getString("categoryName"));
                curItem.put("topRatedListing", basicInfoJsonObj.getString("topRatedListing"));

                JSONObject sellerInfoObj = itemJsonObj.getJSONObject("sellerInfo");
                curItem.put("sellerUserName", sellerInfoObj.getString("sellerUserName"));
                curItem.put("feedbackScore", sellerInfoObj.getString("feedbackScore"));
                curItem.put("positiveFeedbackPercent", sellerInfoObj.getString("positiveFeedbackPercent"));
                curItem.put("feedbackRatingStar", sellerInfoObj.getString("feedbackRatingStar"));
                curItem.put("topRatedSeller", sellerInfoObj.getString("topRatedSeller"));
                curItem.put("sellerStoreName", sellerInfoObj.getString("sellerStoreName"));
                curItem.put("sellerStoreURL", sellerInfoObj.getString("sellerStoreURL"));

                JSONObject shippingInfoObj = itemJsonObj.getJSONObject("shippingInfo");
                curItem.put("shippingType", shippingInfoObj.getString("shippingType"));
                curItem.put("shipToLocations", shippingInfoObj.getString("shipToLocations"));
                curItem.put("expeditedShipping", shippingInfoObj.getString("expeditedShipping"));
                curItem.put("oneDayShippingAvailable", shippingInfoObj.getString("oneDayShippingAvailable"));
                curItem.put("returnsAccepted", shippingInfoObj.getString("returnsAccepted"));
                curItem.put("handlingTime", shippingInfoObj.getString("handlingTime"));

                items.add(curItem);
            }

             //String test = items.get(4).get("location");
            //Toast.makeText(getActivity(), test, Toast.LENGTH_SHORT).show();

        }
    }
}
