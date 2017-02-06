<?php
ini_set('include_path', '/home/mysoreja/Downloads/solr-php-client-master/');

$stop_word_arr_1 = file('stopwords.txt');
$stop_word_arr = array_map('trim', $stop_word_arr_1);

if(!empty($_GET["q"])){
	$str = strtolower($_GET["q"]); 
	$split = [];
	$split = explode(" ", $str);
	$count_of_query_words = count($split);
	$last_word = $split[$count_of_query_words-1];
	// The Apache Solr Client library should be on the include path
	// which is usually most easily accomplished by placing in the
	// same directory as this script ( . or current directory is a default
	// php include path entry in the php.ini)
	require_once('Apache/Solr/Service.php');
	// create a new solr service instance - host, port, and corename
	// path (all defaults in this example)
	$solr = new Apache_Solr_Service('localhost', 8983, '/solr/myexample1/');
	// $additionalParameters = array(
	//  'qt' => '/suggest'
	// );

	$max_no_of_sugg = 4;
	if (strlen($last_word) == 1) {
		$max_no_of_sugg = 10;
	}
	elseif(strlen($last_word) == 2){
		$max_no_of_sugg = 6;
	}
	$json = file_get_contents('http://localhost:8983/solr/myexample1/suggest?indent=on&wt=json&q='.$last_word);

	$arr =[];
	$clean_arr =[];
	$result = json_decode($json,true);
	$terms_arr = $result["suggest"]["suggest"][$last_word]["suggestions"];
	foreach ($terms_arr as $suggest)
	{	
		 foreach ($suggest as $field => $value)
		 {
		 	if($field == 'term'){
		 		$v_term = htmlspecialchars($value, ENT_NOQUOTES, 'utf-8');

		 		if((strpos($v_term,".") == false) and (strpos($v_term,":") == false) and (strpos($v_term,"_") == false)){ //only proceed if it is a good suggestion
		 			$temp_arr = [];
			 		$temp_var = "";
			 		for ($i=0; $i < $count_of_query_words-1 ; $i++) { 
			 			$temp_var = $temp_var." ".$split[$i];
			 		}
			 		$temp_var = $temp_var." ".$v_term;
			 		$temp_var = substr($temp_var, 1);
			 		if ($temp_var !== $str ) { //dont show words entered so far as the suggestion
			 			$temp_arr['label'] = $temp_var;
			 			array_push($arr, $temp_arr);
			 		}
		 		}
	 		}
		 }
		 
		 if (sizeof($arr) == $max_no_of_sugg) 
		 	break;
	}


	foreach ($terms_arr as $suggest)
	{	
		 foreach ($suggest as $field => $value)
		 {
		 	if($field == 'term'){
		 		$v_term = htmlspecialchars($value, ENT_NOQUOTES, 'utf-8');
		 		if(((in_array($v_term, $stop_word_arr)) === false ) and (0 === strpos($v_term, $last_word)) and (strpos($v_term,".") == false) and (strpos($v_term,":") == false) and (strpos($v_term,"_") == false)){ //only proceed if not a stop word and it is a good suggestion
		 			$temp_arr = [];
			 		$temp_var = "";
			 		for ($i=0; $i < $count_of_query_words-1 ; $i++) { 
			 			$temp_var = $temp_var." ".$split[$i];
			 		}
			 		$temp_var = $temp_var." ".$v_term;
			 		$temp_var = substr($temp_var, 1);

			 		if ($temp_var !== $str ) { //dont show words entered so far as the suggestion
			 			$temp_arr['label'] = $temp_var;
			 			array_push($clean_arr, $temp_arr);
			 		}
		 		}
	 		}
		 }
		 
		 if (sizeof($clean_arr) == $max_no_of_sugg) 
		 	break;
	}

	if (sizeof($clean_arr) > 1) {
		$arr = $clean_arr;
	}

	$arr= array_slice($arr, 0, 9);


	echo json_encode($arr);
}
?>
