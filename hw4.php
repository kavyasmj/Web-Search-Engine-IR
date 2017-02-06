<!-- <?php
	// if (!empty($_SERVER['HTTPS']) && ('on' == $_SERVER['HTTPS'])) {
	// 	$uri = 'https://';
	// } else {
	// 	$uri = 'http://';
	// }
	// $uri .= $_SERVER['HTTP_HOST'];
	// header('Location: '.$uri.'/xampp/');
	// exit;
?>
Something is wrong with the XAMPP installation :-(
 -->

<?php
include '/home/mysoreja/Downloads/simplehtmldom_1_5/simple_html_dom.php';
ini_set('include_path', '/home/mysoreja/Downloads/solr-php-client-master/');
include 'SpellCorrector.php';
// make sure browsers see this page as utf-8 encoded HTML
header('Content-Type: text/html; charset=utf-8');

$limit = 10;
$query = isset($_REQUEST['q']) ? $_REQUEST['q'] : false;
$query = strtolower($query);
$results = false;
$additionalParameters = array(
 'sort' => 'pageRankFile desc'
);

if ($query)
{

	// The Apache Solr Client library should be on the include path
	// which is usually most easily accomplished by placing in the
	// same directory as this script ( . or current directory is a default
	// php include path entry in the php.ini)
	require_once('Apache/Solr/Service.php');
	// create a new solr service instance - host, port, and corename
	// path (all defaults in this example)
	$solr = new Apache_Solr_Service('localhost', 8983, '/solr/myexample1/');

	// if magic quotes is enabled then stripslashes will be needed
	if (get_magic_quotes_gpc() == 1)
	{
		$query = stripslashes($query);
	}

	if (!isset($_REQUEST['spell'])) {
		$o_str = $query;
		$split = [];
		$corrected_term = "";
		$split = explode(" ", $query);
		$count_of_query_words = count($split);

		$i = 0;
		foreach ($split as $term){
			$corrected  = "";
			$corrected = SpellCorrector::correct($term);
			if ($i == 0) {
				$corrected_term = $corrected;
			}
			else{
				$corrected_term = $corrected_term." ".$corrected;
			}
			
			$i++;
		}
		if ($corrected_term != $query) {
			$query = $corrected_term;
			$_GET['q'] = str_replace(' ', '+' ,$_GET['q']);
			$corrected_uri = str_replace(' ', '+' ,$corrected_term);
			if ($_GET['algo'] == 'solr') {
				$search_again = "http://localhost/index.php?q=".$_GET['q'].'&algo=solr&spell=off';
				$corrected_url = "http://localhost/index.php?q=".$corrected_uri.'&algo=solr&spell=off';
			}
			else{
				$search_again = "http://localhost/index.php?q=".$_GET['q']."&algo=pr&spell=off";
				$corrected_url = "http://localhost/index.php?q=".$corrected_uri.'&algo=pr&spell=off';
			}
			?>
			<div>
			Showing results for:<a href= <?php echo $corrected_url?>><?php echo $query?></a><br>
			Search instead for: <a href= <?php echo $search_again?>><?php echo $o_str?></a>
			</div>
			<?php
		}
	}

 // in production code you'll always want to use a try /catch for any
 // possible exceptions emitted by searching (i.e. connection
 // problems or a query parsing error)
 try
 {
 	// Since form method='get'
	if ($_GET['algo'] == 'solr') {
		$results = $solr->search($query, 0, $limit);
	}
	else{
		$results = $solr->search($query, 0, $limit, $additionalParameters);
	}
 }
 catch (Exception $e)
 {
 // in production you'd probably log or email this error to an admin
 // and then show a special message to the user but for this example
 // we're going to show the full exception
 die("<html><head><title>SEARCH EXCEPTION</title><body><pre>{$e->__toString()}</pre></body></html>");
 }
}
?>

<!DOCTYPE html>
<html>
<head>
<meta charset=utf-8 />
 <title>PHP Solr Client Example</title>
  <link href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/themes/base/jquery-ui.css" rel="stylesheet" type="text/css"/>
  <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.5/jquery.min.js"></script>
  <script src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/jquery-ui.min.js"></script>
  <script>
  $(document).ready(function(){
    $('#q').focus();
  });
  $(document).ready(function() {
    $('#q').autocomplete({
      source: function(request, response) {
		    $.getJSON("suggest.php", { q: $('#q').val() }, 
		              response);
		  },
    });
  });
  </script>
</head>
 <body>
 <form accept-charset="utf-8" method="get">
 <label for="q">Search:</label>
 <input id="q" name="q" type="text" value="<?php echo htmlspecialchars($query, ENT_QUOTES, 'utf-8'); ?>"/>
 <input type="submit"/><br>  
 Default SOLR Algorithm <input type="radio" checked="checked" name="algo" <?php if (isset($_GET['algo']) && $_GET['algo']=="solr") echo "checked";?> value="solr"> <br>
 Page Ranking Algorithm <input type="radio" name="algo" <?php if (isset($_GET['algo']) && $_GET['algo']=="pr") echo "checked";?> value="pr"> <br>
 </form>
<?php
// display results
if ($results)
{
 $query = $results->responseHeader->params->q;
 $query_list = preg_split('/\s/', $query);

				
 $total = (int) $results->response->numFound;
 $start = min(1, $total);
 $end = min($limit, $total);
?>
 <div>Results <?php echo $start; ?> - <?php echo $end;?> of <?php echo $total; ?>:</div>
 <ol>
<?php
	//fetch combined csv mapping files
    $myArr = array();
	if (($handle = fopen("/home/mysoreja/Downloads/LATimesHuffingtonPostData/LATimesHuffingtonPostData/map.csv", 'r')) !== FALSE) {
	    while (($data = fgetcsv($handle,1000,";")) !== FALSE) {
	    	$myArr[$data[0]] = $data[1];
    }
    fclose($handle);
}

 foreach ($results->response->docs as $doc)
 {
	 foreach ($doc as $field => $value)
	 {

	 	if($field == 'og_url'){
	 		$v_url = htmlspecialchars($value, ENT_NOQUOTES, 'utf-8');
	 	}
		elseif($field == 'title'){
	 		$v_title = htmlspecialchars($value, ENT_NOQUOTES, 'utf-8');
		}
		elseif($field == 'id'){
	 		$v_id = htmlspecialchars($value, ENT_NOQUOTES, 'utf-8');

	 		$html_scrape = file_get_html($v_id);
	 		if ($html_scrape != "") {
			    // $data = $html_scrape->find('body', 0)->plaintext;
			    $data = $html_scrape->plaintext;
			    $all_sentences = preg_split("/(?<!\w\.\w.)(?<![A-Z][a-z]\.)(?<=\.|\?)\s/", $data);
			    $final_str  = "";

			    foreach ($all_sentences as $all_sentence) {
			    	$prev_sentence = $all_sentence;
			    	foreach ($query_list as $query_term) {
			    		$word =' '.$query_term.' ';
			    		if(stripos($all_sentence,$word)){
			    			if ($final_str == "") {
			    				$final_str = $all_sentence;
			    			}
			    			else{
			    				$final_str = $final_str." . ".$all_sentence;
			    			}
			    			
			    			if (strlen($final_str)>155) {
			    				break 2;
			    			}
			    		}
			    	}
			    }	

			    if (strlen($final_str) > 3000) {
			    	foreach ($query_list as $query_term) {
			    		$word =' '.$query_term.' ';
			    		$pos_of_qterm = stripos($final_str,$word);
			    		if ($pos_of_qterm ) {
			    			$soem = substr($final_str,$pos_of_qterm,2055); 
			    			$final_str = $soem;
			    			break;
			    		}
			    	}
			    }
			    if ($final_str == "") {
			    	$v_desc  = $html_scrape->find('title', 0)->innertext;;
			    }
			    else
			    	$v_desc = $final_str;

			   $html_scrape = "";
			   $data = "";
			   $sentences = "";
	 		}
	 		
		}
	 }

	 // incase we had id but no url, map it from csv file data
	 if( !isset($v_url)){
	 	$key = explode('/',$v_id);
	 	$key_last = end($key);
		$v_url = $myArr[$key_last];
	 }

	 // in case title and desc is not set, leave it blank
	 if( !isset($v_title)){
	 	$v_title = "";
	 }

	 if( !isset($v_desc)){
	 	$v_desc = "";
	 }

?>
	<li>
		<b><a href= <?php echo $v_url ?>><?php echo $v_title ?></a></b><br>
		<a style="color:green" href=<?php echo $v_url?>><?php echo $v_url ?></a><br>
		<i>[ id : <?php echo $v_id ?> ]</i><br>
		<?php echo $v_desc?> <br><br>
	</li>
<?php
	//clear the variables
	 unset($v_url);
	 unset($v_title);
	 unset($v_id);
	 unset($v_desc);
 }
?>
 </ol>
<?php
}
?>
 </body>
</html>

