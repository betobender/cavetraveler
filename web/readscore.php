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
	
		$stmt = $dbh->prepare('
			SELECT row_number FROM (
				SELECT @curRow := @curRow + 1 AS row_number, deviceid
				FROM scores
				JOIN (SELECT @curRow := 0) r
				ORDER BY score DESC) AS scores_row
				WHERE scores_row.deviceid = :deviceid');
		try {
			$stmt->execute(array(':deviceid' => $_GET['deviceid']));
			$result = $stmt->fetchAll();
			
			echo "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";
			echo "<Results>\n";
			
			$curRow = 0;
			if(count($result) > 0 ) {
				$curRow = $result[0]['row_number'] - 10;
				if($curRow < 0)
					$curRow = 0;
			}
			
			$stmt = $dbh->prepare('
				SELECT name, score, version, distance, deviceid, @curRow := @curRow + 1 AS row_number
				FROM scores
				JOIN (SELECT @curRow := '.$curRow.') r
				ORDER BY score DESC
				LIMIT '.$curRow.', 20');
			
			$stmt->execute();
			
			foreach($stmt as $r) {
				echo "\t<Result>\n";
				echo "\t\t<Position>".$r['row_number']."</Position>\n";
				echo "\t\t<Name>".$r['name']."</Name>\n";
				echo "\t\t<Score>".$r['score']."</Score>\n";
				echo "\t\t<Version>".$r['version']."</Version>\n";
				echo "\t\t<Distance>".$r['distance']."</Distance>\n";
				echo "\t\t<DeviceId>".$r['deviceid']."</DeviceId>\n";
				echo "\t</Result>\n";
				
			}
			echo "</Results>\n";
		} catch(Exception $e) {
			echo "ERROR\n";
			echo "An error has ocurred: " . $e->getMessage();
		}
	} else {
		echo "ERROR\n";
		echo "An error has occured: invalid hashcode";
	}
?>