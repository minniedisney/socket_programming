<?php
header('Access-Control-Allow-Origin: *');  
date_default_timezone_set('America/Los_Angeles');
$cond=$buy=$ship=$keyStr=$lowStr=$highStr="";
$key = $_GET["keywords"]; $keyStr="&keywords=".$key;
$sort = $_GET["sortby"]; $sortStr="&sortOrder=".$sort;
$page = $_GET["results_per_page"]; $pageStr="&paginationInput.entriesPerPage=".$page;
$pageNumber=$_GET["pageNum"];$pageNumStr="&paginationInput.pageNumber=".$pageNumber;
$num=0;
if(!empty($_GET["min_price"])) {
  $lowStr="&itemFilter[$num].name=MinPrice&itemFilter[$num].value=".$_GET["min_price"];
  $num++;
  }

if(!empty($_GET["max_price"])) {
  $highStr="&itemFilter[$num].name=MaxPrice&itemFilter[$num].value=".$_GET["max_price"];
  $num++;
  }


   $url="http://svcs.ebay.com/services/search/FindingService/v1?siteid=0&SECURITY-APPNAME=USCbed1d4-7e95-4303-ace1-67da6049957&OPERATION-NAME=findItemsAdvanced&SERVICE-VERSION=1.0.0&RESPONSE-DATA-FORMAT=XML$keyStr$lowStr$highStr$sortStr$pageStr$pageNumStr&outputSelector[1]=SellerInfo&outputSelector[2]=PictureURLSuperSize&outputSelector[3]=StoreInfo";
 


$xml = simplexml_load_file($url);
//$temp=array('url'=>(string)$url);
//convert to json

if($xml->paginationOutput->totalEntries==0){
  $temp=array('ack'=>'No results found');
}
else{

$arr1=array('ack'=>(string)$xml->ack,'resultCount'=>(string)$xml->paginationOutput->totalEntries,'pageNumber'=>(string)$xml->paginationOutput->pageNumber,'itemCount'=>(string)$xml->paginationOutput->entriesPerPage);

$arr=$arr2=array();
foreach($xml->searchResult->children() as $item){
  if (empty($item->condition->conditionDisplayName)){
    $basicInfo=array('title'=>(string)$item->title,'viewItemURL'=>(string)$item->viewItemURL,'galleryURL'=>(string)$item->galleryURL,'pictureURLSuperSize'=>(string)$item->pictureURLSuperSize,'convertedCurrentPrice'=>(string)$item->sellingStatus->convertedCurrentPrice,
  'shippingServiceCost'=>(string)$item->shippingInfo->shippingServiceCost,'conditionDisplayName'=>"N/A",'listingType'=>(string)$item->listingInfo->listingType,'location'=>(string)$item->location,
  'categoryName'=>(string)$item->primaryCategory->categoryName,'topRatedListing'=>(string)$item->topRatedListing);
  }
else{
  $basicInfo=array('title'=>(string)$item->title,'viewItemURL'=>(string)$item->viewItemURL,'galleryURL'=>(string)$item->galleryURL,'pictureURLSuperSize'=>(string)$item->pictureURLSuperSize,'convertedCurrentPrice'=>(string)$item->sellingStatus->convertedCurrentPrice,
  'shippingServiceCost'=>(string)$item->shippingInfo->shippingServiceCost,'conditionDisplayName'=>(string)$item->condition->conditionDisplayName,'listingType'=>(string)$item->listingInfo->listingType,'location'=>(string)$item->location,
  'categoryName'=>(string)$item->primaryCategory->categoryName,'topRatedListing'=>(string)$item->topRatedListing);
  }
  $sellerInfo=array('sellerUserName'=>(string)$item->sellerInfo->sellerUserName,'feedbackScore'=>(string)$item->sellerInfo->feedbackScore,'positiveFeedbackPercent'=>(string)$item->sellerInfo->positiveFeedbackPercent,'feedbackRatingStar'=>(string)$item->sellerInfo->feedbackRatingStar,
  'topRatedSeller'=>(string)$item->sellerInfo->topRatedSeller,'sellerStoreName'=>(string)$item->storeInfo->storeName,'sellerStoreURL'=>(string)$item->storeInfo->storeURL);
  $shippingInfo=array('shippingType'=>(string)$item->shippingInfo->shippingType,'shipToLocations'=>(string)$item->shippingInfo->shipToLocations,'expeditedShipping'=>(string)$item->shippingInfo->expeditedShipping,'oneDayShippingAvailable'=>(string)$item->shippingInfo->oneDayShippingAvailable,
  'returnsAccepted'=>(string)$item->returnsAccepted,'handlingTime'=>(string)$item->shippingInfo->handlingTime);
  $temp=array('basicInfo'=>$basicInfo,'sellerInfo'=>$sellerInfo,'shippingInfo'=>$shippingInfo);
  array_push($arr,$temp);
}
$i=0;

while($i<sizeof($arr)){
 $temp=array('item'.$i=>$arr[$i]);
 $arr2=array_merge($arr2,$temp);
 $i++;
}

$temp=array_merge($arr1,$arr2);}
$json = json_encode($temp);
echo $json;


  
?>