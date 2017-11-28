<?php
	// Configuration
	$hostname = 'fdb3.awardspace.com';
	$username = '722189_cave';
	$password = 'cav33xplor3r';
	$database = '722189_cave';
	$secretKey = "cav33xplor3rk3y";

	try {
		$dbh = new PDO('mysql:host='. $hostname .';dbname='. $database, $username, $password);
	} catch(PDOException $e) {
		echo "ERROR\n";
		echo "An error has ocurred: " . $e->getMessage();
	}

	$realHash = md5($_GET['deviceid'] . $secretKey);
	$hash =  $_GET['hash'];

	if($realHash == $hash) { 

		$sth = $dbh->prepare('INSERT INTO scores (deviceid, name, score, version, distance, hash) VALUES (:deviceid, :name, :score, :version, :distance, :hash)');
		try {
			$sth->execute($_GET);
			echo "DATA STORED";
			
		} catch(Exception $e) {
			echo "ERROR\n";
			echo "An error has ocurred: " . $e->getMessage();
		}
	} else {
		echo "ERROR\n";
		echo "An error has occured: invalid hashcode";
	}
?>
      